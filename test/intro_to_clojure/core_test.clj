(ns intro-to-clojure.core-test
  (:use [midje.sweet]
        [intro-to-clojure.core]))


(defn multiplier [factor]
  (fn [x] (* factor x)))

(fact "Some basic clojure stuff"
      ((multiplier 1) 2) => 1)
