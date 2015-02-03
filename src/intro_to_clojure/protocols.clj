(ns intro-to-clojure.protocols)

(defrecord Node [v left right])

(defn add
  [n v]
  (cond
   (nil? n) (Node. v nil nil)
   (< v (:v n)) (Node. (:v n) (add (:left n) v) (:right n))
   :else        (Node. (:v n) (:left n) (add (:right n) v))))

;; Function for pretty printing a tree structure
(defn node-seq
  [n]
  (when n
    (concat (node-seq (:left n))
            [(:v n)]
            (node-seq (:right n)))))

(def sample-tree
  (reduce add nil [3 5 2 4 6 1]))

(node-seq sample-tree)

(defprotocol FiXo
  (f-push [x v])
  (f-pop  [x])
  (f-peek [x]))

(extend-type Node
  FiXo
  (f-push [node value]
          (add node value)))

(node-seq (f-push sample-tree 3/2))

;; The real power of Clojure protocols is that you can
;; add them to existing interfaces and classes!
(extend-type clojure.lang.IPersistentVector
  FiXo
  (f-push [v value]
          (conj v value)))
;; Now f-push is even defined for all classes/interfaces
;; that inherit from IPersisitentVector

(f-push [1 2] 0)


;; This also works for java interfaces and classes:
(defprotocol StringOps
  (palindrome [s]))

(extend-type String
  StringOps
  (palindrome [s] (str s (clojure.string/reverse s))))

(palindrome "partybo")


