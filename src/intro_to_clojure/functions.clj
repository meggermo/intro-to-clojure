(ns intro-to-clojure.functions)

;; --------------------------------------------
;; Functions are 1st class citizens in Clojure
;; because they can be
;; - created on demand
;; - stored in a data structure
;; - passed as arguments to a function
;; - returned as the value of a function
;; --------------------------------------------

;; On demand creation
;; by composition
((comp not even?) 1)
((comp first rest) [1 2 3])
((comp keyword name) (quote blip))

;; by partial application
((partial + 5) 10 20)
((partial map even?) (range 4))

;; Functions as data
(defn join
  {:test (fn []
           (assert
            (= (join "/" [\a \b \c]) "a/b/d")))}
  [separator coll]
  (apply str (interpose separator coll)))
(clojure.test/run-tests)


;; Higher order functions
;; Functions as arguments
(def languages
  [{:name "fortran"   :year 1957 :rank 40 :loc 9.0e6}
   {:name "lisp"      :year 1958 :rank 10 :loc 0.1e6}
   {:name "smalltalk" :year 1968 :rank 11 :loc 0.1e6}
   {:name "haskell"   :year 1990 :rank 10 :loc 0.2e6}])

;; sort the languages by conciseness
;; i.e. highest rank with lowest lines of code
(def sort-by-conciseness
  (partial sort-by #(* (:loc %) (:rank %))))

(map :name (sort-by-conciseness languages))

;; Functions as return values
;; We've already seen some provided by clojure
(defn columns
  "returns a function that sorts by the given columns names"
  [column-names]
  (fn [row]
    (vec (map row column-names))))

(sort-by (columns [:rank :loc]) languages)

;; --------------------------------------------
;; Closures and functions
;; --------------------------------------------
;; using local binding with let
(def times-n [n]
  (let [x n]
    (fn [y] (* y x))))
;; but you cal also close over the function arguments
(defn divisor? [denom]
  (fn [number]
    (zero? (rem number denom))))
(def d7 (divisor? 7))
;; numbers between 1 and 64 divisible by 7:
(filter d7 (range 1 64))


;; --------------------------------------------
;; Recursive functions
;; --------------------------------------------



