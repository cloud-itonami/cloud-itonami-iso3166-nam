(ns statute.facts
  "General-law compliance catalog for Namibia (NAM) -- extends this
  repo's existing `marketentry.facts` (public-procurement market-entry
  only, narrow scope) with a second, orthogonal catalog of statutes a
  company operating in this jurisdiction must generally track for
  compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/
  -arm/-atg/-ben/-btn/-bwa/-caf/-est/-gmb/-lso's `statute.facts`
  (ADR-2607141700, cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL government-adjacent URL -- never
  fabricated.

  Three entries below were confirmed the SAME way: this iteration
  fetched the Namibia Legal Information Institute's (NamibLII,
  `namiblii.org`) own paginated legislation-LISTING pages directly
  (`namiblii.org/legislation/` through `?page=9`, plain server-rendered
  HTML, HTTP 200, NOT behind any challenge -- confirmed by checking
  every page from 1 through 9; page 10 returns HTTP 404, confirming 9 is
  the actual last page) and read the exact anchor text + citation
  string for each Act's row in the table. EVERY attempt this iteration
  made to fetch the individual DOCUMENT page for any Act (a plausible
  real path AND a deliberately invalid control path) returned an
  identical Cloudflare 'Just a moment...' JS-challenge shell (HTTP 403,
  confirmed by inspecting the raw response body) to `curl` this
  iteration -- an HONEST, explicitly-flagged ACCESS gap (a bot-challenge
  on NamibLII's individual-document route specifically, confirmed
  universal by the control-path test, not a claim that these Acts' text
  does not exist). Confidence is therefore MODERATE on each citation's
  exact title/act-number (independently read from NamibLII's own
  listing table's dedicated citation column) and LOW on any
  section-level claim -- accordingly, NO section numbers are cited
  below, only the Act's own title/year/act-number/URL.

  - Company/commercial-entity law: 'Companies Act, 2004' -- NOT found on
    NamibLII's own listing pages this iteration checked (1-9); instead
    independently confirmed from a SEPARATE official source, the
    Government Gazette of the Republic of Namibia No. 7874 (8 August
    2022, Public Procurement Amendment Act, 2022, fetched directly as a
    PDF from cpbn.com.na/index/legal_framework), whose own definitions
    section reads verbatim: 'a co-shareholder of a company incorporated
    under Chapter 4 of the Companies Act, 2004 (Act No. 28 of 2004)'.
  - Labour law: 'Labour Act, 2007' (`namiblii.org/akn/na/act/2007/11`,
    NamibLII's own citation column: 'Act 11 of 2007'). NamibLII's SAME
    listing also separately names 'Labour General Regulations, 2008',
    'Code of Good Practice for labour inspectors, 2024' and 'Labour
    Directives relating to COVID-19' as related instruments -- this
    iteration cites only the primary Act here, the same discipline
    LSO's catalog uses when a listing surfaces multiple related
    instruments without confirming their exact relationship.
  - Tax law: 'Income Tax Act, 1981' (`namiblii.org/akn/na/act/1981/24`,
    NamibLII's own citation column: 'Act 24 of 1981'). This iteration
    did NOT find a Value-Added Tax Act entry anywhere across NamibLII's
    own 9-page legislation listing (an honest gap -- Namibia is widely
    understood to levy VAT, but this iteration will not cite an Act
    number/year for it without having independently read a listing or
    primary-source page that actually names one; extend this catalog
    if/when one is confirmed, do not guess the citation now).

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit. NAM's catalog has 3
  entries -- smaller than LSO's 4 because this iteration could not find
  a Value-Added Tax Act entry on NamibLII's own listing (an honest gap,
  see namespace docstring) and the Companies Act citation had to be
  independently sourced from a Government Gazette PDF rather than
  NamibLII's own listing (which does not surface it in the 9 pages this
  iteration checked)."
  {"NAM"
   [{:statute/id "nam.companies-act-2004"
     :statute/title "Companies Act, 2004"
     :statute/jurisdiction "NAM"
     :statute/kind :law
     :statute/law-number "Companies Act, 2004 (Act No. 28 of 2004) -- confirmed directly from the Government Gazette of the Republic of Namibia No. 7874 (8 August 2022), Public Procurement Amendment Act, 2022 (Act No. 3 of 2022), own text (fetched directly as a PDF, cpbn.com.na/index/legal_framework): 'a co-shareholder of a company incorporated under Chapter 4 of the Companies Act, 2004 (Act No. 28 of 2004)'. This iteration did not independently fetch the Companies Act's own primary statutory text (NamibLII's own listing pages did not surface this Act in the 9 pages checked, and its individual-document route is Cloudflare-gated in any case, see namespace docstring), so no section numbers are cited here beyond what the Gazette's own definitions section names ('Chapter 4')"
     :statute/url "https://www.cpbn.com.na/assets/docs/PP_Amended_Act.pdf"
     :statute/url-provenance :official-gazette-pdf-via-cpbn
     :statute/enacted-date "2004"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:corporate-governance}}
    {:statute/id "nam.labour-act-2007"
     :statute/title "Labour Act, 2007"
     :statute/jurisdiction "NAM"
     :statute/kind :law
     :statute/law-number "Labour Act, 2007 (Act 11 of 2007) -- title, year and Act number confirmed directly from the Namibia Legal Information Institute's (NamibLII) own legislation-listing table (namiblii.org/legislation/?page=5, plain server-rendered HTML fetched directly, HTTP 200); the Act's own detailed section text sits behind a Cloudflare JS-challenge this iteration could not read around -- see namespace docstring for the full access-gap discipline (confirmed universal via a deliberately-invalid control path returning an identical challenge)"
     :statute/url "https://namiblii.org/akn/na/act/2007/11/eng@2023-03-15"
     :statute/url-provenance :official-namiblii-org
     :statute/enacted-date "2007"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:labor}}
    {:statute/id "nam.income-tax-act-1981"
     :statute/title "Income Tax Act, 1981"
     :statute/jurisdiction "NAM"
     :statute/kind :law
     :statute/law-number "Income Tax Act, 1981 (Act 24 of 1981) -- title, year and Act number confirmed directly from NamibLII's own legislation-listing table (namiblii.org/legislation/?page=4, fetched directly). This iteration did not independently fetch the Act's own primary statutory text, only NamibLII's own citation of its title/year/number, so exact section numbers are not claimed here. No Value-Added Tax Act entry was found across NamibLII's own 9-page legislation listing -- an honest gap, not modeled in this catalog"
     :statute/url "https://namiblii.org/akn/na/act/1981/24/eng@2024-09-16"
     :statute/url-provenance :official-namiblii-org
     :statute/enacted-date "1981"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:tax}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-nam statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "NAM")) " NAM statute(s) seeded with an "
                 "official title/year/act-number/URL citation (section-level text "
                 "access-gapped by a Cloudflare JS-challenge on NamibLII's individual "
                 "document pages -- an honest gap, see namespace docstring). "
                 "Extend `statute.facts/catalog`, never fabricate a law-id or "
                 "URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :tax)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
