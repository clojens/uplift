(ns uplift.storage.protocol
  (:import java.util.concurrent.TimeUnit
           java.util.concurrent.Executors)
  (:require [clojure.java.io :refer [writer reader]]
            [clj-bcrypt-wrapper.core :refer [encrypt check-password]]))

(defprotocol Storage
  (init! [this config] "Performs any side effects necessary to initialize the
                       storage medium. Returns a function that reverses these
                       side effects and closes the storage medium.")
  (add-user [this username password])
  (get-user [this username])
  (check-pw [this username password])
  (change-password [this user new-password]))

(defn blank-memory []
  {:next-id 0
   :users {}})

(defrecord MemoryStorage [db]
  Storage
  (init! [_ {path :path}]
    (let [persist-db (fn [] (spit path (str @db)))
          read-db (fn [] (read-string (slurp path)))]
      (try
        (reset! db (read-db))
        (catch java.io.IOException ioe
          (println "Database" path "not found, using test data")
          (reset! db (blank-memory))))
      (let [shutdown-thread (Thread. persist-db)
            shutdown-hook (..
                            Runtime
                            getRuntime
                            (addShutdownHook shutdown-thread))
            thread-pool (Executors/newScheduledThreadPool 1)
            scheduled-exec (.
                            thread-pool
                            (scheduleAtFixedRate persist-db
                                                 (long 1)
                                                 (long 1)
                                                 (. TimeUnit MINUTES)))]
        #(do (.shutdown thread-pool)
             (.. Runtime getRuntime (removeShutdownHook shutdown-thread))
             (persist-db)))))

  (add-user [_ username password]
    (let [next-id (:next-id @db)
          user {:id next-id
                :username username
                :password (encrypt password)}]
      (swap! db (fn [db]
                  (-> db
                    (assoc :next-id (inc next-id))
                    (assoc-in [:users next-id] user))))
      user))

  (get-user [_ username]
    (->> (map val (:users @db))
      (filter #(= (:username %) username))
      (first)))

  (check-pw [this {old-pw :password} password]
    (check-password password old-pw))

  (change-password [_ {id :id} password]
    (swap! db (fn [db]
                (assoc-in db [:users id :password] (encrypt password))))))
