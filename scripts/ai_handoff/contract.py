"""Versioned general-report contract; IDs use an independent restricted workflow."""
from __future__ import annotations

import json
import re
from datetime import date
from pathlib import Path
from typing import Literal

from pydantic import BaseModel, ConfigDict, Field, field_validator

CATALOG = json.loads(Path(__file__).with_name('chala_catalog.json').read_text(encoding='utf-8'))


def resolve(group: str, value: str | None) -> str | None:
    """Exact project ID/source/display aliases only. Never infer campus geography."""
    return next((row['id'] for row in CATALOG[group]
                 if value in (row['id'], row['source_label'], row['display_label'])), None)


class GeneralReportInput(BaseModel):
    model_config = ConfigDict(extra='forbid', str_strip_whitespace=True)
    report_type: Literal['lost', 'found']
    item_kind: Literal['general']
    item_name: str = Field(min_length=1, max_length=160)
    description: str = Field(min_length=1, max_length=4000)
    category_id: str
    location_id: str
    event_date: date
    catalog_version: str
    location_detail: str | None = Field(default=None, max_length=500)
    date_precision: Literal['exact', 'approximate', 'unknown'] = 'unknown'
    photo_reference: str | None = None
    colour: str | None = None
    brand: str | None = None
    model: str | None = None

    @field_validator('category_id', 'location_id')
    @classmethod
    def canonical_id(cls, value, info):
        group = 'categories' if info.field_name == 'category_id' else 'locations'
        if value not in {r['id'] for r in CATALOG[group]}:
            raise ValueError('Unknown catalog ID')
        return value

    @field_validator('catalog_version')
    @classmethod
    def version(cls, value):
        if value != CATALOG['version']:
            raise ValueError('Unsupported catalog version')
        return value

    @field_validator('event_date', mode='before')
    @classmethod
    def iso_date(cls, value):
        if not isinstance(value, str) or not re.fullmatch(r'\d{4}-\d{2}-\d{2}', value):
            raise ValueError('Use an ISO YYYY-MM-DD date')
        return value


class GeneralEvidence(BaseModel):
    """Explicit permissive adapter target, not the new-submission validator."""
    model_config = ConfigDict(extra='forbid')
    report_id: str | None = None
    report_type: Literal['lost', 'found']
    item_kind: Literal['general'] = 'general'
    item_name: str = ''
    description: str = ''
    category_id: str | None = None
    location_id: str | None = None
    event_date: date | None = None
    date_precision: Literal['exact', 'approximate', 'unknown'] = 'unknown'
    catalog_version: str | None = None
    legacy_values: dict[str, str] = Field(default_factory=dict)


def adapt_legacy(raw: dict) -> GeneralEvidence:
    if raw.get('item_kind', 'general') != 'general' or 'id_number' in raw:
        raise ValueError('ID reports cannot enter general NLP')
    legacy = {}
    values = {}
    for field, old, group in [('category_id', 'category', 'categories'), ('location_id', 'location', 'locations')]:
        original = raw.get(field) or raw.get(old)
        values[field] = resolve(group, original)
        if original and values[field] is None:
            legacy[old] = str(original)
    event = raw.get('event_date')
    if event:
        try:
            if not isinstance(event, str) or not re.fullmatch(r'\d{4}-\d{2}-\d{2}', event):
                raise ValueError()
            event = date.fromisoformat(event)
        except ValueError:
            legacy['event_date'] = str(event)
            event = None
    return GeneralEvidence(report_id=raw.get('report_id'), report_type=raw['report_type'],
        item_name=raw.get('item_name') or '', description=raw.get('description') or '',
        event_date=event, date_precision=raw.get('date_precision', 'unknown'),
        catalog_version=raw.get('catalog_version'), legacy_values=legacy, **values)


def evidence_from_input(report: GeneralReportInput) -> GeneralEvidence:
    # Only name + description enter NLP; optional claims remain separate from extracted evidence.
    return GeneralEvidence(**report.model_dump(include=set(GeneralEvidence.model_fields) - {'legacy_values'}))
