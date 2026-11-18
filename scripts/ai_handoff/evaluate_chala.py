"""Run: python -m matching_system.evaluate_chala (no historical result writes)."""
import json
from .chala_fixtures import FIXTURES
from .text_matching import TextMatcher


def evaluate():
    development = {r['group'] for r in FIXTURES if r['split'] == 'development'}
    held_out = [r for r in FIXTURES if r['split'] == 'held_out']
    assert development.isdisjoint({r['group'] for r in held_out})
    inventory = [r for r in held_out if r['report'].report_type == 'found']
    matcher = TextMatcher([r['report'] for r in inventory])
    recalls, reciprocal_ranks, no_match = [], [], []
    for query in held_out:
        if query['report'].report_type != 'lost':
            continue
        relevant = {r['report'].report_id for r in inventory if r['group'] == query['group']}
        ranked = [r['found_report_id'] for r in matcher.match(query['report'])]
        if not relevant:
            no_match.append(not ranked)
            continue
        recalls.append(len(relevant & set(ranked)) / len(relevant))
        reciprocal_ranks.append(next((1 / rank for rank, key in enumerate(ranked, 1) if key in relevant), 0))
    return {'fixture_kind': 'synthetic engineering checks; not campus accuracy',
            'group_split': 'development and held_out physical-item groups disjoint',
            'tfidf_fit': 'held-out found inventory only; no lost query fitting or label training',
            'threshold': 0.08, 'labelled_queries': len(recalls), 'no_match_queries': len(no_match),
            'recall_at_5': sum(recalls) / len(recalls), 'mrr': sum(reciprocal_ranks) / len(reciprocal_ranks),
            'no_match_abstention_rate': sum(no_match) / len(no_match)}


if __name__ == '__main__':
    print(json.dumps(evaluate(), indent=2))
