# 校园二手小程序 · 全站视觉 Apple HI 轻三维柔光风成套升级提示词（交 DSH 出图）

> **给 DSH 的一句话背景说明：**
> 市集首页的 8 个分类图标已率先升级为 **Apple iOS 轻三维柔光微立体 + 磨砂毛玻璃风（Soft 3D & Frosted Glassmorphism）**，效果非常好。现在需要按**完全同一套色板、同一套顶光源角度与双材质语言**，把全站剩余的 **6 大模块（共 22 张素材）** 一次性补齐，彻底统一全站视觉品质。

---

## 一、交付文件总清单与「三种画布背景规则」（必读！）

请逐张独立生成 **22 张 PNG 图片**（不要把多张拼在一张图里），统一推荐导出 **`1024 × 1024 px`**（横幅插画可导出 `1200 × 900 px` 或 `1024 × 1024 px`），全部放在一个文件夹（如 `全站视觉素材/`）中按下列文件名命名。

> ⚠️ **特别注意：本次 22 张图分为 3 种背景/底座形态，请严格区分，不要全部套圆角方块底座！**

| 形态分类 | 包含文件（共 22 张） | 画布与透明通道规则 |
| :--- | :--- | :--- |
| **A 类：无底座 · 纯 3D 主体悬浮**<br>（共 **14 张**） | `home-banner-art.png`（1 张）<br>`tab-shop.png`、`tab-shop-on.png`、`tab-plus.png`、`tab-plus-on.png`、`tab-msg.png`、`tab-msg-on.png`、`tab-me.png`、`tab-me-on.png`（8 张）<br>`empty-goods.png`、`empty-msg.png`、`empty-order.png`（3 张）<br>`icon-ai-sparkle.png`、`icon-search.png`（2 张） | **绝对不要画圆角正方形底板！** 只画居中的 3D 物体本身，四周背景 **100% RGBA 全透明**（不要白底、黑底或棋盘格），边缘阴影需在画布内自然淡出为全透明，不要在画布边界被硬切断。 |
| **B 类：带 Apple 超椭圆底座**<br>（共 **7 张**，与前序分类图标同款） | `app-logo-3d.png`（1 张 · 登录页大主标）<br>`menu-goods.png`、`menu-orders.png`、`menu-reports.png`、`menu-verify.png`、`menu-settings.png`、`menu-wechat.png`（6 张 · 我的页菜单标） | 画面中心为标准 **Apple 超椭圆圆角正方形底座（Squircle，圆角半径约 25%，占画布约 92%）**，顶光线性微渐变 + 顶部细白内高光边；**圆角四角以外区域完全透明**。 |
| **C 类：满画幅渐变底（前端裁圆）**<br>（共 **1 张**） | `default-avatar.png`（默认同学头像） | **满画幅 100% 铺满柔和冰蓝渐变底色**（不要留透明圆角，小程序前端会通过 `border-radius: 50%` 裁切成正圆形头像，满画幅可避免边缘露白）。 |

---

## 二、全站统一色板与 Apple HI 光影铁律（与已完成的 8 个分类图标严格同源）

1. **零深色描边（Zero Outlines）**：全程禁止任何黑色、深灰或深藏青描边轮廓线，完全依靠柔和体积光影、圆润大倒角（Smooth Fillet）与前后材质遮蔽塑形。
2. **统一顶光源（Top-Down Studio Lighting）**：全局柔光位于**正上方偏左 15°**，受光上沿有清透高光，底部有同色系柔和环境遮蔽（AO），绝不使用脏黑阴影。
3. **双材质碰撞（Matte Clay/Ceramic + Frosted Glass）**：温润细腻的亲肤哑光实体材质（奶白、海盐蓝等）搭配局部**半透明磨砂毛玻璃（Frosted Glassmorphism）**配件，兼具温润感与通透果冻触感。
4. **同款四色渐变与品牌色值表**：
   - **冰蓝体系**：渐变底 `#DFF1FD → #B2D8F4` ｜ 海盐亮蓝 `#4A9BE8` ｜ 行动深雾蓝 `#3E7EA3` ｜ 晴空柔蓝 `#6EB5E5`
   - **暖奶油体系**：渐变底 `#FFF6DC → #F7DC88` ｜ 暖琥珀金 `#F3AC3E`
   - **蜜桃粉体系**：渐变底 `#FFE7EF → #F5B7CA` ｜ 珊瑚柔粉 `#EB7396`
   - **薄荷青体系**：渐变底 `#E2F7EE → #B2E3CC` ｜ 青玉翠绿 `#3FB68B`
   - **中性色体系**：纯奶白 `#FFFFFF` ｜ 未选中冷岩灰蓝 `#8A9BA8 → #758896`（专用于 TabBar 未选中态）

---

## 三、分模块逐张独立提示词（共 22 条，一次贴一条即可）

---

### 模块一：市集首页「同校面交」卡片右侧纯插画（1 张 · A 类无底座全透明）

> **设计说明**：为了保证左侧中文（「同校面交 / 约好时间，楼下拿走」）在手机上 100% 锐利清晰，卡片白底和左侧文字改由小程序原生代码渲染，这里**只需要生成右侧的纯 3D 微缩场景透明 PNG**，不要在图里写任何字。

#### 1. 同校面交右侧微立体插画（`home-banner-art.png` · 建议 `1200×900` 或 `1024×1024` 透明 PNG）

