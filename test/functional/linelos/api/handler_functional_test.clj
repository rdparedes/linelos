(ns linelos.api.handler-functional-test
  (:require [midje.sweet :refer :all]
            [cheshire.core :refer [parse-string]]
            [helpers.server :as server]
            [clj-http.client :as client]))

(def ^:private dev-server-port 3333)

(against-background
 [(before :contents (server/start-server dev-server-port))
  (after :contents (server/stop-server))]
 (facts
  "about transactions" :functional
  (fact "it returns a list of transactions given a Gmail search query"
        (let [api-url           (str "http://localhost:" dev-server-port "/transactions")
              search-query      "from:(notificaciones@infopacificard.com.ec) pacificard: consumos"
              query-params      {"query" search-query}
              response          (client/get api-url {:accept :json :query-params query-params})
              first-transaction (first (:transacciones (parse-string (response :body) true)))]
          (response :status) => 200
          (first-transaction :tienda) => (contains #"[\w\-\s]+")
          (first-transaction :monto) => float?
          (first-transaction :fecha) => (contains #"[\w\-\s]+")))))
