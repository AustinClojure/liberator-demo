(ns liberator-demo.models.schema
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as sql]
            [noir.io :as io]))

(def db-store "demo")

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2"
              :subname db-store
              :user "sa"
              :password ""
              :make-pool? true
              :naming {:keys   string/lower-case
                       :fields string/upper-case}})

(defn initialized?
  "checks to see if the database schema is present"
  []
  (.exists (new java.io.File (str (io/resource-path) db-store ".h2.db"))))

(defn create-users-table []
  (sql/with-connection db-spec
    (sql/create-table :users
                      [:login  "varchar(32) PRIMARY KEY"]
                      [:email  "varchar(128)"]
                      [:active "boolean not null"]
                      [:pass   "varchar(128) not null"]
                      [:apikey "char(36) unique"])))

(defn create-games-table []
  (sql/with-connection db-spec
    (sql/create-table :games
                      [:id "identity not null primary key"]
                      [:name "varchar(128) not null"]
                      [:description "varchar(1024)"]
                      [:url "varchar(256)"]
                      [:owner "varchar(20) not null"]
                      ["foreign key (owner) references users(login)"])))

(defn create-scores-table []
  (sql/with-connection db-spec
    (sql/create-table :scores
                      [:score "bigint not null"]
                      [:initials "varchar(3) not null"]
                      [:game "bigint not null"]
                      ["foreign key (game) references games(id)"])))

(defn drop-tables []
  (sql/with-connection db-spec
    (sql/do-commands
     "drop table if exists scores"
     "drop table if exists games"
     "drop table if exists users")))

(defn create-tables []
  (create-users-table)
  (create-games-table)
  (create-scores-table))


