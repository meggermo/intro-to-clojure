(defproject intro-to-clojure "0.0.1-SNAPSHOT"

  :description "An introduction to clojure"
  :url "http://github.com/meggermo/intro-to-clojure"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [midje "1.6.3" :exclusions [org.clojure/clojure]]]

  :profiles {:dev
             {:dependencies [[midje "1.6.3" :exclusions [org.clojure/clojure]]]
              :plugins [[lein-midje "3.1.3"]]}})
