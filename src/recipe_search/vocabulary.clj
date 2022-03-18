(ns recipe-search.vocabulary
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [recipe-search.utils :as rs-utils]
            [cheshire.core :as cheshire]))

(defn get-distinct-words-in-string [s]
  (-> s
      (clojure.string/lower-case)
      (clojure.string/replace #"\n" " ")
      (clojure.string/split #" ")
      (#(map clojure.string/trim %))
      distinct)) 

(defn filter-for-count-greater-than-1 [freqs]
  (filter #(> (second %) 1)
          freqs))

(defn convert-string-coll-to-vocabulary
 "Ideally we would do more pre-processing to turn words like 'don't' and 'wont' into 'do not' and 'will not' and to merge words like simply and simple. this would bring our number of hapax legomena down which speeds up search, but also makes search less brittle"
 [coll]
 (->
  (frequencies coll)
  #_(filter-for-count-greater-than-1) 
  keys
  (zipmap (iterate inc 1))
  #_(assoc :not-in-vocabulary -999))) 
  
  
(defn create-vocabulary []
  (->> rs-utils/all-files
       (map (fn [f] (get-distinct-words-in-string (slurp f))))
       (apply concat)
       convert-string-coll-to-vocabulary
       (cheshire/generate-string)
       (spit (io/file (io/resource "preprocessing/vocabulary.edn")))))


(defn word2vec 
  "replaces every word in a string with a corresponding number in a predefined vocabulary"
  [s]

  (let [ vocabulary (cheshire/parse-string (slurp (io/file (io/resource "preprocessing/vocabulary.edn"))) true)]
   (->> (clojure.string/split s #" ")
        (map #(get vocabulary (keyword %)))
        (remove nil?))))
 
(defn write-vectorized-recipes
  []
  (->> rs-utils/all-files
       (map (fn [f] {:location f :content (word2vec (slurp f))}))
       (cheshire/generate-string)
       (spit (io/file (io/resource "preprocessing/vectorized-recipes.edn")))))
