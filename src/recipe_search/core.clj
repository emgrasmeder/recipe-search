(ns recipe-search.core
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [cheshire.core :as cheshire])
  (:gen-class))

(defn recipes-directory [] (io/resource "recipes"))

(defn contains-substring?
  [haystack needle]
  (s/includes? haystack needle))

(def all-files
  (map #(str (recipes-directory) "/" %) (seq (.list (io/file (recipes-directory))))))


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

(defn optionally-save-recipe [search-string filename]
  (let [recipe (slurp filename)]
    (if (contains-substring? recipe search-string)
      {:title (parse-recipe-name filename) :recipe recipe})))


(defn search-contents [search-string]
 (->> all-files 
      (pmap (partial optionally-save-recipe search-string))
      (filter identity)
      (take 10))) 



(defn check-if-title-contains-string [search-string title-map]
  (if (contains-substring? (:name title-map) search-string)
    {:title (parse-recipe-name (:name title-map))
     :recipe (slurp (:location title-map))}))
  
  

(defn search-titles
 "search recipe titles first, because the search space is smaller and 
 efforts to optimize searching the entire corpus could occasionally overlook optimal results"
  [search-string]
  (->> (pmap (partial check-if-title-contains-string search-string) 
            (cheshire/parse-string (slurp (io/file (io/resource "preprocessing/recipes.edn"))) true))
      (filter identity)
      (take 10)))
      
  
(defn preprocessing 
  "Should be run nightly, for example."
  []
  (->> all-files
       (pmap (fn [f] {:name (clojure.string/lower-case (parse-recipe-name f))
                      :location f}))
       (into [])
       (cheshire/generate-string)
       (spit (io/file (io/resource "preprocessing/recipes.edn")))))
 

(defn collate-recipes 
  [search-string n]
  (->> search-string
       search-titles
       (#(if (< (count %) n)
          (search-contents search-string)
          %))
       conj
       distinct
       (take n)))
  

(defn do-search
  ([search-string] (do-search search-string 10))
  ([search-string n]
   {:search-term search-string
    :num-results n
    :results (collate-recipes search-string n)})) 


(defn convert-pretty-title-to-filename 
  "converts a pretty filename with spaces and capitalization into a 
  filename like the format defined in the problem.
  this doesn't check that the file it's referencing really exists..."
  ([s] (convert-pretty-title-to-filename s (recipes-directory)))
  ([s file-path]
   (-> s
       (clojure.string/replace #" " "-")
       clojure.string/lower-case
       (str ".txt")
       (#(str file-path "/" %)))))


(defn -main
  "Just here to kick everything off"
  [& args]
  (doseq [arg *command-line-args*]
    (when (= arg "setup")
      (preprocessing)
      (println "Done with setup!")
      (System/exit 0)))
  (println "Welcome! This CLI exists as a simple use case showcase, but I was thinking of the code as something that would communicate to another machine.\n"
           "Please enter your search term and we'll give you some recipes.\nSearch for: ")
  (let [input (read-line)]
    (run! println (do-search "thai" 10))))



