# 校园二手小程序 · 全站按钮升级素材提示词（Apple HI 轻三维柔光风 · 交 DSH 出图）

> **给 DSH 的一句话背景说明：**
> 小程序的分类图标、Logo、底栏 TabBar 和空状态插画已完成 Apple 轻 3D 柔光风升级，目前还剩全站各页面的**操作按钮**仍是扁平纯色块。
> 为了保证**按钮文字 100% 锐利清晰、支持动态可变文案（如「发布/保存」「上传校园卡/已上传」）、适配不同宽度的长短按钮且不超出微信小程序 `2MB` 包体积红线**，本次采用 **Apple iOS 原生组件工程架构**：
> - **按钮的立体果冻/毛玻璃胶囊底座 + 锐利中文字体**：由小程序前端通过多层顶部受光内高光、线性微渐变与底部立体厚度阴影渲染；
> - **DSH 负责生成全站按钮所需的 20 张核心 3D 视觉素材**（包含 **2 张独立完整图形按键磁贴** + **18 张高复用无底座透明 3D 按钮内嵌图标**，每张图标均严格针对其所在按钮的深/浅底色定制了高对比度材质，绝不撞色隐形）。

---

## 一、重点专项设计：「我的商品」页面三枚操作按钮的平衡尺寸规范

针对「我的商品」页面原本**左卡巨大（`530×710rpx`）、右侧三个小胶囊悬空（`132×62rpx`）导致左右严重失衡**的问题，本次采用如下黄金比例平衡方案：

1. **卡片与按钮重构尺寸**：
   - 废弃“左巨卡 + 右悬空窄条”布局，改为 **`702 rpx` 满宽对称圆角卡片**（上半部为左侧 `184×184 rpx` 圆角商品图 + 右侧信息区；下半部为等宽三联操作栏）。
   - **三枚操作按钮尺寸（`下架/上架` · `编辑` · `删除`）**：严格按 **`1 : 1 : 1` 等宽横向并排**铺满卡片下沿，单颗按钮精确尺寸为 **`206 × 72 rpx`（胶囊圆角 `36 rpx`，按钮间距 `16 rpx`）**，左右视觉重量 100% 对称平衡。
2. **对应的 DSH 出图比例要求（模块一的 4 张图）**：
   - 每颗 `206 × 72 rpx` 按钮内嵌一枚 **`36 × 36 rpx`** 3D 图标 + `26 rpx` 文字。
   - 因此模块一的 `下架 / 上架 / 编辑 / 删除` 4 张图标在 `1024×1024` 画布中，**主体宽高必须统一占画布的 `78%–80%`、轮廓趋近饱满的 `1:1` 方圆体量且光学中心严格居中**（严禁画成细长棍状或大小不一），确保三颗按钮并排时视觉分量完全均等。

---

## 二、20 张交付文件总表与「画布背景铁律」（必读！）

请逐张独立生成 **20 张 `1024 × 1024 px` PNG 图片**（不要拼在一张大图里，**所有图片内严禁出现任何汉字、英文字母或数字**），全部放入桌面 `全站按钮素材/` 文件夹：

