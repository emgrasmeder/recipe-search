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


(defn capitalize-words 
  "Capitalize every word in a string"
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

(defn save-recipe [search-string filename]
  (let [recipe (slurp filename)]
    (if (contains-substring? recipe search-string)
      {:title (parse-recipe-name filename) :recipe recipe})))




(defn do-search
  ([search-string] (do-search search-string 10))
  ([search-string n]
   {:search-term search-string
    :num-results n
    :results (->> all-files 
                  (pmap (partial save-recipe search-string))
                  (filter identity)
                  (take 10))}))
     


(defn -main
  "Just here to kick everything off"
  [& args]
  (println "Welcome! Please enter your search term and we'll give you some recipes.\nSearch for: ")
  (let [input (read-line)]
    (do-search input 10)))
