# 小程序市集分类图标 · Apple HI 轻三维柔光风升级提示词（交 DSH 出图）

> **给 DSH 的一句话说明：**
> 现有的 8 个分类图标是「深色粗描边 + 纯色平涂」的 2D 扁平贴纸，放在小程序浅雾蓝页面（`#F3F8FC`）上显得太扁平、缺少实体按键的立体感与点击欲。本次需要将这 8 个图标升级为 **Apple iOS 风格（轻三维柔光微立体 + 磨砂毛玻璃点缀）**，同时保留小程序清新的「冰蓝 / 暖奶油 / 蜜桃粉 / 薄荷青」四色交错体系。

---

## 一、交付文件清单与硬性技术规格

请逐一生成 **8 张独立 PNG 图片**（不要拼在一张大图里），规格如下：

1. **画布尺寸**：推荐 **`1024 × 1024 px`**（或 `512 × 512 px`），正方形 1:1。
2. **透明通道（关键）**：**RGBA 透明背景**。画面是一个标准的 **Apple 连续曲率超椭圆圆角正方形（Squircle，圆角半径约为边长的 24%–26%）**，圆角底座占画布中心约 `92%`（或满画幅圆角正方形），**圆角以外的四角区域必须完全透明**（不要带白底、灰底或假透明棋盘格，也不要在画布边缘留下被一刀切断的外部黑影）。
3. **文件命名**（与小程序 `campus-trade/miniprogram/images/cats/` 一一对应）：
   - `books.png` —— 教材书籍（上排 1 · 冰蓝微渐变底座）
   - `digital.png` —— 数码电子（上排 2 · 暖奶油微渐变底座）
   - `appliance.png` —— 宿舍电器（上排 3 · 蜜桃粉微渐变底座）
   - `daily.png` —— 生活日用（上排 4 · 薄荷青微渐变底座）
   - `sports.png` —— 运动户外（下排 1 · 暖奶油微渐变底座）
   - `clothing.png` —— 服饰鞋包（下排 2 · 蜜桃粉微渐变底座）
   - `exam.png` —— 考研考证（下排 3 · 薄荷青微渐变底座）
   - `other.png` —— 其他（下排 4 · 冰蓝微渐变底座）

---

## 二、Apple HI 视觉统一准则（保证 8 个图标像原生一套）

1. **彻底去掉深色描边（Zero Outlines）**：**绝对不要任何黑色、深灰色或墨色轮廓线**。物体的立体边界全部靠柔和光影、圆润大倒角（Smooth Fillet）和前后层叠遮蔽来塑造。
2. **统一自上而下柔光（Top-Down Global Lighting）**：全局光源位于**正上方偏左 15°**。圆角正方形底座采用「顶部明亮清透 → 底部柔和偏深」的线性微渐变，且底座上边缘带有一道极细腻的半透明白色受光内高光边（Inner Specular Rim）。
3. **双材质轻拟物配方（温润哑光实体 + 半透明磨砂毛玻璃）**：主体采用细腻温润的哑光轻 3D 材质（类似 Apple 配件的亲肤哑光树脂/陶瓷质感，拒绝油腻塑料反光），并在局部小部件上搭配**半透明磨砂毛玻璃（Frosted Glassmorphism）**，营造精致通透的果冻层叠感。
4. **同色系柔和接触阴影（Tinted Contact Shadow）**：内部 3D 物体悬浮或轻落在底座上，向底座投射**与底座同色系的柔和弥散阴影**（不要脏黑阴影）。内部主体占底座宽度的 **60%–65%**，饱满立体、中心稳重。
5. **四组微渐变色板（拉开与 `#F3F8FC` 页面底色的层次，上下左右交错不撞色）**：
   - **冰蓝渐变底座**（`books`、`other`）：顶部 `#DFF1FD` → 底部 `#B2D8F4`，主体配奶白 `#FFFFFF` 与海盐亮蓝 `#4A9BE8`。
   - **暖奶油渐变底座**（`digital`、`sports`）：顶部 `#FFF6DC` → 底部 `#F7DC88`，主体配奶白 `#FFFFFF`、琥珀暖金 `#F3AC3E` 与晴空蓝 `#5AA9E6`。
   - **蜜桃粉渐变底座**（`appliance`、`clothing`）：顶部 `#FFE7EF` → 底部 `#F5B7CA`，主体配奶白 `#FFFFFF`、珊瑚粉 `#EB7396` 与柔雾蓝 `#6EB5E5`。
   - **薄荷青渐变底座**（`daily`、`exam`）：顶部 `#E2F7EE` → 底部 `#B2E3CC`，主体配奶白 `#FFFFFF`、青玉绿 `#3FB68B` 与晴空蓝 `#5AA9E6`。

