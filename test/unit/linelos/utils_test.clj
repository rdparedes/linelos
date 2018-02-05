(ns linelos.utils-test
  (:require [midje.sweet :refer :all]
            [linelos.utils :as utils])
  (:import (com.google.api.services.gmail.model Message)))

(facts
 "about trim"
 (fact "it removes all excessive whitespace and tabs from a string"
       (let [unformatted-string "lorem ipsum        dolor sit     amet"
             expected-string    "lorem ipsum dolor sit amet"]
         (utils/trim unformatted-string) => expected-string)))
