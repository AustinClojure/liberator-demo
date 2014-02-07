(ns liberator-demo.middleware
  (:require [environ.core :refer [env]]
            [liberator.dev]
            [selmer.parser :as parser]
            [taoensso.timbre :as timbre]))

(defn log-request [handler]
  (if (env :dev)
    (fn [req]
      (timbre/debug req)
      (handler req))
    handler))

(defn template-error-page [handler]
  (if (env :dev)
    (fn [request]
      (try
        (handler request)
        (catch clojure.lang.ExceptionInfo ex
          (let [{:keys [type error-template] :as data} (ex-data ex)]
            (if (= :selmer-validation-error type)
              {:status 500
               :body (parser/render error-template data)}
              (throw ex))))))
    handler))

(def xtrace "X-Liberator-Trace")

(defn my-trace [handler]
  (let [trace-handler (liberator.dev/wrap-trace handler :header)]
    (fn [request]
      (let [this-handler (if (get-in request [:params :trace])
                           trace-handler
                           handler)]
        (when-let [response (this-handler request)]
          (doseq [v (get-in response [:headers xtrace])]
            (println "*" v))
          (update-in response [:headers] dissoc xtrace))))))