---

## 三、8 个分类图标独立提示词（一次贴一条，已内置完整风格约束）

> 每条均提供 **中文版**（适合即梦 / Seedream / GPT-4o / Banana 等中文理解强的模型）和 **英文版**（适合 Midjourney / Flux 等英文模型），二选一直接复制即可。

### 1. 教材书籍（`books.png` · 上排第 1 个 · 冰蓝微渐变）

**中文提示词：**
```text
Apple iOS App 图标设计风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是一个标准的苹果超椭圆圆角正方形图标底座（Squircle，圆角半径约占边长 25%），四角以外区域完全透明（PNG 透明背景）。底座采用自上而下的柔和冰蓝色线性渐变（顶部清透冰蓝 #DFF1FD 过渡到底部晴空柔蓝 #B2D8F4），底座顶部边缘有一圈极细腻的白色受光内高光。底座正中微斜向（约 15 度立体微倾角）悬浮着一本厚实圆润、微微翻开的精装立体书本，约占底座宽度的 62%：海盐亮蓝渐变硬壳封面（#4A9BE8），温润奶白色哑光书页展现出细腻的层叠厚度与圆角转折，书页中间夹着一枚向上微露的半透明磨砂毛玻璃蓝色书签，书签边缘泛着清透的微光高光。正上方柔和棚拍顶光，书本向冰蓝底座投射柔和的浅蓝色弥散接触阴影。完全无黑色描边，无轮廓线，无文字，无字母，无水印，材质温润细腻有果冻按键般的点击欲，C4D Octane 柔和全局光照渲染，1024x1024 高清独立图标。
```

**English Prompt (Optional):**
```text
Apple iOS app icon style, soft 3D tactile render with frosted glassmorphism, single centered continuous-curve squircle icon container on transparent PNG background. Soft top-down linear gradient sky-blue squircle base (#DFF1FD at top to #B2D8F4 at bottom) with a subtle white inner rim highlight along the top edge. Inside centered (~62% scale), a plump, smoothly rounded 3D hardcover open book tilted at a gentle 15-degree angle: vibrant sea-salt blue matte cover (#4A9BE8), thick layered creamy-white matte pages with soft ambient occlusion, and a translucent frosted-glass light-blue bookmark peeking out from the top pages catching soft specular rim light. Soft top-down studio lighting, soft tinted blue contact shadow cast onto the squircle base. Zero black outlines, no strokes, no text, no watermark, clean minimalist Apple HIG aesthetic, Blender Cycles / Octane 3D UI icon, 1024x1024.
```

---

### 2. 数码电子（`digital.png` · 上排第 2 个 · 暖奶油微渐变）

**中文提示词：**
```text
Apple iOS App 图标设计风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是一个标准的苹果超椭圆圆角正方形图标底座（Squircle，圆角半径约占边长 25%），四角以外区域完全透明（PNG 透明背景）。底座采用自上而下的暖阳奶油色线性渐变（顶部柔光奶白 #FFF6DC 过渡到底部暖杏奶油 #F7DC88），底座顶部边缘有一圈极细腻的白色受光内高光。底座正中微侧倾（约 15 度立体视角）悬浮着一部圆润精致的现代全面屏智能手机，约占底座宽度的 60%：奶白银色哑光圆角金属机身，有一定立体厚度；清透的浅雾蓝渐变玻璃屏幕（#6EB5E5 到 #A8D8F8），屏幕上方轻巧浮着一层圆润的半透明白色磨砂毛玻璃小横条卡片（带有细腻玻璃高光边，呈现立体悬浮层叠感）。正上方柔和棚拍顶光，手机向奶油底座投射柔和的暖琥珀色弥散接触阴影。完全无黑色描边，无轮廓线，屏幕内无杂乱文字或图标，无水印，材质温润通透极具苹果原生质感，1024x1024 高清独立图标。
```

