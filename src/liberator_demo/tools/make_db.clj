(ns liberator-demo.tools.make-db
  (:require [liberator-demo.models.db :as db]))

(defn -main []
  (println "Wiping database and creating dev data")
  (db/hard-reset!)
  (println "Done..."))


