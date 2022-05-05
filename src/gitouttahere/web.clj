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
    [:link {:rel "stylesheet" :href (link/file-path request "/styles/autumn.css")}]]
   [:body
    [:div.logo "gitouttahere.org"]
    [:div.body page]]))

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

