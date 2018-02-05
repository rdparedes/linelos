(ns linelos.gmail.message.utils-test
  (:require [midje.sweet :refer :all]
            [linelos.gmail.message.utils :as utils])
  (:import (java.util Base64)
           (com.google.api.services.gmail.model Message)))

(facts
 "about decode"
 (fact "it returns a decoded raw field from a Message"
       (let [expected-raw             "lorem ipsum dolor sit amet"
             encoded-raw              (.encodeToString (Base64/getEncoder) (.getBytes expected-raw))
             message-with-encoded-raw (.setRaw (Message.) encoded-raw)]
         (utils/decode message-with-encoded-raw) => expected-raw)))
