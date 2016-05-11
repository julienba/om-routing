(ns om-routing.util
  (:require [cljs.pprint :refer [pprint]]))

(defn trace
  ([obj] (pprint obj))
  ([msg obj]
   (print msg)
   (pprint obj)))
