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

(defn create-users-table
  []
  (sql/with-connection db-spec
    (sql/create-table :users
                      [:id "varchar(20) PRIMARY KEY"]
                      [:first_name "varchar(30)"]
                      [:last_name "varchar(30)"]
                      [:email "varchar(30)"] [:admin :boolean]
                      [:last_login :time] [:is_active :boolean]
                      [:pass "varchar(100)"])))

(defn create-tables
  "creates the database tables used by the application"
  []
  (create-users-table))
