(ns liberator-demo.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [liberator-demo.routes.home :refer [home-routes]]
            [liberator-demo.middleware :as middleware]
            [liberator-demo.routes.api :refer [api-routes]]
            [liberator-demo.routes.cljsexample :refer [cljs-routes]]
            [noir.util.middleware :refer [app-handler]]
            [selmer.parser :as parser]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init []
  (timbre/set-config! [:appenders :rotor]
                      {:min-level :info
                       :enabled? true
                       :async? false
                       :max-message-per-msecs nil
                       :fn rotor/appender-fn})
  (timbre/set-config! [:shared-appender-config :rotor]
                      {:path "liberator_demo.log"
                       :max-size (* 512 1024)
                       :backlog 10})
  (when (env :dev)
    (parser/cache-off!))

  (timbre/info "liberator-demo started successfully"))

(defn destroy []
  (timbre/info "liberator-demo is shutting down..."))

(def app
  (app-handler [api-routes
                cljs-routes
                home-routes
                app-routes]

               :middleware [#'middleware/my-trace
                            middleware/template-error-page
                            middleware/log-request]
               :access-rules []
               :formats [:json-kw :edn]))

