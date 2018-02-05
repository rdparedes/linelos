(ns linelos.utils)

; Source: http://www.markhneedham.com/blog/2013/09/22/clojure-stripping-all-the-whitespace/
(defn trim [text] (clojure.string/replace (clojure.string/trim text) #"\s{2,}" " "))
