"""Internal restricted workflow design. NOT exposed as a public API endpoint.

Normalization is outer-whitespace trimming only. Case, punctuation and leading
zeros are significant. Never pass these values to NLP or logging.
"""
from dataclasses import dataclass, field
from .contract import CATALOG


@dataclass(frozen=True)
class PrivateId:
    id_type: str
    number: str = field(repr=False)

    def __post_init__(self):
        if self.id_type not in {r['id'] for r in CATALOG['id_types']}:
            raise ValueError('Unsupported ID type')
        if not isinstance(self.number, str) or not self.number.strip():
            raise ValueError('ID number must be a nonempty string')


def compare_ids(lost: PrivateId, found: PrivateId) -> dict:
    return {'exact_match': lost.id_type == found.id_type and lost.number.strip() == found.number.strip(),
            'requires_staff_verification': True, 'ownership_approved': False,
            'next_staff_action': 'Verify ownership privately before release.'}
