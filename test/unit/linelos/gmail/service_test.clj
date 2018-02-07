(ns linelos.gmail.service-test
  (:require [midje.sweet :refer :all]
            [linelos.gmail.service :refer [search get-message]]
            [linelos.gmail.core :refer [execute-list-query execute-get-query]]
            [linelos.gmail.message.utils :refer [decode]]
            [linelos.gmail.message.pacificard :as message-parser])
  (:import (com.google.api.services.gmail.model ListMessagesResponse Message)))

(def fake-id "fake-message-1")
(def secound-fake-id "fake-message-2")
(def fake-message (.setId (Message.) fake-id))
(def conn "a fake connection")

(facts
 "about search"
 (let [search-query "from:(somebody) title"
       second-fake-message (.setId (Message.) secound-fake-id)
       fake-gmail-response (.setMessages (ListMessagesResponse.) [fake-message])
       next-page-token "a-token"
       second-fake-gmail-response (-> (ListMessagesResponse.)
                                      (.setMessages [second-fake-message])
                                      (.setNextPageToken next-page-token))]
   (fact "it returns a list of message ids from gmail when search query has one page of results"
         (against-background (execute-list-query conn search-query) => fake-gmail-response)
         (search conn search-query) => [fake-id])
   (fact "it returns a list of message ids from gmail when search query has several pages of results"
         (against-background [(execute-list-query conn search-query) => second-fake-gmail-response
                              (execute-list-query conn search-query next-page-token) => fake-gmail-response])
         (search conn search-query) => [secound-fake-id fake-id])
   (fact "it returns an empty list when search query returns no results"
         (against-background (execute-list-query conn search-query) => (-> (ListMessagesResponse.)))
         (search conn search-query) => [])))

(facts
 "about get-message"
 (let [mail-id "a mail id"
       fake-snippet "message snippet"
       fake-body "message body"
       fake-vendor "a vendor"
       fake-amount 2.5
       fake-date "a date"]
   (fact "it returns data from mail given its id"
         (against-background [(execute-get-query conn mail-id) => (.setSnippet fake-message
                                                                               fake-snippet)
                              (decode fake-message) => fake-body
                              (message-parser/get-vendor fake-snippet) => fake-vendor
                              (message-parser/get-amount fake-body) => fake-amount
                              (message-parser/get-date fake-body) => fake-date])
         (let [expected-data {:fecha  fake-date
                              :monto  fake-amount
                              :tienda fake-vendor}]
           (get-message conn mail-id) => expected-data))))