| 形态分类 | 文件名清单（共 20 张） | 画布与透明规则 & 配色对比原则 |
| :--- | :--- | :--- |
| **A1 类：深蓝主按钮专用 · 珠光白高亮 3D 图标**<br>（共 **6 张**，无底座全透明） | `btn-pri-login.png`（登录/注册下一步）<br>`btn-pri-buy.png`（下单/模拟付款）<br>`btn-pri-publish.png`（发布/保存）<br>`btn-pri-send.png`（聊天发送）<br>`btn-pri-search.png`（搜索按钮）<br>`btn-pri-confirm.png`（确认面交/提交认证/通过） | **绝对不要画外框底座！100% 透明背景**，主体占画布约 `78%`。因为这些图标要放在**深海盐蓝（`#3E7EA3`）主按钮**内部，主体必须采用**纯奶白哑光白瓷（`#FFFFFF`）+ 清透冰晶毛玻璃（`#D8EEFE`）+ 微量暖奶油金（`#FFE394`）点缀**，确保在深蓝按钮上晶莹醒目！ |
| **A2 类：浅色次按钮/胶囊专用 · 彩色微立体图标**<br>（共 **10 张**，无底座全透明） | `btn-op-offshelf.png`（下架）<br>`btn-op-onshelf.png`（上架）<br>`btn-op-edit.png`（编辑）<br>`btn-wechat.png`（微信登录）<br>`btn-chip-unread.png`（只看未读）<br>`btn-chip-readall.png`（全部已读）<br>`btn-camera-upload.png`（上传校园卡/头像）<br>`btn-chat-order.png`（本单聊天/看订单）<br>`btn-retry-undo.png`（重试/撤回）<br>`btn-filter-pin.png`（面交点筛选定位） | **绝对不要画外框底座！100% 透明背景**，主体占画布约 `78%`。因为这些图标放在**浅冰蓝/暖奶油/薄荷白等浅色立体胶囊**内部，主体采用饱和清澈的**海盐蓝（`#4A9BE8`）/ 青玉绿（`#3FB68B`）/ 暖琥珀（`#F3AC3E`）+ 半透明磨砂毛玻璃**，在浅底上层次分明。 |
| **A3 类：危险警示按钮专用 · 珊瑚粉 3D 图标**<br>（共 **2 张**，无底座全透明） | `btn-op-delete.png`（我的商品删除）<br>`btn-warn-action.png`（退出登录/取消订单/申诉/举报） | **绝对不要画外框底座！100% 透明背景**，主体占画布约 `78%`。采用**珊瑚柔红（`#E06377`）+ 纯白/浅樱粉半透明磨砂毛玻璃**双材质，匹配浅樱粉危险按钮底色。 |
| **B 类：独立完整图形按钮磁贴**<br>（共 **2 张**，整张图就是按钮本身） | `tile-photo-add.png`（发布页上传照片方块大钮 · 超椭圆底座四角透明）<br>`btn-photo-del.png`（发布页照片右上角删除圆钮 · 正圆毛玻璃珠四周透明） | 这 2 张**自带立体按键底座**（`tile-photo-add.png` 为占画布 `92%` 的冰蓝超椭圆底座；`btn-photo-del.png` 为占画布 `86%` 的正圆形深雾灰蓝磨砂玻璃圆珠底座），底座以外区域 100% 全透明。 |

---

## 三、分模块逐张独立提示词（共 20 条，一次贴一条）

---

### 模块一：「我的商品」卡片平衡操作栏专属图标（4 张 · 1:1 饱满等量体态 · 无底座全透明）

#### 1. 下架按钮图标（`btn-op-offshelf.png` · 放入冰蓝柔光立体胶囊）

**中文提示词：**
```text
Apple iOS 轻三维柔光按钮微图标（Soft 3D & Frosted Glass UI Action Icon），背景 100% 完全透明（严禁画外围正方形或圆形背景底座！）。画面正中是一个宽高比接近 1:1、占画布约 78% 的饱满圆润 3D「商品下架收纳」组合符号：下半部分是一只温润海盐亮蓝渐变（#5AA9E6 过渡到 #3E7EA3）的圆角敞口浅收纳托盘盒，正上方一枚饱满圆润的纯奶白色（#FFFFFF）融合冰蓝半透明磨砂毛玻璃的粗圆角向下箭头正轻巧落入盒中。表面光滑浑圆如鹅卵石，零棱线，正上方柔和顶光，箭头在盒口投下极细腻的浅蓝层叠柔影，无外部切断黑影。完全无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D button icon with frosted glassmorphism, isolated floating object on a 100% transparent PNG background (STRICTLY NO outer squircle or circle plate). Centered (~78% canvas scale, balanced 1:1 aspect ratio), a plump, pebble-smooth 3D "off-shelf archive" symbol: a rounded open tray box in smooth sea-salt blue gradient (#5AA9E6 to #3E7EA3) at the bottom, with a chubby rounded-end creamy-white (#FFFFFF) and frosted-glass downward arrow gently descending into the tray. Ultra-smooth dome surfaces, zero sharp ridges, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 2. 上架按钮图标（`btn-op-onshelf.png` · 放入薄荷青柔光立体胶囊）

**中文提示词：**
```text
Apple iOS 轻三维柔光按钮微图标（Soft 3D & Frosted Glass UI Action Icon），背景 100% 完全透明（严禁画外围背景底座！与下架图标体量完全等大协调）。画面正中是一个宽高比接近 1:1、占画布约 78% 的饱满圆润 3D「重新上架」组合符号：下半部分是一只温润青玉薄荷绿渐变（#4EC499 过渡到 #359E78）的圆角敞口浅托盘底座，正上方一枚饱满圆润的纯奶白色（#FFFFFF）融合清透薄荷磨砂毛玻璃的粗圆角向上箭头正轻盈升起。表面光滑浑圆无棱线，正上方柔和顶光，边缘泛着细腻白色玻璃高光。完全无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D button icon with frosted glassmorphism, isolated floating object on a 100% transparent PNG background (STRICTLY NO outer squircle plate, matching exact visual weight of off-shelf icon). Centered (~78% scale, balanced 1:1 aspect ratio), a plump, smooth 3D "put on shelf" symbol: a rounded open tray base in fresh jade-mint gradient (#4EC499 to #359E78) below a chubby rounded-end creamy-white (#FFFFFF) and frosted-glass upward arrow rising briskly above it. Ultra-smooth rounded surfaces, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 3. 编辑按钮图标（`btn-op-edit.png` · 放入暖奶油柔光立体胶囊，与两侧下架/删除体量等大）

