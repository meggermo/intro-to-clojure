(ns intro-to-clojure.project-euler-test
  (:use [midje.sweet]
        [intro-to-clojure.project-euler]))

(facts "about divisors"

       (fact "divisor?"
             (divisor? 1 1) => truthy
             (divisor? 1 2) => falsey
             )

       (fact "divisors?"
             (divisors? 1 1) => truthy
             (divisors? 10 5 2) => truthy
             )
       )

(fact "answer to Problem 1"
      (reduce + (filter #(divides? % 3 5) (range 1000))) => 33165

      )
