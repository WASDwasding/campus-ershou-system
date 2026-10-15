# Campus Trade

iOS-first React Native (Expo) campus second-hand marketplace. Lake-blue visual language, student-ID verification, and **smart low-light product posting** with client-side recognition simulation.

## Features

- **Student-only auth** — Student ID + legal name matched against a mock university registry
- **Home** — Search, category chips, nearby (same-dorm) strip, proximity-sorted waterfall
- **Smart Post** — Camera → low-light enhance / denoise → category → red-box flaw marks → condition → auto-filled form (price only to publish)
- **Manual Post** — Library photos + full form
- **Product detail** — Image carousel with flaw overlays, dorm + campus credit, Contact / Reserve
- **Messages** — In-app chat; WeChat / phone / email patterns auto-blocked
- **Profile** — Postings, favorites, orders, credit center
- **Orders** — Reserve → meetup → complete → mutual rating → credit delta
- **API stubs** — `services/api.ts` reserved for backend; mock data in `data/mock.ts`

## Requirements

- Node.js 20+
- Expo Go on iPhone, or Xcode for simulator / device builds
- iOS only (no Android adaptation)

## Quick start

```bash
cd campus-trade
npm install
npx expo start --ios
```

### Demo login

| Student ID | Name |
|---|---|
| `202301001` | Alex Chen (Lakeview Hall F) |
| `202405099` | Taylor Wu (new registry user) |

## Project structure

```
app/                 Expo Router screens (tabs + stack)
components/          UI primitives
constants/theme.ts   Lake-blue design tokens (iOS HIG-inspired type scale)
context/             Auth + post draft state
data/mock.ts         Seed content
services/api.ts      Reserved REST-shaped API (mock)
services/vision.ts   Frontend smart-recognition pipeline
services/chatFilter.ts
types/
```

## Wiring a real backend

Replace mock bodies in `services/api.ts` with `fetch` / client calls. Point `runSmartRecognition` in `services/vision.ts` at `POST /api/vision/analyze` when the ML service is ready.

## Design

- Primary: `#4BA3C7` (lake blue)
- Light campus background `#F5FAFC`
- Fixed bottom tabs: Home · Post · Messages · Profile
- SF-style type scale via `Typography` tokens
