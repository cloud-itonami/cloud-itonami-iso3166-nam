(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "NAM" 0)
        s (registry/register-submit "eng-1" "NAM" 0)]
    (is (= "NAM-DFT-000000" (get d "draft_number")))
    (is (= "NAM-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "NAM" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest board-routing-threshold-lookup
  (testing "each category/contract-type cell resolves to its own exact Annexure 1 threshold"
    (is (= 25000000 (get-in registry/board-routing-threshold-matrix [:category-1 :goods])))
    (is (= 35000000 (get-in registry/board-routing-threshold-matrix [:category-1 :works])))
    (is (= 5000000 (get-in registry/board-routing-threshold-matrix [:category-3 :non-consultancy-services])))))

(deftest compute-board-routing-required
  (testing "estimated value strictly exceeding the published threshold requires Board routing"
    (is (true? (registry/compute-board-routing-required? :category-2 :goods 25000000.0)))
    (is (false? (registry/compute-board-routing-required? :category-2 :goods 20000000.0))
        "exactly at the threshold is 'within' per Regulation 2(2), not 'exceeding'")
    (is (false? (registry/compute-board-routing-required? :category-2 :goods 15000000.0))))
  (testing "an unrecognized category/contract-type never guesses a threshold"
    (is (nil? (registry/compute-board-routing-required? :category-9 :goods 1.0)))
    (is (nil? (registry/compute-board-routing-required? :category-1 :unknown-type 1.0)))))

(deftest board-routing-matches-claim
  (testing "a claim that EXACTLY matches the independently recomputed routing matches"
    (is (true? (registry/board-routing-matches-claim?
                {:pe-category :category-2 :contract-type :goods
                 :estimated-value 25000000.0 :claimed-board-routing? true})))
    (is (true? (registry/board-routing-matches-claim?
                {:pe-category :category-3 :contract-type :works
                 :estimated-value 5000000.0 :claimed-board-routing? false}))))
  (testing "a claim opposite to the independently recomputed routing does not match"
    (is (false? (registry/board-routing-matches-claim?
                 {:pe-category :category-2 :contract-type :goods
                  :estimated-value 25000000.0 :claimed-board-routing? false}))
        "N$25M exceeds Category 2 goods' N$20M threshold -- Board routing IS required, claim of false is wrong"))
  (testing "missing or unrecognized category/contract-type fails closed"
    (is (false? (registry/board-routing-matches-claim?
                 {:contract-type :goods :estimated-value 1.0 :claimed-board-routing? true})))
    (is (false? (registry/board-routing-matches-claim?
                 {:pe-category :category-9 :contract-type :goods
                  :estimated-value 1.0 :claimed-board-routing? true})))))

(deftest board-routing-mismatch-claim-is-entity-scope-gated
  (testing "an engagement NOT seeking a board-routing determination is never flagged, even if the claim is wrong"
    (is (false? (registry/board-routing-mismatch-claim?
                 {:seeking-board-routing-determination? false
                  :pe-category :category-2 :contract-type :goods
                  :estimated-value 25000000.0 :claimed-board-routing? false}))))
  (testing "a determination-seeking engagement whose claim does NOT match the independently recomputed routing -> mismatch"
    (is (true? (registry/board-routing-mismatch-claim?
                {:seeking-board-routing-determination? true
                 :pe-category :category-2 :contract-type :goods
                 :estimated-value 25000000.0 :claimed-board-routing? false}))))
  (testing "a determination-seeking engagement whose claim DOES match -> not flagged"
    (is (false? (registry/board-routing-mismatch-claim?
                 {:seeking-board-routing-determination? true
                  :pe-category :category-2 :contract-type :goods
                  :estimated-value 25000000.0 :claimed-board-routing? true})))))

;; ---------------------------------------------------------------------------
;; Money is compared at money precision, not at double precision
;; ---------------------------------------------------------------------------

(deftest whole-unit-fees-were-already-correct-and-stay-correct
  (testing "the seeded shape: base + rate x months in whole currency units"
    (is (registry/engagement-fee-matches-claim?
         {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
           :claimed-fee 860000.0}))))

(deftest cent-denominated-fees-are-no-longer-rejected-while-correct
  (testing "`(== (double claimed) (+ (double base) (* (double rate) (double months))))`
            rejected CORRECT totals once an amount carried cents -- 40,989 of
            327,060 combinations (12.5%), against 0 of 327,060 in whole units"
    (let [bad (for [m (range 1 37)
                    bc (range 10000 90000 2100)
                    rc (range 500 6000 210)
                    :let [truth (/ (+ bc (* rc m)) 100.0)]
                    :when (not (registry/engagement-fee-matches-claim?
                                {:base-fee (/ bc 100.0) :monthly-rate (/ rc 100.0)
                                  :monitoring-months m :claimed-fee truth}))]
                [m (/ bc 100.0) (/ rc 100.0) truth])]
      (is (empty? bad) (str "false rejections: " (count bad) " e.g. " (first bad))))))

(deftest a-genuinely-wrong-fee-is-still-caught
  (testing "rounding to money precision must not blunt the check"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.01})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 859999.99})))))

(deftest an-unverifiable-fee-never-matches
  (testing "un-verifiable is not the same as correct, and not a crash"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee "500000" :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.0})))
    (is (nil? (registry/compute-engagement-fee {:base-fee 500000 :monthly-rate 30000})))))
