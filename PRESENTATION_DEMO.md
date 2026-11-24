# Local Lost & Found demo

The connected Android app submits reports to the authenticated API in `SemProject_MLEngine`. The Firestore worker ranks active found reports with TF-IDF retrieval and knowledge agents. Results show ranked possible matches, lexical similarity and an explanation; staff verify ownership before release. The offline version 2 hybrid benchmark is not the deployed model.

Start the Firebase emulators, seed accounts, API and worker with `SemProject_MLEngine/scripts/start_local.ps1` as described in that repository's `FIREBASE_SETUP.md`. Then build in this repository:

```powershell
.\gradlew.bat -PdemoFirebase=true testDebugUnitTest assembleDebug
```

The flag forces the fictional demo project and `http://10.0.2.2:8000` for an Android emulator, even when local live Firebase settings exist. The debug APK is `app/build/outputs/apk/debug/app-debug.apk`. Demo login uses seeded `demo-office@strathmore.edu` and `demo-student@strathmore.edu` with `LocalDemo123!`. The flag does not affect release builds. `10.0.2.2` is specific to an Android emulator; a physical phone needs reachable development hosts.

On 23 November 2026, normal and demo debug builds and unit tests passed. The service HTTP/Firebase/worker smoke passed separately. No phone was connected, and the configured emulator could not start because C: lacked free disk space. Complete the app sign-in → found report → lost report → worker → ranked results walkthrough on a device before claiming a mobile end-to-end demonstration.

## Live algorithm walkthrough

Use the emulator workflow above so the final Android app sends reports to the authenticated MLEngine API and the worker. Do not substitute a notebook function call or the local preview endpoint for the app demonstration.

1. Sign in as the seeded office account, submit a fictional found item with a clear category, date, location and description, then confirm it appears in Reports.
2. Sign in as the seeded student account and submit a lost report for that item using a rephrased description. Refresh the report after the worker runs and open Possible matches.
3. Show the rank, text-similarity value and fact explanation. Explain that TF-IDF matches words and short phrases, while the knowledge agents separately record category, location and date agreement, conflict or missing information. Similarity is not a probability and the app never authorizes release.
4. Change the wording to a valid variation and compare the suggestions. Then try a distinct item such as a violet telescope. If the app shows no text candidates, explain that the inventory may be incomplete; if it shows a weak candidate, explain the lexical false-suggestion risk. Do not treat either output as proof that an item is or is not in the office.
5. Demonstrate a rejected form by leaving the item name blank or entering fewer than three description characters. The submit action should remain unavailable. The date picker also prevents future dates; it does not accept arbitrary invalid calendar dates.

Use invented reports only. The local seed accounts and Firebase emulators do not contain institutional reports. The app's `PossibleMatchesScreen` preview is sample-only; enter through the connected authenticated workflow for the live demonstration.

## Three-person quickfire plan (5:30)

All three presenters attend the session with cameras on throughout, as required by the assessment brief.

| Time | Presenter | Segment |
| --- | --- | --- |
| 0:00-0:45 | Person 1 | State the lost-and-found problem, who uses the app, and that staff make ownership decisions. Explain that the demo uses fictional records. |
| 0:45-2:45 | Person 2 | Drive the Android app: office submits a found item, student submits a paraphrased lost report, then refreshes and opens ranked suggestions. |
| 2:45-4:00 | Person 3 | Explain the score and fact trace. Show the wording variation and either a no-candidate result or a weak lexical suggestion; demonstrate the blocked invalid form. |
| 4:00-5:00 | Person 1 | Show `SemProject_MLEngine/notebooks/NLP_Final.ipynb`, the Logistic Regression baseline and held-out Top-1 accuracy, Recall@5, MRR and no-match false suggestions. State that all benchmark labels are constructed fiction. |
| 5:00-5:30 | Person 3, with Person 2 ready for questions | State the live-demo limits, confirm staff review before handover, and close. |

Before presenting, agree on the sample item, make sure the local emulators/API/worker are healthy, install the demo build, and rehearse the account handoffs. If the connected app path fails, disclose that clearly; do not present notebook output as evidence of a successful mobile run.
