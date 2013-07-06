(defproject uplift "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :plugins [[lein-ring "0.8.5"]
            [lein-cljsbuild "0.3.2"]]
  :source-paths ["src/clj"]
  :cljsbuild {:builds
              [{:source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/uplift.js"
                           :optimizations :advanced
                           :pretty-print false}}]}
  :ring {:handler uplift.core/app :port 8080 :auto-refresh? true}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"] ; clojure itself

                 [com.datomic/datomic-free "0.8.4020"]

                 [ring-server "0.2.8"] ; the server
                 [compojure "1.1.5"] ; routing

                 [org.clojure/tools.reader "0.7.4"] ; edn reader

                 [clj-time "0.5.1"] ; time

                 [enlive "1.1.1"] ; templating, server-side
                 [enfocus "2.0.0-SNAPSHOT"] ; templating, client-side

                 ])
