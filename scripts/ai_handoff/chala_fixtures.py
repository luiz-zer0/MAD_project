"""Entirely synthetic engineering fixtures; group identifies the fictional physical item.

Development groups are disjoint from held-out groups. Do not tune on held-out
metrics or interpret this tiny handcrafted set as real-campus accuracy.
"""
from .contract import CATALOG, GeneralEvidence


def row(key, group, split, report_type, name, description, category, location, day='2026-11-10', precision='exact'):
    return {'group': group, 'split': split, 'report': GeneralEvidence(
        report_id=key, report_type=report_type, item_name=name, description=description,
        category_id=category, location_id=location, event_date=day, date_precision=precision,
        catalog_version=CATALOG['version'])}


FIXTURES = [
    row('dev-l1', 'dev-keys', 'development', 'lost', 'Three keys', 'Round ring with blue tag', 'keys', 'msb'),
    row('dev-f1', 'dev-keys', 'development', 'found', 'Three keys', 'Blue tag on round ring', 'keys', 'msb_1'),
    row('test-l1', 'test-metal', 'held_out', 'lost', 'Blue metal water bottle', 'Scratched lid and metal body', 'metal_water_bottle', 'msb'),
    row('test-f1', 'test-metal', 'held_out', 'found', 'Blue metal water bottle', 'Metal body with scratched lid', 'metal_water_bottle', 'cafeteria'),
    row('test-f2', 'test-plastic', 'held_out', 'found', 'Blue plastic water bottle', 'Plastic body with scratched lid', 'plastic_water_bottle', 'msb'),
    row('test-l3', 'test-glasses', 'held_out', 'lost', 'Black spectacles', 'Rectangular glasses with bent arm', 'spectacles', 'main_auditorium', precision='approximate'),
    row('test-f3', 'test-glasses', 'held_out', 'found', 'Black spectacles', 'Rectangular glasses with bent arm', 'accessories', 'microsoft_auditorium', day='2026-11-09'),
    row('test-f4', 'test-case', 'held_out', 'found', 'Black spectacle case', 'Rectangular glasses case with worn clasp', 'spectacle_cases', 'main_auditorium'),
    row('test-l5', 'test-shoes', 'held_out', 'lost', 'Red trainers', 'Nike shoes with white soles', 'shoes', 'sports_complex'),
    row('test-f5', 'test-shoes', 'held_out', 'found', 'Red trainers', 'Nike shoes with white soles', 'shoes', 'admin_block'),
    row('test-l6', 'test-no-match', 'held_out', 'lost', 'Violin', 'Bow and rosin', 'accessories', 'aberdere_lab'),
]
