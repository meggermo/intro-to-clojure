(ns intro-to-clojure.concurrency
  (:import [java.util.concurrent Executors]))

;; ---------------------------------------
;; Utility stuff
;; ---------------------------------------
(def *pool* (Executors/newFixedThreadPool
             (*  (.availableProcessors (Runtime/getRuntime)))))

;; Utility function to execute a function
;; multiple times on multiple threads
(defn do-threads!
  [f & {:keys [thread-count exec-count] :or {thread-count 1 exec-count 1}}]
  (dotimes
    [t thread-count]
    (.submit *pool*
             #(dotimes [_ exec-count] (f)))))

;; Returns a sequence of yx coordinate pairs
;; for a square matrix of size matrix-size
(defn neighbors
  ([matrix-size yx-coord]
   (neighbors [[-1 -1] [-1 0] [-1 1] [0 -1] [0 1] [1 -1] [1 0] [1 1]] matrix-size yx-coord))
  ([deltas matrix-size yx-coord]
   (filter
    (fn [neighbor-yx-coord] (every? #(< -1 % matrix-size) neighbor-yx-coord))
    (map #(map + yx-coord %) deltas))))

;; ---------------------------------------
;; End of utility stuff
;; ---------------------------------------


;; ---------------------------------------
;; Refs
;; ---------------------------------------


;; Initial  3x3 chess-board
(def initial-board
  [[:- :k :-]
   [:- :- :-]
   [:- :K :-]])

;; applies a function to each square of the board
(defn board-map
  [f b]
  (vec (map #(vec (for [x %] (f x))) b)))

;; Here is the function that creates the initial
;; state variables (the refs) for the chess game
(defn reset-game!
  []
  (def board (board-map ref initial-board))
  (def move-order (ref [[:K [2 1]] [:k [0 1]]]))
  (def move-count (ref 0)))

;; function that returns possible moves
;; for a king at a given yx coordinate
(def king-moves
  (partial neighbors 3))

;; A legal move should not kick the opponent from board
(defn legal-move?
  [desired-pos other-king-pos]
  (when
    (not= desired-pos other-king-pos) desired-pos))

;; Returns a vector containing the moving king and
;; a randomly selected legal move for that king.
(defn select-move
  [[[moving-king moving-king-pos]
    [_other-king other-king-pos]]]
  (let [shuffled-moves (shuffle (king-moves moving-king-pos))
        selected-move (some #(legal-move? % other-king-pos) shuffled-moves)]
  [moving-king selected-move]))


(reset-game!)
(take 5 (repeatedly #(select-move @move-order)))

(defn place-piece
  [old-piece new-piece] new-piece)

;; We're going to alter the state by manipulating the refs:
;; * put king in destination position
;; * empty the king's previous position
;; * increment the move counter by one
;; 1st argument is the return value of select-move
;; 2nd argument is the move-order ref
(defn move-king
  [[king-to-move destination] [[_ moving-king-pos] [_ _]]]
  (alter (get-in board destination) place-piece king-to-move)
  (alter (get-in board moving-king-pos) place-piece :-)
  (alter move-count inc))

;; Function to update the move-order ref
(defn update-move-order
  [move]
    (alter move-order #(vector (second %) move)))

;; Now combine both alteration functions
;; The dosync makes sure that the 4 refs are modified
;; in a coordinated fashion
(defn make-move []
  (dosync
   (let [move (select-move @move-order)]
     (move-king move @move-order)
     (update-move-order move))))

;; Let's see what happens
(reset-game!)
(make-move)

;; Now let's have fun
(defn play-chess
  [move-fn times threads]
  (do-threads! move-fn :thread-count threads :exec-count times))

(reset-game!)
(play-chess make-move 10 10)
(board-map #(dosync (deref %)) board)
(dosync @move-count)

;; Make sure you set your transaction boundaries right!
(defn make-bad-move []
  (let [move (select-move @move-order)]
    (dosync (move-king move @move-order))
    (dosync (update-move-order move))))

(reset-game!)
(play-chess make-bad-move 10 10)
(board-map #(dosync (deref %)) board)
(dosync @move-count)

;; The new value of the move-count does not depend on it's
;; in-transation state and also not on any other refs.
;; In those situations you can use commute instead of alter
;; Since we know for sure that the source and destinatinon
;; values are always different we can use commute there too:
(defn move-king
  [[king-to-move destination] [[_ moving-king-pos] [_ _]]]
  (commute (get-in board destination) place-piece king-to-move)
  (commute (get-in board moving-king-pos) place-piece :-)
  (commute move-count inc))

(reset-game!)
(play-chess make-move 10 10)
(board-map #(dosync (deref %)) board)
(dosync @move-count)





