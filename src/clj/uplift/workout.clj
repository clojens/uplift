(ns uplift.workout
  (:require [datomic.api :refer [q db] :as d])
  (:require [clj-time.core :as clj-time]
            [clj-time.coerce :as coerce]
            [clj-time.format]))

;; uplift.workout
;;
;; retrieves and modifies workouts from datomic
;; a user has many workouts
;; a workout has a day and some activities
;; an activity has a name (e.g. "squat") and many lifts
;; a lift has a weight, reps, and sets.

(declare to-longday from-longday get-all-workout user-workout)

(defn get-all-workout
  "Gets all workout data in a seq, for a user's day"
  [conn user day]
  (q '[:find ?activity-type ?lift ?weight ?reps ?sets
       :in $ ?user ?day
       :where [?user :user/workouts ?workout]
              [?workout :workout/day ?day]
              [?workout :workout/activities ?activity]
              [?activity :activity/type ?activity-type]
              [?activity :activity/lifts ?lift]
              [?lift :lift/weight ?weight]
              [?lift :lift/reps ?reps]
              [?lift :lift/sets ?sets]]
     (db conn)
     (:db/id user)
     day))

(defn user-workout [conn user day]
  (into {} (map (fn [[k v]]
                  [k (map nnext v)])
                (group-by (comp keyword first) (get-all-workout
                                                 conn
                                                 user
                                                 (to-longday day))))))

(defn to-longday
  "Convenience fn to convert clj-time to long with only Y-M-D"
  ([] (coerce/to-long (clj-time/today-at 00 00)))
  ([t] (coerce/to-long
         (clj-time/date-time (clj-time/year t)
                             (clj-time/month t)
                             (clj-time/day t)))))

(defn from-longday
  "Convenience fn to convert Y-M-D back to clj-time"
  [l] (coerce/from-long l))
