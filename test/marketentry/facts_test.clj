(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest nam-has-spec-basis
  (let [sb (facts/spec-basis "NAM")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/board-routing-spec-basis "NAM")))))

(deftest nam-rep-spec-basis-is-honestly-absent
  (testing "no Namibia-specific representative/director exclusion-extension provision was confirmed this iteration -- deliberately not claimed"
    (is (nil? (facts/rep-spec-basis "NAM")))))

(deftest nam-corporate-number-spec-basis-is-honestly-absent
  (testing "NamRA and gov.na/mof.gov.na were unreachable (connection-level failure) this iteration -- deliberately not claimed"
    (is (nil? (facts/corporate-number-spec-basis "NAM")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "NAM")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "NAM" all)))
    (is (not (facts/required-evidence-satisfied? "NAM" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["NAM" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest board-routing-spec-basis-threshold-matrix
  (let [br (facts/board-routing-spec-basis "NAM")]
    (is (= 25000000 (get-in br [:board-routing-threshold-matrix :category-1 :goods])))
    (is (= 35000000 (get-in br [:board-routing-threshold-matrix :category-1 :works])))
    (is (= 20000000 (get-in br [:board-routing-threshold-matrix :category-1 :consultancy-services])))
    (is (= 15000000 (get-in br [:board-routing-threshold-matrix :category-1 :non-consultancy-services])))
    (is (= 20000000 (get-in br [:board-routing-threshold-matrix :category-2 :goods])))
    (is (= 30000000 (get-in br [:board-routing-threshold-matrix :category-2 :works])))
    (is (= 15000000 (get-in br [:board-routing-threshold-matrix :category-2 :consultancy-services])))
    (is (= 10000000 (get-in br [:board-routing-threshold-matrix :category-2 :non-consultancy-services])))
    (is (= 15000000 (get-in br [:board-routing-threshold-matrix :category-3 :goods])))
    (is (= 20000000 (get-in br [:board-routing-threshold-matrix :category-3 :works])))
    (is (= 10000000 (get-in br [:board-routing-threshold-matrix :category-3 :consultancy-services])))
    (is (= 5000000 (get-in br [:board-routing-threshold-matrix :category-3 :non-consultancy-services])))))