**中文提示词：**
```text
Apple iOS 轻三维柔光按钮微图标（Soft 3D & Frosted Glass UI Action Icon），背景 100% 完全透明（严禁画外围背景底座！）。为了与旁边的方形收纳盒、废纸篓保持完全一致的 1:1 饱满块面平衡感，画面正中（占画布约 78%）是一枚圆润饱满的纯奶白色结合暖奶油半透明磨砂毛玻璃的圆角小便签垫板（#FFFDF7），垫板右上方斜叠着一支短胖圆润可爱的 45 度 3D 触控短铅笔：笔身采用暖琥珀金渐变（#F5B947 过渡到 #E69A28），圆润笔头与笔尾圆帽采用海盐亮蓝（#4A9BE8）质感，铅笔向下方小垫板投下一抹柔和暖色层叠微影。表面光滑圆润无棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D button icon with frosted glassmorphism, isolated floating object on a 100% transparent PNG background (STRICTLY NO outer container). Designed with a balanced 1:1 visual mass (~78% canvas scale): a plump, thick rounded mini note-tile in translucent warm-ivory frosted glass (#FFFDF7) overlapped diagonally by a chubby, cute 45-degree 3D stylus pencil featuring a warm amber-gold (#F5B947 to #E69A28) smooth barrel and rounded sea-salt blue (#4A9BE8) tip and cap, casting a soft warm layer shadow onto the mini tile. Ultra-smooth pebble curves, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 4. 删除按钮图标（`btn-op-delete.png` · 放入樱花粉危险柔光立体胶囊）

**中文提示词：**
```text
Apple iOS 轻三维柔光按钮微图标（Soft 3D & Frosted Glass UI Action Icon），背景 100% 完全透明（严禁画外围背景底座！）。画面正中是一个宽高比接近 1:1、占画布约 78% 的圆润精致 3D 小巧收纳废纸篓：桶身采用温润珊瑚柔红渐变（#EB7388 过渡到 #D65369）哑光陶瓷质感，桶身正面嵌有两条圆润微凹的浅樱粉竖向圆角短槽；桶口上方微微抬起并轻倾 8 度悬浮着一枚带圆环小提手的纯奶白融合浅粉半透明磨砂毛玻璃圆润桶盖（#FFE8EE），桶盖边缘泛着清亮白高光并向桶口投下柔和层叠微影。整体Q弹圆润不冰冷，零棱线，完全无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D button icon with frosted glassmorphism, isolated floating object on a 100% transparent PNG background (STRICTLY NO outer squircle plate). Centered (~78% canvas scale, balanced 1:1 visual mass), an adorable, plump rounded 3D trash bin: smooth coral-rose matte body (#EB7388 to #D65369) with two subtle vertical rounded pill grooves, topped by a slightly lifted, gently tilted (8-degree) translucent blush-white (#FFE8EE) frosted-glass rounded lid with a cute loop handle catching crisp white rim highlights. Ultra-smooth tactile curves, zero black outlines, no text, 1024x1024 RGBA PNG.
```

---

### 模块二：发布页「上传商品图 & 删除图片」独立完整图形按钮（2 张 · B 类整块按键）

#### 5. 发布页上传商品照片方块按钮（`tile-photo-add.png` · `1024×1024` 超椭圆底座四角透明）

**中文提示词：**
```text
Apple iOS 轻三维柔光独立方形相机上传按钮（Soft 3D & Glassmorphism Upload Tile Button）。画面中心是一个标准的苹果超椭圆圆角正方形按键底座（Squircle，圆角半径约 24%，占画布约 92%，四角以外完全透明 PNG）：底座采用清透柔和的浅雾冰蓝自上而下线性微渐变（顶部 #EDF6FD 过渡到底部 #CFE6F8），底座上边缘带有一圈清亮纯白受光内高光边。底座正中悬浮着一台圆润可爱的奶白色（#FFFFFF）哑光 3D 微单小相机（占底座宽度约 56%）：相机正中嵌有一枚晶莹通透的海盐亮蓝（#4A9BE8）多层磨砂琉璃圆镜头（带细腻白色镜面反光点）；相机右上方轻巧叠放着一枚圆润饱满的青玉薄荷色或晴空蓝（#3E8EDE）半透明毛玻璃圆形小徽章，内嵌纯白圆角「＋」加号。正上方柔和顶光，相机向冰蓝底座投射柔和浅蓝色弥散接触阴影。无黑色描边，无任何文字，极具点击上传欲望，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D square photo-upload button tile, centered continuous-curve squircle base (~92% canvas, transparent outer corners) in airy ice-blue top-down gradient (#EDF6FD to #CFE6F8) with a crisp white top inner specular rim. Centered inside (~56% width) floats a plump, smooth creamy-white (#FFFFFF) matte 3D mini camera featuring a glossy translucent sea-salt blue (#4A9BE8) glass lens with a crisp white catchlight, accented at its upper-right corner by a raised translucent cerulean (#3E8EDE) frosted-glass circular badge containing a clean rounded white plus (+) symbol. Soft blue-tinted contact shadow on the squircle base. Zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 6. 已上传照片右上角删除小圆钮（`btn-photo-del.png` · `1024×1024` 正圆毛玻璃珠四周透明）

