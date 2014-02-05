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
  (GET "/about" [] (about-page)))