**中文提示词：**
```text
Apple iOS 轻三维柔光微立体 UI 插画（Soft 3D & Frosted Glass Miniature Illustration），背景 100% 完全透明（PNG Alpha 透明通道，不要任何背景底板或外框）。画面呈现一组温馨精致、微侧视（约 18 度等轴微立体视角）的校园宿舍楼下面交书格小景：主体是一组温润奶白色（#FFFFFF）大圆角哑光双层矮书架格，格子里整齐微倾摆放着几本圆润厚实的冰蓝（#6EB5E5）、暖奶油（#F7DC88）、蜜桃粉（#F5B7CA）、薄荷青（#B2E3CC）轻 3D 精装书本，旁边放着一只精致的半透明海盐蓝（#4A9BE8）磨砂毛玻璃小收纳盒；书架右上方轻巧悬浮着一枚圆润晶莹的珊瑚粉与毛玻璃拼接的 3D 定位水滴小徽章（代表楼下就近面交），正上方垂下两盏小巧圆润的暖奶油白磨砂圆罩小吊灯。正上方偏左柔和棚拍顶光，物体底部带有极轻柔且向四周自然淡出为全透明的浅蓝灰接触柔影（阴影绝不能触碰画布边缘）。完全无黑色描边，无轮廓线，绝对不要任何文字、汉字、字母或水印，与苹果轻 3D 分类图标材质光影完全一致，高清渲染。
```

**English Prompt (Optional):**
```text
Apple HIG style soft 3D miniature UI illustration with frosted glassmorphism, isolated floating 3D object cluster on a 100% transparent PNG background (NO card frame, NO background plate). A cozy, plump rounded creamy-white (#FFFFFF) matte two-shelf campus pickup cubby angled gently at 18 degrees, neatly holding chubby rounded 3D books in pastel sky-blue (#6EB5E5), warm cream (#F7DC88), blossom pink (#F5B7CA), and mint jade (#B2E3CC), alongside a translucent sea-salt blue (#4A9BE8) frosted-glass parcel cube. Above the right corner floats a cute glossy coral-pink & frosted-glass 3D map location pin badge, with two tiny rounded warm-cream matte pendant lamps hanging gently above. Soft top-left studio illumination, subtle soft ambient shadow under the shelf fading smoothly to complete transparency well inside canvas borders. Zero black outlines, no strokes, strictly NO text or letters, clean C4D Octane / Blender Cycles UI render, 1200x900.
```

---

### 模块二：登录页 Hero 品牌立体主标（垫图 1:1 锁死原版双猫轮廓 · B 类珠光白超椭圆底座）

> ⚠️ **重要修正（必用垫图模式）**：不要仅靠纯文字画对称 `∞` 字！生图时必须将原版 `logo-原图参考.png` 作为输入参考图（Image-to-Image / Reference Image）上传，100% 锁死原版「左上双耳朝上、右上光滑圆拱、右下双耳朝下、左下探出小尾尖」的精确轮廓，仅升级 3D 材质光影。

#### 2. 登录页双猫环抱立体主标（`app-logo-3d.png` · `1024×1024` 四角透明，三段分离零黏连 + 表面光滑圆润版）

**中文提示词：**
```text
严格 1:1 锁死参考图中蓝色双猫品牌图形的精确 2D 剪影边界与留白穿插缝隙（零轮廓膨胀、零交汇黏连！）：原图由「三个完全独立、互不接触的分离部件」组成——①左上竖立双耳左猫连同向中心右上方弯起的卷尾、②右下倒置双耳右猫连同从右上向中心左下方斜穿的猫尾、③左下方单独探出的一小截独立收尖猫尾。必须 100% 保留这三者之间所有的留白穿透窄缝（特别是正中心两条平行错开猫尾之间的斜向留白切缝、左猫右侧脸颊与斜穿猫尾之间的留白窄缝、以及左下独立尾尖与左猫下腹之间的留白断口，严禁发生任何像素粘连、搭桥或融合成团！同时完整保留左上与右下猫耳根部的清晰凹口）。在此丝毫不差的三段分离精确剪影内部，将其升级为 Apple iOS App Store 应用图标（1024x1024 四角透明 PNG）：中心是珠光纯白到极浅冰蓝微渐变的苹果超椭圆圆角底座（#FFFFFF 过渡到 #EAF4FC，占画布约 92%，顶部带细腻白高光边）；底座正中悬浮这组三段分离穿插的双猫图形（占底座宽度约 68%），仅在每一段笔画的内部赋予光滑浑圆的水滴果冻 3D 拱起体积（横截面平滑圆润、零中轴屋脊棱线、零折面），色彩延续清澈的左上浅冰蓝（#BCDAFA）、中部晴空蓝（#82B1F2）平滑过渡至右下海盐亮蓝（#4D85E4），表面呈现温润丝滑的瓷釉与半透明磨砂琉璃质感，受光上沿带有柔和连贯的纯白丝绸高光，向珠光白底座投射轻柔浅冰蓝接触阴影（阴影清透，绝不堵塞留白切缝）。无黑色描边，无五官，无文字。
```

**English Prompt (With `logo-原图参考.png` attached as reference image):**
```text
Strictly lock 100% to the exact 2D silhouette boundaries and negative-space through-cuts of the attached reference mark—ZERO contour dilation, ZERO bridging or webbing at intersections! Crucial topology rule: the mark consists of THREE completely separate, non-touching islands separated by crisp negative-space channels: (1) the left upright two-eared cat sweeping into an upward-curling center-right tail, (2) the right inverted two-eared cat sweeping from top-right into a downward-angled center-left tail, and (3) the detached tapered tail tip below the left cat's belly. Keep ALL three crisp negative-space gaps 100% open, razor-clean, and completely unmerged (especially the diagonal gap between the two parallel passing tails at dead center, the narrow slit between the left cat's cheek and the descending tail, the clean cut separating the bottom-left tail tip, and the crisp ear-base notches). Inside these exact, non-dilated silhouettes ONLY, render as a premium Apple iOS App Store icon (1024x1024 RGBA PNG, pearl-white to whisper-ice-blue #FFFFFF–#EAF4FC continuous-curve squircle base ~92% canvas, transparent outer corners): sculpt each of the three separated pieces independently with ultra-smooth, pillowy water-droplet 3D dome cross-sections (zero central bevel ridge, zero faceted creases), luminous clean gradient from icy blue (#BCDAFA) through sky blue (#82B1F2) to cerulean (#4D85E4), silky porcelain-and-jelly glaze with smooth top-left studio softbox highlights, and a subtle light sky-blue contact shadow on the white squircle base that leaves every white gap crystal clear. Zero black outlines, no text.
```

