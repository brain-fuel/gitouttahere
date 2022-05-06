(ns gitouttahere.web
  (:require
   [gitouttahere.highlight :refer [highlight-code-blocks]]
   [clojure.java.io :as io]
   [clojure.string :as cstr]
   [hiccup.page :refer [html5 include-css]]
   [markdown.core :as md]
   [optimus.assets :as assets]
   [optimus.link :as link]
   [optimus.optimizations :as optimizations]
   [optimus.prime :as optimus]
   [optimus.strategies :refer [serve-live-assets]]
   [stasis.core :as stasis]))

(def this-year
  (-> (java.time.LocalDateTime/now)
      .getYear
      .toString))

(def copyright-notice
  (html5
   [:p.copyright_text
    (str "Copyright &#169; 2022"
         (cond
           (= "2022" this-year) ""
           :else (str " - " this-year))
         " Matt Laine")]
   [:p.copyright_text
    "Licensed under CC BY-SA 4.0"]
   [:p.copyright_text
    "Code licensed under GPL v3."]))

(defn get-assets []
  (assets/load-assets "public" [#".*"]))

(defn layout-page [request page]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:title "Git Outta Here"]
    [:link {:rel "stylesheet" :href (link/file-path request "/styles/main.css")}]
    [:link {:rel "stylesheet" :href (link/file-path request "/styles/autumn-code.css")}]]
   [:body
    [:div.wrapper
     [:header.site-header
      [:h1.site-header--title "gitouttahere.org"]]
     [:main.container
      [:div.body page]]
     [:footer copyright-notice]]]))

(defn partial-pages [pages]
  (zipmap (keys pages)
          (map #(fn [req] (layout-page req %)) (vals pages))))

(defn markdown-pages [pages]
  (zipmap (map #(cstr/replace % #"\.md$" "/") (keys pages))
          (map #(fn [req] (layout-page req (md/md-to-html-string %)))
               (vals pages))))

(defn get-raw-pages []
  (stasis/merge-page-sources
   {:public
    (stasis/slurp-directory "resources/public" #".*\.(html|css|js)$")
    :partials
    (partial-pages (stasis/slurp-directory "resources/partials" #".*\.html$"))
    :markdown
    (markdown-pages (stasis/slurp-directory "resources/md" #".*\.md$"))}))

(defn prepare-page [page req]
  (-> (if (string? page)
        page
        (page req))
      highlight-code-blocks))

(defn prepare-pages [pages]
  (zipmap (keys pages)
          (map #(partial prepare-page %) (vals pages))))

(defn get-pages []
  (-> (get-raw-pages)
      (prepare-pages)))

(def app
  (optimus/wrap (stasis/serve-pages get-pages)
                get-assets
                optimizations/all
                serve-live-assets))
