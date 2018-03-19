(ns linelos.api.handler
  (:require [linelos.settings
             :refer
             [frontend-app-url]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.cors :refer [wrap-cors]]
            [linelos.gmail.service :refer [get-message search]]
            [linelos.gmail.core :as gmail]
            [clojure.string :refer [blank?]]))

(def server-error
  {:status  500
   :headers {"Content-Type" "application/json"}
   :body    {:message "Ha ocurrido un error inesperado"}})

(defn response [body]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    body})

(defn redirect [url]
  {:status  302
   :headers {"Location"     url
             "Content-Type" "application/json"}
   :body    ""})

(defn forbidden [body]
  {:status  403
   :headers {"Content-Type" "application/json"}
   :body    body})

(defn- handle-get-transactions [session query]
  (if-not (session "credentials")
    (forbidden
     {:error    "Necesitas hacer login en Google para ver esto"
      :location (gmail/get-authorization-url)})
    (if (blank? query)
      (response {})
      (let [conn          (gmail/get-connection
                           (get-in session ["credentials" "access_token"]))
            transactions  (map #(get-message conn %) (search conn query))]
        (response {:transacciones transactions})))))

(defn handle-get-oauth2callback [code]
  (let [credentials (gmail/get-credentials code)]
    (assoc (redirect frontend-app-url) :session {"credentials" credentials})))

(defroutes app-routes
  (GET "/transactions" {{query :query} :params, session :session}
    (handle-get-transactions session query))
  (GET "/oauth2callback" [code]
    (handle-get-oauth2callback code))
  (route/not-found "Not Found"))

(defn wrap-unexpected-exception [handler]
  (fn [request]
    (try (handler request)
         (catch Exception e server-error))))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-defaults (assoc-in api-defaults [:session :flash] false))
      (wrap-cors :access-control-allow-origin #".*"
                 :access-control-allow-methods [:get]
                 :access-control-allow-credentials "true")
      (wrap-session
       {:cookie-attrs {:secure true}
        :cookie-name  "session"})
      (wrap-unexpected-exception)))
