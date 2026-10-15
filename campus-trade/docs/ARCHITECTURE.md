# Campus Trade — Engineering Notes

## Smart recognition pipeline (frontend)

`services/vision.ts` orchestrates the demo ML UX:

1. Low-light enhancement via `expo-image-manipulator` resize/compress (stand-in for ISP tone mapping)
2. Denoise step (timed progress UI)
3. Category classification (seeded heuristic from image URI)
4. Flaw boxes in relative coordinates `x,y,width,height ∈ [0,1]`
5. Condition grade from flaw count

`FlawOverlay` maps those boxes onto carousel / review images in red.

## Proximity sort

`api.dormDistance(a,b)` → `0` same building, else 1–5. Home feed sorts by this then recency.

## Chat filter

`services/chatFilter.ts` strips phone / WeChat / QQ / email before messages leave the client.
