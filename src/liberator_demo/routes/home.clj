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



(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))

  (ANY "/users" [usr-json]
       (resource
         :allowed-methods [:post :get]
         :available-media-types ["text/html" "application/json" "application/edn"]
         :handle-ok (fn [ctx]
                      (db/get-users))
         :post! (fn [ctx]
                  (let [body (get-in ctx [:request :body-params])]
                    ;; NOT SAFE!
                    (db/create-user body)
                    ctx))

         ;; actually http requires absolute urls for redirect but let's
         ;; keep things simple.
         :post-redirect? (fn [ctx]
                           {:location (format (str "%s/users/%s")
                                              (get-in ctx [:request :context])
                                              (get-in ctx [:request :body-params :id]))})))

  (ANY "/users/:x" [x]
       (resource
         :allowed-methods [:get]
         :available-media-types ["text/html" "application/json" "application/edn"]
         :exists? (fn [ctx]
                    (if-let [d (db/get-user x)] {::id d}))
         :handle-ok ::id)))

