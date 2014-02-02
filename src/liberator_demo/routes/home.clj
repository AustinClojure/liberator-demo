(ns liberator-demo.routes.home
  (:use [compojure.core])
  (:require [liberator-demo.views.layout :as layout]
            [liberator-demo.util :as util]
            [liberator.core :refer [resource defresource]]
            [liberator-demo.models.db :as db]
            [taoensso.timbre :refer [debug]]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

;(defresource parameter [txt]
;             :available-media-types ["text/plain"]
;             :handle-ok (fn [_]
;                          (format "The text is %s" (db/get-user txt))))

;(defresource parameter [txt]
;             :available-media-types ["application/json"]
;             :handle-ok (fn [_]
;                          (format "User data is: %s" (json/write-str (db/get-user txt)))))


(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))

  ;; REST Resources
  ; (ANY "/users/:txt" [txt] (parameter txt))

  (ANY "/users" [usr-json]
       (resource
         :allowed-methods [:post :get]
         :available-media-types ["text/html" "application/json" "application/edn"]
         :handle-ok (fn [ctx]
                      (db/get-users))
         :post! (fn [ctx]
                  (let [body (get-in ctx [:request :body-params])]
                    (debug (format (str "\n\n\tpost! got a body of %s") body))
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