---

### 模块三：底部导航栏 TabBar 图标（共 8 张 · A 类无底座全透明，4 组「未选中 + 选中」）

> ⚠️ **TabBar 极其重要的统一规范**：
> 1. **绝对不要画外围圆角正方形底板！** 必须是**直接悬浮在 100% 透明背景上的独立单图标**，物体占画布中心约 **76%–80%**（四周留约 10% 透明呼吸边距），保证缩放到手机底栏 `27pt` 时清晰饱满。
> 2. **未选中态（4 张）**：统一的冷调岩灰蓝微立体哑光材质（顶部 `#93A5B3` → 底部 `#758896`），干净克制、圆润无黑线。
> 3. **选中态（4 张）**：统一的**海盐行动蓝微立体主体（`#5AA9E6 → #3E7EA3`）+ 半透明冰蓝/奶白磨砂毛玻璃双层点缀（`#D4ECFC` / `#FFFFFF`）**，造型轮廓与对应的未选中态保持一致，点击切换时就像图标被瞬间“点亮充能”。

#### 3. 市集 · 未选中（`tab-shop.png` · 无底座全透明）

**中文提示词：**
```text
Apple iOS 底部导航栏 TabBar 图标（未选中态），轻三维极简圆润符号（Soft 3D Minimalist Glyph），背景 100% 完全透明（不要任何圆角正方形底座或背景框！）。画面正中是一个正面居中、占画布宽度约 78% 的圆润极简小市集店铺图标：由上方带三个圆弧波浪下沿的遮阳篷屋顶，与下方带拱形小门洞的圆角矩形店面组成。整体采用统一的冷调岩灰蓝哑光微立体材质（自上而下由 #93A5B3 柔和过渡到 #758896），倒角饱满圆润，带有极克制的顶光受光面，无夸张长投影。完全无黑色描边，无杂色，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS TabBar navigation icon (unselected state), minimalist plump soft-3D rounded glyph isolated on a 100% transparent PNG background (STRICTLY NO squircle background container or frame). Front-facing orthographic view centered (~78% canvas scale) of a clean, friendly market shopfront icon: a rounded scalloped awning roof sitting above a smooth rounded-rectangle shop body with a simple recessed arch doorway. Uniform cool slate-grey matte finish with subtle top-down gradient (#93A5B3 to #758896) and smooth rounded edges. Minimalist silhouette, no long shadows, zero black outlines, no text, 1024x1024 transparent PNG.
```

#### 4. 市集 · 选中（`tab-shop-on.png` · 无底座全透明）

**中文提示词：**
```text
Apple iOS 底部导航栏 TabBar 图标（选中高亮态），轻三维柔光双材质符号（Soft 3D & Frosted Glass Duotone Glyph），背景 100% 完全透明（不要任何圆角正方形底座或背景框！）。画面正中是一个正面居中、占画布宽度约 78% 的圆润立体小市集店铺图标（造型与未选中态完全对应）：下方圆角店面主体采用饱满温润的海盐行动蓝线性渐变（#5AA9E6 过渡到 #3E7EA3），中间嵌有温润奶白色小拱门；上方三个圆弧波浪下沿的遮阳篷采用清透晶莹的冰蓝色半透明磨砂毛玻璃材质（#CBE7FA），边缘带有明亮细腻的白色玻璃高光边，向下方店面投下一线柔和的层叠微影。无黑色描边，无外部大黑影，无文字，晶莹Q弹，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS TabBar navigation icon (selected active state), plump soft-3D duotone glyph with frosted glassmorphism isolated on a 100% transparent PNG background (STRICTLY NO squircle background container). Front-facing centered view (~78% canvas scale) of a cute rounded market shopfront icon: smooth rounded shop body in vibrant sea-salt blue gradient (#5AA9E6 to #3E7EA3) with a creamy-white arch door, topped with a translucent ice-blue (#CBE7FA) frosted-glass scalloped awning roof catching crisp white specular rim highlights and casting a subtle soft layer shadow onto the shop body. Zero black outlines, no text, 1024x1024 transparent PNG.
```

#### 5. 发布 · 未选中（`tab-plus.png` · 无底座全透明）

**中文提示词：**
```text
Apple iOS 底部导航栏 TabBar 图标（未选中态），轻三维极简圆润符号，背景 100% 完全透明（不要外围正方形背景底板！）。画面正中是一个正面居中、占画布宽度约 78% 的饱满正圆形圆润勋章块，正中心嵌有一个粗细匀称、末端全圆角的纯白色微凹「＋」加号符号。圆形主体采用统一的冷调岩灰蓝哑光微立体材质（自上而下由 #93A5B3 柔和过渡到 #758896），边缘圆润柔和，带有极克制的顶光高光。完全无黑色描边，无长投影，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS TabBar navigation icon (unselected state), minimalist plump soft-3D rounded glyph isolated on a 100% transparent PNG background (NO outer square container). Front-facing centered view (~78% canvas scale) of a smooth, plump circular disc in uniform cool slate-grey matte gradient (#93A5B3 to #758896), featuring a clean, thick rounded-end white plus (+) symbol centered inside with subtle recessed soft beveling. Minimalist, zero black outlines, no harsh shadows, no text, 1024x1024 transparent PNG.
```

#### 6. 发布 · 选中（`tab-plus-on.png` · 无底座全透明）

