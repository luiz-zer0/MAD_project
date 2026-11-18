"""Local synthetic prototype. No persistence, authentication or ID ingestion."""
from fastapi import FastAPI, HTTPException
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from pydantic import BaseModel, ConfigDict, Field
from .contract import CATALOG, GeneralReportInput, evidence_from_input
from .chala_fixtures import FIXTURES
from .text_matching import TextMatcher

app = FastAPI(title='Campus Lost and Found: text-first preview')
inventory = [r['report'] for r in FIXTURES if r['report'].report_type == 'found']
matcher = TextMatcher(inventory)


@app.exception_handler(RequestValidationError)
async def redact_validation_error(request, error):
    # Pydantic's default response echoes rejected inputs (including accidental ID numbers).
    return JSONResponse(status_code=422, content={'detail': 'Invalid general report contract. Check required fields, canonical IDs, ISO date and catalog version. Private ID fields are not accepted.'})


@app.get('/health')
def health():
    return {'status': 'ok', 'mode': 'synthetic_preview', 'persistence': False, 'notifications': False}


@app.get('/catalog')
def catalog():
    return CATALOG


@app.get('/reports')
def list_reports():
    return {'mode': 'synthetic_preview', 'reports': [
        r.model_dump(mode='json', include={'report_id', 'report_type', 'item_kind', 'item_name', 'category_id', 'location_id'})
        for r in inventory]}


@app.post('/reports')
def create_report(report: GeneralReportInput):
    raise HTTPException(status_code=501, detail='Submission unavailable: durable storage, authentication and idempotency are not implemented. No report was saved.')


class MatchRequest(BaseModel):
    model_config = ConfigDict(extra='forbid')
    report: GeneralReportInput
    limit: int = Field(default=5, ge=1, le=5)


@app.post('/matches')
def find_matches(request: MatchRequest):
    if request.report.report_type != 'lost':
        raise HTTPException(status_code=422, detail='Matching requires a lost general report')
    matches = matcher.match(evidence_from_input(request.report), request.limit)
    return {'mode': 'synthetic_preview', 'status': 'possible_matches' if matches else 'no_match',
            'score_meaning': 'text similarity and ranking evidence, not ownership probability',
            'matches': matches}

# No ID endpoint until restricted authentication, storage, redacted observability and staff authorization exist.
