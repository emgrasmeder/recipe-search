(ns recipe-search.core
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (:gen-class))

(def recipes-directory (io/resource "recipes"))

(defn contains-substring?
  [haystack needle]
  (s/includes? haystack needle))

(def all-files
  (map #(str recipes-directory "/" %) (seq (.list (io/file recipes-directory)))))


(defn get-recipe [file])



(defn do-search
  "Here's the meat"
  ([search-string] (do-search search-string 10))
  ([search-string n]
   {:search-term search-string
    :num-results n
    :results     (take n (filter #(contains-substring? (slurp %) search-string) all-files))}))



(defn -main
  "Just here to kick everything off"
  [& args]
  (println "Welcome! Please enter your search term and we'll give you some recipes.\nSearch for: ")
  (let [input (read-line)]
    (do-search input 10)))