**中文提示词：**
```text
Apple iOS 轻三维磨砂毛玻璃圆形关闭小按钮（Frosted Glass Circular Close Badge），画面正中是一颗饱满圆润的正圆形半透明深岩雾蓝灰磨砂毛玻璃扁圆珠按键（直径占画布约 86%，正圆以外四周区域 100% 完全透明 PNG！）。圆珠底座采用温润通透的深雾蓝灰磨砂玻璃质感（#3B5164，带轻微半透明玉石感与顶部清亮白色玻璃受光高光环），圆珠正中心嵌有一枚粗细匀称、四个末端全圆角的纯奶白色（#FFFFFF）微立体「×」交叉关闭符号，光滑无棱角。完全无黑色描边，无多余文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple iOS frosted-glass circular close badge button, centered plump circular semi-translucent slate-navy (#3B5164) frosted-glass button disc (~86% canvas diameter) on a 100% transparent PNG background outside the circle, featuring a delicate white top specular rim highlight and a clean, chubby rounded-end pure-white (#FFFFFF) 3D cross (×) symbol centered inside. Smooth pebble finish, zero black outlines, no text, 1024x1024 RGBA PNG.
```

---

### 模块三：深海盐蓝主按钮（Primary Buttons）专用「珠光白高亮」3D 微图标（6 张 · 无底座全透明）

> ⚠️ **色彩特别提醒**：以下 6 张图标将放在**深海盐蓝（`#3E7EA3`）实心果冻主按钮**左侧！主体必须以**纯奶白（`#FFFFFF`）+ 清亮冰晶毛玻璃（`#E2F2FF`）+ 局部暖奶油金（`#FFE08A`）点缀**为主，确保放在蓝色按钮上明亮清透、绝不顺色隐身！

#### 7. 登录 / 注册下一步主按钮图标（`btn-pri-login.png` · 用于登录、发送验证码、下一步、完成注册）

**中文提示词：**
```text
Apple iOS 深色主按钮内嵌专用高亮 3D 微图标（Luminous Pearl-White Soft 3D Button Glyph），背景 100% 完全透明（严禁画外围方形或圆形底板！）。画面正中（占画布约 78%）是一枚代表「安全登录进入」的圆润立体组合符号：右侧是一道饱满圆润的冰晶半透明磨砂毛玻璃圆弧门框C形环（#DCEFFE，带明亮纯白高光边），一枚温润纯奶白色（#FFFFFF）哑光白瓷圆头向右箭头正轻快穿入弧环中心，箭头尾部点缀着一颗小巧晶莹的暖奶油金（#FFE08A）圆珠微光点。表面如水滴般光滑浑圆无棱线，无暗黑阴影，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG luminous pearl-white soft 3D icon designed for placement on dark-blue primary buttons, isolated on a 100% transparent PNG background (NO background plate). Centered (~78% canvas scale), a plump, smooth 3D "sign-in enter" symbol: a smooth rounded C-shaped portal arc in bright translucent ice-white (#DCEFFE) frosted glass on the right, with a chubby rounded-end pure creamy-white (#FFFFFF) matte arrow gliding rightward into its center, accented with a tiny luminous warm-gold (#FFE08A) bead dot near the tail. Ultra-smooth pebble curves, bright clean shading, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 8. 下单 / 模拟付款主按钮图标（`btn-pri-buy.png` · 用于商品详情「下单」、订单详情「模拟付款」）

**中文提示词：**
```text
Apple iOS 深色主按钮内嵌专用高亮 3D 微图标，背景 100% 完全透明（严禁画外围底板！）。画面正中（占画布约 78%）是一只圆润挺括、微倾 10 度的纯奶白色（#FFFFFF）哑光白瓷 3D 迷你手提购物袋：袋口配有清透晶莹的冰晶白半透明磨砂毛玻璃拱形提手（#E4F3FF），购物袋正面右下角轻巧叠嵌着一枚圆润闪亮的暖奶油金（#FFE08A）小圆盾闪电/星芒微章（代表快捷安心下单付款）。表面如凝脂白玉般光滑圆润无棱线，无脏灰阴影，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG luminous pearl-white soft 3D icon for dark-blue primary buttons, isolated on a 100% transparent PNG background (NO background plate). Centered (~78% scale), an adorable plump rounded 3D pure creamy-white (#FFFFFF) matte shopping tote bag gently angled at 10 degrees with a translucent ice-crystal (#E4F3FF) frosted-glass arched handle, accented at its front bottom-right corner with a glossy warm-cream gold (#FFE08A) circular lightning/sparkle badge. Ultra-smooth curves, bright clean highlights, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 9. 发布 / 保存主按钮图标（`btn-pri-publish.png` · 用于发布页底栏「发布/保存」、资料设置「保存」）

