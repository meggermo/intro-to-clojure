(ns intro-to-clojure.functions)

;; --------------------------------------------
;; Functions are 1st class citizens in Clojure
;; because they can be
;; - created on demand
;; - stored in a data structure
;; - passed as arguments to a function
;; - returned as the value of a function
;; --------------------------------------------

;; So how do you define a function?
;; use a function to create them!
;; use (doc f) to get documentation on f
(defn my-fn!
  "This is my first clojure function"
  [x]
  ;; functions return the value of the
  ;; last evaluated expression
  (println "hello" x)
  true
  false
  (.toUpperCase x)
)

(my-fn! "there")

;; Or use the fn function for defining anonymous functions
;; and bind them to a symbol yourself
(def join
  (fn [sep coll]
    (apply str (interpose sep coll))))

(join \- [\a "and" \b])

;; Or use the # special form
(def times-two
  #(* 2 %))

(times-two 3)

;; Clojure has many ways to construct function out of functions
;; composition
((comp not even?) 1)
((comp first rest) [1 2 3])
((comp keyword name) (quote blip))

;; partial application
(let [plus-5 (partial + 5)]
  (plus-5 10 20))

;; The first argument of the function map is the function even?
(let [all-even (partial map even?)]
  (all-even (range 4)))


;; --------------------------------------------
;; Closures and functions
;; --------------------------------------------
;; using local binding with let
(defn times-n [n]
  (let [x n]
    (fn [y] (* y x))))

;; You can close over the function arguments directly too
(defn divisor? [denom]
  (fn [number]
    (zero? (rem number denom))))

(def d7 (divisor? 7))

;; numbers between 1 and 64 divisible by 7:
(filter d7 (range 1 64))
