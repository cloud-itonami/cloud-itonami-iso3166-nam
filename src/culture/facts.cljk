(ns culture.facts
  "Country-level regional-culture catalog for Namibia (NAM) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"NAM"
   [{:culture/id "nam.dish.kapana"
     :culture/name "Kapana"
     :culture/country "NAM"
     :culture/kind :dish
     :culture/summary "Grilled beef street-food dish in Namibia, popular in Windhoek's Katutura township markets."
     :culture/url "https://en.wikipedia.org/wiki/Kapana"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nam.dish.vetkoek"
     :culture/name "Vetkoek"
     :culture/country "NAM"
     :culture/kind :dish
     :culture/summary "Traditional fried-dough bread, listed among indigenous dishes in the Namibian cuisine article."
     :culture/url "https://en.wikipedia.org/wiki/Namibian_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nam.dish.mopane-worm"
     :culture/name "Mopane worm"
     :culture/name-local "Omagungu"
     :culture/country "NAM"
     :culture/kind :dish
     :culture/summary "Edible caterpillar (Gonimbrasia belina) consumed and commercially traded in Namibia among other southern African countries, known locally as omagungu (Ovambo) or oshuungu (Oshikwanyama)."
     :culture/url "https://en.wikipedia.org/wiki/Mopane_worm"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nam.beverage.oshikundu"
     :culture/name "Oshikundu"
     :culture/country "NAM"
     :culture/kind :beverage
     :culture/summary "Traditional Namibian drink made from fermented millet flour, brans and malted sorghum flour mixed with tepid water, originating from the Aawambo people of northern Namibia."
     :culture/url "https://en.wikipedia.org/wiki/Oshikundu"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nam.beverage.windhoek-lager"
     :culture/name "Windhoek Lager"
     :culture/country "NAM"
     :culture/kind :beverage
     :culture/summary "Beer produced by Namibia Breweries Limited, founded in Windhoek, Namibia in 1920 and now owned by Heineken Beverages as of 2023."
     :culture/url "https://en.wikipedia.org/wiki/Windhoek_Lager"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nam.craft.herero-dress"
     :culture/name "Herero dress"
     :culture/name-local "Ohorokova"
     :culture/country "NAM"
     :culture/kind :craft
     :culture/summary "Distinctive Victorian-style floor-length gown (ohorokova) worn by Herero women in Namibia, with a horizontal horned headdress (otjikaiva) paying homage to cattle central to Herero life."
     :culture/url "https://en.wikipedia.org/wiki/Herero_people"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "nam.heritage.twyfelfontein"
     :culture/name "Twyfelfontein"
     :culture/country "NAM"
     :culture/kind :heritage
     :culture/summary "Ancient rock art site in Namibia's Kunene Region containing over 2,500 engravings created by hunter-gatherers and Khoikhoi herders over 6,000 years; became Namibia's first UNESCO World Heritage Site in 2007."
     :culture/url "https://en.wikipedia.org/wiki/Twyfelfontein"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-nam culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "NAM"))
                 " NAM entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