**中文提示词：**
```text
Apple iOS 底部导航栏 TabBar 图标（选中高亮态），轻三维柔光双材质符号，背景 100% 完全透明（不要外围正方形背景底板！）。画面正中是一个正面居中、占画布宽度约 78% 的果冻质感正圆形立体按键：圆形底座采用饱满清透的海盐行动蓝微渐变（#5AA9E6 过渡到 #3E7EA3），顶部边缘带有一道清亮内高光；圆盘正中心微微凸起一枚温润纯白与半透明磨砂毛玻璃融合的粗圆角「＋」加号符号，加号向蓝色圆盘投下一圈极细腻的柔和内阴影。无黑色描边，无文字，极具点击欲，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS TabBar navigation icon (selected active state), plump soft-3D tactile duotone glyph with frosted glassmorphism isolated on a 100% transparent PNG background (NO outer square container). Front-facing centered view (~78% canvas scale) of a glossy-matte sea-salt blue gradient (#5AA9E6 to #3E7EA3) plump circular button with a delicate white top rim highlight, featuring a slightly raised, chubby rounded-end creamy-white & frosted-glass plus (+) symbol in the center casting a soft subtle blue contact shadow onto the disc. Zero black outlines, no text, 1024x1024 transparent PNG.
```

#### 7. 通知 · 未选中（`tab-msg.png` · 无底座全透明）

**中文提示词：**
```text
Apple iOS 底部导航栏 TabBar 图标（未选中态），轻三维极简圆润符号，背景 100% 完全透明（不要任何正方形背景底板！）。画面正中是一个正面居中、占画布宽度约 78% 的饱满椭圆形圆角聊天气泡图标（左下角带一个小巧圆润的气泡小尖角），气泡正中横向排列着两个简洁圆润的纯白色小圆点。气泡整体采用统一的冷调岩灰蓝哑光微立体材质（自上而下由 #93A5B3 柔和过渡到 #758896），倒角圆滑，受光克制。无黑色描边，无长投影，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS TabBar navigation icon (unselected state), minimalist plump soft-3D rounded glyph isolated on a 100% transparent PNG background (STRICTLY NO squircle container). Front-facing centered view (~78% canvas scale) of a smooth, chubby rounded speech bubble with a cute rounded tail at bottom-left, rendered in uniform cool slate-grey matte gradient (#93A5B3 to #758896), with two clean rounded white dots centered horizontally inside. Zero black outlines, no harsh shadows, no text, 1024x1024 transparent PNG.
```

#### 8. 通知 · 选中（`tab-msg-on.png` · 无底座全透明）

**中文提示词：**
```text
Apple iOS 底部导航栏 TabBar 图标（选中高亮态），轻三维柔光双材质符号，背景 100% 完全透明（不要任何正方形背景底板！）。画面正中是一个正面居中、占画布宽度约 78% 的饱满圆润立体聊天气泡图标（左下角带圆润小尾角）：主气泡采用清透饱满的海盐行动蓝微渐变（#5AA9E6 过渡到 #3E7EA3），顶部带有细腻受光高光；气泡内部居中有两颗圆润微凸的纯白与冰蓝磨砂毛玻璃小圆点，且气泡右上角边缘轻巧叠嵌着一小颗晶莹的暖奶油金（#F7DC88）半透明毛玻璃圆润微光点。无黑色描边，无文字，灵动通透，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS TabBar navigation icon (selected active state), plump soft-3D duotone glyph with frosted glassmorphism isolated on a 100% transparent PNG background (STRICTLY NO squircle container). Front-facing centered view (~78% canvas scale) of a plump rounded speech bubble in vibrant sea-salt blue gradient (#5AA9E6 to #3E7EA3) with a soft top specular highlight, two raised creamy-white frosted-glass dots inside, and a tiny luminous warm-cream (#F7DC88) translucent glass accent dot nestled cleanly on its upper-right shoulder. Zero black outlines, no text, 1024x1024 transparent PNG.
```

#### 9. 我的 · 未选中（`tab-me.png` · 无底座全透明）

**中文提示词：**
```text
Apple iOS 底部导航栏 TabBar 图标（未选中态），轻三维极简圆润符号，背景 100% 完全透明（不要任何正方形背景底板！）。画面正中是一个正面居中、占画布高度约 78% 的极简圆润人物半身剪影图标：上方是一颗饱满正圆球形头部，下方是圆润厚实的弧形双肩半身块（头肩之间留有干净微距）。整体采用统一的冷调岩灰蓝哑光微立体材质（自上而下由 #93A5B3 柔和过渡到 #758896），无五官，倒角圆滑。无黑色描边，无长投影，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS TabBar navigation icon (unselected state), minimalist plump soft-3D rounded glyph isolated on a 100% transparent PNG background (STRICTLY NO squircle container). Front-facing centered view (~78% canvas scale) of a clean, friendly abstract user profile silhouette: a smooth 3D sphere head floating slightly above a plump rounded-capsule shoulder/chest form, in uniform cool slate-grey matte gradient (#93A5B3 to #758896). Faceless, zero black outlines, no harsh shadows, no text, 1024x1024 transparent PNG.
```

#### 10. 我的 · 选中（`tab-me-on.png` · 无底座全透明）

**中文提示词：**
```text
Apple iOS 底部导航栏 TabBar 图标（选中高亮态），轻三维柔光双材质符号，背景 100% 完全透明（不要任何正方形背景底板！）。画面正中是一个正面居中、占画布高度约 78% 的极简圆润立体人物半身图标：下方弧形双肩半身采用饱满温润的海盐行动蓝线性渐变（#5AA9E6 过渡到 #3E7EA3）；上方正圆球形头部采用清透晶莹的冰蓝色半透明磨砂毛玻璃材质（#CBE7FA 融合纯白柔光），边缘泛着细腻白色受光高光，无五官。无黑色描边，无文字，通透精致，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS TabBar navigation icon (selected active state), plump soft-3D duotone glyph with frosted glassmorphism isolated on a 100% transparent PNG background (STRICTLY NO squircle container). Front-facing centered view (~78% canvas scale) of a friendly abstract user profile glyph: plump rounded shoulder form in vibrant sea-salt blue gradient (#5AA9E6 to #3E7EA3), paired with a luminous translucent ice-blue (#CBE7FA) frosted-glass sphere head above catching a crisp white top-left specular highlight. Faceless, zero black outlines, no text, 1024x1024 transparent PNG.
```

