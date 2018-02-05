(ns linelos.gmail.message.utils
  (:import (com.google.api.client.repackaged.org.apache.commons.codec.binary StringUtils)))

(defn decode
  "Decodes a gmail.model.Message"
  [message]
  (-> message .decodeRaw StringUtils/newStringUtf8))
