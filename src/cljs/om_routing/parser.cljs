(ns om-routing.parser
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [clojure.string :as string]
            [om-routing.util :refer [trace]]
            [om-routing.ui.home :refer [Home]]
            [om-routing.ui.dashboard :refer [Dashboard]]
            [om-routing.ui.search :refer [AutoCompleter]]))


;; =============================================================================
;; Routing plumbing

(def route->component
  {:app/home Home
   :app/dashboard Dashboard
   :app/search AutoCompleter})

(def route->factory
  (zipmap (keys route->component)
    (map om/factory (vals route->component))))

;; =============================================================================
;; Read

(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [state query]} k params]
  (let [st @state]
    (if-let [[_ v] (find st k)]
      {:value v}
      (do
        (print "Not found" k)
        {:value :not-found}))))

; Original method from blogpost
;; (defmethod read :route/data
;;    [{:keys [state query]} k _]
;;    (let [st @state
;;          route (get st :app/route)
;;          route (cond-> route
;;                  (= (second route) '_) pop)]
;;      ;; since the route is an `ident`, it could also
;;      ;; be passed as the second argument to `db->tree`
;;      ;; if our data was normalized

;;      ;(println " Parser => " st)

;;      {:value (get-in st route)}))


(defmethod read :route/data
   [{:keys [state query parser ast] :as env} k _]
   (let [st @state
         route (get st :app/route)
         route (cond-> route
                 (= (second route) '_) pop)
         root-query-key (first route)
         my-query (get query root-query-key)
         route-state (get @state root-query-key)
         route-component (get route->component (first route))
         tree-data (om/tree->db route-component route-state true)
         result (parser (assoc env :state (atom tree-data)) my-query)]

     ;(trace tree-data)

     {:value result}))

(defmethod read :app/route
   [{:keys [state query]} k _]
   (let [st @state]
     {:value (get st k)}))

(defn read-list [st k query]
  (om/db->tree query (get st k) st))

(defmethod read :dashboard/items
  [{:keys [state query]} k _]
  {:value (read-list @state k query)})

(defmethod read :dashboard/authors
  [{:keys [state query]} k _]
  (println "inside :dashboard/authors => ")
  {:value (read-list @state k query)})

(defmethod read :search/results
  [{:keys [state ast] :as env} k {:keys [query]}]
  (merge
    {:value (get @state k [])}
    (when-not (or (string/blank? query)
                  (< (count query) 3))
      {:search ast})))

;; =============================================================================
;; Mutate

(defmulti mutate om/dispatch)

(defmethod mutate 'change/route!
  [{:keys [state]} _ {:keys [route]}]
  {:value {:keys [:app/route]}
   :action
   (fn []
     (swap! state assoc :app/route route))})
