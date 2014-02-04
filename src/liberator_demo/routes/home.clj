(ns liberator-demo.routes.home
  (:use [compojure.core])
  (:require [liberator-demo.views.layout :as layout]
            [liberator-demo.util :as util]
            [liberator.core :refer [resource defresource]]
            [liberator-demo.models.db :as db]
            [taoensso.timbre :refer [debug]]))

(defn home-page []
  (layout/render "home.html"))

(defn about-page []
  (layout/render "about.html"))

(defn auth-apikey [ctx]
  (when-let [apikey (get-in ctx [:request :params :apikey])]
    (when-let [user (db/user-by-apikey apikey)]
      (assoc ctx :user user))))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))

  (ANY "/test" []
       (resource
        :allowed-methods [:get]
        :available-media-types ["application/edn" "application/json"]
        :authorized? auth-apikey
        :handle-ok (fn [ctx]
                     {:u (get-in ctx [:user :login])})))

  (ANY "/users" [usr-json]
       (resource
        :allowed-methods [:post :get]
        :available-media-types ["text/html" "application/json" "application/edn"]
        :authorized? auth-apikey
        :handle-ok (fn [ctx]
                     (db/get-users))
        :post! (fn [ctx]
                 (let [body (get-in ctx [:request :body-params])]
                   ;; NOT SAFE!
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
                 :handle-ok ::id)))

