(ns intro-to-clojure.multimethods)

;; Polymorphism with multimethods

;; It will dispatch on the keyword :os
(defmulti home :os)
(defmethod home ::unix
  [m] (:home m))

(defmulti compiler :os)
(defmethod compiler ::unix
  [m] (:compiler "cc"))

(def unix-env
  {:os ::unix, :home "/home", :compiler "cc"})
(def os-x-env
  {:os ::os-x, :home "/Users", :compiler "gcc"})

;; Now we can call the multimethod
(home unix-env)

;; but clojure cannot yet dispatch for os-x because it is not defined
(home os-x-env)

;; We can fix it by letting it derive from unix
(derive ::os-x ::unix)

;; and now os-x is dispatched to the same function as unix
(home os-x-env)

;; Suppose we add another type into the mix
(derive ::os-x ::bsd)
(defmethod home ::bsd [m] (:home m))

;; Now there are two methods to choose from, but which one to use?
(home os-x-env)
(parents ::os-x)
(descendants ::unix)
(descendants ::bsd)

;; We can fix it by declaring an ordering preference
(prefer-method home ::unix ::bsd)
(home os-x-env)

;; But you can also dispatch on any other function
(defmulti do-something #(get-in % [:left :v]))

(defmethod do-something 1
  [m] (get-in m [:right :v]))
(defmethod do-something 2
  [m] (get-in m [:left :left :v]))

(def t1
  {:v 0 :left {:v 1} :right {:v "dispactched on left value 1"}})
(def t2
  {:v 0 :left {:v 2 :left {:v "dispatched on left value 2"}}})

(do-something t1)
(do-something t2)