**中文提示词：**
```text
Apple iOS 深色主按钮内嵌专用高亮 3D 微图标，背景 100% 完全透明（严禁画外围底板！）。画面正中（占画布约 78%）是一枚代表「发布上架」的圆润立体符号：下方是一朵饱满轻盈的冰晶白半透明磨砂毛玻璃圆润小云托（#DCEFFE，边缘泛着明亮纯白玻璃折射光），正中一枚温润纯奶白色（#FFFFFF）哑光白瓷圆头粗箭头稳健向上升起，箭头右上方伴随一颗小巧圆润的暖奶油金（#FFE08A）四芒微星点。光滑浑圆零棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG luminous pearl-white soft 3D icon for dark-blue primary buttons, isolated on a 100% transparent PNG background (NO background plate). Centered (~78% scale), a plump rounded 3D "publish upload" symbol: a chubby pure creamy-white (#FFFFFF) matte upward arrow rising cleanly from a soft translucent ice-white (#DCEFFE) frosted-glass rounded cloud cushion at its base, with a tiny rounded warm-gold (#FFE08A) sparkle star at upper-right. Ultra-smooth dome surfaces, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 10. 聊天发送主按钮图标（`btn-pri-send.png` · 用于订单聊天底栏「发送」按钮）

**中文提示词：**
```text
Apple iOS 深色主按钮内嵌专用高亮 3D 微图标，背景 100% 完全透明（严禁画外围底板！）。画面正中（占画布约 80%）是一架向右上 35 度角轻快飞翔的圆润 Q 弹 3D 纸飞机：摒弃尖锐扎手的折纸锐角，所有机翼边缘与机头均做成圆滑饱满的水滴圆角鹅卵石流线体（Smooth Pebble Paper Plane）；左右两片机翼采用温润纯奶白色（#FFFFFF）哑光白瓷质感，机腹中央折槽采用清透晶莹的冰蓝色半透明磨砂毛玻璃（#CBE8FE），机尾后方轻巧点缀一颗细小的暖奶油金微光圆珠。光滑圆润零死板硬折痕，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG luminous pearl-white soft 3D paper-plane send icon for dark-blue primary buttons, isolated on a 100% transparent PNG background (NO background plate). Centered (~80% scale), a friendly, plump, pebble-rounded 3D paper airplane soaring toward upper-right at 35 degrees: softly filleted rounded wingtips and nose (no sharp spiky paper edges), pure creamy-white (#FFFFFF) smooth satin wings paired with a translucent ice-blue (#CBE8FE) frosted-glass center fold and a tiny warm-gold (#FFE08A) trailing sparkle bead. Ultra-smooth curves, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 11. 搜索主按钮图标（`btn-pri-search.png` · 用于搜索页右上角深蓝「搜索」按钮）

