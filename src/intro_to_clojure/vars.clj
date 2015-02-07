(ns intro-to-clojure.vars)

;; Vars are created with the def special form.
;; It creates a Var and interns it into the current namespace
;; No initial value is given so the root is not bound
(def foo)
foo

;;Supplying an initial value binds it's root to that value
(def foo 1)
foo

;; By default Vars are static and therefore all threads see
;; the same root binding.
(.start (Thread. (fn[] (prn foo))))
(.start (Thread. (fn[] (prn foo))))
;; Static vars can't override the root binding
(binding [foo 2]
  (+ foo foo))

;; For that, vars must be tagged dynamic
(def ^:dynamic foo 1)

;; so that you can override the root binding
(binding [foo 2]
  (.start (Thread. (fn[] (binding [foo 4] (prn (+ foo foo))))))
  (+ foo foo))
(+ foo foo)

;; The var special form or the #' reader macro can be used to get
;; an interned Var object instead of its current value.

(defn my-fn []
  (Thread/sleep 1000)
  (println "Hi"))

;; Start off calling the function in another thread
(def my-loop
  (future (doall (repeatedly my-fn))))

(defn my-fn []
  (Thread/sleep 1000)
  (println "Hello!"))

(future-cancel my-loop)

;; By using the var reader macro makes it possible to
;; change the implementation on the fly and see the result immediately.
;; This works for functions, because the var returned by the var reader
;; macro implements IFn and when called, delegates to the function it
;; refers to.
(def my-loop
  (future (doall (repeatedly #'my-fn))))

(future-cancel my-loop)