**English Prompt (Optional):**
```text
Apple iOS app icon style, soft 3D tactile render with frosted glassmorphism, single centered continuous-curve squircle icon container on transparent PNG background. Soft top-down linear gradient warm cream squircle base (#FFF6DC at top to #F7DC88 at bottom) with a subtle white inner rim highlight. Inside centered (~60% scale), a sleek, plump 3D rounded bezel-less smartphone gently angled at 15 degrees with visible smooth silver-white matte body thickness; its glossy screen features a soft sky-blue gradient (#6EB5E5 to #A8D8F8) with a tiny floating translucent white frosted-glass pill card hovering slightly above the screen surface casting a soft layer shadow. Soft top-down studio illumination, warm amber-tinted soft drop shadow on the cream squircle base. Zero black outlines, no strokes, no text or numbers, clean minimalist Apple HIG aesthetic, 1024x1024.
```

---

### 3. 宿舍电器（`appliance.png` · 上排第 3 个 · 蜜桃粉微渐变）

**中文提示词：**
```text
Apple iOS App 图标设计风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是一个标准的苹果超椭圆圆角正方形图标底座（Squircle，圆角半径约占边长 25%），四角以外区域完全透明（PNG 透明背景）。底座采用自上而下的蜜桃柔粉色线性渐变（顶部樱花浅粉 #FFE7EF 过渡到底部蜜桃柔粉 #F5B7CA），底座顶部边缘有一圈极细腻的白色受光内高光。底座正中微侧视（约 12 度微立体角）放置一台圆润饱满、造型可爱的奶白色复古双门迷你小冰箱，约占底座高度的 64%：箱体采用温润哑光奶油白烤漆陶瓷质感，大圆角边缘受光柔和，上下双门之间有细腻的立体凹槽分缝；左侧上下配有两枚精致的柔雾蓝（#6EB5E5）半透明磨砂毛玻璃胶囊形立体小把手，上层门右侧贴着一颗小小的珊瑚粉圆润立体圆点磁贴。正上方柔和顶光，冰箱向粉色底座投射柔和的蜜桃粉弥散接触阴影。完全无黑色描边，无轮廓线，无文字，无水印，Q弹温润想点击，1024x1024 高清独立图标。
```

**English Prompt (Optional):**
```text
Apple iOS app icon style, soft 3D tactile render with frosted glassmorphism, single centered continuous-curve squircle icon container on transparent PNG background. Soft top-down linear gradient blossom-pink squircle base (#FFE7EF at top to #F5B7CA at bottom) with a delicate white inner rim highlight. Inside centered (~64% scale), an adorable, chubby rounded 3D mini two-door retro dorm refrigerator at a subtle 12-degree 3D angle: smooth creamy-white matte ceramic finish with soft chamfered edges, subtle 3D horizontal door seam, two translucent frosted sky-blue (#6EB5E5) pill-shaped handles on the left, and one tiny glossy coral-pink circular magnet on the upper door. Soft top-down studio lighting, soft rose-tinted contact shadow on the pink squircle base. Zero black outlines, no strokes, no text, clean Apple HIG icon design, 1024x1024.
```

---

### 4. 生活日用（`daily.png` · 上排第 4 个 · 薄荷青微渐变）

**中文提示词：**
```text
Apple iOS App 图标设计风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是一个标准的苹果超椭圆圆角正方形图标底座（Squircle，圆角半径约占边长 25%），四角以外区域完全透明（PNG 透明背景）。底座采用自上而下的薄荷青玉色线性渐变（顶部清透薄荷 #E2F7EE 过渡到底部青玉薄荷 #B2E3CC），底座顶部边缘有一圈极细腻的白色受光内高光。底座正中微俯视（约 18 度立体视角）放置一只饱满圆润的奶白色立体陶瓷马克杯，约占底座宽度的 60%：温润奶白釉面杯身配圆润厚实的右侧C形杯把，微俯角度能看到圆润杯口内盛着清透晶莹的浅晴空蓝（#6EB5E5）果冻感液体光泽；杯身中下部包裹着一圈半透明青玉色磨砂毛玻璃防烫杯套，形成细腻的玻璃与白瓷材质对比。正上方柔和顶光，杯底向薄荷底座投射柔和的青绿色弥散接触阴影。完全无黑色描边，无轮廓线，不要烟雾水汽，无文字，无水印，1024x1024 高清独立图标。
```

