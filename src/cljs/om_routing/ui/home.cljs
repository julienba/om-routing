(ns om-routing.ui.home
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui Home
  static om/IQuery
  (query [this]
    [:home/title :home/content])
  Object
  (render
   [this]
   (let [{:keys [home/title home/content] :as props} (om/props this)]
     ;(println "Render Home => " props)
     (dom/div
      #js {:className "wrapper"}
      (dom/h3 nil title)
      (dom/p nil content)))))
