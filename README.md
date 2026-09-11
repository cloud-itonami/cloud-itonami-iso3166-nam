# cloud-itonami-iso3166-nam

Open ISO 3166 Blueprint for **NAM**: Namibia.

This repository designs a forkable OSS business for an independent
public-sector market-entry consultant: an already-incorporated operator
(e.g. a `cloud-itonami-cofog-{code}`, `cloud-itonami-isco-{code}`,
`cloud-itonami-unspsc-{segment}` or `cloud-itonami-{ISIC}` blueprint
fork) gets a Compliance Advisor + independent **Market-Entry Compliance
Governor** to navigate public-procurement registration, local business/
tax registration, and local-content rules in Namibia, so the operator
can win and service a government contract without hiring a full in-house
compliance department.

## No robotics premise — digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) — the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set — it always requires human sign-off (mirrors
`cloud-itonami-M6910`'s `filing-submit-never-auto-at-any-phase`
invariant).

## What this is NOT

- **Not the government of Namibia.** See
  [`docs/business-model.md`](docs/business-model.md) for the boundary with
  `com-etzhayyim-ooyake` (read-only civic mirror), `matsurigoto` (sovereign
  statecraft), `com-etzhayyim-toritsugi` (individual citizen concierge),
  `legal-entity.etzhayyim.com` (read-only data aggregation), and
  `cloud-itonami-M6910` (company incorporation — a different regulatory
  phase this blueprint assumes is already complete).
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Namibian-licensed counsel
  or a registered agent where the law requires licensed representation.

## Capability layer

Resolves via [`kotoba-lang/iso3166`](https://github.com/kotoba-lang/iso3166)
(ISO 3166 `NAM`). Required capabilities:

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as every `cloud-itonami-iso3166-*` sibling in this fleet:

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the Central
  Procurement Board of Namibia (CPBN, `cpbn.com.na` -- not `cpbn.na`,
  which does not resolve), established under the Public Procurement
  Act, 2015 (Act No. 15 of 2015, as amended by the Public Procurement
  Amendment Act, 2022, Act No. 3 of 2022); the Business and
  Intellectual Property Authority (BIPA, `bipa.na`, established under
  the BIPA Act, 2016, Act No. 8 of 2016) for company registration, with
  its own published fee schedule (form CM2, Companies Act 2004 (Act
  No. 28 of 2004) Section 68(1), current fee N$100.00). `governor.cljc`'s
  flagship check independently recomputes whether an engagement's own
  claimed CPBN Board-routing determination matches CPBN's own published
  Public Procurement Regulations (Government Gazette No. 6255, 1 March
  2017) Annexure 1 threshold matrix -- a 3-category x 4-contract-type
  (goods/works/consultancy services/non-consultancy services) lookup of
  12 distinct N$ ceilings, above which the Board (not the public entity
  buyer itself) must conduct the bidding process. This is a check shape
  genuinely different from every other iso3166 sibling's (see the
  namespace docstrings for the full research trail and honestly-narrowed
  scope, including facts this iteration could NOT verify: the Namibia
  Revenue Agency (NamRA) and the general government portal (`gov.na` /
  `mof.gov.na`) were all unreachable this iteration -- a genuine
  connection-level failure, not a bot-challenge, distinct from every
  other Namibian host tried in the same session -- so no TIN/tax-
  registration authority is cited; the Namibia Investment Promotion and
  Development Board (NIPDB) could not be reached at any candidate
  domain and a web search to find its real domain was unavailable this
  session, so no NIPDB mechanism is modeled; and the Public Procurement
  Act's own local-content/"local supplier" preference mechanism
  (sections 70/71) is confirmed to EXIST via the 2022 Amendment Act's
  own definitions but its substantive numeric criteria were not
  independently read, so it is not modeled as a spec-basis-backed
  check).
- `src/statute/facts.cljk` -- general-law catalog: the Companies Act,
  2004 (Act No. 28 of 2004, confirmed via the Government Gazette No.
  7874 Amendment Act text rather than NamibLII's own listing, which did
  not surface it); the Labour Act, 2007 (Act 11 of 2007); and the
  Income Tax Act, 1981 (Act 24 of 1981). Titles/years/Act-numbers are
  confirmed directly from the Namibia Legal Information Institute's
  (NamibLII, `namiblii.org`) own paginated legislation-listing pages
  (checked in full, pages 1 through 9; page 10 returns HTTP 404) except
  the Companies Act, whose citation came from the Government Gazette
  PDF instead. No Value-Added Tax Act entry was found anywhere across
  NamibLII's own listing -- an honest gap, not modeled. Detailed
  section text for every Act could not be independently confirmed this
  iteration (see the namespace docstring for the Cloudflare
  individual-document access gap, confirmed universal via a
  deliberately-invalid control-path test).

Every citation is curl/WebFetch-verified against an official source
(cpbn.com.na, bipa.na, namiblii.org, bon.com.na, sacu.int, and two
actual Government Gazette PDFs of the Republic of Namibia downloaded
and read directly: No. 7874 of 8 August 2022 and No. 6255 of 1 March
2017). NamibLII's own legislation-LISTING pages loaded as ordinary
server-rendered HTML, but EVERY individual document page (where an
Act's own detailed section text would be) returned a Cloudflare
'Just a moment...' JS-challenge (HTTP 403) to `curl` this iteration --
confirmed universal (not evidence of a specific document's
non-existence) by also fetching a deliberately invalid document path,
which returned an identical challenge. See `marketentry.facts`'s and
`statute.facts`'s docstrings for the full disclosure of what this
iteration could and could not independently confirm.

Namibia is a member of the Southern African Customs Union (SACU,
confirmed directly from sacu.int's own member-state navigation, which
lists exactly five member-state pages: Botswana, Eswatini, Lesotho,
Namibia, South Africa) and its currency, the Namibia Dollar, is pegged
to the South African rand -- confirmed directly and currently from the
Bank of Namibia's own homepage (`bon.com.na`), a June 2026 Monetary
Policy Committee news item reading verbatim: '...with a view to
continue safeguarding the peg between the Namibia Dollar and the South
African Rand, the MPC decided to raise the Repo rate to 6.75 percent.'

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Namibia:

- `src/culture/facts.cljk` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
