(ns intro-to-clojure.macros
  (:require [clojure.walk :as walk])
  (:import [java.io BufferedReader InputStreamReader]
           [java.net URL]))

;; Rules of macro-club
;; * Don't write a macro if a function will do
;; * write examples
;; * expand example by hand
;; * make use of the macroexpand functions
;; * break up in smaller functions if possible

(eval 42)
(eval '(list 1 2))
(eval (list (symbol "+") 1 2))

(defmacro do-until
  "Continues evaluating pred, expression pairs until
   the first pred returns a falsey value"
  [& clauses]
  (when clauses
    (list `when (first clauses)
        (if (next clauses)
          (second clauses)
          (throw (IllegalArgumentException. "do-until requres an even number of forms")))
        (cons 'do-until (nnext clauses)))))

(defn example []
  (do-until
   true  (prn "Yep!")
   []    (prn "Still true!")
   :x    (prn "And again true!")
   false (prn "Nope, won't print")
   true  (prn "Anything after the first falsey value won't be printed")))
(example)
(macroexpand
 '(do-until true 1 false 2))
(walk/macroexpand-all
 '(do-until true 1 true 2 false 3 false 4))


(defmacro with-resource
  [binding close-fn & body]
  `(let ~binding
     (try
       (do ~@body)
       (finally
        (~close-fn ~(binding 0))))))

(defn url-stream [url]
  (-> url URL. .openStream InputStreamReader. BufferedReader.))


(let [stream (url-stream "http://www.joyofclojure.com/")]
  (with-resource
    [page stream]
    #(.close %)
    (.readLine page)))
