(ns linelos.gmail.core
  (:require [linelos.settings
             :refer
             [app-name
              app-url
              app-default-port
              gmail-secret-path
              email-address]])
  (:import (com.google.api.client.extensions.java6.auth.oauth2 AuthorizationCodeInstalledApp)
           (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver)
           (com.google.api.client.googleapis.auth.oauth2 GoogleAuthorizationCodeFlow
                                                         GoogleClientSecrets
                                                         GoogleAuthorizationCodeFlow$Builder
                                                         GoogleCredential
                                                         GoogleCredential$Builder)
           (com.google.api.client.auth.oauth2 Credential BearerToken TokenResponse)
           (com.google.api.client.googleapis.javanet GoogleNetHttpTransport)
           (com.google.api.client.http HttpTransport)
           (com.google.api.client.json.jackson2 JacksonFactory)
           (com.google.api.client.json JsonFactory)
           (com.google.api.client.util.store FileDataStoreFactory)
           (com.google.api.services.gmail Gmail GmailScopes Gmail$Builder)
           (com.google.api.services.gmail.model ListMessagesResponse Message)
           (com.google.api.client.repackaged.org.apache.commons.codec.binary StringUtils)))

(def ^:private http-transport
  (GoogleNetHttpTransport/newTrustedTransport))

(def ^:private json-factory (JacksonFactory/getDefaultInstance))

(def ^:private app-port
  (if (= app-default-port "80") "" (str ":" app-default-port)))

(def ^:private oauth-url
  (str app-url app-port "/oauth2callback"))

(defn get-authorization-url []
  (with-open [reader (clojure.java.io/reader gmail-secret-path)]
    (let [client-secrets           (GoogleClientSecrets/load json-factory reader)
          code-flow-builder        (GoogleAuthorizationCodeFlow$Builder.
                                    http-transport
                                    json-factory
                                    client-secrets
                                    (list GmailScopes/GMAIL_READONLY))
          flow                     (-> code-flow-builder
                                       (.setAccessType "offline")
                                       .build)]
      (-> flow .newAuthorizationUrl
          (.setRedirectUri oauth-url) .build))))

(defn get-credentials [code]
  (with-open [reader (clojure.java.io/reader gmail-secret-path)]
    (let [client-secrets                  (GoogleClientSecrets/load json-factory reader)
          code-flow-builder               (GoogleAuthorizationCodeFlow$Builder.
                                           http-transport
                                           json-factory
                                           client-secrets
                                           (list GmailScopes/GMAIL_READONLY))
          flow                            (-> code-flow-builder
                                              (.setAccessType "offline")
                                              .build)
          credentials                     (-> flow (.newTokenRequest code)
                                              (.setGrantType "authorization_code")
                                              (.setRedirectUri oauth-url)
                                              .execute)]
      credentials)))

(defn get-connection [access-token]
  (with-open [reader (clojure.java.io/reader gmail-secret-path)]
    (let [client-secrets           (GoogleClientSecrets/load json-factory reader)
          credential               (.setAccessToken
                                    (-> (GoogleCredential$Builder.)
                                        (.setClientSecrets client-secrets)
                                        (.setJsonFactory json-factory)
                                        (.setTransport http-transport)
                                        .build)
                                    access-token)
          gmail                    (->
                                    (Gmail$Builder. http-transport json-factory credential)
                                    (.setApplicationName app-name)
                                    .build)]
      gmail)))

(defn- with-gmail-messages [conn]
  (-> conn .users .messages))

(defn execute-list-query
  "Returns a list of messages matching a query"
  ([conn query]
   (-> (with-gmail-messages conn)
       (.list email-address)
       (.setQ query)
       .execute))
  ([conn query page-token]
   (-> (with-gmail-messages conn)
       (.list email-address)
       (.setQ query)
       (.setPageToken page-token)
       .execute)))

(defn execute-get-query
  "Returns a message given its id"
  [conn mail-id]
  (-> (with-gmail-messages conn) (.get email-address mail-id) (.setFormat "raw") .execute))