**中文提示词：**
```text
Apple iOS 深色主按钮内嵌专用高亮 3D 微图标，背景 100% 完全透明（严禁画外围底板！）。画面正中（占画布约 80%）是一枚呈 45 度角放置的饱满圆润珠光白 3D 放大镜：圆环形粗镜框与短胖圆润胶囊手柄采用温润明亮的纯奶白色（#FFFFFF）哑光白瓷质感，镜框内部嵌有一片清透晶莹的浅冰蓝半透明磨砂毛玻璃镜片（#D4EEFF），镜片左上方带有一道清脆明亮的纯白弧形反光高光。表面浑圆丝滑零棱线，在深蓝底色上极其醒目清晰，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG luminous pearl-white soft 3D magnifying glass icon tailored for dark-blue primary buttons, isolated on a 100% transparent PNG background (NO background plate). Centered (~80% scale), a plump, chubby 45-degree 3D search magnifier: pure creamy-white (#FFFFFF) smooth matte circular bezel and rounded capsule handle, enclosing a semi-translucent ice-crystal (#D4EEFF) frosted-glass lens with a bright white curved specular glint. Ultra-smooth pebble finish, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 12. 确认面交 / 提交认证 / 通过主按钮图标（`btn-pri-confirm.png` · 用于确认已面交、提交认证、去认证、管理员通过）

**中文提示词：**
```text
Apple iOS 深色主按钮内嵌专用高亮 3D 微图标，背景 100% 完全透明（严禁画外围方形底板！）。画面正中（占画布约 78%）是一枚饱满圆润的 3D 安心确认小盾章：盾章主体采用清透晶莹的冰白融合浅薄荷青半透明磨砂毛玻璃材质（#E0F7F0，边缘泛着明亮纯白玻璃高光环），盾心正中微微凸起一枚厚实圆润、末端全圆角的纯奶白色（#FFFFFF）哑光白瓷对勾「✓」符号。光滑浑圆无棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG luminous pearl-white & mint-ice soft 3D confirmation badge icon for dark-blue primary buttons, isolated on a 100% transparent PNG background (NO outer square plate). Centered (~78% scale), a plump, smoothly rounded 3D shield-medallion in luminous translucent mint-white (#E0F7F0) frosted glass with bright white rim highlights, featuring a raised, chubby rounded-end pure creamy-white (#FFFFFF) matte 3D checkmark centered on front. Ultra-smooth curves, zero black outlines, no text, 1024x1024 RGBA PNG.
```

---

### 模块四：微信登录、通知胶囊、认证上传与次级操作专用 3D 微图标（6 张 · 浅底高对比 · 无底座全透明）

#### 13. 登录页「微信登录」按钮图标（`btn-wechat.png` · 放入珠光白描边立体按钮）

**中文提示词：**
```text
Apple iOS 轻三维柔光微信双气泡按钮微图标（Soft 3D & Frosted Glass WeChat Bubbles Glyph），背景 100% 完全透明（严禁画外围正方形圆角底座！只要中间两个交叠气泡本身！）。画面正中（占画布宽度约 82%）是两个一左一右错落交叠的饱满圆润 3D 聊天气泡：左后方较大的圆润气泡采用清透温润的青玉翠绿渐变（#46C690 过渡到 #2FA876）哑光材质，上面嵌有两颗纯白小圆点；右前方略小的交叠气泡采用纯奶白色融合浅薄荷半透明磨砂毛玻璃材质（#F2FBF7），上面嵌有两颗青玉绿小圆点，前后气泡之间有细腻柔和的层叠微影与明亮玻璃高光边。光滑浑圆无棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D overlapping chat-bubbles icon for WeChat Login button, isolated directly on a 100% transparent PNG background (STRICTLY NO outer squircle background plate—only the two interlocking bubbles!). Centered (~82% width), a larger plump rounded bubble at upper-left in smooth jade-emerald gradient (#46C690 to #2FA876) with two tiny white dots, overlapped at lower-right by a smaller plump rounded bubble in luminous creamy-white & mint frosted glass (#F2FBF7) with two tiny jade dots, casting a delicate soft layer shadow. Ultra-smooth pebble curves, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 14. 通知页「只看未读」筛选胶囊图标（`btn-chip-unread.png` · 放入左上筛选胶囊）

