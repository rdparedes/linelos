(defproject linelos "1.0.0"
  :description "Linelos app"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :license
  {:name "MIT"
   :url  "https://opensource.org/licenses/MIT"}
  :source-paths ["src/"]
  :test-paths ["test/unit" "test/functional"]
  :aliases
  {"test:unit"       ["midje" ":filter" "-functional"]
   "test:functional" ["midje" ":filter" "functional"]
   "test:watch"      ["midje" ":autotest" ":filter" "-functional "]
   "test:coverage"   ["cloverage" "--runner" ":midje"]
   "test"            ["do" "test:unit," "test:functional"]
   "cljfmt-fix"      ["cljfmt" "fix"]
   "check"           ["do" "kibit," "cljfmt-fix," "test"]}
  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [com.google.api-client/google-api-client "1.22.0"]
   [com.google.oauth-client/google-oauth-client-jetty "1.22.0"]
   [com.google.apis/google-api-services-gmail "v1-rev70-1.22.0"]
   [compojure "1.6.0"]
   [ring/ring-core "1.6.3"]
   [ring/ring-defaults "0.3.1"]
   [ring/ring-jetty-adapter "1.6.3"]
   [ring/ring-json "0.4.0"]
   [clj-http "3.7.0"]
   [cheshire "5.8.0"]
   [environ "1.1.0"]
   [peridot "0.5.0"]]
  :plugins
  [[lein-kibit "0.1.5"]
   [lein-midje "3.2.1"]
   [lein-cloverage "1.0.9"]
   [lein-ring "0.12.1"]
   [lein-cljfmt "0.5.7"]]
  :ring
  {:handler linelos.api.handler/app
   :port    3478}
  :main ^:skip-aot linelos.api.handler
  :target-path "target/%s"
  :profiles
  {:uberjar {:aot :all}
   :dev     {:dependencies [[midje "1.8.3"]
                            [ring/ring-mock "0.3.2"]]}})