---

### 模块四：全站通用「空状态」轻 3D 悬浮微插画（共 3 张 · A 类无底座全透明）

> **设计说明**：空状态插画直接浮在小程序 `#F3F8FC` 浅雾蓝背景中央，因此**绝对不要画外框底座**，背景必须 100% 透明；色调温和清雅、略低饱和，传递轻松治愈的校园氛围。

#### 11. 商品 / 搜索为空（`empty-goods.png` · 用于市集无商品、搜索无结果、我的商品为空）

**中文提示词：**
```text
Apple iOS 治愈系空状态轻三维微插画（Soft 3D & Frosted Glass Empty State Illustration），背景 100% 完全透明（不要任何外框或圆角方形底座！）。画面正中（约占画布 72%）是一只微侧倾（约 15 度俯视立体角）的温润奶白色（#FFFFFF）大圆角哑光收纳空盒，盒口微微敞开，内部泛着柔和的浅冰蓝内阴影（#DCEEFB）；收纳盒右前方轻巧斜靠着一枚晶莹通透的海盐晴空蓝（#6EB5E5）半透明磨砂毛玻璃圆润放大镜，盒口上方轻轻飘浮着两颗小小的暖奶油色（#F7DC88）和蜜桃粉色（#F5B7CA）圆润磨砂小圆珠点缀。正上方柔和顶光，盒子底部带有在画布内自然淡出为全透明的极浅蓝灰接触柔影。完全无黑色描边，无文字，无问号字母，安静治愈，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG style soft 3D empty-state UI illustration with frosted glassmorphism, isolated floating object composition on a 100% transparent PNG background (NO background card or squircle). Centered (~72% canvas scale), a cute, plump rounded creamy-white (#FFFFFF) matte open storage box viewed from a gentle 15-degree elevated angle with soft ice-blue (#DCEEFB) interior shading; leaning gently against its front-right corner is a translucent sky-blue (#6EB5E5) frosted-glass magnifying glass with crisp white rim highlights, while two tiny pastel cream (#F7DC88) and blossom-pink (#F5B7CA) matte spheres float softly above the empty box. Soft top-down studio light, gentle soft ambient ground shadow fading completely to transparent well inside canvas edges. Zero black outlines, no text or symbols, serene and friendly, 1024x1024 transparent PNG.
```

#### 12. 通知为空（`empty-msg.png` · 用于通知页暂无新消息）

**中文提示词：**
```text
Apple iOS 治愈系空状态轻三维微插画（Soft 3D & Frosted Glass Empty State Illustration），背景 100% 完全透明（不要任何外框或圆角方形底座！）。画面正中（约占画布 70%）是一只微倾（约 15 度轻盈倾角）悬浮的圆润可爱 3D 小铃铛：铃铛主体采用清透温润的浅冰蓝到纯白柔和哑光渐变（#E4F3FE 到 #FFFFFF），底部露出一颗饱满的海盐亮蓝（#5AA9E6）小圆球铃舌，铃铛腰部环绕着一圈晶莹的半透明薄荷青（#B2E3CC）磨砂毛玻璃装饰环；铃铛右上方静静悬浮着两颗一大一小的半透明纯白磨砂毛玻璃安静小气泡圆珠。正上方柔和顶光，下方带有自然淡出为全透明的浅蓝弥散柔影。完全无黑色描边，无文字或Z字母，宁静柔和，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG style soft 3D empty-state UI illustration with frosted glassmorphism, isolated floating object on a 100% transparent PNG background (NO background plate or squircle). Centered (~70% canvas scale), a serene, plump rounded 3D notification bell gently tilted at 15 degrees: smooth ice-blue to creamy-white (#E4F3FE to #FFFFFF) matte bell body, a glossy sky-blue (#5AA9E6) spherical clapper peeking underneath, and a translucent mint-jade (#B2E3CC) frosted-glass band around its waist; floating peacefully near the upper right are two tiny translucent frosted-glass bubble spheres. Soft top-down studio lighting, subtle soft shadow beneath fading smoothly to complete transparency. Zero black outlines, no letters or text, calm minimalist aesthetic, 1024x1024 transparent PNG.
```

#### 13. 订单 / 举报记录为空（`empty-order.png` · 用于订单列表为空、举报列表为空）

**中文提示词：**
```text
Apple iOS 治愈系空状态轻三维微插画（Soft 3D & Frosted Glass Empty State Illustration），背景 100% 完全透明（不要任何外框或圆角方形底座！）。画面正中（约占画布 72%）是一块微侧倾（约 14 度立体视角）悬浮的圆润 3D 空白记录夹板：后层是温润晴空柔蓝渐变（#B2D8F4）的大圆角哑光背板，前层贴着一张厚实圆润的纯奶白色（#FFFFFF）空白卡片（上面仅有两条极浅雾蓝色 #E4F0F8 圆角空白短横槽），夹板顶部夹着一枚晶莹通透的蜜桃粉（#F5B7CA）半透明磨砂毛玻璃圆角小夹子。正上方柔和顶光，底部带有自然淡出为全透明的浅蓝灰柔和阴影。完全无黑色描边，无任何文字数字，干净通透，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG style soft 3D empty-state UI illustration with frosted glassmorphism, isolated floating object on a 100% transparent PNG background (NO outer squircle container). Centered (~72% canvas scale), a plump, smoothly rounded 3D blank clipboard tilted gently at 14 degrees: soft sky-blue (#B2D8F4) matte rounded backing board, a thick creamy-white (#FFFFFF) blank rounded paper sheet on front with two very faint recessed ice-blue (#E4F0F8) pill grooves, topped with a translucent blossom-pink (#F5B7CA) frosted-glass clip catching soft specular highlights. Soft top-down studio lighting, gentle soft shadow below fading smoothly to complete transparency. Zero black outlines, no text, clean Apple aesthetic, 1024x1024 transparent PNG.
```

