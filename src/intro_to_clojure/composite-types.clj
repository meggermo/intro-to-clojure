(ns intro-to-clojure.skalar-types)

;; Composite data types
(type (vector 1 2))
(type (list 1 2))
(type (hash-map 1 2 3 4))
(type (hash-set 1 2 3 4))
;; preserves insertion order
(type (array-map 4 3 2 1))

;; Syntactic sugar for collections
 [1 2]
'(1 2)
 {1 2 2 3}
#{1 2}
