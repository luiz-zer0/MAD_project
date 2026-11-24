# Deliverable 3: ML-first presentation and app walkthrough

## Presentation focus

This is an AI-class presentation. Spend most of the 5:30 explaining the data, preparation, model choices, held-out results and what they do not prove. Use the Android app for a short supporting example of how ranked NLP suggestions reach an end user. The active worker uses TF-IDF; the Logistic Regression and hybrid results are offline benchmark comparisons.

## Data and model facts to present

- The user-provided Office transcription has 33 June–July category rows totalling 388 reported items and 24 August rows totalling 299. Its aggregate category counts are shown in `SemProject_MLEngine/notebooks/NLP_Final.ipynb`. These are quantities, not item-level reports or match labels. Verify the transcription against the Office source; the June–July small-umbrella disposal destination is blank.
- The separate controlled benchmark has 24 fictional found reports, 27 fictional lost queries, 192 constructed pair labels (24 positive and 168 negative), and 27 evaluation queries. The held-out test has eight matched queries and two no-match queries.
- Item groups stay within one split. Fit the text vocabulary on training data, choose settings on validation data, and use the test split only for final scores.
- The comparison covers keyword overlap, structured attributes, Logistic Regression, TF-IDF retrieval and a hybrid. The validator checks the data structure and split boundaries; it cannot prove that constructed labels represent physical-item truth.

| Method | Top-1 accuracy (matched queries) | Recall@5 | MRR | No-match false suggestions |
| --- | ---: | ---: | ---: | ---: |
| Keyword overlap | 0.250 (2/8) | 0.875 | 0.541 | 1/2 |
| Structured attributes | 1.000 (8/8) | 1.000 | 1.000 | 1/2 |
| Logistic Regression | 1.000 (8/8) | 1.000 | 1.000 | 1/2 |
| TF-IDF | 0.250 (2/8) | 0.500 | 0.391 | 2/2 |
| Hybrid | 1.000 (8/8) | 1.000 | 1.000 | 2/2 |

These are small constructed-fiction results, not real campus accuracy. Perfect attribute and hybrid scores reflect fields reused by the example generator. TF-IDF misses paraphrases and produces above-cutoff suggestions for both test no-match queries. Explain Top-1 as first-place accuracy, Recall@5 as retrieval within five candidates, MRR as a measure rewarding higher ranks, and false suggestions as no-match queries above a validation-selected cutoff. Similarity is not a probability or a release decision.

## Three-person run of show (5:30)

All three team members attend with cameras on throughout.

| Time | Presenter | Focus and screen cue |
| --- | --- | --- |
| 0:00–0:25 | Person 1 | State the research question: rank found-item candidates from lost-item text while staff retain the final decision. |
| 0:25–1:10 | Person 1 | Show the Office inventory counts and category totals. Explain that aggregate counts describe the problem, but cannot train or score item matching. |
| 1:10–1:55 | Person 2 | Show benchmark size, quality checks and group split. Explain train-only vocabulary fitting and validation/test separation. |
| 1:55–2:35 | Person 2 | Compare the Logistic Regression baseline with TF-IDF; explain distinctive word weights, unigrams/bigrams and cosine ranking. Name the active worker method. |
| 2:35–3:35 | Person 3 | Present the held-out table above and define the measures. Be clear that all labels are constructed fiction. |
| 3:35–4:20 | Person 3 | Interpret weak TF-IDF results, false suggestions, synthetic-data limits and fact-level explanation. Explain why no candidate is not proof of absence. |
| 4:20–5:05 | Person 2 | Briefly show a rehearsed fictional found report and a paraphrased lost query in the Android app; point out the ranked output and explanation. If time permits, vary the wording or show a blocked incomplete form. |
| 5:05–5:30 | Person 1 | Close with the need for verified item-level pairs and a rehearsed live app path; staff verify ownership and handover. |

### Short talk track

**Person 1:** “Can a lost-and-found system rank useful candidates from a short report? Our Office summaries show the type and volume of unclaimed items, but they contain aggregate counts only. We keep those counts separate from the item-level benchmark.”

**Person 2:** “We prepare fictional found reports and lost claims, validate the tables, and split by item group to reduce leakage. Logistic Regression is our pair-classification baseline. The connected worker uses TF-IDF to emphasize distinctive words and short phrases, then ranks candidate text by cosine similarity.”

**Person 3:** “The held-out scores compare five methods, but they are not campus performance. TF-IDF ranks two of eight matched queries first and produces two strong suggestions on two no-match queries. The structured and hybrid perfect scores are a limitation of the constructed examples. The app explanation shows supporting facts; staff still decide.”

For the app handoff, Person 2 signs in with the seeded Office demo account, opens a prepared fictional found report, then uses the seeded student account to submit a paraphrased lost report. Refresh after the worker runs, show the candidates and their explanation, and briefly demonstrate a valid wording variation or an incomplete form rejected by the app.

## Notebook walkthrough

Open `SemProject_MLEngine/notebooks/NLP_Final.ipynb` and move through:

1. Task and decision boundary.
2. Office aggregate sources, category totals and missing source field.
3. Fictional benchmark provenance, data validation and quality checks.
4. Grouped split and Logistic Regression baseline.
5. TF-IDF feature preparation and cosine retrieval.
6. Validation-based selection, held-out metrics and no-match false suggestions.
7. Explanation behavior and limitations.

## Rehearsal and evidence limits

- Start Firebase emulators, the API and worker using the MLEngine local setup guide; build/install the Android demo variant and confirm both seeded accounts can sign in.
- The Android debug build, unit tests and APK assembly passed on 24 November 2026. A device-level Android-to-Firebase-to-worker walkthrough has not been verified; rehearse it before claiming live integration.
- Use fictional records only. Do not present Office aggregate counts as physical item examples or training labels.
- If the connected app flow fails, say so plainly. Do not substitute notebook output or the local preview API for a successful Android demonstration.
- Follow the instructor's approved AI-use and attribution rules, and have the team verify the Office transcription and fictional labels before submission.
