"""Reproducible TF-IDF retrieval and small, inspectable knowledge rules.

No learned ownership probability. No location/date hard rejection. No ID support.
"""
from __future__ import annotations

import re
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from .contract import GeneralEvidence

LEXICON = {
    'colour': ['black', 'white', 'blue', 'navy', 'red', 'green', 'yellow', 'brown', 'grey', 'gray', 'silver', 'gold', 'pink', 'purple', 'orange'],
    'brand': ['samsung', 'apple', 'iphone', 'lenovo', 'hp', 'dell', 'nike', 'adidas', 'jbl', 'sony'],
    'material': ['plastic', 'metal', 'metallic', 'steel', 'aluminium', 'aluminum', 'leather', 'fabric', 'wooden', 'glass'],
    'distinctive_features': ['cracked screen', 'cracked corner', 'bent arm', 'scratched lid', 'damaged zip', 'white soles', 'worn clasp'],
}


def extract_facts(report: GeneralEvidence) -> list[dict]:
    facts = []
    for source in ('item_name', 'description'):
        text = getattr(report, source)
        for attribute, terms in LEXICON.items():
            for term in terms:
                for match in re.finditer(r'\b' + re.escape(term) + r'\b', text, re.I):
                    # Conservative handling of explicit negation: don't assert the negated value.
                    if re.search(r'\b(?:not|no|without)\s+(?:a\s+)?$', text[max(0, match.start()-15):match.start()], re.I):
                        continue
                    value = {'gray': 'grey', 'metallic': 'metal', 'steel': 'metal', 'aluminium': 'metal', 'aluminum': 'metal'}.get(term, term)
                    facts.append(dict(attribute=attribute, value=value, source=source, start=match.start(), end=match.end(), text=match.group()))
        # Model extraction is deliberately explicit; no guessed model from a brand.
        for match in re.finditer(r'\bmodel\s*[:=]?\s+([a-z0-9][a-z0-9-]*)', text, re.I):
            facts.append(dict(attribute='model', value=match.group(1).lower(), source=source,
                              start=match.start(1), end=match.end(1), text=match.group(1)))
    return facts


def reason(query: GeneralEvidence, found: GeneralEvidence, similarity: float) -> dict:
    support, conflicts, unknowns = [], [], []
    score = similarity
    left, right = extract_facts(query), extract_facts(found)
    for attribute in (*LEXICON, 'model'):
        a = {f['value'] for f in left if f['attribute'] == attribute}
        b = {f['value'] for f in right if f['attribute'] == attribute}
        if not a or not b:
            unknowns.append(attribute)
        elif a & b:
            support.append(f'{attribute}: {", ".join(sorted(a & b))}')
            score += .025
        elif attribute == 'distinctive_features':
            unknowns.append('distinctive features not corroborated')
        else:
            conflicts.append(f'{attribute}: {", ".join(sorted(a))} versus {", ".join(sorted(b))}')
            score -= .12
    if query.category_id and found.category_id:
        if query.category_id == found.category_id:
            support.append('category agreement')
            score += .12
        elif 'accessories' in (query.category_id, found.category_id):
            unknowns.append('Accessories overlaps specific categories; staff should classify')
        else:
            conflicts.append('category labels differ; possible misclassification')
            score -= .04
        pair = {query.category_id, found.category_id}
        if pair in ({'metal_water_bottle', 'plastic_water_bottle'}, {'spectacles', 'spectacle_cases'}):
            conflicts.append('distinct item kind/material in catalog; inspect before proceeding')
            score -= .25
    else:
        unknowns.append('category')
    if query.location_id and found.location_id:
        if query.location_id == found.location_id:
            support.append('same reported location')
            score += .04
        else:
            conflicts.append('locations differ; item may have moved (not excluded)')
    else:
        unknowns.append('location')
    if query.event_date and found.event_date:
        if query.date_precision != 'exact' or found.date_precision != 'exact':
            unknowns.append('date precision; review chronology, do not infer exact timing')
        if found.event_date < query.event_date:
            conflicts.append('found date precedes last-seen date; staff review required')
        elif query.date_precision == found.date_precision == 'exact':
            support.append('reported chronology is compatible')
    else:
        unknowns.append('event_date')
    return dict(found_report_id=found.report_id, text_similarity=round(similarity, 6), ranking_score=round(score, 6),
                supporting_facts=support, conflicts=conflicts, unknowns=unknowns,
                evidence={'lost': left, 'found': right},
                next_staff_action='Review conflicts and privately verify ownership and custody before any handover.',
                requires_human_verification=True, ownership_approved=False)


class TextMatcher:
    def __init__(self, found_reports: list[GeneralEvidence]):
        if any(not isinstance(r, GeneralEvidence) or r.item_kind != 'general' for r in found_reports):
            raise ValueError('Only general reports may enter TF-IDF')
        self.reports = [r for r in found_reports if r.report_type == 'found']
        self.vectorizer = TfidfVectorizer(lowercase=True, strip_accents='unicode', ngram_range=(1, 2), stop_words='english')
        self.matrix = None
        if self.reports:
            try:
                self.matrix = self.vectorizer.fit_transform([r.item_name + ' ' + r.description for r in self.reports])
            except ValueError as error:
                if 'empty vocabulary' not in str(error):
                    raise

    def match(self, lost: GeneralEvidence, limit: int = 5, min_similarity: float = .08) -> list[dict]:
        if not isinstance(lost, GeneralEvidence) or lost.item_kind != 'general' or lost.report_type != 'lost':
            raise ValueError('Only lost general reports can query this matcher')
        if not 1 <= limit <= 5:
            raise ValueError('Return between one and five possible matches')
        if self.matrix is None:
            return []
        vector = self.vectorizer.transform([lost.item_name + ' ' + lost.description])
        similarities = cosine_similarity(vector, self.matrix)[0]
        candidates = [reason(lost, report, float(sim)) for report, sim in zip(self.reports, similarities)
                      if sim > 0 and sim >= min_similarity]
        return sorted(candidates, key=lambda r: (-r['ranking_score'], r['found_report_id'] or ''))[:limit]
