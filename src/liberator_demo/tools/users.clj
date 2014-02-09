(ns liberator-demo.tools.users
  (:require [liberator-demo.models.db :as db]))

(defn -main []
  (doseq [user (db/get-users)]
    (println (:login user) (:apikey user))))