**English Prompt (Optional):**
```text
Apple iOS app icon style, soft 3D tactile render with frosted glassmorphism, single centered continuous-curve squircle icon container on transparent PNG background. Soft top-down linear gradient mint-jade squircle base (#E2F7EE at top to #B2E3CC at bottom) with a subtle white inner rim highlight. Inside centered (~60% scale), a plump, rounded 3D creamy-white ceramic mug viewed from a gentle 18-degree elevated angle with a chubby curved handle on the right; translucent sky-blue (#6EB5E5) glossy liquid visible inside the rim, and a translucent mint frosted-glass grip sleeve wrapped around the lower mug body creating rich material contrast. Soft top-down studio lighting, soft jade-tinted contact shadow on the mint squircle base. Zero black outlines, no steam, no strokes, no text, clean Apple HIG aesthetic, 1024x1024.
```

---

### 5. 运动户外（`sports.png` · 下排第 1 个 · 暖奶油微渐变）

**中文提示词：**
```text
Apple iOS App 图标设计风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是一个标准的苹果超椭圆圆角正方形图标底座（Squircle，圆角半径约占边长 25%），四角以外区域完全透明（PNG 透明背景）。底座采用自上而下的暖阳奶油色线性渐变（顶部柔光奶白 #FFF6DC 过渡到底部暖杏奶油 #F7DC88），底座顶部边缘有一圈极细腻的白色受光内高光。底座正中微斜向（约 20 度轻快立体倾角）悬浮着一只造型圆润饱满的 3D 轻量化健身小哑铃，约占底座宽度的 64%：中间是温润银白色哑光阳极氧化金属圆握把，左右两端各配有双层圆润饱满的海盐晴空蓝渐变（#5AA9E6 到 #3E8EDE）亲肤磨砂橡胶配重圆盘，外侧叠有一圈精致的半透明磨砂毛玻璃装饰环，边缘圆滑有柔和受光高光。正上方柔和顶光，哑铃向奶油底座投射柔和的暖琥珀色弥散阴影。完全无黑色描边，无轮廓线，无文字，无数字刻度，无水印，1024x1024 高清独立图标。
```

**English Prompt (Optional):**
```text
Apple iOS app icon style, soft 3D tactile render with frosted glassmorphism, single centered continuous-curve squircle icon container on transparent PNG background. Soft top-down linear gradient warm cream squircle base (#FFF6DC at top to #F7DC88 at bottom) with a delicate white inner rim highlight. Inside centered (~64% scale), a smooth, chubby 3D fitness dumbbell tilted dynamically at 20 degrees: smooth anodized silver-white matte center grip bar, and plump rounded sky-blue gradient (#5AA9E6 to #3E8EDE) soft-touch matte weight plates on both ends accented with translucent frosted-glass outer rings catching soft rim light. Soft top-down studio illumination, warm amber-tinted soft drop shadow cast onto the cream squircle base. Zero black outlines, no strokes, no numbers or text, clean Apple HIG aesthetic, 1024x1024.
```

---

### 6. 服饰鞋包（`clothing.png` · 下排第 2 个 · 蜜桃粉微渐变）

**中文提示词：**
```text
Apple iOS App 图标设计风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是一个标准的苹果超椭圆圆角正方形图标底座（Squircle，圆角半径约占边长 25%），四角以外区域完全透明（PNG 透明背景）。底座采用自上而下的蜜桃柔粉色线性渐变（顶部樱花浅粉 #FFE7EF 过渡到底部蜜桃柔粉 #F5B7CA），底座顶部边缘有一圈极细腻的白色受光内高光。底座正中微侧转（约 14 度立体视角）放置一只饱满挺括、下宽上窄的圆角奶白色 3D 时尚托特手提包（精品购物袋），约占底座宽度的 58%：温润奶白色哑光皮革质感袋身，袋口有自然的立体厚度与柔和内阴影，上方配有拱形的柔雾蓝渐变（#6EB5E5）圆润立体提手，提手根部悬挂着一枚精致小巧的半透明珊瑚粉磨砂毛玻璃圆角小吊牌。正上方柔和顶光，手提包向粉色底座投射柔和的蜜粉弥散接触阴影。完全无黑色描边，无轮廓线，无品牌字样，无文字，无水印，1024x1024 高清独立图标。
```

