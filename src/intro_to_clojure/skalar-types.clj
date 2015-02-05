(ns intro-to-clojure.skalar-types)

;; --------------------------------------------
;; Integers
;; An iteger is a sequence of digits
;; optionally starting with a sign
;; Clojure will infer the type for you
;; --------------------------------------------

(type 1)
(type 912731278913889141849147393814819)
;; You're not stuck to base-10
;; There's the usual hex notation
(= 0xFF 255)
;; Radix notation up to base 36 is supported
(= 2r11 3)
(= 3r12 5)

;; casting functions are available, but rarely needed
(type (int 1))
(type (short 1))
(type (byte 1))
(type (biginteger 1))
(type (bigint 1))
(identical? (bigint 1e99) (biginteger 1e99))
;; Clojure has it's own bigint with better equals/hashCode

;; Clojure detects overflow
(+ Long/MAX_VALUE 1)

;; --------------------------------------------
;; Floating point numbers
;; --------------------------------------------
(type 2.123456789123456789123456789)
;; Floating literals are truncated by default
;; For high precision numbers use the M literal
(type 2.123456789123456789123456789M)

;; --------------------------------------------
;; chars and strings
;; --------------------------------------------
(type \c)
(type "What you'd expect")

;; Until now, there isn't much different from Java
;; But now for some Clojure specific  types

;; --------------------------------------------
;; Rational numbers
;; --------------------------------------------
(type 1/2)
(type (+ 1/3 1/3 1/3))

;; --------------------------------------------
;; Keywords
;; --------------------------------------------
(type :keyword)
;; Keywords always refer to themselves
;; evaluating a keyword always returns itself:
(identical? :x :x)

;; As keys in maps
(def population
  (hash-map :zombies 1000,
            :humans  1))
;; keywords are functions
(ifn? :zombies)
;; so you can use them to lookup values
(:zombies population)
;; The other way around works too, because
;; hash maps are functions too
(population :humans)

;; As enumerations
(def size
  (hash-set :small :medium :large))

;; --------------------------------------------
;; Symbols are used to refer to things by a name.
;; They are roughly analogous to identifiers in Java
;; --------------------------------------------
(type (quote x))
(name (quote x))
;; Clojure symbols are however more than only identifiers
(= 'hippo 'hippo)
(identical? 'hippo 'hippo)
;; So why should they be considerend different?
;; Because you can attach metadata to symbols
(let [x (with-meta 'hippo {:gender :male})
      y (with-meta 'hippo {:gender :female})]
  [(= x y)
   (identical? x y)
   (meta x)
   (meta y)])
