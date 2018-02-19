(ns linelos.api.handler-test
  (:require [midje.sweet :refer :all]
            [linelos.api.handler :refer [app]]
            [cheshire.core :refer [parse-string]]
            [linelos.gmail.service :refer [get-message search]]
            [linelos.gmail.core :refer [get-authorization-url]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.util.response :refer [redirect]]
            [ring.mock.request :as mock]
            [peridot.core :as peridot]
            [linelos.gmail.core :refer [get-credentials get-connection]]))

(def ^:private fake-message-id "message-1")
(def ^:private fake-params "params")
(def ^:private fake-oauth-code "some-code")
(def ^:private fake-token "a super token")
(def ^:private fake-credentials {"access_token" fake-token})
(def ^:private fake-connection "some fake connection stuff")
(def ^:private fake-message
  {:fecha  "a date"
   :monto  2.5
   :tienda "some vendor"})

(facts
 "about get-transactions"
 (facts "when user is authenticated"
        (against-background
         [(get-credentials fake-oauth-code) => fake-credentials
          (get-connection fake-token) => fake-connection
          (search fake-connection fake-params) => [fake-message-id]
          (get-message fake-connection fake-message-id) => fake-message])
        (fact "it returns 200 with a list of transactions when gmail search is successful"
              (let [response      (-> (peridot/session app)
                                      (peridot/request (str "/oauth2callback?code=" fake-oauth-code))
                                      (peridot/request (str "/transactions?query=" fake-params))
                                      (get :response))
                    response-body (parse-string (response :body) true)]
                (response :status) => 200
                (response-body :transacciones) => [fake-message]))

        (fact "it returns 200 with empty body when no params are sent in the request"
              (let [response      (-> (peridot/session app)
                                      (peridot/request (str "/oauth2callback?code=" fake-oauth-code))
                                      (peridot/request "/transactions")
                                      (get :response))
                    response-body (parse-string (response :body) true)]
                (response :status) => 200
                response-body => {})))
 (facts "when user is not authenticated"
        (fact "it responds with a redirect url to gmail authorization page when credentials are not stored in session"
              (against-background (get-authorization-url) => "some-auth-url")
              (let [response      (app (mock/request :get "/transactions"))
                    response-body (parse-string (response :body) true)]
                (response :status) => 403
                (response-body :location) => (contains "some-auth-url")))))
