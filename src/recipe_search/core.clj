(ns recipe-search.core
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [recipe-search.preprocessing :as rs-preprocessing]
            [recipe-search.utils :as rs-utils]
            [cheshire.core :as cheshire])
  (:gen-class))


(defn contains-substring?
  [haystack needle]
  (s/includes? haystack needle))


(defn optionally-save-recipe [search-string filename]
  (let [recipe (slurp filename)]
    (if (contains-substring? recipe search-string)
      {:title (rs-utils/parse-recipe-name filename) :recipe recipe})))

(defn search-vectorized-contents 
  "Ideally it should be trivial to search for a vectorized search term across vectorized recipes
  I didn't save the vectorized-recipes in the correct format to make that happen
  and the way it works now, I think loading the vectorized recipes takes even longer than just loading the strings!"
  [search-string]
 (->> (io/file (io/resource "preprocessing/vectorized-recipes.edn")) 
      slurp
      (fn [thing] (print "and this is basically where I gave up"))))

(defn search-contents [search-string]
 (->> rs-utils/all-files 
      (pmap (partial optionally-save-recipe search-string))
      (filter identity)
      (take 10))) 


(defn check-if-title-contains-string [search-string title-map]
  (if (contains-substring? (:name title-map) search-string)
    {:title (rs-utils/parse-recipe-name (:name title-map))
     :recipe (slurp (:location title-map))}))
  
  

(defn search-titles
 "search recipe titles first, because the search space is smaller and 
 efforts to optimize searching the entire corpus could occasionally overlook optimal results"
  [search-string]
  (->> (pmap (partial check-if-title-contains-string search-string) 
            (cheshire/parse-string (slurp (io/file (io/resource "preprocessing/recipes.edn"))) true))
      (filter identity)
      (take 10)))
      

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
  ([s] (convert-pretty-title-to-filename s (rs-utils/recipes-directory)))
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
      (rs-preprocessing/preprocess)
      (println "Done with setup!")
      (System/exit 0)))
  (println "Welcome! This CLI exists as a simple use case showcase, but I was thinking of the code as something that would communicate to another machine.\n"
           "Please enter your search term and we'll give you some recipes.\nSearch for: ")
  (let [input (read-line)]
    (run! println (do-search "thai" 10))))



