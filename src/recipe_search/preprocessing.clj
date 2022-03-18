(ns recipe-search.preprocessing
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [recipe-search.vocabulary :as rs-vocab]
            [recipe-search.utils :as rs-utils]
            [cheshire.core :as cheshire]))


(defn preprocess
  "Should be run nightly, for example."
  []
  (->> rs-utils/all-files
       (pmap (fn [f] {:name (clojure.string/lower-case (rs-utils/parse-recipe-name f))
                      :location f}))
       (into [])
       (cheshire/generate-string)
       (spit (io/file (io/resource "preprocessing/recipes.edn"))))
  (rs-vocab/create-vocabulary))
