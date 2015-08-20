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

;; syntax quote: prevents evaluation
;; and expands symbols to fully qualified names
(println `(+ 1 2))

;; unquote does the opposite of quote
(println `(+ 1 2 ~(+ 1 2)))

;; unquote splice
;; The @ will tell Clojure to unpack the sequence x
;; and splice it into the resulting list
(let [x '(2 3)] (println `(1 ~@x)))
;; without the splice you'd end up with a nested sequence
(let [x '(2 3)] (println `(1 ~x)))


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


;; Another example safely handling IO:
(defmacro with-resource
  [[resource & _ :as bindings] close-fn & body]
  `(let ~bindings
     (try
       (do ~@body)
       (finally
        (~close-fn ~resource)))))

(defn url-stream [url]
  (-> url URL. .openStream InputStreamReader. BufferedReader.))

(let [stream (url-stream "http://www.joyofclojure.com/hello")]
  (with-resource
    [web-page stream, blip 123345] ;; binding
    #(.close %)                ;;close-fn
    (print blip)               ;; body
    (.readLine web-page)))


(defmacro def-watched
  [n & v]
  `(do
     (def ~n ~@v)
     (add-watch #'~n
                :re-bind
                (fn [~'k ~'r old-val# new-val#] (prn old-val# "->" new-val#)))))

(def-watched y 1)
(def y 3)

