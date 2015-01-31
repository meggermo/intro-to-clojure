(ns intro-to-clojure.core)

;; Clojure's numeric types

;; Discrete types
(type 1)
(type (int 1))
(type (short 1))
(type (byte 1))
(type (biginteger 1))
(type (bigint 1))
(identical? (bigint 1e99) (biginteger 1e99))

;; Floating point types
(type 1.0)
(type (float 1.0))
(type 1M)

;; Rational types
(type 1/2)
(type (+ 1/3 1/3 1/3))


;; Clojure's chars and strings
(type \c)
(type "What you'd expect")
;; Clojure also has an m like type
(type :x)
;; Symbols are a very essential part of Clojure
(type 'x)

;; Collection types
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