**中文提示词：**
```text
Apple iOS 轻三维柔光胶囊微图标，背景 100% 完全透明（严禁画外围方形底板！）。画面正中（占画布约 78%）是一只圆润可爱的海盐亮蓝渐变（#5AA9E6 过渡到 #3E7EA3）3D 迷你小铃铛（或圆角小信封），腰部套着一圈半透明冰蓝磨砂毛玻璃环，右上角醒目叠嵌着一颗饱满晶莹的暖琥珀奶油金（#F5B642）3D 圆球未读提醒点（带清亮白色高光点）。光滑圆润无棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D unread-filter icon for notification chip, isolated on a 100% transparent PNG background (NO outer plate). Centered (~78% scale), a cute, chubby rounded 3D mini notification bell in smooth sea-salt blue gradient (#5AA9E6 to #3E7EA3) with a translucent ice-blue frosted-glass rim, featuring a prominent glossy warm-amber gold (#F5B642) 3D unread sphere badge nestled on its upper-right shoulder. Ultra-smooth curves, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 15. 通知页「全部已读」操作胶囊图标（`btn-chip-readall.png` · 放入右上操作胶囊）

**中文提示词：**
```text
Apple iOS 轻三维柔光胶囊微图标，背景 100% 完全透明（严禁画外围方形底板！）。画面正中（占画布宽度约 82%）是两枚一前一后优雅平行错落交叠的饱满圆润 3D「双对勾（✓✓）」全部已读符号：后方微露的对勾采用清透的海盐晴空蓝半透明磨砂毛玻璃材质（#6EB5E5），前方主对勾采用温润饱满的青玉薄荷绿渐变（#44BE8F 过渡到 #2FA377）哑光圆管材质，末端如水滴鹅卵石般浑圆顺滑，前后对勾交叠处有细腻柔和的立体微影与玻璃高光边。零棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D double-checkmark "read all" icon for notification chip, isolated on a 100% transparent PNG background (NO outer plate). Centered (~82% width), two plump, chubby rounded-end 3D checkmarks (✓✓) staggered side-by-side with subtle overlap: the rear checkmark in translucent sky-blue (#6EB5E5) frosted glass, and the front main checkmark in smooth jade-mint gradient (#44BE8F to #2FA377) with luscious water-droplet rounded tips and soft layer shadow. Zero sharp ridges, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 16. 认证与资料页「上传校园卡 / 上传头像」按钮图标（`btn-camera-upload.png` · 放入浅冰蓝次按钮）

**中文提示词：**
```text
Apple iOS 轻三维柔光按钮微图标，背景 100% 完全透明（严禁画外围底板！）。画面正中（占画布宽度约 80%）是一台饱满圆润的 3D 拍照上传小相机：机身采用温润海盐亮蓝渐变（#5AA9E6 过渡到 #3E7EA3）哑光材质，顶部带有可爱的圆角小取景凸起与暖奶油金（#F5B947）小快门圆钮，机身正中嵌有一圈纯奶白色镜头环与清透冰晶磨砂毛玻璃圆镜片（带清亮白色反光高光）。光滑浑圆零棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D camera upload icon for light pastel secondary buttons, isolated on a 100% transparent PNG background (NO outer plate). Centered (~80% width), a plump, chubby rounded 3D mini camera: smooth sea-salt blue gradient (#5AA9E6 to #3E7EA3) matte body, tiny warm-gold (#F5B947) shutter button on top, and a creamy-white lens ring framing a luminous translucent ice-blue frosted-glass lens with a crisp white glint. Ultra-smooth pebble curves, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 17. 订单与详情页「本单聊天 / 查看聊天记录 / 查看订单」图标（`btn-chat-order.png` · 放入浅冰蓝次按钮）

**中文提示词：**
```text
Apple iOS 轻三维柔光按钮微图标，背景 100% 完全透明（严禁画外围底板！）。画面正中（占画布宽度约 80%）是两个一前一后圆润交叠的 3D 订单沟通双气泡：左后方气泡采用温润海盐行动蓝渐变（#5AA9E6 过渡到 #3E7EA3），右前方略小的气泡采用清透纯白融合浅冰蓝半透明磨砂毛玻璃材质（#EDF7FE），内部嵌有两条简洁圆润的海盐蓝胶囊短横线。光滑圆润无棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D order-chat icon for secondary buttons, isolated on a 100% transparent PNG background (NO outer plate). Centered (~80% width), two overlapping plump rounded 3D chat bubbles: a smooth sea-salt blue gradient (#5AA9E6 to #3E7EA3) rear bubble and a translucent ice-white (#EDF7FE) frosted-glass front bubble featuring two clean rounded sky-blue pill lines inside. Ultra-smooth curves, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 18. 全站通用「重试 / 撤回申诉」回旋刷新图标（`btn-retry-undo.png` · 放入浅冰蓝胶囊按钮）

**中文提示词：**
```text
Apple iOS 轻三维柔光按钮微图标，背景 100% 完全透明（严禁画外围底板！）。画面正中（占画布约 78%）是一枚饱满圆润的 3D 环形回旋刷新箭头符号：圆弧主体由海盐亮蓝（#4A9BE8）平滑渐变过渡到尾端的清透冰蓝半透明磨砂毛玻璃（#CBE8FE），首端带有一个圆润饱满的水滴圆角箭头尖端，整个圆环横截面如圆润软胶圆管般光滑无棱线。无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D circular refresh/retry arrow icon, isolated on a 100% transparent PNG background (NO outer plate). Centered (~78% scale), a plump, tubular 3D 300-degree circular arrow transitioning smoothly from vibrant sea-salt blue (#4A9BE8) at the rounded arrowhead tip into translucent ice-blue (#CBE8FE) frosted glass toward the tail. Smooth water-droplet rounded cross-section, zero sharp ridges, zero black outlines, no text, 1024x1024 RGBA PNG.
```

