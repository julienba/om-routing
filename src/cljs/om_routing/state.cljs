(ns om-routing.state
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))


(def init-data
  {:app/route '[:app/home _]

   :app/home
   {:home/title "Home page"
    :home/content "This is the homepage. There isn't a lot to see here."}

   :app/dashboard
   {:dashboard/items
     [{:id 0 :type :dashboard/post
       ;:author "Laura Smith"
       :author {:id 0}
       :title "A Post!"
       :content "Lorem ipsum dolor sit amet, quem atomorum te quo"}
      {:id 1 :type :dashboard/photo
       :title "A Photo!"
       :image "photo.jpg"
       :caption "Lorem ipsum"}
      {:id 2 :type :dashboard/post
       :author "Jim Jacobs"
       :title "Another Post!"
       :content "Lorem ipsum dolor sit amet, quem atomorum te quo"}
      {:id 3 :type :dashboard/graphic
       :title "Charts and Stufff!"
       :image "chart.jpg"}
      {:id 4 :type :dashboard/post
       ;:author "May Fields"
       :author {:id 1}
       :title "Yet Another Post!"
       :content "Lorem ipsum dolor sit amet, quem atomorum te quo"}
      ]
    :dashboard/authors
    [{:id 0 :name "Laura Smith"}
     {:id 1 :name "May Fields"}]}

   :app/search {}})