---

### 模块五：「我的」页面 iOS 设置风圆角菜单图标（共 6 张 · B 类超椭圆底座）

> **设计说明**：放在「我的」页面 6 行功能菜单左侧（类似 iPhone「设置」列表的彩色圆角小标）。全部采用与分类图标同款的 **B 类超椭圆底座（占画布 92%，四角透明）**，内部图形更简洁大块（占底座约 62%），确保缩小到 `52rpx` 时依然一眼看清。

#### 14. 我的商品（`menu-goods.png` · 暖奶油微渐变底座）

**中文提示词：**
```text
Apple iOS 设置列表圆角图标风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是标准苹果超椭圆圆角正方形底座（Squircle，圆角半径约 25%，占画布约 92%），四角完全透明。底座为自上而下的暖阳奶油色线性渐变（#FFF6DC 过渡到 #F7DC88），顶部带细腻白色受光内高光。底座正中微斜放置一枚饱满圆润的 3D 商品价格小吊牌与立方小礼盒组合（约占底座 62%）：纯奶白色哑光圆角小方盒，正面斜叠一枚晶莹的海盐亮蓝（#4A9BE8）半透明磨砂毛玻璃圆角价格吊牌（带一个圆润小穿孔，无任何数字文字）。顶光柔和，投射暖琥珀色柔和接触阴影。无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS Settings-style squircle icon, soft 3D tactile render with frosted glassmorphism, centered continuous-curve squircle base (~92% canvas) on transparent PNG background. Warm cream top-down gradient squircle (#FFF6DC to #F7DC88) with subtle white top inner rim highlight. Inside centered (~62% scale), a plump rounded creamy-white (#FFFFFF) 3D parcel cube overlapped by a tilted translucent sea-salt blue (#4A9BE8) frosted-glass rounded price tag with a clean circular hole (no numbers or text). Soft top-down lighting, soft warm amber contact shadow. Zero black outlines, 1024x1024.
```

#### 15. 买入和卖出订单（`menu-orders.png` · 冰蓝微渐变底座）

**中文提示词：**
```text
Apple iOS 设置列表圆角图标风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是标准苹果超椭圆圆角正方形底座（Squircle，圆角半径约 25%，占画布约 92%），四角完全透明。底座为自上而下的冰蓝色线性渐变（#DFF1FD 过渡到 #B2D8F4），顶部带细腻白色受光内高光。底座正中是两张前后微错叠放的圆润 3D 订单小票卡片（约占底座 62%）：后层为半透明海盐蓝（#4A9BE8）磨砂毛玻璃卡，前层为温润纯奶白色哑光圆角票据卡，卡面居中嵌有两条简洁圆润的海盐蓝胶囊短横条。顶光柔和，投射浅蓝色接触阴影。无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS Settings-style squircle icon, soft 3D tactile render with frosted glassmorphism, centered continuous-curve squircle base (~92% canvas) on transparent PNG background. Sky-blue top-down gradient squircle (#DFF1FD to #B2D8F4) with subtle white top inner rim highlight. Inside centered (~62% scale), two layered plump 3D order receipt cards: a tilted translucent sea-salt blue (#4A9BE8) frosted-glass card behind a smooth creamy-white (#FFFFFF) matte rounded receipt card featuring two minimal sky-blue pill bars. Soft top-down lighting, soft blue contact shadow. Zero black outlines, no text, 1024x1024.
```

#### 16. 我的举报（`menu-reports.png` · 蜜桃粉微渐变底座）

**中文提示词：**
```text
Apple iOS 设置列表圆角图标风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是标准苹果超椭圆圆角正方形底座（Squircle，圆角半径约 25%，占画布约 92%），四角完全透明。底座为自上而下的蜜桃柔粉色线性渐变（#FFE7EF 过渡到 #F5B7CA），顶部带细腻白色受光内高光。底座正中是一枚饱满圆润的 3D 安全守护小盾牌（约占底座 62%）：外层盾身为温润纯奶白色哑光材质，盾牌中心嵌有一枚晶莹通透的珊瑚粉（#EB7396）半透明磨砂毛玻璃内盾心，正中有一颗圆润微凸的纯白小圆点与短竖条组成的柔和感叹提示符。顶光柔和，投射蜜粉色柔和接触阴影。无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS Settings-style squircle icon, soft 3D tactile render with frosted glassmorphism, centered continuous-curve squircle base (~92% canvas) on transparent PNG background. Blossom-pink top-down gradient squircle (#FFE7EF to #F5B7CA) with subtle white top inner rim highlight. Inside centered (~62% scale), a plump, friendly 3D rounded safety shield: smooth creamy-white (#FFFFFF) matte outer shield framing a translucent coral-pink (#EB7396) frosted-glass inner shield with a clean rounded white exclamation mark symbol in the center. Soft top-down lighting, soft rose contact shadow. Zero black outlines, no text, 1024x1024.
```

#### 17. 校园认证（`menu-verify.png` · 薄荷青微渐变底座）