#### 19. 搜索页与发布页「面交地点筛选」定位微图标（`btn-filter-pin.png` · 放入「全部面交点」与地点选择框）

**中文提示词：**
```text
Apple iOS 轻三维柔光微图标，背景 100% 完全透明（严禁画外围底板！）。画面正中（占画布高度约 80%）是一枚饱满圆润如水滴宝石般的 3D 校园面交定位针（Map Pin）：水滴外身采用温润海盐行动蓝渐变（#5AA9E6 过渡到 #3E7EA3），中心圆孔嵌有一颗晶莹通透的纯奶白与暖奶油半透明磨砂毛玻璃圆心珠（#FFFDF5），上边缘泛着清亮纯白受光高光。表面浑圆光滑零棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D map-pin filter icon, isolated on a 100% transparent PNG background (NO outer plate). Centered (~80% height), a plump, teardrop-smooth 3D location pin in vibrant sea-salt blue gradient (#5AA9E6 to #3E7EA3) with a luminous creamy-white (#FFFDF5) frosted-glass inner circle bead catching crisp top-left specular highlights. Ultra-smooth pebble curves, zero black outlines, no text, 1024x1024 RGBA PNG.
```

---

### 模块五：危险与警示操作专用「珊瑚柔粉」3D 微图标（1 张通用 · 无底座全透明）

#### 20. 退出登录 / 取消订单 / 申诉 / 举报警示图标（`btn-warn-action.png` · 放入浅樱粉危险立体按钮）

**中文提示词：**
```text
Apple iOS 轻三维柔光警示操作按钮微图标（Soft 3D & Frosted Glass Coral Warning/Exit Badge），背景 100% 完全透明（严禁画外围正方形底板！）。画面正中（占画布约 78%）是一枚饱满圆润的正圆形双材质警示徽章：外圈采用温润珊瑚柔红渐变（#EB7388 过渡到 #D65369）结合浅樱粉半透明磨砂毛玻璃光泽，顶部带有细腻白色受光高光环；圆心内部微微凸起一枚粗细匀称、末端如水滴般圆滑的纯奶白色（#FFFFFF）圆头感叹提示符「!」。整体温润精致有品质感，绝不刺眼粗糙，零棱线，无黑色描边，无文字，1024x1024 透明 PNG。
```

**English Prompt (Optional):**
```text
Apple HIG soft 3D coral warning/caution action icon for danger/exit buttons, isolated on a 100% transparent PNG background (NO outer square plate). Centered (~78% scale), a plump, smooth circular medallion crafted in coral-rose gradient (#EB7388 to #D65369) blended with translucent blush frosted glass and a crisp white top rim highlight, featuring a raised, chubby rounded-end pure creamy-white (#FFFFFF) exclamation mark (!) centered inside. Smooth pebble finish, zero black outlines, no text, 1024x1024 RGBA PNG.
```

---

## 四、通用反向提示词（Negative Prompt，可全局复用）

```text
flat 2D vector, thick black outlines, dark strokes, chiseled bevel ridge, sharp central spine, faceted low-poly creases, text, Chinese characters, English letters, numbers, outer squircle frame on A-class icons, white background, black background, harsh black drop shadow, cropped edges, watermark
```

---

## 五、DSH 出图后 20 秒核对表

1. **共 20 张独立 PNG**，放入桌面 `全站按钮素材/` 文件夹，文件名与上表一致。
2. **底座核对**：只有 `tile-photo-add.png`（带超椭圆底座）和 `btn-photo-del.png`（正圆毛玻璃珠）有按键底托，**其余 18 张全部无外框底座、100% 透明背景直接悬浮主体**。
3. **深蓝主按钮专用 6 张（`btn-pri-*.png`）核对**：主体必须是**明亮珠光奶白色 + 冰晶毛玻璃**（不要画成深蓝色，否则放到深蓝主按钮上会看不清）。
4. **「我的商品」4 张（`btn-op-*.png`）核对**：`下架 / 上架 / 编辑 / 删除` 四个图标在画布中的视觉大小一致（均约占 78%、接近 1:1 饱满块面），放到等宽三联按钮栏里绝对平衡！
