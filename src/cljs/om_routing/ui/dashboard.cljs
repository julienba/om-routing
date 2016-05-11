(ns om-routing.ui.dashboard
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            ;[taoensso.timbre :as timbre :refer (error)]
            ))

(defui Author
  static om/Ident
  (ident
   [this props]
   [:id (or (:id props) (second props))])
  static om/IQuery
  (query
   [this]
   [:id :name]))

(defui Post
  static om/IQuery
  (query [this]
         [:id :type :title {:author (om/get-query Author)} :content])
  Object
  (render
   [this]
   (dom/div nil (str (om/props this)))))

(def post-ui (om/factory Post {:keyfn :id}))

(defui Photo
  static om/IQuery
  (query [this]
         [:id :type :title :image :caption])
  Object
  (render
   [this]
   (dom/div nil (str (om/props this)))))

(def photo-ui (om/factory Photo {:keyfn :id}))

(defui Graphic
  static om/IQuery
  (query [this]
         [:id :type :image])
  Object
  (render
   [this]
   (dom/div nil (str (om/props this)))))

(def graphic-ui (om/factory Graphic {:keyfn :id}))

(defui DashboardItem
  static om/Ident
  (ident [this {:keys [id type]}]
    [type id])
  static om/IQuery
  (query [this]
    (zipmap
      [:dashboard/post :dashboard/photo :dashboard/graphic]
      (map #(conj % :favorites)
        [(om/get-query Post)
         (om/get-query Photo)
         (om/get-query Graphic)])))
  Object
  (render
   [this]
   (let [{:keys [author type] :as props} (om/props this)]
     (dom/li nil
      (dom/div #js {:className "media-body"}
               (condp = type
                 :dashboard/post (post-ui props)
                 :dashboard/photo (photo-ui props)
                 :dashboard/graphic (graphic-ui props)
                 (do
                   (println (str "No rendering for type: " type))
                   (dom/div nil "")))))
     )))

(def item-ui (om/factory DashboardItem {:keyfn :id}))

(defui Dashboard
  static om/IQuery
  (query [_]
         [{:dashboard/items (om/get-query DashboardItem)}
          {:dashboard/authors (om/get-query Author)}])
  Object
  (render
   [this]
   (let [{:keys [dashboard/items]} (om/props this)]
     (dom/div
      #js {:className "wrapper"}
      ;(println "Items => " items)
      (dom/h2 nil "Items:")
      (apply dom/ul nil
             (map item-ui items))))))