**中文提示词：**
```text
Apple iOS 设置列表圆角图标风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是标准苹果超椭圆圆角正方形底座（Squircle，圆角半径约 25%，占画布约 92%），四角完全透明。底座为自上而下的薄荷青玉色线性渐变（#E2F7EE 过渡到 #B2E3CC），顶部带细腻白色受光内高光。底座正中是一张横向圆润的 3D 奶白色校园学生证小卡片（约占底座宽度的 64%）：卡片左侧是一个海盐晴空蓝（#5AA9E6）圆润小人像剪影块，右侧是一条浅蓝色短横条；卡片右下角叠放着一枚晶莹的青玉绿（#3FB68B）半透明磨砂毛玻璃圆形认证小徽章，内嵌纯白微立体对勾。顶光柔和，投射青绿色接触阴影。无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS Settings-style squircle icon, soft 3D tactile render with frosted glassmorphism, centered continuous-curve squircle base (~92% canvas) on transparent PNG background. Mint-jade top-down gradient squircle (#E2F7EE to #B2E3CC) with subtle white top inner rim highlight. Inside centered (~64% scale), a plump rounded horizontal creamy-white (#FFFFFF) 3D student ID card featuring a sky-blue (#5AA9E6) mini avatar silhouette on the left and a single clean pill bar on the right, with a glossy translucent jade-green (#3FB68B) frosted-glass checkmark badge overlapping the bottom-right corner. Soft top-down lighting, soft jade contact shadow. Zero black outlines, no text, 1024x1024.
```

#### 18. 编辑资料（`menu-settings.png` · 冰蓝微渐变底座）

**中文提示词：**
```text
Apple iOS 设置列表圆角图标风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是标准苹果超椭圆圆角正方形底座（Squircle，圆角半径约 25%，占画布约 92%），四角完全透明。底座为自上而下的冰蓝色线性渐变（#DFF1FD 过渡到 #B2D8F4），顶部带细腻白色受光内高光。底座正中是一支沿对角线 45 度轻盈悬浮的短胖圆润 3D 触控绘图笔（约占底座 62%）：纯奶白色哑光圆润笔身，笔头为圆润的海盐亮蓝（#4A9BE8），笔尾套着一截晶莹的半透明海盐蓝磨砂毛玻璃笔帽，笔尖左下方有一小段圆润的白色胶囊短划线。顶光柔和，投射浅蓝色接触阴影。无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS Settings-style squircle icon, soft 3D tactile render with frosted glassmorphism, centered continuous-curve squircle base (~92% canvas) on transparent PNG background. Sky-blue top-down gradient squircle (#DFF1FD to #B2D8F4) with subtle white top inner rim highlight. Inside centered (~62% scale), a chubby, smooth rounded 3D edit stylus pen angled at 45 degrees: creamy-white (#FFFFFF) matte body, rounded sea-salt blue (#4A9BE8) tip, and a translucent sea-salt blue frosted-glass end cap, accompanied by a tiny rounded white pill stroke at bottom-left. Soft top-down lighting, soft blue contact shadow. Zero black outlines, no text, 1024x1024.
```

#### 19. 绑定微信（`menu-wechat.png` · 薄荷青微渐变底座）

**中文提示词：**
```text
Apple iOS 设置列表圆角图标风格，轻三维柔光微立体渲染（Soft 3D & Glassmorphism）。画面中心是标准苹果超椭圆圆角正方形底座（Squircle，圆角半径约 25%，占画布约 92%），四角完全透明。底座为自上而下的薄荷青玉色线性渐变（#E2F7EE 过渡到 #B2E3CC），顶部带细腻白色受光内高光。底座正中是两个一左一右错落交叠的饱满圆润 3D 双聊天气泡（约占底座宽度的 64%）：左上方较大的气泡为青玉绿渐变（#3FB68B）温润哑光材质（带两颗纯白小圆点），右下方略小的前置气泡为纯白融合半透明磨砂毛玻璃材质（带两颗青玉绿小圆点），前后气泡之间有细腻的层叠柔影与玻璃高光边。顶光柔和，投射青绿色接触阴影。无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS Settings-style squircle icon, soft 3D tactile render with frosted glassmorphism, centered continuous-curve squircle base (~92% canvas) on transparent PNG background. Mint-jade top-down gradient squircle (#E2F7EE to #B2E3CC) with subtle white top inner rim highlight. Inside centered (~64% scale), two overlapping plump rounded 3D chat bubbles: a larger jade-green (#3FB68B) smooth matte bubble at back-left with two tiny white dots, overlapped at front-right by a smaller translucent white frosted-glass bubble with two tiny jade dots, casting a soft layer shadow. Soft top-down lighting, soft jade contact shadow. Zero black outlines, no text, 1024x1024.
```

---

### 模块六：全局点睛组件素材（共 3 张 · 默认头像 + AI 星徽 + 搜索放大镜）

#### 20. 默认同学萌猫头像（`default-avatar.png` · **C 类满画幅底色，不留透明边**）

**中文提示词：**
```text
Apple Memoji 风格轻三维柔光头像插画（Soft 3D Toy-like Avatar），1024x1024 正方形满画幅背景（注意：背景需要 100% 铺满整个正方形画布，不要圆角裁切，不要透明四角！）。背景采用自上而下的清透雾蓝渐变（顶部 #E6F4FE 柔和过渡到底部 #BFE0F8）。画面正中偏下位置是一只正面微笑的极简圆润 3D 奶白色小猫咪头像（头部居中饱满，占画布宽度约 64%，方便前端圆形裁切不切耳）：温润细腻的纯奶白色（#FFFFFF）哑光白瓷毛发质感，两只圆润三角形小猫耳内侧透着淡淡的蜜桃柔粉（#F5B7CA），两颗圆润乌亮而温柔的深墨蓝（#2E4150）小圆眼睛带清亮眼神高光，小巧的粉色倒三角鼻头，脖子上系着一圈精致的海盐蓝（#5AA9E6）半透明磨砂毛玻璃圆润小领圈。正上方柔和棚拍顶光，立体圆润亲切，完全无黑色描边，无文字，1024x1024。
```

