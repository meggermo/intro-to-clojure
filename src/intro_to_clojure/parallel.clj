(ns intro-to-clojure.parallel
  (:require [clojure [xml :as xml]]
            [clojure [zip :as zip]]))

;; --------------------------------
;; Futures
;; --------------------------------

;; split a sequential operation into discrete parts

;; Futures represent expressions that yet have to be
;; computed
(time
 (let [x (future (do (Thread/sleep 5000) (+ 41 1)))]
  [@x @x]))


(def x (promise))
(def y (promise))
(def z (promise))

(def task-z (future (deliver z (+ @x @y))))
(def task-x (future (deliver x 10)))
(def task-y (future (deliver y 5)))

(send-off (agent nil) (print @z))
(send-off (agent nil) #(@task-z))
(send-off (agent nil) #(@task-x))
(send-off (agent nil) #(@task-y))
