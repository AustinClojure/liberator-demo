(ns liberator-demo.routes.home
  (:use compojure.core)
  (:require [liberator-demo.views.layout :as layout]
            [liberator-demo.util :as util]
            [liberator.core :refer [resource defresource]]
            [liberator-demo.models.db :as db]))


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
         :available-media-types ["text/html"]
         :handle-ok (fn [ctx]
            ;(debug (format (str "\n\n\thandle-ok got %s") (:request ctx)))
            ;(format  (str "<html>Post text/plain to this resource.<br>\n"
            ;              "There are %d posts at the moment. You posted %s") 4 (:request ctx)))
                      (db/get-users))
         :post! (fn [ctx]
                  ;(debug (str ctx))
                  (let [body (get-in ctx [:request :body-params])]
                     (debug (format (str "\n\n\tpost! got a body of %s") body))
                     ;; *** Put database insert here ***
                      (db/create-user body)
                     ctx))
         ;; actually http requires absolute urls for redirect but let's
         ;; keep things simple.
         :post-redirect? (fn [ctx]
                           ; (debug (format (str "\n\n\tpost-redirct called with %s") ctx))
                           ;;; :context comes from the Immutant app server and gives us our
                           ;;; top-level context-path for our web app.
                           {:location (format (str "%s/users/%s") (get-in ctx [:request :context]) (get-in ctx [:request :body-params :id]))})))
  (ANY "/users/:x" [x]
       (resource
         :allowed-methods [:get]
         :available-media-types ["text/html"]
         :exists? (fn [ctx]
                    ;(debug (format (str "\n\n\tIn redirect postbox/:x called with %s") x))
                    (if-let [d (db/get-user x)] {::id d}))
         :handle-ok ::id))

  (ANY "/locations" [location-json]
       (resource
         :allowed-methods [:post :get]
         :available-media-types ["text/html"]
         :handle-ok (fn [ctx]
                      ;(debug (format (str "\n\n\thandle-ok got %s") (:request ctx)))
                      ;(format  (str "<html>Post text/plain to this resource.<br>\n"
                      ;              "There are %d posts at the moment. You posted %s") 4 (:request ctx)))
                      (db/get-locations))
         :post! (fn [ctx]
                  ;(debug (str ctx))
                  (let [body (get-in ctx [:request :body-params])]
                    (debug (format (str "\n\n\tpost! got a body of %s") body))
                    ;; *** Put database insert here ***
                    (db/create-location body)
                    ctx))
         ;; actually http requires absolute urls for redirect but let's
         ;; keep things simple.
         :post-redirect? (fn [ctx]
                           ; (debug (format (str "\n\n\tpost-redirct called with %s") ctx))
                           ;;; :context comes from the Immutant app server and gives us our
                           ;;; top-level context-path for our web app.
                           {:location (format (str "%s/locations/%s") (get-in ctx [:request :context]) (get-in ctx [:request :body-params :my_phone_number]))})))
  (ANY "/locations/:x" [x]
       (resource
         :allowed-methods [:get]
         :available-media-types ["text/html"]
         :exists? (fn [ctx]
                    ;(debug (format (str "\n\n\tIn redirect postbox/:x called with %s") x))
                    (if-let [d (db/get-location x)] {:my_phone_number d}))
         :handle-ok ::id))


  )

