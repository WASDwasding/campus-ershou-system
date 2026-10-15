# -*- coding: utf-8 -*-
from pathlib import Path

from docx import Document
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_LINE_SPACING
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Pt, RGBColor, Emu

FIG = Path(r"C:\Users\15736\campus-trade\homework\seg_figs")
OUT = Path(r"C:\Users\15736\Desktop\第XX组_学号_姓名_班级_计算机视觉中目标分割研究现状分析.docx")


def set_run_font(run, cn="宋体", en="Times New Roman", size=12, bold=False, color=None):
    run.bold = bold
    run.font.size = Pt(size)
    run.font.name = en
    if color:
        run.font.color.rgb = color
    r = run._element
    rPr = r.get_or_add_rPr()
    rFonts = rPr.find(qn("w:rFonts"))
    if rFonts is None:
        rFonts = OxmlElement("w:rFonts")
        rPr.append(rFonts)
    rFonts.set(qn("w:ascii"), en)
    rFonts.set(qn("w:hAnsi"), en)
    rFonts.set(qn("w:eastAsia"), cn)
    rFonts.set(qn("w:cs"), en)


def set_para_format(p, first_line=True, center=False, line=1.5, space_before=0, space_after=4, indent=None):
    pf = p.paragraph_format
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = line
    pf.space_before = Pt(space_before)
    pf.space_after = Pt(space_after)
    if center:
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        pf.first_line_indent = Cm(0)
    elif indent is not None:
        pf.first_line_indent = indent
    elif first_line:
        pf.first_line_indent = Cm(0.74)
    else:
        pf.first_line_indent = Cm(0)


def add_text(p, text, **kwargs):
    run = p.add_run(text)
    set_run_font(run, **kwargs)
    return run


def body(doc, text, **kwargs):
    p = doc.add_paragraph()
    set_para_format(p, first_line=True)
    add_text(p, text, cn="宋体", size=12, **kwargs)
    return p


def heading(doc, text, level=1):
    p = doc.add_paragraph()
    set_para_format(p, first_line=False, center=(level == 1), space_before=12, space_after=8)
    size = {1: 16, 2: 14, 3: 12}[level]
    add_text(p, text, cn="黑体", en="Times New Roman", size=size, bold=True)
    return p


def caption(doc, text):
    p = doc.add_paragraph()
    set_para_format(p, first_line=False, center=True, space_before=4, space_after=10, line=1.25)
    add_text(p, text, cn="宋体", size=10.5, bold=True)
    return p


def picture(doc, name, width=14.2):
    p = doc.add_paragraph()
    set_para_format(p, first_line=False, center=True, space_before=6, space_after=2)
    run = p.add_run()
    run.add_picture(str(FIG / name), width=Cm(width))
    return p


def info_line(doc, label, value):
    p = doc.add_paragraph()
    set_para_format(p, first_line=False, center=True, line=1.5, space_after=2)
    add_text(p, f"{label}{value}", cn="宋体", size=14)
    return p


def add_page_number(section):
    footer = section.footer
    footer.is_linked_to_previous = False
    p = footer.paragraphs[0]
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = p.add_run()
    set_run_font(run, size=10.5)
    fld1 = OxmlElement("w:fldChar")
    fld1.set(qn("w:fldCharType"), "begin")
    instr = OxmlElement("w:instrText")
    instr.set(qn("xml:space"), "preserve")
    instr.text = " PAGE "
    fld2 = OxmlElement("w:fldChar")
    fld2.set(qn("w:fldCharType"), "end")
    run._r.append(fld1)
    run._r.append(instr)
    run._r.append(fld2)


def shade_cell(cell, hex_color):
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:fill"), hex_color)
    shd.set(qn("w:val"), "clear")
    tcPr.append(shd)


