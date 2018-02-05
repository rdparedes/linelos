(ns linelos.gmail.service
  (:require [linelos.gmail.core :refer [execute-list-query execute-get-query]]
            [linelos.utils :refer [trim]]
            [linelos.gmail.message.utils :refer [decode]]
            [linelos.gmail.message.pacificard :as message-parser]))

(def ^:private get-message-id (comp :id bean))

(defn search
  "Returns a list of message ids"
  [conn query]
  (loop [gmail-response (execute-list-query conn query)
         accumulator    []]
    (let [current-page-results (map get-message-id (.getMessages gmail-response))
          messages             (concat accumulator current-page-results)
          next-page-token      (.getNextPageToken gmail-response)]
      (if (nil? next-page-token)
        messages
        (recur (execute-list-query conn query next-page-token)
          messages)))))

(defn get-message
  "Returns a message with detailed information"
  [conn mail-id]
  (let [gmail-message (execute-get-query conn mail-id)
        message-body  (-> gmail-message (decode) (trim))
        vendor        (message-parser/get-vendor (.getSnippet gmail-message))
        amount        (message-parser/get-amount message-body)
        date          (message-parser/get-date message-body)]
    {:tienda vendor
     :monto  amount
     :fecha  date}))
