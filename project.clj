(defproject gitouttahere "0.1.0-SNAPSHOT"
  :description "Website for gitouttahere.org"
  :url "https://gitouttahere.org"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clygments           "2.0.2"]
                 [enlive              "1.1.6"]
                 [markdown-clj        "1.11.1"]
                 [hiccup              "1.0.5"]
                 [optimus             "2022-02-13"]
                 [ring                "1.2.1"]
                 [stasis              "2.5.1"]]
  :ring {:handler gitouttahere.web/app}
  :profiles {:dev {:plugins [[lein-ring "0.12.6"]]}}
  :aliases {"build-site" ["run" "-m" "gitouttahere.web/export"]}
  :repl-options {:init-ns gitouttahere.core})
