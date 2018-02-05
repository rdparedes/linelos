(ns linelos.gmail.message.pacificard-test
  (:require [midje.sweet :refer :all]
            [linelos.gmail.message.pacificard
             :refer
             [get-vendor get-amount get-date]])
  (:import (java.util Date)
           (java.text SimpleDateFormat)))

(def vendor "Algún Establecimiento")
(def transaction-amount 99.99)
(def date-format "dd/MM/yyyy 'a las' hh:mm")
(def date-formatter (SimpleDateFormat. date-format))
(def transaction-date (.parse date-formatter "20/08/2017 a las 04:14"))
(def email-body
  (str "<strong>Establecimiento:</strong>= " vendor "<br />=0A <b>"
       "Fecha= de la transacci&oacute;n</b> "
       (.format date-formatter transaction-date)
       "<br />=0A = <b>Monto</b> $ " transaction-amount ".<br />=0A"))

(facts
 "about get-vendor"
 (fact "it returns a vendor name given a valid email snippet"
       (let [email-snippet (str "blah. Establecimiento: " vendor)]
         (get-vendor email-snippet) => vendor))
 (fact "it returns nil if no vendor name could be retrieved from snippet"
       (get-vendor "invalid string") => nil))

(facts
 "about get-amount"
 (fact "it returns the transaction amount given a valid email body"
       (get-amount email-body) => transaction-amount)
 (fact "it returns nil if no amount could be retrieved from email body"
       (get-amount "invalid string") => nil))

(facts
 "about get-date"
 (fact "it returns a transaction date given a valid email body"
       (get-date email-body) => transaction-date)
 (fact "it returns nil if no transaction-date could be retrieved from email body"
       (get-date "invalid string") => nil))
