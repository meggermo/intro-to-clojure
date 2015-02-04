(ns intro-to-clojure.concurrency
  (:import [java.util.concurrent Executors]))

;; Refs, Agents and Atoms
;; ----------------------
;; Semantics for accessing their value is the same for all
;; but for modification they have their own.

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

(defn board-map
  "applies a function to each square of the board"
  [f b]
  (vec (map #(vec (for [x %] (f x))) b)))

(defn reset-game!
  " Here is the function that creates the initial
    state variables (the refs) for the chess game"
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


;; ---------------------------------------
;; Agents: asynchronous
;; ---------------------------------------
;; send and send-off

;; Example use case: serialize access to resources

(def log-agent (agent 0))

(defn do-log
  [msg-id message]
  (println msg-id ":" message)
  (inc msg-id))

(defn do-step
  "Try to look busy"
  [channel message & {:keys [delayMillis] :or {delayMillis 1}}]
  (Thread/sleep delayMillis)
  (send-off log-agent do-log (str channel message)))

(defn three-step
  [channel]
  (do-step channel " Initializing (step 0)")
  (do-step channel " Starting up  (step 1)")
  (do-step channel " Really busy  (step 2)")
  (do-step channel " Done         (step 3)"))

(defn go-to-work []
  (do-threads! #(three-step "alpha"))
  (do-threads! #(three-step "beta "))
  (do-threads! #(three-step "gamma")))

(go-to-work)
;; The alpha, beta and gamma logs are out of order
;; but the msg-id increments linearly as expected.

@log-agent

(defn important-msg
  "If you want to be sure that the agent has completed
   use await. It will block the current thread until
   work is completed."
  []
  (do-step "PRIO-1" " This must be executed" :delayMillis 10000)
  (await log-agent)
  @log-agent)

(important-msg)


;; Error handling
;; There are 2 error handling modes :fail and :continue
;; Agents use :fail by default`

;; Suppose you're trying to (re)set it's value
(send log-agent (fn [] 100))
;; It looks OK, but ...
@log-agent
;; It's in fail state
(agent-error log-agent)
;; Trying to reset it with a correct function will also fail
(send log-agent (fn [_] 100))
;; You'll need to restart it
(restart-agent log-agent 0 :clear-actions true)

;; Other state is :continue
(defn handle-error
  [an-agent error-message]
  (println "ERROR: " error-message))

;; Configure the agent to delegate error handling
(set-error-handler! log-agent handle-error)
(set-error-mode! log-agent :continue)
(send log-agent (fn [x] (/ x 0)))
(send log-agent (fn [] 1))



;; ---------------------------------------
;; Atoms: synchronous, but uncoordinated
;; ---------------------------------------


;; For Compare-And-Swap spinning operations:
;; Atomic computation of a new value based on current value
;; and swap in this new value


(def *ticker* (atom 0))
(defn tick [] (swap! *ticker* inc))
(do-threads! tick :thread-count 100 :exec-count 100)
@*ticker*





