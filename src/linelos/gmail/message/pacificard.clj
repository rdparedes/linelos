(ns linelos.gmail.message.pacificard
  (:import (java.util Date)
           (java.text SimpleDateFormat ParseException)))

(def vendor-regex #"Establecimiento:\s*(.*)")
(def amount-regex #"Monto.+\$\s*(\d*\.?\d+)")
(def date-format "dd/MM/yyyy hh:mm")
(def date-regex
  #"Fecha.+(\d{2}[\/\-]\d{2}[\/\-]\d{4})\sa\slas\s*([\d:]*)")

(defn get-vendor [text] (last (re-find vendor-regex text)))

(defn get-amount [text]
  (try
    (-> (re-find amount-regex text) last read-string)
    (catch NullPointerException e nil)))

(defn get-date [text]
  (try
    (let [date-formatter (SimpleDateFormat. date-format)
          results        (re-find date-regex text)
          hhmm           (last results)
          ddmmyy         (second results)]
      (.parse date-formatter (str ddmmyy " " hhmm)))
    (catch ParseException e nil)))
