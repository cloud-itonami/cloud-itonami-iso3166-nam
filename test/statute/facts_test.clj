(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest nam-has-spec-basis
  (let [sb (facts/spec-basis "NAM")]
    (is (= 3 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["NAM" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["nam.labour-act-2007"]
         (mapv :statute/id (facts/by-topic "NAM" :labor))))
  (is (= ["nam.income-tax-act-1981"]
         (mapv :statute/id (facts/by-topic "NAM" :tax))))
  (is (= ["nam.companies-act-2004"]
         (mapv :statute/id (facts/by-topic "NAM" :corporate-governance))))
  (is (empty? (facts/by-topic "ATL" :labor))))
