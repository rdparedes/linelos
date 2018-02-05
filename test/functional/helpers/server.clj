(ns helpers.server
  (:require [linelos.api.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]))

(def server (atom nil))

(defn start-server
  "Starting ring server"
  [port]
  (swap! server
         (fn [_] (run-jetty app {:port port :join? false}))))

(defn stop-server
  []
  (.stop @server))
