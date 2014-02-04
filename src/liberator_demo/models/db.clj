(ns liberator-demo.models.db
  (:use [korma.core]
        [korma.db :only (defdb)])
  (:require [liberator-demo.models.schema :as schema]))

(defdb db schema/db-spec)

(defentity users)

(defn create-user [user]
  (insert users (values user)))

(defn update-user [id first-name last-name email]
  (update users
          (set-fields {:first_name first-name
                       :last_name last-name
                       :email email})
          (where {:id id})))

(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))

(defn get-users []
  (select users))

(defn user-by-apikey [apikey]
  (when (not (empty? apikey))
    (first (select users (where (and (= :active true)
                                     (= :apikey apikey)))))))

(defn new-apikey []
  (str (java.util.UUID/randomUUID)))

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
                :apikey (new-apikey)}))