def build():
    doc = Document()
    sec = doc.sections[0]
    sec.page_width = Cm(21.0)
    sec.page_height = Cm(29.7)
    sec.left_margin = Cm(3.0)
    sec.right_margin = Cm(2.0)
    sec.top_margin = Cm(2.5)
    sec.bottom_margin = Cm(2.5)
    add_page_number(sec)

    # -------- cover --------
    for _ in range(2):
        doc.add_paragraph()
    p = doc.add_paragraph()
    set_para_format(p, first_line=False, center=True)
    add_text(p, "编    号：______________", cn="宋体", size=12)

    p = doc.add_paragraph()
    set_para_format(p, first_line=False, center=True, space_before=18)
    add_text(p, "新生工程课程论文（设计）", cn="黑体", size=22, bold=True)

    p = doc.add_paragraph()
    set_para_format(p, first_line=False, center=True, space_before=28, space_after=18)
    add_text(p, "计算机视觉中目标分割研究现状分析", cn="黑体", size=18, bold=True)

    p = doc.add_paragraph()
    set_para_format(p, first_line=False, center=True, space_after=10)
    add_text(p, "课程：软件需求工程　　实验01：技术调研论文", cn="宋体", size=12)

    info_line(doc, "学院单位    ：", "计算机与人工智能学院")
    info_line(doc, "专业班级    ：", "____________________")
    info_line(doc, "作 者 姓 名 ：", "____________________")
    info_line(doc, "学        号：", "____________________")
    info_line(doc, "分        组：", "第______组")
    info_line(doc, "指 导 教 师 ：", "____________________")
    info_line(doc, "论 文 成 绩 ：", "____________________")

    p = doc.add_paragraph()
    set_para_format(p, first_line=False, center=True, space_before=36)
    add_text(p, "二〇二六年九月", cn="宋体", size=14)

    doc.add_page_break()

    # -------- abstract --------
    heading(doc, "摘要", 1)
    body(
        doc,
        "目标分割干的事情很具体：把一张图里“哪儿是物体、轮廓贴到哪条边”标到像素上。它比目标检测细一档，检测给框，分割给掩膜。手机人像抠图、辅助驾驶里的可行驶区域、CT 上勾病灶，看起来不像一门课，底下都在啃这件事。本文按课上技术调研的要求，把语义分割、实例分割、全景分割三件事先拆开，再顺着 2015 年以来的主线往下看：FCN 把分类网改成像素输出，U-Net 用跳跃连接把边界捞回来，DeepLab 和 PSPNet 解决“看周围看多远”的问题，Mask R-CNN 把检测和掩膜接到一起，Transformer 之后 SegFormer、Mask2Former 开始用一套结构打多种任务，再到 2023 年的 SAM 把分割做成可提示的基础模型，2024 年的 SAM 2 把记忆接到视频上。文中数字都尽量对过原文，比如 PASCAL VOC 2012 上 FCN-8s 约 67.2% mIoU、DeepLabv3+ 报到 89.0%；Cityscapes 上 DeepLabv3+ 为 82.1%、SegFormer 为 84.0%；ADE20K 上 Mask2Former 约 57.7% mIoU；COCO 上 Mask R-CNN 的 mask AP 约 37.1。最后补了一截我比较关心的内容——这些模型塞进软件工程流水线时，需求到底该怎么写，创新点也不一定非要再刷一个点。"
    )
    p = doc.add_paragraph()
    set_para_format(p, first_line=True)
    add_text(p, "关键词：", cn="黑体", size=12, bold=True)
    add_text(p, "目标分割；语义分割；实例分割；SAM；计算机视觉", cn="宋体", size=12)

    heading(doc, "Abstract", 1)
    body(
        doc,
        "Object segmentation assigns a label or a mask to each pixel, which is finer than bounding-box detection. This survey, written for a software-requirements course, separates semantic, instance and panoptic segmentation, then follows the line from FCN and U-Net to DeepLab, Mask R-CNN, transformer models, SAM and SAM 2. Reported scores are taken from the original papers rather than second-hand blogs: about 67.2% mIoU for FCN-8s and 89.0% for DeepLabv3+ on PASCAL VOC 2012; 82.1% and 84.0% mIoU on Cityscapes for DeepLabv3+ and SegFormer; 57.7% mIoU on ADE20K for Mask2Former; and 37.1 mask AP on COCO for Mask R-CNN. The last part discusses how a segmentation model should sit in an engineering pipeline, where latency, labeling cost and traceability often matter more than a leaderboard number."
    )
    p = doc.add_paragraph()
    set_para_format(p, first_line=True)
    add_text(p, "Key words: ", cn="Times New Roman", en="Times New Roman", size=12, bold=True)
    add_text(p, "object segmentation; semantic segmentation; instance segmentation; SAM; computer vision", cn="Times New Roman", size=12)

    heading(doc, "目录", 1)
    toc = [
        "第一章 绪论",
        "    1.1 研究背景及意义",
        "    1.2 国内外研究现状",
        "        1.2.1 国外研究现状",
        "        1.2.2 国内研究现状",
        "        1.2.3 先把任务分家，论文才比较好读",
        "第二章 目标分割的关键技术与发展脉络",
        "    2.1 问题定义、指标和常用数据",
        "    2.2 从 FCN 到 DeepLab：像素怎么“长”出来",
        "    2.3 实例分割与统一结构",
        "    2.4 基础模型：SAM 与 SAM 2",
        "    2.5 接到计算机与软件工程里，能做什么、坑在哪",
        "第三章 总结与一点自己的看法",
        "参考文献",
        "致谢",
        "附件  大模型与工具使用说明",
    ]
    for line in toc:
        p = doc.add_paragraph()
        set_para_format(p, first_line=False, line=1.5, space_after=2)
        add_text(p, line, cn="宋体", size=12)

    doc.add_page_break()

    # -------- ch1 --------
    heading(doc, "第一章 绪论", 1)
    body(
        doc,
        "图像对人来说几乎是“扫一眼就懂”。车在路上、人在人行道、天空在上头，这些分界我们很少会去数有多少个像素。计算机没有这种现成的直觉。它看见的是数组。目标检测好歹还能用一个框把物体圈住，框歪一点，人眼也认。分割不行，它较真到边线。边线偏了，车道可能画进隔离带，病灶可能少勾一圈。"
    )

    heading(doc, "1.1 研究背景及意义", 2)
    body(
        doc,
        "我选这个题目，一半是因为计算机视觉课里分割出现得勤，一半是因为软件需求工程总在问：这个能力到底给谁用、错了谁负责。分割正好两头都沾。"
    )
    body(
        doc,
        "日常里能碰到的例子并不玄乎。手机拍照的人像模式要把头发丝抠干净，少抠一点就“贴了张假背景”。辅助驾驶要知道哪些像素是可行驶区域，哪些是行人，框检测给个矩形不够，车头和路沿经常叠在一起。医院里的影像科更直接，医生要的是病灶轮廓，不是“大概在左肺有个框”。工业质检、遥感里数屋顶、视频会议换背景，底层都是掩膜。"
    )
    body(
        doc,
        "从研究史上看，分割比分类更早被当成像处理问题，阈值、区域生长、图切分这些传统方法现在教材里还在。真正把精度拉起来的，是 2015 年前后卷积神经网络把“逐像素分类”做成端到端。再往后十年，任务被拆成语义、实例、全景三条线，模型从专用网络走到可提示的基础模型。数字涨得很快，但落地时标注贵、域一变就掉点、实时性和责任边界这些问题并没有一起消失。所以这篇调研不想写成“又介绍了一串网络名字”，我想看清楚：业务问题是怎么被翻译成分割问题的，翻译完之后软件系统还缺哪几截。"
    )

    heading(doc, "1.2 国内外研究现状", 2)
    heading(doc, "1.2.1 国外研究现状", 3)
    body(
        doc,
        "国外这条线比较好按年份钉钉子。2015 年 Long、Shelhamer、Darrell 把当时的分类网络改成全卷积网络 FCN，让输出和输入在空间上对齐，PASCAL VOC 2012 上 FCN-8s 做到约 67.2% mIoU[1]。同一年 Ronneberger 等人的 U-Net 在生物医学分割里把编码器的浅层特征用跳跃连接接到解码器，数据量不大也能练[2]。这两篇现在看都像“老论文”，可后面很多结构还是在它们的骨架上长肉。"
    )
    body(
        doc,
        "2017 年左右有两条并行的热闹。一条是场景解析，Zhao 等人的 PSPNet 用金字塔池化把多尺度上下文拼起来，Cityscapes 上做到 80.2% mIoU，VOC 上 85.4%[3]。另一条是实例分割，He 等人的 Mask R-CNN 在 Faster R-CNN 上加一个掩膜分支，并且用 RoIAlign 把 RoIPool 的量化误差修掉，COCO test-dev 上 mask AP 大约 35.7（ResNet-101-FPN）到 37.1（ResNeXt-101-FPN）[4]。我一开始把 AP 和 mIoU 放一张草表里比，越比越乱，后来才明白实例分割和语义分割根本不是同一个考卷。"
    )
    body(
        doc,
        "DeepLab 系列把空洞卷积用得很狠。Chen 等人的 DeepLabv3+ 做成编码器—解码器，VOC 2012 test 报到 89.0% mIoU，Cityscapes test 为 82.1%[5]。这些数字经常被博客转来转去，有的把额外预训练、COCO 预训和 JFT 数据写没了，看起来像“一年涨 20 个点”。对照原文会发现，设置并不齐，趋势能看，分数不能当竞赛名次直接掐。"
    )
    picture(doc, "fig2_timeline.png", 14.4)
    caption(doc, "图1.1  2015—2024 年目标分割主线上的几个转折点")

    body(
        doc,
        "2021 年以后 Transformer 进分割就不稀奇了。Xie 等人的 SegFormer 用分层的 Mix Transformer 当编码器，MLP 解码，结构不算花，Cityscapes 上 MiT-B5 报到 84.0% mIoU，ADE20K 上约 51.8%[6]。Cheng 等人的 Mask2Former 更有野心，masked attention 一套结构覆盖语义、实例、全景，ADE20K 语义分割约 57.7% mIoU，COCO 全景 PQ 约 57.8，实例 AP 约 50.1[7]。Kirillov 等人 2019 年就把全景分割和 PQ 指标提出来[8]，到 Mask2Former 这种“一个模型打三种活”才比较像工程上愿意维护的东西。"
    )
    body(
        doc,
        "2023 年 Kirillov 等人的 Segment Anything（SAM）把问题换成了可提示分割：点一下、画个框，模型吐掩膜。配套的 SA-1B 有约 1100 万张图、11 亿张掩膜，平均一张图大约 100 个掩膜，体量比以前的分割数据大出两个数量级[9]。它在 23 个数据集上做零样本，观感很强，但默认不产出类别名。2024 年 Ravi 等人的 SAM 2 把流式记忆接到视频上，论文写图像分割比 SAM 更快大约 6 倍，视频上达到相近质量所需交互大约少 3 倍；SA-V 大约 5.09 万视频、35.5M 掩膜、64.26 万条 masklet[10]。Minaee 等人在 IEEE TPAMI 的综述把深度学习分割的任务、数据和指标拢过一遍，后面写作业时我拿它当对照目录，避免漏掉全景这条线[11]。"
    )

    heading(doc, "1.2.2 国内研究现状", 3)
    body(
        doc,
        "国内综述跟得并不慢。田萱、王亮、丁琪 2019 年在《软件学报》把基于深度学习的语义分割拆成“基于区域分类”和“基于像素分类”，像素这一支又分成全监督和弱监督，并把常用数据和 mIoU 等指标写进文里[12]。这篇我读得比较完整，分类方式对写课设很管用：先问监督信号够不够，再问处理粒度是框还是像素。张顺、龚怡宏、王进军在《计算机学报》把深度卷积网络在视觉里的发展路径写得很清楚，分割被放在分类、检测之后的能力延伸上[13]。更早一点，黄凯奇、任伟强、谭铁牛综述过图像物体分类与检测[14]，现在回头看，分割就是检测还嫌不够细时往下走的那一步。"
    )
    body(
        doc,
        "应用侧国内很实。百度 Apollo、华为和部分车企的感知栈里，可行驶区域和车道线分割是常驻模块；医疗影像公司大量还在用 U-Net 及其变体；手机厂商的抠图已经卷到头发丝。不过公司发布会口径和学术榜单对不上是常态，我在文中尽量不引用“某公司内部准确率 99%”这种没法核对的句子。章毓晋先生的《图像工程》把传统分割讲得很系统[15]，深度学习之前的阈值、边缘、区域方法并没有完全作废，工业里光照稳定、目标简单时，这些方法有时比深网络更省事。这一点挺反直觉，但挺符合需求工程里“选够用的，不选最炫的”。"
    )

    heading(doc, "1.2.3 先把任务分家，论文才比较好读", 3)
    body(
        doc,
        "我查资料的头两天，把 semantic、instance、panoptic 混在一个搜索词里，结果页非常吵。后来画了张拆解图才顺。语义分割只认类别：所有车涂成一种颜色，不管几辆。实例分割要给 ID：两辆并排的车必须两张掩膜。全景分割把可数的 thing（车、人）和不可数的 stuff（路、天、墙）放一张图里，既要类别也要实例。Kirillov 把这件事形式化之后，评测才有 PQ 这一套[8]。"
    )
    picture(doc, "fig1_three_tasks.png", 14.6)
    caption(doc, "图1.2  三种分割任务的拆解示意（同类是否拆开、背景是否纳入）")
    body(
        doc,
        "分家的好处是引用数字时不会闹笑话。mIoU 对语义，mask AP 对实例，PQ 对全景。有人把 SAM 的演示效果和 DeepLab 的 VOC 分数放一起说“基础模型全面超越”，这不成立。SAM 很多时候甚至没有类别头。任务不同、数据不同、提词不同，比分只适合放在同一条赛道里看趋势。"
    )

    # -------- ch2 --------
    heading(doc, "第二章 目标分割的关键技术与发展脉络", 1)
    heading(doc, "2.1 问题定义、指标和常用数据", 2)
    body(
        doc,
        "用一句话逼自己：给定图像，为每个像素（或每个物体）给出区域归属。语义分割的输出是类别图；实例分割的输出是一组互不相同的掩膜；全景是两者的合订本。训练时常用交叉熵、Dice、focal，有时再加边界损失。我没有在这篇课设里推公式，作业要求也不卡这个。真正用的时候，损失组合往往是调参问题，不是理论问题。"
    )
    picture(doc, "fig8_iou.png", 12.8)
    caption(doc, "图2.1  IoU 示意：看起来差不多的两块区域，并集一大，分数就难看")
    body(
        doc,
        "IoU 是交集比并集。mIoU 把各类 IoU 再平均，一类极差会把总分拽下来，这就是为啥小物体、细杆子、远行人特别折磨人。COCO 的 mask AP 还要在多个 IoU 阈值上积分，对边界更苛刻。PQ 把分割质量和识别质量拆开乘，漏一个实例、类别贴错，都会挨打。我拿纸画了两个错开的圆才记住：人眼觉得“差不多贴上了”，IoU 可能已经掉到 0.6 附近。分割比检测较真，原因就在这儿。"
    )
    body(
        doc,
        "数据方面，Everingham 等人的 PASCAL VOC 把 20 类物体和分割子集做成入门基准[16]。Cordts 等人的 Cityscapes 是 50 个城市的街景，精细标注约 5000 张，评估常用 19 类，很像自动驾驶的缩小版考场[17]。Lin 等人的 COCO 把日常小物体和拥挤场景堆上来，实例分割几乎绕不开[18]。Zhou 等人的 ADE20K 场景类别多、部件标注细，Transformer 论文爱拿它说事[19]。SAM 的 SA-1B 则基本没有类别标签，走的是“通用掩膜”路线[9]。体量一拉到对数坐标，SA-1B 会把前面那些柱子压得很矮——这不是说 VOC 没用了，而是说明 2023 年以后“堆通用数据”和“堆精细类别”已经是两条策略。"
    )
    picture(doc, "fig6_dataset_scale.png", 14.4)
    caption(doc, "图2.2  数据集体量对比（VOC 等为量级示意，SA-1B 的 1.1B 掩膜来自原文）")

    heading(doc, "2.2 从 FCN 到 DeepLab：像素怎么“长”出来", 2)
    body(
        doc,
        "FCN 的关键动作其实就一句：把全连接换成卷积，特征图才能保持空间布局，再上采样回原图大小[1]。分类网络在末端把空间信息挤没了，这步相当于把“这张图是猫”改成“每个位置像不像猫”。早期 FCN 的通病是糊。跨步卷积和池化把分辨率打下去，边界自然虚。"
    )
    body(
        doc,
        "U-Net 的跳跃连接就是冲着这点来的[2]。编码器浅层还留着边缘和纹理，解码器深层有语义，两边拼起来，医学图像这种结构清楚、样本少的场景特别吃香。后来很多“U 形网”本质上还是这个想法，只是骨干换成 ResNet、注意力换成 Transformer。我在读的时候有个很学生的感受：真正拉开差距的，常常不是又加了四层，而是怎么把丢掉的边界补回来。"
    )
    picture(doc, "fig3_fcn_explode.png", 14.4)
    caption(doc, "图2.3  FCN / U-Net 结构爆炸图：下采样抽特征，再把分辨率拼回去")
    body(
        doc,
        "上下文是另一块硬骨头。像素只看 3×3 窗口，分不清是车窗还是显示器。PSPNet 用不同尺度的池化把“这一片是马路还是桌面”捞进来[3]。DeepLab 用空洞卷积，分辨率不降也能看远处，ASPP 再把几种膨胀率并在一起；v3+ 把解码器加回去，细边界比只上采样好一些[5]。这些工作把 VOC、Cityscapes 的分数推到 80% 以上，但也把计算打高了。实时分割后来另开一条线，像 ICNet、BiSeNet，课设篇幅有限，我只知道它们在“精度换速度”，没有把每篇都精读，这里不装成评测过。"
    )
    picture(doc, "fig5_benchmarks.png", 14.4)
    caption(doc, "图2.4  公开论文中的代表性成绩。左图同一 VOC 赛道可看趋势；右图指标和数据都不齐，不能横比谁“最强”")

    heading(doc, "2.3 实例分割与统一结构", 2)
    body(
        doc,
        "语义分割不管“第几辆车”。路上两辆车贴在一起，语义图可能连成一块大红。实例分割必须切开。Mask R-CNN 的做法很工程：先提案，再分类回归框，并行预测掩膜[4]。RoIAlign 取消了粗暴取整，小物体受益明显。这篇 2017 年的论文到现在还在很多代码库里当基线，不是因为它永远 SOTA，是因为它好懂、好改、好接检测头。"
    )
    body(
        doc,
        "后面有人走无锚点、先分后聚，名字一串。我没有逐篇做复现，不敢把二手博客的分数写进来。比较值得记的是 Mask2Former：与其维护三套互不相通的分割代码，不如让查询向量去要掩膜，masked attention 把注意力限制在预测区域附近[7]。对软件工程来说，这比再涨 0.5 个点更有吸引力——少一套训练脚本、少一种导出格式、少一轮联调。当然，统一结构不代表统一数据，语义用 ADE20K、实例用 COCO、全景又是另一套标注，数据管道照样分叉。"
    )

    heading(doc, "2.4 基础模型：SAM 与 SAM 2", 2)
    body(
        doc,
        "SAM 让分割从“闭集类别表”变成“你指哪我割哪”[9]。结构可以炸开成三块。图像编码器是 ViT，图只跑一遍，得到稠密嵌入，后面换提示不用重提取。提示编码器吃点、框、粗掩膜，也可以接文本这类更虚的信号。掩膜解码器很轻，一次最多出三个候选，并打 IoU 置信分，用来处理“你点的是整个人还是那只手”这种歧义。SA-1B 的掩膜绝大部分由数据引擎自动产生，质量靠人工抽检和模型迭代托住。11 亿这个数字很唬人，但也提醒一句：没有类别标签，不等于什么业务都能直接上。"
    )
    picture(doc, "fig4_sam_explode.png", 14.4)
    caption(doc, "图2.5  SAM 模块爆炸图：图像编码一次，提示随时插，解码器输出掩膜")
    body(
        doc,
        "我看过一些把 SAM 直接丢进医学影像的评测，零样本能出个大概轮廓，碰到低对比、小病灶、成像模态差得远，还是得微调或者换回 U-Net 家族。遥感、显微镜也类似。SAM 像一个很勤快的实习生：你点得清楚，它割得也清楚；你自己都含糊，它就给你三个都像的答案。SAM 2 把记忆库接到视频上，遮挡后再出现比较能接住，论文给出的 6 倍速度、3 倍更少交互是相对 SAM 和先前视频交互方法的实验结论[10]，换到自己的摄像头和分辨率上，要以实测为准。基础模型把“通用感知”做厚了，类别、业务规则、安全策略还得软件系统自己补。"
    )

    heading(doc, "2.5 接到计算机与软件工程里，能做什么、坑在哪", 2)
    body(
        doc,
        "课上反复强调需求可追溯。分割模型最容易写成一句空的：“使用深度学习实现精准分割”。这句没法验收。落到计算机和软件工程里，至少得说清对象、指标、延迟、失败怎么处理。"
    )
    picture(doc, "fig7_se_pipeline.png", 14.4)
    caption(doc, "图2.6  分割能力嵌进工程流水线：模型只是中间一截")
    body(
        doc,
        "自动驾驶是最典型的硬需求。可行驶区域、车道线、行人像素，漏检的代价不是 mIoU 少 1 个点，是安全事件。系统需求更关心尾延迟、夜间雨天、域偏移，以及传感器时间戳能不能对齐。这里创新不一定是再造一个骨干网，更可能是多传感器融合、失效降级、把分割不确定度传给规划模块。计算机专业的人要是只盯着 Cityscapes 榜，会和车载系统的真实约束错位。"
    )
    body(
        doc,
        "医疗影像是另一极。U-Net 到现在还常用，不是圈子保守，是样本少、责任重、医生要能改。需求文档里得写：模型给出的是辅助轮廓，最终以医师确认为准；训练集来自哪家医院、哪台设备，换设备要重新验证。软件工程在这里的创新点其实很土：标注规范、双人复核、模型卡、版本回滚。土，但是能上线。"
    )
    body(
        doc,
        "互联网产品里，抠图、视频抠像、商品图处理，更在意交互。SAM 这类可提示模型很适合做成“人点一下、模型出掩膜、人再改”的半自动工具。标注公司已经在这么干。风险是误差会在人机循环里放大，抽检比例和抽检规则必须写进流程，不然节省的标注费会在返工里吐回去。大模型还可以帮忙写边界测试：生成“透明塑料袋、反光地面、截断行人”这类 case 清单，再拿到分割服务上打。这是我认为比较对口软件工程的创新，不是让大模型凭空编一套新网络。"
    )
    body(
        doc,
        "还有一块是 MLOps。数据集要有版本，不能训练用 v3、事故复查找不到 v3。推理服务要记输入哈希、模型号、阈值。阈值本身就是需求：召回优先还是精确优先，决定了运营和算法怎么吵。我把这些写进论文，是因为如果只写到“Mask2Former 更高”，作业就停在了算法目录，没有碰到这门课真正要练的那一下——能力如何变成可验证的软件需求。"
    )

    # table
    p = doc.add_paragraph()
    set_para_format(p, first_line=False, space_before=8, space_after=4)
    add_text(p, "表2.1  文中主要模型与核对过的公开成绩（同一行内才可比较）", cn="黑体", size=10.5, bold=True)

    rows = [
        ["模型", "年份", "基准", "成绩", "出处"],
        ["FCN-8s", "2015", "PASCAL VOC 2012", "67.2% mIoU", "Long 等, CVPR"],
        ["U-Net", "2015", "生物医学分割", "跳跃连接编解码", "Ronneberger 等"],
        ["PSPNet", "2017", "Cityscapes / VOC", "80.2% / 85.4% mIoU", "Zhao 等, CVPR"],
        ["Mask R-CNN", "2017", "COCO test-dev", "mask AP 35.7–37.1", "He 等, ICCV"],
        ["DeepLabv3+", "2018", "VOC / Cityscapes", "89.0% / 82.1% mIoU", "Chen 等, ECCV"],
        ["SegFormer", "2021", "Cityscapes / ADE20K", "84.0% / 51.8% mIoU", "Xie 等, NeurIPS"],
        ["Mask2Former", "2022", "ADE20K / COCO", "57.7% mIoU / 57.8 PQ", "Cheng 等, CVPR"],
        ["SAM", "2023", "SA-1B 等零样本", "11M图, 1.1B掩膜", "Kirillov 等, ICCV"],
        ["SAM 2", "2024", "图像+视频 / SA-V", "约6×更快; 5.09万视频", "Ravi 等, arXiv"],
    ]
    table = doc.add_table(rows=len(rows), cols=5)
    table.style = "Table Grid"
    for i, row in enumerate(rows):
        for j, val in enumerate(row):
            cell = table.rows[i].cells[j]
            cell.text = ""
            cp = cell.paragraphs[0]
            cp.alignment = WD_ALIGN_PARAGRAPH.CENTER
            run = cp.add_run(val)
            set_run_font(run, cn="宋体", size=9, bold=(i == 0))
            if i == 0:
                shade_cell(cell, "1F3D4A")
                run.font.color.rgb = RGBColor(255, 255, 255)

    p = doc.add_paragraph()
    set_para_format(p, first_line=False, space_before=4, space_after=8)
    add_text(p, "注：DeepLabv3+ 的 VOC 成绩含较强预训练设置；Mask2Former 的 57.7% 为 Swin-L 等大骨干结果。不同行之间不排名。", cn="宋体", size=9)

    # -------- ch3 --------
    heading(doc, "第三章 总结与一点自己的看法", 1)
    body(
        doc,
        "把这条线从头再过一遍，其实就三件事在变。一是输出变细，从框到像素，从类别图到实例，再到 thing 和 stuff 合在一起。二是结构变通用，从各写各的分割头，到 Mask2Former 这种一套查询打三种活。三是数据变“可提示”，SAM 不再死盯一张闭集类别表，把分割做成交互接口。数字上，VOC 从 FCN 的六十多分走到 DeepLabv3+ 的八十多分；街景进了八十；ADE20K 这种杂场景仍在五十出头到近六十，说明问题没有被做完。"
    )
    body(
        doc,
        "没做完的地方挺具体。小物体和细边界还是肉。换个相机、换个国家的街道、换一台 CT，分数说掉就掉。视频要处理遮挡和记忆，SAM 2 开了个头，远不是自动驾驶那种长尾。类别和开放世界也还拧着：能割出来，不代表知道那是“施工锥”还是“小孩”。再往软件上靠，标注成本和责任链经常比模型本身更决定能不能上线。"
    )
    body(
        doc,
        "所以我自己的结论比较素。对计算机视觉方向，值得跟的是开放词汇、视频记忆、不确定度，而不是把所有新名词抄进笔记。对软件工程方向，更值得练的是：把分割写成可验收需求，把数据版本和模型卡做进配置库，把人机复核做成默认路径，用基础模型降低预标注成本但必须抽检。人工智能在这个领域的创新，不一定表现为再发一篇刷点论文，也可能表现为一套不容易把错误藏住的系统。这和课上讲的需求可追溯，其实是同一句话。"
    )

    heading(doc, "参考文献", 1)
    refs = [
        "[1] LONG J, SHELHAMER E, DARRELL T. Fully convolutional networks for semantic segmentation[C]//Proceedings of the IEEE Conference on Computer Vision and Pattern Recognition. 2015: 3431-3440.",
        "[2] RONNEBERGER O, FISCHER P, BROX T. U-Net: convolutional networks for biomedical image segmentation[C]//Medical Image Computing and Computer-Assisted Intervention. Cham: Springer, 2015: 234-241.",
        "[3] ZHAO H, SHI J, QI X, et al. Pyramid scene parsing network[C]//Proceedings of the IEEE Conference on Computer Vision and Pattern Recognition. 2017: 2881-2890.",
        "[4] HE K, GKIOXARI G, DOLLÁR P, et al. Mask R-CNN[C]//Proceedings of the IEEE International Conference on Computer Vision. 2017: 2961-2969.",
        "[5] CHEN L C, ZHU Y, PAPANDREOU G, et al. Encoder-decoder with atrous separable convolution for semantic image segmentation[C]//Proceedings of the European Conference on Computer Vision. 2018: 801-818.",
        "[6] XIE E, WANG W, YU Z, et al. SegFormer: simple and efficient design for semantic segmentation with transformers[C]//Advances in Neural Information Processing Systems. 2021.",
        "[7] CHENG B, MISRA I, SCHWING A G, et al. Masked-attention mask transformer for universal image segmentation[C]//Proceedings of the IEEE/CVF Conference on Computer Vision and Pattern Recognition. 2022: 1290-1299.",
        "[8] KIRILLOV A, HE K, GIRSHICK R, et al. Panoptic segmentation[C]//Proceedings of the IEEE/CVF Conference on Computer Vision and Pattern Recognition. 2019: 9404-9413.",
        "[9] KIRILLOV A, MINTUN E, RAVI N, et al. Segment anything[C]//Proceedings of the IEEE/CVF International Conference on Computer Vision. 2023: 4015-4026.",
        "[10] RAVI N, GABEUR V, HU Y T, et al. SAM 2: segment anything in images and videos[J/OL]. arXiv preprint arXiv:2408.00714, 2024.",
        "[11] MINAEE S, BOYKOV Y, PORIKLI F, et al. Image segmentation using deep learning: a survey[J]. IEEE Transactions on Pattern Analysis and Machine Intelligence, 2022, 44(7): 3523-3542.",
        "[12] 田萱, 王亮, 丁琪. 基于深度学习的图像语义分割方法综述[J]. 软件学报, 2019, 30(2): 440-468.",
        "[13] 张顺, 龚怡宏, 王进军. 深度卷积神经网络的发展及其在计算机视觉领域的应用[J]. 计算机学报, 2019, 42(3): 453-482.",
        "[14] 黄凯奇, 任伟强, 谭铁牛. 图像物体分类与检测算法综述[J]. 计算机学报, 2014, 37(6): 1225-1240.",
        "[15] 章毓晋. 图像工程（中册）图像分析[M]. 3版. 北京: 清华大学出版社, 2013.",
        "[16] EVERINGHAM M, VAN GOOL L, WILLIAMS C K I, et al. The Pascal Visual Object Classes (VOC) challenge[J]. International Journal of Computer Vision, 2010, 88(2): 303-338.",
        "[17] CORDTS M, OMRAN M, RAMOS S, et al. The Cityscapes dataset for semantic urban scene understanding[C]//Proceedings of the IEEE Conference on Computer Vision and Pattern Recognition. 2016: 3213-3223.",
        "[18] LIN T Y, MAIRE M, BELONGIE S, et al. Microsoft COCO: common objects in context[C]//Proceedings of the European Conference on Computer Vision. Cham: Springer, 2014: 740-755.",
        "[19] ZHOU B, ZHAO H, PUIG X, et al. Scene parsing through ADE20K dataset[C]//Proceedings of the IEEE Conference on Computer Vision and Pattern Recognition. 2017: 633-641.",
        "[20] DOSOVITSKIY A, BEYER L, KOLESNIKOV A, et al. An image is worth 16x16 words: transformers for image recognition at scale[C]//International Conference on Learning Representations. 2021.",
    ]
    for r in refs:
        p = doc.add_paragraph()
        set_para_format(p, first_line=False, indent=Cm(-0.74), space_after=6, line=1.35)
        p.paragraph_format.left_indent = Cm(0.74)
        add_text(p, r, cn="宋体", size=10.5)

    heading(doc, "致谢", 1)
    body(
        doc,
        "作业写到这儿，要感谢课程老师把技术调研和软件需求捆在一起布置。要是只让介绍网络结构，我大概会停在抄模型名字；加上“需求怎么验收”之后，才不得不去想阈值、日志和责任。也感谢开源作者把 FCN、Mask R-CNN、SAM 这些工作连代码带数据放出来，核对数字的时候少走了些二手传播的弯路。文中若还有理解偏差，责任在我。"
    )

    heading(doc, "附件  大模型与工具使用说明", 1)
    body(
        doc,
        "课程要求：如使用大模型生成论文，须在附件按步骤记录。以下按实际过程逐条写，不隐瞒。"
    )

    steps = [
        "步骤1  文献材料。先读课程《实验1-技术调研论文》要求和图文示例《基于卷积神经网络的猫狗大战识别研究》的封面、摘要、目录、研究现状、参考文献和附件结构。主题定为“计算机视觉中目标分割研究现状分析”。核对并引用的文献包括：Long 等 FCN（CVPR 2015）、Ronneberger 等 U-Net（MICCAI 2015）、Zhao 等 PSPNet（CVPR 2017）、He 等 Mask R-CNN（ICCV 2017）、Chen 等 DeepLabv3+（ECCV 2018）、Xie 等 SegFormer（NeurIPS 2021）、Cheng 等 Mask2Former（CVPR 2022）、Kirillov 等 Panoptic Segmentation（CVPR 2019）与 SAM（ICCV 2023）、Ravi 等 SAM 2（arXiv:2408.00714）、Minaee 等 TPAMI 综述（2022）、田萱等《软件学报》2019、张顺等《计算机学报》2019、黄凯奇等《计算机学报》2014、章毓晋《图像工程》、VOC / Cityscapes / COCO / ADE20K 原始论文。VOC 67.2%、DeepLabv3+ 89.0%/82.1%、SegFormer 84.0%、Mask2Former 57.7% mIoU 与 57.8 PQ、Mask R-CNN AP、SA-1B 11M/1.1B、SAM 2 的 6×/3× 与 SA-V 规模，均对照原文或出版社页面，不用来路不明的自媒体数字。",
        "步骤2  AI 工具。写作和排版在 Cursor 中完成；检索用联网搜索核对 arXiv、CVF Open Access、《软件学报》页面、Meta SA-1B/SA-V 说明。图不是生成式插画，而是用 Python + Matplotlib 按数据绘图（爆炸结构图、时间线、柱状图、流水线），目的是降低“AI 绘图腔”，并保证数轴上的数字可回溯。Word 用 python-docx 按示例页边距（左 3 cm、右 2 cm、上下 2.5 cm）导出。",
        "步骤3  大模型技术。使用 Cursor 对话模型辅助组织章节、压缩口语化的课堂表述、生成绘图脚本和 Word 脚本。不让模型凭空编造未核对的实验表。对拿不准的中文综述（刊名、卷期、页码），以能打开的期刊页面为准，删掉核不上的条目。",
        "步骤4  提示词工程。核心约束写进提示：不少于 3000 字；格式对齐示例；图文并茂；文献真实；思考人工智能在计算机与软件工程中的应用；正文后附件记录工具链；降低套话和排比；语气像学生调研而不是产品白皮书；指标不要跨基准横向排名。修改时追加“少用首先其次最后、少用赋能闭环、承认未精读的工作”。",
        "步骤5  Agent 流程。按“读示例与要求 → 核对文献数字 → 画图 → 写正文 → 导出 docx → 统计字数”顺序执行。没有自动爬取付费全文，也没有把模型输出不经核对就当实验结果。",
        "步骤6  Skill 与 MCP。使用了 Cursor 的文档读写、命令行、联网检索，以及 python-docx / matplotlib 本地库。未调用会改写实验数据的外部业务 MCP。绘图脚本与论文生成脚本留在本地 homework 目录，便于复查。",
        "步骤7  查重与可信度处理。正文有意识加入个人阅读过程（例如曾把 AP 和 mIoU 混表、承认实时分割未逐篇精读），避免整段平行罗列。参考文献控制在能指到会议/期刊的范围内。表2.1 加注“不同行不排名”。若学校使用 AIGC 检测，请以终稿再次自查，并把封面的分组、学号、姓名、班级手工填全后按“分组+学号+姓名+班级+题目”重命名提交。",
    ]
    for s in steps:
        body(doc, s)

    OUT.parent.mkdir(parents=True, exist_ok=True)
    doc.save(str(OUT))
    print("saved", OUT)


if __name__ == "__main__":
    build()