**English Prompt (Optional):**
```text
Apple iOS app icon style, soft 3D tactile render with frosted glassmorphism, single centered continuous-curve squircle icon container on transparent PNG background. Soft top-down linear gradient blossom-pink squircle base (#FFE7EF at top to #F5B7CA at bottom) with a subtle white inner rim highlight. Inside centered (~58% scale), a plump, structured 3D rounded trapezoid tote shopping bag angled gently at 14 degrees: creamy-white smooth matte body with soft volume and recessed top opening shadow, a smooth rounded sky-blue (#6EB5E5) arched handle on top, and a tiny translucent coral-pink frosted-glass charm tag hanging from the handle base. Soft top-down studio lighting, soft rose-tinted contact shadow on the pink squircle base. Zero black outlines, no strokes, no logos or text, clean Apple HIG aesthetic, 1024x1024.
```

---

### 7. 考研考证（`exam.png` · 下排第 3 个 · 薄荷青微渐变）

**中文提示词：**
```text
Apple iOS App 图标设计风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是一个标准的苹果超椭圆圆角正方形图标底座（Squircle，圆角半径约占边长 25%），四角以外区域完全透明（PNG 透明背景）。底座采用自上而下的薄荷青玉色线性渐变（顶部清透薄荷 #E2F7EE 过渡到底部青玉薄荷 #B2E3CC），底座顶部边缘有一圈极细腻的白色受光内高光。底座正中呈现前后两层微立体错落叠放的圆角资料卡，整体约占底座宽度的 60%：后层是一张微斜的半透明青蓝色磨砂毛玻璃底板，前层是一张温润饱满的纯白哑光 3D 圆角文档纸页（右上角带有自然立体的圆润翻折角，投下细微折角阴影），白纸上有两条简洁圆润的浅海盐蓝（#6EB5E5）微凸胶囊短横条；文档右下角微悬浮叠放一枚圆润晶莹的青玉绿（#3FB68B）半透明毛玻璃圆形小徽章，内嵌简洁的纯白微立体对勾。正上方柔和顶光，整体向薄荷底座投射柔和青绿色弥散阴影。完全无黑色描边，无轮廓线，无文字字母，无水印，层次丰富精致，1024x1024 高清独立图标。
```

**English Prompt (Optional):**
```text
Apple iOS app icon style, soft 3D tactile render with frosted glassmorphism, single centered continuous-curve squircle icon container on transparent PNG background. Soft top-down linear gradient mint-jade squircle base (#E2F7EE at top to #B2E3CC at bottom) with a subtle white inner rim highlight. Inside centered (~60% scale), layered 3D exam document cards: a slightly tilted translucent sky-blue frosted-glass backing card behind a plump, smooth creamy-white 3D rounded document sheet with a soft 3D dog-ear folded top-right corner and two minimal rounded sky-blue (#6EB5E5) pill bars; overlapping the bottom-right corner is a glossy jade-green (#3FB68B) frosted-glass circular badge with a clean white 3D checkmark. Soft top-down studio lighting, soft jade-tinted contact shadow on the mint squircle base. Zero black outlines, no strokes, no text or letters, clean Apple HIG aesthetic, 1024x1024.
```

---

### 8. 其他（`other.png` · 下排第 4 个 · 冰蓝微渐变）

**中文提示词：**
```text
Apple iOS App 图标设计风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是一个标准的苹果超椭圆圆角正方形图标底座（Squircle，圆角半径约占边长 25%），四角以外区域完全透明（PNG 透明背景）。底座采用自上而下的柔和冰蓝色线性渐变（顶部清透冰蓝 #DFF1FD 过渡到底部晴空柔蓝 #B2D8F4），底座顶部边缘有一圈极细腻的白色受光内高光。底座正中是由四个饱满圆润如鹅卵石按键般的 3D 小圆角方块组成的 2x2 矩阵（整组约占底座宽度的 58%，间距匀称）：左上、左下、右下三个小方块为温润细腻的纯白哑光微立体圆角块，带有柔和受光面；右上角那一个小方块为微微浮起、略带 10 度俏皮倾角的海盐亮蓝（#4A9BE8）半透明磨砂毛玻璃方块，边缘泛着精致的白色玻璃高光，向下方投出清透的蓝色柔影。正上方柔和顶光，四个小立体块均向冰蓝底座投射柔和浅蓝接触阴影。完全无黑色描边，无轮廓线，无文字，无水印，极具苹果控制中心组件触感，1024x1024 高清独立图标。
```

