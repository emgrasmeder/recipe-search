(ns recipe-search.core-test
  (:require [clojure.test :as t]
            [recipe-search.core :as rs-core]))


(t/deftest string-in-file-test
  (t/testing "should find a needle in a haystack"
    (t/is (rs-core/contains-substring? "a haystack (contains a needle)" "a needle"))
    (t/is (not (rs-core/contains-substring? "a haystack" "a needle")))))

(t/deftest contains-substring-test
  (t/testing "should find a word we knows exists in a file to be in that file"
    (let [filestring (slurp (str rs-core/recipes-directory "/" "parsnip-mash.txt"))
          should-be-true (rs-core/contains-substring? filestring "water")]
      (t/is should-be-true))))


(t/deftest do-search-test
  (t/testing "should find that the word 'pan' exists in our recipe book, sort of an end to end test"
    (t/is (not (empty? (rs-core/do-search "pan"))))))

(t/deftest result-format-test
  (t/testing "should return a map ready for use in the frontend"
    (let [results (rs-core/do-search "tomato")]
      (t/is (= (:search-term results) "tomato"))
      (t/is (= (:num-results results) 10))
      (t/is (= (count (:results results)) 10))))
  (t/testing "should return a list of results with the correct format"
    (let [results (:results (rs-core/do-search "tomato"))]
      (t/is (= (set '[:title :recipe]) (set (keys (first results)))))))

  (t/testing "should return a recipe title in the title of the results"
    (let [titles (map #(get % :title) (:results (rs-core/do-search "stilton")))]
      (t/is (contains? (set titles) "Broccoli Soup With Stilton")))))
