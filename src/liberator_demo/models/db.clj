(ns liberator-demo.models.db
  (:use [korma.core]
        [korma.db :only (defdb)])
  (:require [liberator-demo.models.schema :as schema]))

(defdb db schema/db-spec)

(defn create-user [user]
  (insert :users (values user)))

(defn update-user [id first-name last-name email]
  (update :users
          (set-fields {:first_name first-name
                       :last_name last-name
                       :email email})
          (where {:id id})))

(defn get-user [id]
  (first (select :users
                 (where {:id id})
                 (limit 1))))

(defn get-users []
  (select :users))

(defn user-by-login [login]
  (when (not (empty? login))
    (first (select :users (where (and (= :active true)
                                     (= :login login)))))))

(defn user-by-apikey [apikey]
  (when (not (empty? apikey))
    (first (select :users (where (and (= :active true)
                                     (= :apikey apikey)))))))

(defn new-apikey []
  (str (java.util.UUID/randomUUID)))

;; ----------------------------------------

(defn create-game [game]
  (insert :games (values game)))

(defn game-by-id [game-id]
  (first (select :games (where (= :id game-id)))))

(defn games-by-owner [login]
  (select :games (where (= :owner login))))

(defn scores-by-game [game-id]
  (let [size 10]
    (select :scores
            (fields :initials :score)
            (where (= :game game-id))
            (order :score :DESC)
            (limit size))))

(defn add-score [game-id score name]
  (insert :scores (values {:score score
                           :initials name
                           :game game-id})))

;; ----------------------------------------

(defn create-dev-data []
  (create-user {:login "norman"
                :email "orb@nostacktrace.com"
                :active true
                :pass "encryptthis"
                :apikey (new-apikey)})
  (create-user {:login "sam"
                :email "styaypufd@mac.com"
                :active true
                :pass "encryptthistoo"
                :apikey (new-apikey)})
  (create-user {:login "nobody"
                :email "inactive-user@example.com"
                :active false
                :pass "mypasswordisweak"
                :apikey (new-apikey)})
  (create-game {:name "Donkey Kong"
                :description "..."
                :url "http://donkeykong.game"
                :owner "norman"})
  (create-game {:name "Pac Man"
                :description "..."
                :url "http://pacman.game"
                :owner "sam"}))

(defn hard-reset! []
  (schema/drop-tables)
  (schema/create-tables)
  (create-dev-data))