**English Prompt (Optional):**
```text
Apple Memoji-inspired soft 3D character avatar portrait, FULL-BLEED 1024x1024 square background (fill the entire square canvas edge-to-edge with gradient, NO rounded corners or transparent margins so frontend circular crop is seamless). Smooth top-down pastel ice-blue gradient background (#E6F4FE to #BFE0F8). Centered cleanly inside (~64% width, ample margin around ears for circular cropping), an adorable, minimalist plump 3D creamy-white (#FFFFFF) kitten head and upper shoulders facing forward: smooth matte ceramic-like finish, cute rounded ears with soft blossom-pink (#F5B7CA) inner ears, gentle glossy dark-slate (#2E4150) bead eyes with tiny catchlights, tiny pink nose, wearing a sleek translucent sky-blue (#5AA9E6) frosted-glass collar. Soft top-down studio light, zero black outlines, no text, 1024x1024.
```

#### 21. AI 智能草稿 & 建议价专属柔光星徽（`icon-ai-sparkle.png` · **A 类无底座全透明**）

**中文提示词：**
```text
Apple Intelligence 风格轻三维柔光星芒图标（Soft 3D & Iridescent Frosted Glass Sparkle Icon），背景 100% 完全透明（不要任何圆角正方形底座或背景框！）。画面正中（约占画布 78%）是由一颗饱满圆润的四芒主星与右上、左下两颗小巧圆润的四芒伴星组成的 3D 星簇符号：主星采用清透梦幻的柔光多彩微渐变磨砂玻璃材质（将海盐亮蓝 #4A9BE8、蜜桃柔粉 #F5B7CA 与暖奶油金 #F7DC88 自然柔和融为一体），星芒边缘圆滑厚实如水滴宝石，泛着细腻的纯白玻璃折射高光，完全不刺眼、不霓虹。无黑色描边，无外部大面积光晕切边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple Intelligence-inspired soft 3D AI sparkle icon, isolated floating glyph on a 100% transparent PNG background (STRICTLY NO squircle background container). Centered (~78% canvas scale), a plump, pebble-smooth 3D four-pointed main star accompanied by two tiny rounded companion stars at top-right and bottom-left; rendered in luminous, softly blended pastel frosted glass transitioning smoothly across sea-salt blue (#4A9BE8), blossom pink (#F5B7CA), and warm cream gold (#F7DC88), with crisp white specular rim highlights and rounded tactile edges. Zero black outlines, no clipped outer glow, no text, 1024x1024 transparent PNG.
```

#### 22. 首页胶囊搜索框微立体放大镜（`icon-search.png` · **A 类无底座全透明**）

**中文提示词：**
```text
Apple iOS 极简轻三维毛玻璃搜索放大镜图标（Soft 3D & Frosted Glass Search Magnifier），背景 100% 完全透明（不要任何圆角正方形底座或背景框！）。画面正中（约占画布 78%）是一枚呈 45 度角（镜框在左上，手柄朝右下）放置的饱满圆润 3D 放大镜：圆环形镜框与圆润胶囊手柄采用温润的海盐雾蓝色哑光金属质感（#4E88B4 到 #3E7EA3），圆形镜片内部嵌有一片清透晶莹的浅冰蓝色半透明磨砂毛玻璃（#D4ECFC，约 55% 半透明度），镜片左上弧边带有一抹清亮的纯白反光高光。无黑色描边，无杂乱阴影，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG style soft 3D search magnifying glass icon with frosted glassmorphism, isolated floating glyph on a 100% transparent PNG background (STRICTLY NO squircle container). Centered (~78% canvas scale), a plump, smoothly rounded 3D magnifying glass angled at 45 degrees (lens top-left, rounded handle bottom-right): smooth anodized sea-salt slate-blue (#4E88B4 to #3E7EA3) matte circular frame and chubby handle, enclosing a semi-translucent ice-blue (#D4ECFC) frosted-glass lens catching a crisp diagonal white specular glint. Zero black outlines, no harsh shadows, no text, 1024x1024 transparent PNG.
```

---

## 四、通用反向提示词（Negative Prompt，可全局复用）

```text
flat 2D vector, thick black outlines, dark strokes, comic line art, clipart, harsh black shadows, neon cyberpunk colors, dark background, photorealistic messy textures, text, Chinese characters, English letters, numbers, watermark, signature, multiple icons in one grid image, cropped edges, white background on transparent assets, checkerboard pattern
```

---

## 五、DSH 交付前 30 秒快速核对表（全套 22 张）

- [ ] **数量与命名（共 22 张）**：
  - 模块一（1 张）：`home-banner-art.png`
  - 模块二（1 张）：`app-logo-3d.png`
  - 模块三（8 张）：`tab-shop.png`、`tab-shop-on.png`、`tab-plus.png`、`tab-plus-on.png`、`tab-msg.png`、`tab-msg-on.png`、`tab-me.png`、`tab-me-on.png`
  - 模块四（3 张）：`empty-goods.png`、`empty-msg.png`、`empty-order.png`
  - 模块五（6 张）：`menu-goods.png`、`menu-orders.png`、`menu-reports.png`、`menu-verify.png`、`menu-settings.png`、`menu-wechat.png`
  - 模块六（3 张）：`default-avatar.png`、`icon-ai-sparkle.png`、`icon-search.png`
- [ ] **底座检查（最关键）**：
  - 只有 `app-logo-3d.png` 和 6 张 `menu-*.png` **有**超椭圆圆角方块底座；
  - `default-avatar.png` 是**满画幅铺满冰蓝渐变底**；
  - 其余 **14 张（横幅插画 + 8 张 TabBar + 3 张空状态 + AI 星徽 + 搜索放大镜）全部无底座**，直接悬浮在 100% 全透明背景上！
- [ ] **风格一致性**：全程零黑线描边，顶光柔和，色彩严格落在冰蓝 / 暖奶油 / 蜜桃粉 / 薄荷青四色体系内。
