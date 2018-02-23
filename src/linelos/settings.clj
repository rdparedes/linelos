(ns linelos.settings
  (:require [environ.core :refer [env]]))

(def app-name "linelos")
(def app-url (get env :app-url))
(def frontend-app-url (get env :frontend-app-url))
(def app-default-port (get env :app-default-port))
(def email-address (get env :client-email))
(def gmail-secret-path (get env :gmail-secret-path))
