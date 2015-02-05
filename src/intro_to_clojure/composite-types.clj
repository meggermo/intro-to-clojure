(ns intro-to-clojure.skalar-types)

;; --------------------------------------------
;; Composite data types
;; --------------------------------------------
(type (vector 1 2))
(type (list 1 2))
(type (hash-map 1 2 3 4))
(type (hash-set 1 2 3 4))
;; preserves insertion order
(type (array-map 4 3 2 1))

;; --------------------------------------------
;; Syntactic sugar for collections
;; --------------------------------------------

;; A Vector
 [1 2]
;; A list
'(1 2)
;; A hash map
 {1 2 2 3}
;; A hash map
#{1 2}

;; Persistent data structures
(def x [:a :b :c])
(def y (replace {:a :z} x))
y
x

;; --------------------------------------------
;; The sequence abstraction
;; --------------------------------------------

;; The seq function returns a sequence view of a collection
(def s (seq [1 2 3]))

;; A sequence has a head and a (possibly empty) tail
(first s)
(rest s)
(rest (rest (rest s)))
(next (rest (rest s)))

;;
(defn nest-recursive
  "turns a flat seq into a nested vector"
  [[x & xs]]
  (if x
    [x (nest-recursive xs)]
    []))

;; Looks fine
(nest-recursive (range 3))

;; But alas, the limitations of recursive functions
(nest-recursive (range 2000000))


;; We can fix this by using the lazy-seq macro:
(defn nest-lazy
  [xs]
  (lazy-seq
   (if (seq xs)
     [(first xs) (nest-lazy (rest xs))]
     [])))

;; No more stack overflow
(dorun (nest-lazy (range 2000000)))

;; lazy sequences are useful to produce infinite streams

(defn triangle-fn [n] (/ (* n (+ n 1)) 2))

(def naturals (iterate inc 1))
(def triangles (map triangle-fn naturals))

(take 9 triangles)
(take 9 (filter even? triangles))
(nth  triangles 99)









