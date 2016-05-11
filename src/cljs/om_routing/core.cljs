(ns om-routing.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.dom :as gdom]
            [cljs.core.async :as async :refer [<! >! put! chan]]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
;;             [om-routing.ui.home :refer [Home]]
;;             [om-routing.ui.dashboard :refer [Dashboard]]
;;             [om-routing.ui.search :refer [AutoCompleter]]
            [om-routing.state :as state]
            [om-routing.parser :as parser])
  (:import [goog Uri]
           [goog.net Jsonp]))

(enable-console-print!)

;; =============================================================================
;; Routing plumbing

;; (def route->component
;;   {:app/home Home
;;    :app/dashboard Dashboard
;;    :app/search AutoCompleter})

;; (def route->factory
;;   (zipmap (keys route->component)
;;     (map om/factory (vals route->component))))

;; =============================================================================
;; Remote Wikipedia

(def base-url
  "http://en.wikipedia.org/w/api.php?action=opensearch&format=json&search=")

(defn jsonp
  ([uri] (jsonp (chan) uri))
  ([c uri]
   (let [gjsonp (Jsonp. (Uri. uri))]
     (.send gjsonp nil #(put! c %))
     c)))

(defn search-loop [c]
  (go
    (loop [[query cb] (<! c)]
      (let [[_ results] (<! (jsonp (str base-url query)))]
        (cb {:search/results results}))
      (recur (<! c)))))

(defn send-to-chan [c]
  (fn [{:keys [search]} cb]
    (when search
      (let [{[search] :children} (om/query->ast search)
            query (get-in search [:params :query])]
        (put! c [query cb])))))

(def send-chan (chan))

;; =============================================================================
;; Main component

(defui Root
  static om/IQuery
  (query [this]
    [:app/route
    {:route/data (zipmap (keys parser/route->component)
                   (map om/get-query (vals parser/route->component)))}])
  Object
  (render [this]
    (let [{:keys [app/route route/data] :as props} (om/props this)]
      (println "Render Root => " route)
      (dom/section
       nil

       (dom/nav nil
                (apply dom/ul nil
                       (map (fn [route-key]
                              (dom/li nil
                                      (dom/button #js {:onClick #(om/transact! this `[(change/route! {:route [~route-key ~'_]})])}
                                                  (str route-key))))
                            (keys parser/route->component))))

       (dom/h1 nil "Routing test")
       ((parser/route->factory (first route)) data)))))

(def reconciler
  (om/reconciler
    {:state state/init-data
     :parser (om/parser {:read parser/read
                         :mutate parser/mutate})
     :send    (send-to-chan send-chan)
     :remotes [:remote :search]}))

(om/add-root! reconciler
  Root (gdom/getElement "app"))

