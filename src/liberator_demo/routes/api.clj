(ns liberator-demo.routes.api
  (:use [compojure.core])
  (:require [liberator-demo.views.layout :as layout]
            [liberator-demo.util :as util]
            [liberator.core :refer [resource defresource handle-unauthorized]]
            [liberator-demo.models.db :as db]
            [taoensso.timbre :refer [debug]]))


(def api-media-types ["text/html" "application/json" "application/edn"])

(defn auth-apikey [ctx]
  (when-let [apikey (get-in ctx [:request :params :apikey])]
    (when-let [user (db/user-by-apikey apikey)]
      (assoc ctx :user user))))

(defn game-exists [ctx]
  (let [game-id (get-in ctx [:request :params :game])]
    (when-let [game (db/game-by-id game-id)]
      (if (= (:owner game) (get-in ctx [:user :login]))
        (assoc ctx :game game)))))

(defn games-for-user [ctx]
  (when-let [user (:user ctx)]
    (db/games-by-owner (:login user))))

(defn scores-for-game [ctx]
  (when-let [game (:game ctx)]
    (db/scores-by-game (:id game))))

(defn parse-score [text]
  (when text
    (try
      (Long/parseLong text)
      (catch NumberFormatException _ nil))))

(defn post-score [ctx]
  (let [score (get-in ctx [:request :params :score])
        name  (get-in ctx [:request :params :name])
        game  (:game ctx)]
        (when (and score name game)
          (do
            (db/add-score (:id game) score name)
            ctx))))

(def api-routes
  (context "/api" []
   (ANY "/test" []
        (resource
         :allowed-methods [:get]
         :available-media-types ["application/edn" "application/json"]
         :authorized? auth-apikey
         :handle-ok (fn [ctx]
                      {:u (get-in ctx [:user :login])})))


   ;; ----------------------------------------
   (ANY "/games" []
        (resource
         :allowed-methods [:get]
         :available-media-types api-media-types
         :authorized? auth-apikey
         :handle-ok games-for-user))

   (ANY "/game/:game" []
        (resource
         :allowed-methods [:get]
         :available-media-types api-media-types
         :authorized? auth-apikey
         :exists? game-exists
         :handle-ok :game))

   (ANY "/game/:game/scores" []
        (resource
         :allowed-methods [:post :get]
         :available-media-types api-media-types
         :authorized? auth-apikey
         :exists? game-exists
         :post! post-score
         :handle-ok scores-for-game
         :handle-created scores-for-game))


   ;; ----------------------------------------
   ;; USERS - just test code here
   (ANY "/users" [usr-json]
        (resource
         :allowed-methods [:post :get]
         :available-media-types ["text/html" "application/json" "application/edn"]
         :authorized? auth-apikey
         :handle-ok (fn [ctx]
                      (db/get-users))
         :post! (fn [ctx]
                  (let [body (get-in ctx [:request :body-params])]
                    ;; VERY NOT SAFE!!!!
                    (db/create-user body)
                    ctx))

         :post-redirect? (fn [ctx]
                           {:location (format (str "%s/users/%s")
                                              (get-in ctx [:request :context])
                                              (get-in ctx [:request :body-params :id]))})))

   (ANY "/users/:x" [x]
        (resource :authorized? auth-apikey
                  :allowed-methods [:get]
                  :available-media-types ["text/html" "application/json" "application/edn"]
                  :exists? (fn [ctx]
                             (if-let [d (db/get-user x)] {::id d}))
                  :handle-ok ::id))))
