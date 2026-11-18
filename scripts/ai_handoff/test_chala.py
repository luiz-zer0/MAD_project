import asyncio
import json
import pytest
from pydantic import ValidationError
from matching_system.contract import CATALOG, GeneralReportInput, adapt_legacy, resolve
from matching_system.chala_fixtures import FIXTURES
from matching_system.text_matching import TextMatcher, extract_facts
from matching_system.id_matching import PrivateId, compare_ids
from matching_system.evaluate_chala import evaluate
from matching_system.api import create_report, find_matches, MatchRequest, redact_validation_error
from fastapi import HTTPException


def payload():
    return dict(report_type='lost', item_kind='general', item_name='Blue metal bottle', description='Scratched lid',
                category_id='metal_water_bottle', location_id='msb', event_date='2026-11-10', catalog_version=CATALOG['version'])


def fixture(key):
    return next(r['report'] for r in FIXTURES if r['report'].report_id == key)


def test_catalog():
    for group, count in [('categories', 18), ('locations', 12), ('id_types', 3)]:
        assert len(CATALOG[group]) == len({r['id'] for r in CATALOG[group]}) == count
        for row in CATALOG[group]:
            assert resolve(group, row['source_label']) == resolve(group, row['display_label']) == row['id']
    assert resolve('locations', 'MSB') != resolve('locations', 'MSB 1')
    assert resolve('locations', 'Microsoft Auditorium') != resolve('locations', 'Main Auditorium')
    assert resolve('locations', 'Aberdere lab') == 'aberdere_lab'


@pytest.mark.parametrize('field', ['item_name', 'description', 'category_id', 'location_id', 'event_date', 'catalog_version'])
def test_new_submission_requires_fields(field):
    data = payload(); data[field] = ''
    with pytest.raises(ValidationError): GeneralReportInput(**data)


def test_contract_and_unknown_legacy():
    assert GeneralReportInput(**payload()).photo_reference is None
    for location in ['Library', 'Student Centre', 'Main Gate', 'Auditorium', 'Business School']:
        report = adapt_legacy(dict(report_type='lost', description='old text', location=location, category='Other'))
        assert report.location_id is None and report.category_id is None and report.event_date is None
        assert report.legacy_values['location'] == location and report.item_name == ''
    with pytest.raises(ValidationError): GeneralReportInput(**{**payload(), 'event_date': '2026-02-30'})
    with pytest.raises(ValidationError): GeneralReportInput(**{**payload(), 'report_id': 'client-assigned'})
    with pytest.raises(ValidationError): GeneralReportInput(**{**payload(), 'private_identifying_clues': 'secret'})


def test_cross_location_material_and_fallback():
    results = TextMatcher([fixture('test-f1'), fixture('test-f2')]).match(fixture('test-l1'))
    assert results[0]['found_report_id'] == 'test-f1'
    assert any('locations differ' in c for c in results[0]['conflicts'])
    assert 'brand' in results[0]['unknowns']
    assert any('material' in c for c in results[1]['conflicts'])
    assert all(not r['ownership_approved'] for r in results)


def test_glasses_cases_accessories_and_uncertain_date():
    results = TextMatcher([fixture('test-f3'), fixture('test-f4')]).match(fixture('test-l3'))
    assert results[0]['found_report_id'] == 'test-f3'
    assert any('Accessories' in u for u in results[0]['unknowns'])
    assert any('date precision' in u for u in results[0]['unknowns'])
    assert any('precedes' in c for c in results[0]['conflicts'])
    assert any('distinct item kind' in c for c in results[1]['conflicts'])


def test_provenance_no_inferred_brand_model_and_no_match():
    report = fixture('test-l1')
    for fact in extract_facts(report):
        assert getattr(report, fact['source'])[fact['start']:fact['end']] == fact['text']
    assert not any(f['attribute'] in ('brand', 'model') for f in extract_facts(report))
    assert TextMatcher([fixture('test-f1')]).match(fixture('test-l6')) == []
    assert TextMatcher([]).match(report) == []


def test_id_exact_and_redaction():
    a = PrivateId('student_id', ' 00001234 ')
    assert compare_ids(a, PrivateId('student_id', '00001234'))['exact_match']
    for b in [PrivateId('staff_id', '00001234'), PrivateId('student_id', '1234'), PrivateId('student_id', '00005678')]:
        assert not compare_ids(a, b)['exact_match']
    assert '00001234' not in repr(a) + json.dumps(compare_ids(a, a))
    with pytest.raises(ValueError): adapt_legacy(dict(report_type='lost', item_kind='id', id_number='00001234'))
    with pytest.raises(ValidationError): GeneralReportInput(**{**payload(), 'id_number': '00001234'})
    response = asyncio.run(redact_validation_error(None, None))
    assert '00001234' not in response.body.decode()


def test_api_does_not_acknowledge_submission():
    with pytest.raises(HTTPException) as error: create_report(GeneralReportInput(**payload()))
    assert error.value.status_code == 501
    response = find_matches(MatchRequest(report=GeneralReportInput(**payload())))
    assert response['mode'] == 'synthetic_preview' and len(response['matches']) <= 5


def test_grouped_evaluation():
    metrics = evaluate()
    assert metrics['labelled_queries'] == 3
    assert 0 <= metrics['recall_at_5'] <= 1 and 0 <= metrics['mrr'] <= 1
    assert metrics['no_match_abstention_rate'] == 1
