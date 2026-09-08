(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `board-routing-required?` / `board-routing-matches-claim?` /
  `board-routing-mismatch-claim?` are the SAME discipline applied to a
  genuinely Namibia-specific mechanism: the Central Procurement Board of
  Namibia's (CPBN) own Public Procurement Regulations (Government
  Gazette No. 6255, 1 March 2017, Government Notice No. 47, fetched
  directly as a PDF from cpbn.com.na/index/legal_framework), Regulation
  2, which reads verbatim: 'The Board must conduct the bidding process
  on behalf of a public entity for the award of a contract that exceed
  the threshold for such public entity as specified in Annexure 1. ... A
  public entity must conduct its own bidding process for the award of a
  contract that is within the threshold as specified in Annexure 1. ...
  public entities are categorised into Category 1 to Category 3, as
  specified in Annexure 1'. Annexure 1 itself ('PROCUREMENT THRESHOLDS
  FOR PUBLIC ENTITIES', 'Section 8(a) of Act', 'Regulation 2') publishes
  an exact N$ threshold for each of the three categories crossed with
  each of four contract types (goods / works / consultancy services /
  non-consultancy services) -- a 3x4 = 12-constant matrix, reproduced
  verbatim in `board-routing-threshold-matrix` below.

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors: LSO's Contractors Registration Certificate
  is a DISCRETE-CATEGORY -> FIXED-CONSTANT LOOKUP-TABLE EQUALITY test
  (one dimension, A/B/C/D -> one fee each), GMB's GIEPA Special
  Investment Certificate is an ORIGIN-CONDITIONAL INVESTMENT-AMOUNT
  THRESHOLD (one dimension, domestic/foreign -> one threshold each,
  bidder must MEET OR EXCEED it to be ELIGIBLE for an incentive), CAF's
  Marché réservé mechanism is a MULTI-CRITERION INCLUSION-ELIGIBILITY
  test over the bidder's own workforce composition/legal form, and
  Estonia's digital-signing-method check tests the VALIDITY OF THE
  FILING'S OWN EXECUTION INSTRUMENT (a procedural axis, not the bidder's
  business substance at all). Namibia's Board-routing mechanism is none
  of these: it is a TWO-DIMENSIONAL (category x contract-type) THRESHOLD
  LOOKUP whose test direction is INVERTED relative to GMB's -- GMB's
  threshold is a MINIMUM the bidder's own investment must reach or
  exceed to become ELIGIBLE for a benefit; Namibia's threshold is a
  CEILING which, once EXCEEDED, changes WHO CONDUCTS THE PROCUREMENT
  ITSELF (the public entity buyer's own procedural routing, not the
  bidder's eligibility for anything) -- staying within it keeps the
  public entity buyer in charge of its own bidding process, exceeding it
  hands the bidding process to the CPBN Board instead. The engagement
  declares its own believed routing (`:claimed-board-routing?`) for a
  procurement whose own category/contract-type/estimated-value it also
  declares, and the governor independently recomputes the true routing
  from the published matrix and compares for EQUALITY against the
  claim -- catching an operator who misclassifies a procurement (e.g.
  understating its estimated value or the buyer's category) to make it
  look like it can bypass CPBN Board-level process.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement portal. It builds the RECORD an
  operator would keep, not the act of submitting a portal registration
  itself (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [kotoba.lang.text :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(def ^:private money-scale
  "Sub-minor-unit scale used when comparing two money amounts: 1/10000 of
  a unit. Coarser than double representation error by many orders of
  magnitude, finer than any real currency's minor unit (2 decimals for
  most, 3 for KWD/BHD/OMR, 0 for JPY/KRW)."
  10000)

(defn- money=
  "Exact-at-money-precision equality for two amounts.

  `==` on raw doubles is NOT the right comparison for money. With
  whole-unit fees the two agree, but as soon as an amount carries
  cents the sum `base + rate x months` is routinely not the double
  nearest the true total, and a CORRECT claim compares false: measured
  on this exact shape, 40,989 of 327,060 cent-denominated combinations
  (12.5%) were rejected while being right, against 0 of 327,060 in
  whole units.

  Rounding both sides to `money-scale` before comparing removes the
  representation error while preserving every distinction money can
  actually carry."
  [x y]
  (and (number? x) (number? y)
       (= (Math/round (* money-scale (double x)))
          (Math/round (* money-scale (double y))))))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  ;; nil when any field is not a number: an un-recomputable engagement is
  ;; un-verifiable, which is neither `correct` nor a ClassCastException
  ;; thrown out of the caller.
  (when (and (number? base-fee) (number? monthly-rate) (number? monitoring-months))
    (+ (double base-fee)
       (* (double monthly-rate) (double monitoring-months)))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (money= claimed-fee (compute-engagement-fee engagement)))

(def board-routing-threshold-matrix
  "Government Gazette of the Republic of Namibia No. 6255 (1 March
  2017), Government Notice No. 47, Public Procurement Regulations,
  Annexure 1 ('PROCUREMENT THRESHOLDS FOR PUBLIC ENTITIES', Section 8(a)
  of Act, Regulation 2, fetched directly as a PDF): the estimated-value
  ceiling (Namibia Dollars, N$) for each public-entity category (1/2/3)
  crossed with each contract type, within which the public entity
  itself conducts its own bidding process; ABOVE which the CPBN Board
  must conduct the bidding process on the public entity's behalf."
  {:category-1 {:goods 25000000 :works 35000000 :consultancy-services 20000000 :non-consultancy-services 15000000}
   :category-2 {:goods 20000000 :works 30000000 :consultancy-services 15000000 :non-consultancy-services 10000000}
   :category-3 {:goods 15000000 :works 20000000 :consultancy-services 10000000 :non-consultancy-services 5000000}})

(defn compute-board-routing-required?
  "The ground-truth Board-routing determination for
  `pe-category`/`contract-type`/`estimated-value`, independently
  recomputed from Annexure 1's own published threshold matrix: `true`
  (CPBN Board must conduct the bid) when `estimated-value` EXCEEDS the
  published threshold for that category/contract-type; `false` (the
  public entity conducts its own bid) when it is within the threshold.
  An unrecognized `pe-category`/`contract-type` combination simply
  returns nil -- never guesses a threshold outside the published
  matrix."
  [pe-category contract-type estimated-value]
  (when-let [threshold (get-in board-routing-threshold-matrix [pe-category contract-type])]
    (> (double estimated-value) (double threshold))))

(defn board-routing-matches-claim?
  "Does `engagement`'s own `:claimed-board-routing?` equal the
  INDEPENDENTLY recomputed `compute-board-routing-required?` for its
  own declared `:pe-category`/`:contract-type`/`:estimated-value`? An
  unrecognized category/contract-type fails closed (does not throw,
  never guesses). NOTE: `compute-board-routing-required?` legitimately
  returns `false` (not just nil) for a recognized category/contract-type
  whose estimated value is within the threshold -- this function uses
  `some?`, not `when-let`/`if-let`, to distinguish that valid `false`
  from the nil 'unrecognized' case (a `when-let` here would wrongly
  treat a valid computed `false` the same as an unrecognized nil)."
  [{:keys [pe-category contract-type estimated-value claimed-board-routing?]}]
  (let [computed (compute-board-routing-required? pe-category contract-type estimated-value)]
    (boolean (and (some? computed)
                  (= (boolean claimed-board-routing?) computed)))))

(defn board-routing-mismatch-claim?
  "Does `engagement` declare `:seeking-board-routing-determination?
  true` (i.e. it is asking this actor to confirm which body conducts
  the bidding process for its own declared procurement) while the
  INDEPENDENTLY recomputed `board-routing-matches-claim?` is false? An
  engagement not seeking a board-routing determination is never flagged
  by this check (entity/engagement-scope-gated, the same discipline
  LSO's `:seeking-contractor-registration?`-gated check and GMB's
  `:seeking-sic?`-gated check use)."
  [{:keys [seeking-board-routing-determination?] :as engagement}]
  (boolean (and seeking-board-routing-determination?
                (not (board-routing-matches-claim? engagement)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