**English Prompt (Optional):**
```text
Apple iOS app icon style, soft 3D tactile render with frosted glassmorphism, single centered continuous-curve squircle icon container on transparent PNG background. Soft top-down linear gradient sky-blue squircle base (#DFF1FD at top to #B2D8F4 at bottom) with a delicate white inner rim highlight. Inside centered (~58% scale), a 2x2 bento grid of four plump, pebble-smooth 3D rounded mini-squares: three tiles (top-left, bottom-left, bottom-right) in creamy pure-white smooth matte finish, while the top-right tile is slightly elevated and tilted 10 degrees in translucent sea-salt blue (#4A9BE8) frosted glass with crisp white specular rim highlights and a soft blue layer shadow. Soft top-down studio illumination, soft blue-tinted contact shadows cast onto the squircle base. Zero black outlines, no strokes, no text, clean Apple HIG aesthetic, 1024x1024.
```

---

## 四、通用反向提示词（Negative Prompt，如工具有反向框可统一粘贴）

```text
flat 2D vector, thick black outlines, dark strokes, cartoon comic line art, doodle, clipart, harsh black shadows, neon colors, dark background, photorealistic messy textures, metallic grunge, text, Chinese characters, English letters, numbers, watermark, signature, multiple icons in one image, cropped edges, white square background, checkerboard pattern
```

---

## 五、DSH 出图后自检 4 条（发回前看一眼）

1. **没有黑线框**：8 张图里没有任何深蓝色/黑色粗轮廓描边，全靠体积光影与半透明磨砂材质塑形。
2. **四角真透明**：PNG 放到浅蓝底色 `#F3F8FC` 上时，圆角外没有白角或被硬切断的灰色阴影框。
3. **大小一致**：8 个圆角正方形底座在各自画布中的占比一致（约 92% 或统一满画幅圆角），内部物体大小视觉均衡（约占底座 60%）。
4. **色相交错对号**：`books / other` 是冰蓝渐变，`digital / sports` 是暖奶油渐变，`appliance / clothing` 是蜜桃粉渐变，`daily / exam` 是薄荷青渐变。

---

## 附（可选）：如果希望首页下方「同校面交」横幅也同步脱去黑描边、升级同款轻 3D 柔光风

> 如果当前只改 8 个分类图标，此条可忽略；如果想让分类区下方的横幅卡片也一起告别黑线扁平感，可顺便生成下面这张纯右侧轻 3D 插画（文件名 `home-banner.png`，尺寸 `1500×480`）：

```text
Apple 轻三维柔光微立体插画横幅（Soft 3D & Frosted Glass UI Banner），尺寸 1500x480，一张纯白色（#FFFFFF）大圆角横版卡片（圆角半径约为高度的 12%，圆角以外透明）。卡片左侧 58% 为干净留白并排好四行清晰中文（第一行小字「同校面交」#3E7EA3；第二行粗体大字「约好时间，楼下拿走」#2E4150；第三行左侧小字「认证同学之间」#7D8F9C，右侧「就近 ›」#3E7EA3）。卡片右侧 42% 是与白底自然柔和融合的 Apple 风轻 3D 微立体校园书架一角：温润奶白色哑光圆角矮书格，里面整齐微倾排列着几本圆润饱满的冰蓝（#7EB6D9）、暖奶油（#F7DC88）、蜜桃粉（#F5B7CA）、薄荷青（#B2E3CC）轻 3D 立体书本与一枚半透明磨砂毛玻璃收纳盒，上方垂下两盏小巧温润的奶油白磨砂圆罩小吊灯，洒下清透柔和的顶光与浅蓝弥散阴影。全程无任何黑色描边，无粗轮廓线，无照片感，无人物，与上方 Apple 风分类图标材质光影完全统一。
```
