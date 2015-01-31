(ns intro-to-clojure.project-euler)

(defn divides?
  ([x d]
   (= 0 (rem x d)))
  ([x d & ds]
   (every? identity (map #(divisor-of? x %) (conj ds d)))))

(divides? 12 1 2 3 4 5)

