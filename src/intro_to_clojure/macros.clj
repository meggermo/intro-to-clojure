(ns intro-to-clojure.macros
  (:require [clojure.walk :as walk]))

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
  [& clauses]
  (when clauses
    (list `when (first clauses)
          (if (next clauses)
            (second clauses)
            (throw (IllegalArgumentException. "do-until requres an even number of forms")))
          (cons 'do-until (nnext clauses)))))

(macroexpand-1 '(do-until true (prn 1) false (prn 2)))
