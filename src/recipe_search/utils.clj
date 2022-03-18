(ns recipe-search.utils
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [cheshire.core :as cheshire]))
(defn capitalize-words 
  "Capitalize every word in a string, copied from the clojure doc example:
  https://clojuredocs.org/clojure.string/capitalize#example-54590f4ee4b0dc573b892fb8"
  [s]
  (->> (clojure.string/split (str s) #"\b") 
       (map clojure.string/capitalize)
       clojure.string/join))




(defn parse-recipe-name
  [filename]
  (-> filename
      io/file
      .getName
      (clojure.string/split #"\.txt")
      first
      (clojure.string/replace "-" " ")
      capitalize-words))


(defn recipes-directory [] (io/resource "recipes"))

(def all-files
  (map #(str (recipes-directory) "/" %) (seq (.list (io/file (recipes-directory))))))

