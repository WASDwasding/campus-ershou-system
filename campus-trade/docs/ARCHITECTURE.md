# Campus Trade — Engineering Notes

课程小程序在 `../miniprogram/`，接口见仓库 `doc/base-API.md` v1.6.1。学号注册登录保留，并增加微信登录。本文件只记录同目录 Expo 原型里的前端试验，不作为对接约定。



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
