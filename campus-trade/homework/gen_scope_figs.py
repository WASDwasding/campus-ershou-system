# -*- coding: utf-8 -*-
"""Visio-style figures for Experiment 3: Vision and Scope."""
from pathlib import Path

import matplotlib.pyplot as plt
from matplotlib.patches import (
    Circle,
    Ellipse,
    FancyArrowPatch,
    FancyBboxPatch,
    FancyBboxPatch as FBP,
    Rectangle,
    RegularPolygon,
    Wedge,
)
from matplotlib.lines import Line2D
import numpy as np

OUT = Path(r"C:\Users\15736\campus-trade\homework\scope_figs")
OUT.mkdir(parents=True, exist_ok=True)

plt.rcParams["font.sans-serif"] = ["Microsoft YaHei", "SimHei", "SimSun"]
plt.rcParams["axes.unicode_minus"] = False
plt.rcParams["pdf.fonttype"] = 42

NAVY = "#1B365D"
BLUE = "#2E75B6"
CYAN = "#5B9BD5"
TEAL = "#1F7A8C"
ORANGE = "#ED7D31"
GREEN = "#548235"
GOLD = "#C9A227"
RED = "#C45911"
SLATE = "#5B6770"
INK = "#1F2A37"
PAPER = "#F4F7FB"
BOX = "#FFFFFF"
LINE = "#2F4A6D"
SOFT = "#D6E3F0"


def save(fig, name):
    p = OUT / name
    fig.savefig(p, dpi=220, bbox_inches="tight", facecolor=fig.get_facecolor())
    plt.close(fig)
    print("saved", p)


def rbox(ax, x, y, w, h, fc=BOX, ec=LINE, lw=1.4, r=0.08, z=3, alpha=1):
    shadow = FancyBboxPatch(
        (x + 0.04, y - 0.05), w, h,
        boxstyle=f"round,pad=0.012,rounding_size={r}",
        facecolor="#00000018", edgecolor="none", zorder=z - 1,
    )
    ax.add_patch(shadow)
    p = FancyBboxPatch(
        (x, y), w, h,
        boxstyle=f"round,pad=0.012,rounding_size={r}",
        facecolor=fc, edgecolor=ec, linewidth=lw, zorder=z, alpha=alpha,
    )
    ax.add_patch(p)
    return p


def t(ax, x, y, s, size=9, color=INK, weight="normal", ha="center", va="center", z=5):
    ax.text(x, y, s, fontsize=size, color=color, fontweight=weight,
            ha=ha, va=va, zorder=z, linespacing=1.25)


def arrow(ax, x1, y1, x2, y2, color=ORANGE, lw=1.5, rad=0):
    ax.add_patch(FancyArrowPatch(
        (x1, y1), (x2, y2),
        arrowstyle="-|>", mutation_scale=12, lw=lw, color=color,
        connectionstyle=f"arc3,rad={rad}", zorder=4,
    ))


def header(ax, title, subtitle=None, xmax=12, ymax=7.2):
    ax.add_patch(Rectangle((0, ymax - 0.72), xmax, 0.72, facecolor=NAVY, edgecolor="none", zorder=6))
    ax.add_patch(Rectangle((0, ymax - 0.78), xmax, 0.06, facecolor=ORANGE, edgecolor="none", zorder=6))
    t(ax, 0.28, ymax - 0.36, title, size=13, color="white", weight="bold", ha="left", z=7)
    if subtitle:
        t(ax, xmax - 0.28, ymax - 0.36, subtitle, size=8.5, color="#D6E3F0", ha="right", z=7)


def canvas(w, h):
    fig, ax = plt.subplots(figsize=(w, h), facecolor="white")
    ax.set_xlim(0, w)
    ax.set_ylim(0, h)
    ax.set_aspect("equal")
    ax.axis("off")
    ax.add_patch(Rectangle((0, 0), w, h, facecolor=PAPER, edgecolor="#C5D0DC", lw=0.8, zorder=0))
    return fig, ax


def fig1_as_is_to_be():
    w, h = 13.2, 7.4
    fig, ax = canvas(w, h)
    header(ax, "图1  现状痛点 → 做成色识别之后", "As-Is / To-Be", w, h)

    rbox(ax, 0.35, 0.45, 6.0, 5.85, fc="#F8EDE6", ec=ORANGE, lw=1.6)
    t(ax, 3.35, 5.95, "现在（靠嘴说成色）", 12, ORANGE, "bold")
    pains = [
        ("卖家自己填“九成新”", "没有统一尺子，水分很大"),
        ("买家只能放大照片猜", "折角、笔记、黄页看不清"),
        ("价格对不上成色", "好书卖亏，差书卖贵，容易吵"),
        ("退换扯皮成本高", "面交后才发现内页乱画"),
    ]
    for i, (a, b) in enumerate(pains):
        y = 4.85 - i * 1.05
        rbox(ax, 0.6, y, 5.5, 0.9, fc="white", ec="#E8C4A8")
        ax.add_patch(Circle((1.05, y + 0.45), 0.22, facecolor=ORANGE, edgecolor="none", zorder=5))
        t(ax, 1.05, y + 0.45, str(i + 1), 10, "white", "bold")
        t(ax, 1.5, y + 0.58, a, 10, INK, "bold", ha="left")
        t(ax, 1.5, y + 0.28, b, 8.5, SLATE, ha="left")

    rbox(ax, 6.85, 0.45, 6.0, 5.85, fc="#E8F2EA", ec=GREEN, lw=1.6)
    t(ax, 9.85, 5.95, "以后（照片说话）", 12, GREEN, "bold")
    gains = [
        ("拍封面 + 内页即可", "系统自动找磨损、笔记、污渍"),
        ("给出成色等级和依据", "九五新 / 九成新……看得见为什么"),
        ("挂牌价更对得上", "同类课本、相近成色，价格好比"),
        ("买卖双方少吵架", "争议时还能回头看识别记录"),
    ]
    for i, (a, b) in enumerate(gains):
        y = 4.85 - i * 1.05
        rbox(ax, 7.1, y, 5.5, 0.9, fc="white", ec="#B7D3BE")
        ax.add_patch(Circle((7.55, y + 0.45), 0.22, facecolor=GREEN, edgecolor="none", zorder=5))
        t(ax, 7.55, y + 0.45, str(i + 1), 10, "white", "bold")
        t(ax, 8.0, y + 0.58, a, 10, INK, "bold", ha="left")
        t(ax, 8.0, y + 0.28, b, 8.5, SLATE, ha="left")

    ax.annotate("", xy=(6.78, 3.4), xytext=(6.42, 3.4),
                arrowprops=dict(arrowstyle="-|>", color=NAVY, lw=2.2, mutation_scale=16))
    save(fig, "fig1_as_is_to_be.png")


def fig2_context():
    w, h = 13.4, 8.0
    fig, ax = canvas(w, h)
    header(ax, "图2  系统上下文图（和谁打交道）", "Context Diagram  ·  Visio 风格", w, h)

    rbox(ax, 4.55, 3.15, 4.3, 2.15, fc=NAVY, ec=NAVY, r=0.12)
    t(ax, 6.7, 4.45, "校园二手课本", 12, "white", "bold")
    t(ax, 6.7, 4.05, "成色识别交易平台", 12, "white", "bold")
    t(ax, 6.7, 3.55, "BookCondition Campus Trade", 8, CYAN)

    actors = [
        (0.4, 5.55, 2.55, 1.35, "卖书的同学", "拍照上传课本\n确认成色后上架", ORANGE),
        (0.4, 3.55, 2.55, 1.35, "买书的同学", "按成色筛选\n看瑕疵位置再下单", BLUE),
        (0.4, 1.45, 2.55, 1.35, "平台运营同学", "抽检识别结果\n处理举报与下架", TEAL),
        (10.45, 5.55, 2.55, 1.35, "成色识别服务", "检测书本区域\n输出等级+依据", GREEN),
        (10.45, 3.55, 2.55, 1.35, "校园身份服务", "学号/微信登录\n本校本院校验", GOLD),
        (10.45, 1.45, 2.55, 1.35, "对象存储", "课本照片原图\n与标注结果", SLATE),
    ]
    for x, y, ww, hh, title, sub, c in actors:
        rbox(ax, x, y, ww, hh, fc="white", ec=c, lw=1.7)
        ax.add_patch(Rectangle((x, y + hh - 0.08), ww, 0.08, facecolor=c, edgecolor="none", zorder=5))
        t(ax, x + ww / 2, y + hh - 0.42, title, 10, c, "bold")
        t(ax, x + ww / 2, y + 0.48, sub, 8, SLATE)

    # arrows with labels
    def lab_arrow(x1, y1, x2, y2, lab, c=NAVY):
        arrow(ax, x1, y1, x2, y2, color=c, lw=1.35)
        t(ax, (x1 + x2) / 2, (y1 + y2) / 2 + 0.18, lab, 7.2, c)

    lab_arrow(2.95, 6.15, 4.55, 4.85, "照片 / 定价")
    lab_arrow(4.55, 4.55, 2.95, 5.85, "成色建议", BLUE)
    lab_arrow(2.95, 4.15, 4.55, 4.15, "筛选 / 下单")
    lab_arrow(4.55, 3.85, 2.95, 3.85, "瑕疵图", BLUE)
    lab_arrow(2.95, 2.2, 4.7, 3.15, "抽检工单")
    lab_arrow(8.85, 4.7, 10.45, 6.0, "推理请求")
    lab_arrow(10.45, 5.7, 8.85, 4.4, "等级+框", GREEN)
    lab_arrow(8.85, 4.1, 10.45, 4.2, "登录校验")
    lab_arrow(8.85, 3.4, 10.45, 2.2, "存图")
    save(fig, "fig2_context.png")


def fig3_vision():
    w, h = 13.4, 6.6
    fig, ax = canvas(w, h)
    header(ax, "图3  前景一句话：拍书 → 看出新旧 → 放心买卖", "Vision Storyboard", w, h)
    steps = [
        ("01", "拍", "封面、侧边、\n内页各拍一张", "#1B365D"),
        ("02", "认", "找出书在哪儿\n标出折角污渍笔记", "#2E75B6"),
        ("03", "判", "给出成色等级\n和置信度", "#1F7A8C"),
        ("04", "挂", "自动填成色\n卖家可微调说明", "#548235"),
        ("05", "买", "买家按成色筛\n点开看红框位置", "#C45911"),
    ]
    for i, (no, big, sub, c) in enumerate(steps):
        x = 0.45 + i * 2.58
        rbox(ax, x, 1.7, 2.28, 3.55, fc="white", ec=c, lw=1.8, r=0.12)
        ax.add_patch(Circle((x + 1.14, 4.55), 0.38, facecolor=c, edgecolor="none", zorder=5))
        t(ax, x + 1.14, 4.55, no, 11, "white", "bold")
        t(ax, x + 1.14, 3.55, big, 22, c, "bold")
        t(ax, x + 1.14, 2.55, sub, 9.5, SLATE)
        if i < 4:
            arrow(ax, x + 2.32, 3.4, x + 2.52, 3.4, color=ORANGE, lw=2)
    t(ax, 6.7, 0.95, "核心不是“再做一个闲鱼”，而是把课本新旧这件事从口头承诺变成看得见的识别结果。", 9, SLATE)
    save(fig, "fig3_vision.png")


def fig4_pipeline():
    w, h = 13.6, 8.15
    fig, ax = canvas(w, h)
    header(ax, "图4  课本成色识别流水线（本项目技术核心）", "Computer Vision Pipeline", w, h)

    layers = [
        (0.35, 6.05, "L0  采集层", "宿舍灯光、手机摄像头、多角度拍摄引导", "#1B365D"),
        (0.35, 4.55, "L1  图像预处理", "纠正曝光 · 去噪 · 拉直书页 · 统一尺寸", "#2E75B6"),
        (0.35, 3.05, "L2  感知层（重点）", "书本检测  |  封面/内页分类  |  瑕疵分割  |  文字涂改检测", "#1F7A8C"),
        (0.35, 1.55, "L3  成色决策层", "规则 + 模型打分 → 成色等级、置信度、人工复核阈值", "#548235"),
        (0.35, 0.35, "L4  业务接入层", "上架表单回填  ·  商品详情红框  ·  按成色搜索  ·  争议留痕", "#C45911"),
    ]
    for x, y, title, sub, c in layers:
        rbox(ax, x, y, 8.15, 1.28, fc="white", ec=c, lw=1.6)
        ax.add_patch(Rectangle((x, y), 0.16, 1.28, facecolor=c, edgecolor="none", zorder=5))
        t(ax, x + 0.45, y + 0.86, title, 11, c, "bold", ha="left")
        t(ax, x + 0.45, y + 0.38, sub, 9, SLATE, ha="left")
        if y > 0.4:
            arrow(ax, 4.4, y, 4.4, y - 0.22, color=ORANGE, lw=1.4)

    rbox(ax, 8.75, 2.35, 4.5, 4.95, fc="white", ec=NAVY, lw=1.6)
    t(ax, 11.0, 6.9, "L2 感知层拆开看", 10.5, NAVY, "bold")
    dets = [
        ("书本检测", "把书从桌面/床铺抠出来", BLUE),
        ("区域分工", "封面 / 书脊 / 内页", TEAL),
        ("瑕疵分割", "折角、破损、污渍、受潮", ORANGE),
        ("笔记检测", "划线、涂改、贴便签", GOLD),
        ("纸张老化", "发黄、掉页边缘", GREEN),
    ]
    for i, (a, b, c) in enumerate(dets):
        yy = 6.15 - i * 0.72
        rbox(ax, 8.95, yy, 4.1, 0.62, fc=PAPER, ec=c, lw=1.2, r=0.06)
        t(ax, 9.15, yy + 0.31, a, 9, c, "bold", ha="left")
        t(ax, 12.85, yy + 0.31, b, 7.8, SLATE, ha="right")
    save(fig, "fig4_pipeline.png")


def fig5_grades():
    w, h = 13.2, 6.5
    fig, ax = canvas(w, h)
    header(ax, "图5  课本成色等级（给人和给机器同一把尺子）", "Condition Taxonomy", w, h)
    grades = [
        ("A", "近全新", "几乎无磨损\n无笔记无污渍", 95, "#548235", "建议价接近原价"),
        ("B", "九五新", "轻微使用痕迹\n少量铅笔印", 88, "#70AD47", "最常见“刚用一学期”"),
        ("C", "九成新", "边角轻折\n有划线但不密", 75, "#2E75B6", "能读、观感还行"),
        ("D", "八成新", "折角/污渍明显\n或笔记较多", 58, "#ED7D31", "便宜出，需写清楚"),
        ("E", "较差", "破损掉页受潮\n或涂改严重", 30, "#C45911", "建议低价或仅配件"),
    ]
    for i, (code, name, desc, score, c, tip) in enumerate(grades):
        x = 0.4 + i * 2.54
        rbox(ax, x, 1.15, 2.35, 4.35, fc="white", ec=c, lw=1.8)
        ax.add_patch(Rectangle((x, 4.95), 2.35, 0.55, facecolor=c, edgecolor="none", zorder=5))
        t(ax, x + 1.17, 5.22, f"{code}  {name}", 11, "white", "bold")
        # battery bar
        ax.add_patch(Rectangle((x + 0.35, 4.35), 1.65, 0.32, facecolor="#E6E6E6", edgecolor="none", zorder=5))
        ax.add_patch(Rectangle((x + 0.35, 4.35), 1.65 * score / 100, 0.32, facecolor=c, edgecolor="none", zorder=6))
        t(ax, x + 1.17, 3.85, f"参考分 {score}", 9, c, "bold")
        t(ax, x + 1.17, 2.95, desc, 9, INK)
        t(ax, x + 1.17, 1.7, tip, 8, SLATE)
    t(ax, 6.6, 0.55, "等级由瑕疵种类、面积占比、是否伤及文字内容综合得出；低置信度时提示卖家补拍，不硬判。", 8.5, SLATE)
    save(fig, "fig5_grades.png")


def fig6_trace():
    w, h = 13.4, 7.6
    fig, ax = canvas(w, h)
    header(ax, "图6  业务目标如何落到系统特性（可追溯）", "BO  →  Feature  Traceability", w, h)

    bos = [
        (0.4, 5.55, "BO-1 少扯皮", "成色有依据"),
        (0.4, 4.05, "BO-2 更好定价", "同类好对比"),
        (0.4, 2.55, "BO-3 更快成交", "筛选少犹豫"),
        (0.4, 1.05, "BO-4 更敢上架", "拍照就能填"),
    ]
    for x, y, a, b in bos:
        rbox(ax, x, y, 3.15, 1.2, fc=NAVY, ec=NAVY)
        t(ax, x + 1.57, y + 0.75, a, 10, "white", "bold")
        t(ax, x + 1.57, y + 0.35, b, 8.5, CYAN)

    feats = [
        (9.7, 6.05, "F-1 多角度拍摄引导", ORANGE),
        (9.7, 5.05, "F-2 成色自动识别", GREEN),
        (9.7, 4.05, "F-3 瑕疵红框展示", BLUE),
        (9.7, 3.05, "F-4 按成色筛选", TEAL),
        (9.7, 2.05, "F-5 参考价区间", GOLD),
        (9.7, 1.05, "F-6 识别记录留痕", RED),
    ]
    for x, y, a, c in feats:
        rbox(ax, x, y, 3.3, 0.82, fc="white", ec=c, lw=1.5)
        t(ax, x + 1.65, y + 0.41, a, 9.5, c, "bold")

    links = [
        (3.55, 6.15, 9.7, 5.46),
        (3.55, 6.05, 9.7, 4.46),
        (3.55, 5.95, 9.7, 1.46),
        (3.55, 4.65, 9.7, 5.46),
        (3.55, 4.55, 9.7, 2.46),
        (3.55, 3.15, 9.7, 4.46),
        (3.55, 3.05, 9.7, 3.46),
        (3.55, 1.65, 9.7, 6.46),
        (3.55, 1.55, 9.7, 5.46),
    ]
    for a in links:
        ax.add_patch(FancyArrowPatch(
            (a[0], a[1]), (a[2], a[3]),
            arrowstyle="-", mutation_scale=8, lw=0.9, color="#8FA4B8",
            connectionstyle="arc3,rad=0.04", zorder=2, alpha=0.85,
        ))
    t(ax, 6.6, 0.45, "一条业务目标至少对应一条可演示的特性，评审时顺着箭头问：没有这条功能，目标还成立吗？", 8.5, SLATE)
    save(fig, "fig6_trace.png")


def fig7_scope():
    w, h = 13.4, 7.7
    fig, ax = canvas(w, h)
    header(ax, "图7  范围边界：围墙里做什么、墙外先不做", "In-Scope / Out-of-Scope", w, h)

    ax.add_patch(FancyBboxPatch(
        (0.45, 0.7), 8.3, 6.05,
        boxstyle="round,pad=0.02,rounding_size=0.15",
        facecolor="#EAF2F8", edgecolor=NAVY, lw=2.0, zorder=1,
    ))
    t(ax, 4.6, 6.4, "本学期范围内  In-Scope", 12, NAVY, "bold")
    insides = [
        "校园身份登录与本校本院校验",
        "课本/教辅发布、浏览、搜索、收藏",
        "拍照引导 + 成色识别 + 红框展示",
        "按成色/学院/面交点筛选",
        "站内留言、预约面交（模拟）",
        "识别低置信度时的补拍与人工确认",
        "简易运营抽检、下架与举报",
    ]
    for i, s in enumerate(insides):
        y = 5.7 - i * 0.68
        rbox(ax, 0.75, y, 7.7, 0.58, fc="white", ec=BLUE, lw=1.1, r=0.06)
        ax.add_patch(Circle((1.1, y + 0.29), 0.13, facecolor=GREEN, edgecolor="none", zorder=6))
        t(ax, 1.45, y + 0.29, s, 9.5, INK, ha="left")

    rbox(ax, 9.05, 0.7, 3.95, 6.05, fc="#FBE9E0", ec=ORANGE, lw=1.8)
    t(ax, 11.02, 6.35, "范围外  Out", 11, ORANGE, "bold")
    outs = [
        "全品类二手（数码/衣服）",
        "真实支付与钱包",
        "快递物流与仓储",
        "跨校全国交易",
        "大模型自动议价",
        "扫描整书OCR入库",
        "改造学校教务系统",
        "复杂司法级仲裁庭",
    ]
    for i, s in enumerate(outs):
        y = 5.55 - i * 0.58
        t(ax, 9.35, y, "不  " + s, 9, "#8A3B12", ha="left")
    save(fig, "fig7_scope.png")


def fig8_stakeholder():
    w, h = 12.8, 7.6
    fig, ax = canvas(w, h)
    header(ax, "图8  涉众洋葱图：谁用、谁管、谁受影响", "Stakeholder Onion", w, h)
    cx, cy = 6.4, 3.45
    rings = [
        (2.85, "#D6E3F0", "影响层"),
        (2.15, "#B7C9DC", "关注层"),
        (1.45, "#5B9BD5", "使用层"),
        (0.78, "#1B365D", "核心"),
    ]
    for r, c, _ in rings:
        ax.add_patch(Circle((cx, cy), r, facecolor=c, edgecolor="white", lw=2, zorder=2))
    t(ax, cx, cy + 0.12, "成色识别", 10, "white", "bold", z=6)
    t(ax, cx, cy - 0.22, "引擎", 10, "white", "bold", z=6)

    labels = [
        (cx, cy + 1.72, "买卖双方学生", NAVY),
        (cx - 3.7, cy + 0.15, "运营 / 抽检", NAVY),
        (cx + 3.75, cy + 0.15, "需求分析人员", NAVY),
        (cx - 2.6, cy - 2.35, "学院教师\n（关心资料循环）", SLATE),
        (cx + 2.7, cy - 2.35, "学校信息化\n（账号与部署）", SLATE),
        (cx, cy - 3.15, "家长/同学圈口碑（间接）", SLATE),
    ]
    for x, y, s, col in labels:
        t(ax, x, y, s, 9, col, "bold")
    save(fig, "fig8_stakeholder.png")


def fig9_usecase():
    w, h = 13.4, 7.8
    fig, ax = canvas(w, h)
    header(ax, "图9  用例图（只画本学期真正要做的）", "Use Case  ·  UML", w, h)

    def stick(x, y, name, color=NAVY):
        ax.add_patch(Circle((x, y + 1.55), 0.22, facecolor="white", edgecolor=color, lw=1.6, zorder=5))
        ax.add_patch(Rectangle((x - 0.02, y + 0.85), 0.04, 0.5, facecolor=color, edgecolor="none", zorder=5))
        ax.plot([x - 0.28, x + 0.28], [y + 1.22, y + 1.22], color=color, lw=1.6, zorder=5)
        ax.plot([x, x - 0.22], [y + 0.85, y + 0.45], color=color, lw=1.6, zorder=5)
        ax.plot([x, x + 0.22], [y + 0.85, y + 0.45], color=color, lw=1.6, zorder=5)
        t(ax, x, y + 0.18, name, 9, color, "bold")

    stick(1.15, 4.55, "卖家")
    stick(12.2, 4.55, "买家", BLUE)
    stick(12.2, 1.35, "运营", ORANGE)

    sys_x, sys_y, sys_w, sys_h = 2.55, 0.7, 8.15, 6.2
    ax.add_patch(FancyBboxPatch(
        (sys_x, sys_y), sys_w, sys_h,
        boxstyle="round,pad=0.02,rounding_size=0.1",
        facecolor="white", edgecolor=NAVY, lw=1.6, zorder=1,
    ))
    t(ax, 6.62, 6.55, "校园二手课本成色识别平台", 10, NAVY, "bold")

    cases = {
        "shot": (4.35, 5.55, "引导拍照并上传"),
        "rec": (4.35, 4.45, "识别成色并确认"),
        "pub": (4.35, 3.35, "发布 / 下架课本"),
        "filter": (8.15, 5.55, "按成色浏览筛选"),
        "box": (8.15, 4.45, "查看瑕疵红框"),
        "meet": (8.15, 3.35, "预约面交沟通"),
        "audit": (6.25, 2.15, "抽检识别结果"),
        "ban": (6.25, 1.2, "处理举报下架"),
    }
    for x, y, name in cases.values():
        ax.add_patch(Ellipse((x, y), 3.15, 0.78, facecolor="#EAF2F8", edgecolor=BLUE, lw=1.3, zorder=3))
        t(ax, x, y, name, 8.7, INK)

    def link(x1, y1, x2, y2, c=NAVY):
        arrow(ax, x1, y1, x2, y2, color=c, lw=1.15)

    link(1.55, 5.85, 2.78, 5.55)
    link(1.55, 5.55, 2.78, 4.45)
    link(1.55, 5.25, 2.78, 3.35)
    link(11.7, 5.85, 9.72, 5.55, BLUE)
    link(11.7, 5.55, 9.72, 4.45, BLUE)
    link(11.7, 5.25, 9.72, 3.35, BLUE)
    link(11.7, 2.65, 7.82, 2.15, ORANGE)
    link(11.7, 2.35, 7.82, 1.2, ORANGE)
    save(fig, "fig9_usecase.png")


def fig10_risk():
    w, h = 12.6, 7.5
    fig, ax = canvas(w, h)
    header(ax, "图10  业务与项目风险矩阵（发生可能 × 影响）", "Risk Matrix", w, h)

    # grid
    ox, oy, s = 2.15, 1.05, 1.55
    colors = [
        ["#F8CBAD", "#F4B183", "#C45911", "#833C0C"],
        ["#FFE699", "#F8CBAD", "#F4B183", "#C45911"],
        ["#C6E0B4", "#FFE699", "#F8CBAD", "#F4B183"],
        ["#E2EFDA", "#C6E0B4", "#FFE699", "#F8CBAD"],
    ]
    for i in range(4):
        for j in range(4):
            ax.add_patch(Rectangle((ox + j * s, oy + i * s), s, s,
                                   facecolor=colors[3 - i][j], edgecolor="white", lw=2, zorder=2))
    t(ax, ox - 0.85, oy + 2 * s, "影响\n↑ 大", 9, NAVY, "bold")
    t(ax, ox + 2 * s, oy - 0.45, "发生可能性  →  高", 9, NAVY, "bold")
    for i, lab in enumerate(["低", "中低", "中高", "高"]):
        t(ax, ox - 0.35, oy + i * s + s / 2, lab, 8, SLATE)
        t(ax, ox + i * s + s / 2, oy - 0.18, lab, 8, SLATE)

    items = [
        (ox + 2.15 * s, oy + 2.55 * s, "R1 光线差\n识别不准", "white"),
        (ox + 2.55 * s, oy + 1.55 * s, "R2 卖家\n只拍好面", "white"),
        (ox + 1.15 * s, oy + 2.55 * s, "R3 数据不够\n课本种类杂", INK),
        (ox + 0.35 * s, oy + 1.15 * s, "R4 小程序\n审核限制", INK),
        (ox + 1.55 * s, oy + 0.35 * s, "R5 学期末\n访问高峰", INK),
        (ox + 0.25 * s, oy + 2.65 * s, "R6 不做项目\n信任继续差", INK),
    ]
    for x, y, stext, col in items:
        t(ax, x, y, stext, 8, col, "bold")

    rbox(ax, 8.7, 1.15, 3.55, 5.15, fc="white", ec=NAVY)
    t(ax, 10.47, 5.9, "应对思路", 11, NAVY, "bold")
    mitig = [
        "R1 拍摄引导+补光提示\n低置信度强制补拍",
        "R2 必须拍内页/书脊\n缺页不给高等级",
        "R3 先锁“本校公共课”\n再扩专业书",
        "R4 核心识别走自有服务\n小程序只做展示",
        "R5 图片异步识别排队",
        "R6 用可演示样例验收",
    ]
    for i, m in enumerate(mitig):
        t(ax, 8.95, 5.35 - i * 0.68, m, 8, SLATE, ha="left")
    save(fig, "fig10_risk.png")


def fig11_roadmap():
    w, h = 13.4, 7.2
    fig, ax = canvas(w, h)
    header(ax, "图11  版本路线：先能认准课本，再谈平台做大", "Release Roadmap", w, h)
    ax.plot([0.7, 12.7], [3.55, 3.55], color=NAVY, lw=2.6, zorder=2)
    vers = [
        (1.3, "V0.5", "第4–6周", "拍摄引导\n人工成色标签\n打通发布浏览", BLUE, 4.55),
        (4.15, "V1.0", "第7–12周", "成色识别上线\n红框+等级回填\n按成色筛选", GREEN, 0.95),
        (7.1, "V1.1", "下学期初", "参考价区间\n抽检工作台\n低置信复核", ORANGE, 4.55),
        (10.05, "V2.0", "后续（范围外）", "更多品类\n支付/物流\n跨校（不做承诺）", SLATE, 0.95),
    ]
    for x, ver, when, desc, c, yb in vers:
        ax.add_patch(Circle((x + 1.05, 3.55), 0.18, facecolor=c, edgecolor="white", lw=2, zorder=5))
        rbox(ax, x, yb, 2.35, 1.75, fc="white", ec=c, lw=1.6)
        t(ax, x + 1.17, yb + 1.42, ver, 12, c, "bold")
        t(ax, x + 1.17, yb + 1.08, when, 8, SLATE)
        t(ax, x + 1.17, yb + 0.48, desc, 8, INK)
        if yb > 3.5:
            ax.plot([x + 1.05, x + 1.05], [3.73, yb], color=c, lw=1.2, ls="--", zorder=2)
        else:
            ax.plot([x + 1.05, x + 1.05], [3.37, yb + 1.75], color=c, lw=1.2, ls="--", zorder=2)
    t(ax, 6.7, 0.38, "本实验文档锁定的交付是 V1.0：课本成色可识别、可展示、可筛选。后面只登记，不写进本期验收。", 8.5, SLATE)
    save(fig, "fig11_roadmap.png")


def fig12_env():
    w, h = 13.4, 7.5
    fig, ax = canvas(w, h)
    header(ax, "图12  运行环境与逻辑部署（解决方案约束可视化）", "Operational Environment", w, h)

    rbox(ax, 0.4, 4.55, 3.9, 2.05, fc="white", ec=ORANGE, lw=1.6)
    t(ax, 2.35, 6.2, "客户端", 11, ORANGE, "bold")
    t(ax, 2.35, 5.55, "微信小程序 / 校园网浏览器", 9, INK)
    t(ax, 2.35, 5.05, "拍照、预览红框、下单沟通", 8.5, SLATE)

    rbox(ax, 4.7, 4.55, 4.0, 2.05, fc="white", ec=BLUE, lw=1.6)
    t(ax, 6.7, 6.2, "应用服务", 11, BLUE, "bold")
    t(ax, 6.7, 5.55, "Spring Boot 业务 API", 9, INK)
    t(ax, 6.7, 5.05, "登录 · 商品 · 订单 · 消息", 8.5, SLATE)

    rbox(ax, 9.1, 4.55, 3.9, 2.05, fc="white", ec=GREEN, lw=1.6)
    t(ax, 11.05, 6.2, "识别服务", 11, GREEN, "bold")
    t(ax, 11.05, 5.55, "独立 CV 推理进程", 9, INK)
    t(ax, 11.05, 5.05, "GPU/CPU 异步任务队列", 8.5, SLATE)

    rbox(ax, 0.4, 1.35, 3.9, 2.2, fc="white", ec=GOLD, lw=1.6)
    t(ax, 2.35, 3.15, "身份", 11, GOLD, "bold")
    t(ax, 2.35, 2.5, "学号校验 / 微信登录", 9, INK)
    t(ax, 2.35, 2.0, "本期可用白名单模拟", 8.5, SLATE)

    rbox(ax, 4.7, 1.35, 4.0, 2.2, fc="white", ec=TEAL, lw=1.6)
    t(ax, 6.7, 3.15, "数据", 11, TEAL, "bold")
    t(ax, 6.7, 2.5, "MySQL 业务库  +  Redis", 9, INK)
    t(ax, 6.7, 2.0, "照片对象存储（校内部署）", 8.5, SLATE)

    rbox(ax, 9.1, 1.35, 3.9, 2.2, fc="white", ec=SLATE, lw=1.6)
    t(ax, 11.05, 3.15, "运维边界", 11, SLATE, "bold")
    t(ax, 11.05, 2.5, "Nginx 反代 / HTTPS", 9, INK)
    t(ax, 11.05, 2.0, "日志与识别抽检记录", 8.5, SLATE)

    arrow(ax, 4.3, 5.55, 4.7, 5.55, color=NAVY)
    arrow(ax, 8.7, 5.55, 9.1, 5.55, color=NAVY)
    arrow(ax, 6.7, 4.55, 6.7, 3.55, color=NAVY)
    arrow(ax, 2.35, 4.55, 2.35, 3.55, color=NAVY)
    t(ax, 6.7, 0.65, "约束：识别不塞进小程序本地；照片不出校园网演示环境；并发按期末退课卖书高峰估算。", 8.5, SLATE)
    save(fig, "fig12_env.png")


def fig13_swimlane():
    w, h = 13.6, 8.35
    fig, ax = canvas(w, h)
    header(ax, "图13  卖课本主流程（泳道图）", "BPMN-like Swimlane", w, h)

    lanes = [
        (5.85, "卖家", "#F8EDE6"),
        (4.15, "小程序", "#EAF2F8"),
        (2.45, "识别服务", "#E8F2EA"),
        (0.75, "平台规则", "#FFF6E0"),
    ]
    ax.add_patch(Rectangle((0.25, 0.75), 13.1, 6.8, facecolor="white", edgecolor=NAVY, lw=1.3, zorder=1))
    for y, name, bg in lanes:
        ax.add_patch(Rectangle((0.25, y), 13.1, 1.7, facecolor=bg, edgecolor="#C5D0DC", lw=0.8, zorder=2))
        ax.add_patch(Rectangle((0.25, y), 1.55, 1.7, facecolor=NAVY, edgecolor="none", zorder=3))
        t(ax, 1.02, y + 0.85, name, 9, "white", "bold")

    boxes = [
        (2.05, 6.12, "选课本\n拍三张图", ORANGE),
        (4.55, 6.12, "看建议成色\n可改说明", ORANGE),
        (9.15, 6.12, "确认上架", ORANGE),
        (2.05, 4.42, "压缩上传\n展示进度", BLUE),
        (6.75, 4.42, "回填表单\n画红框", BLUE),
        (4.55, 2.72, "检测+分割\n输出等级", GREEN),
        (9.15, 2.72, "写识别记录", GREEN),
        (6.75, 1.05, "置信度不够\n要求补拍", GOLD),
        (10.85, 1.05, "抽检样本", GOLD),
    ]
    for x, y, lab, c in boxes:
        rbox(ax, x, y, 2.05, 1.15, fc="white", ec=c, lw=1.4, r=0.08)
        t(ax, x + 1.02, y + 0.58, lab, 8, INK)

    arrow(ax, 4.1, 6.7, 4.55, 6.7, color=NAVY, lw=1.2)
    arrow(ax, 3.07, 6.12, 3.07, 5.57, color=NAVY, lw=1.2)
    arrow(ax, 4.1, 4.98, 6.75, 4.98, color=NAVY, lw=1.2)
    arrow(ax, 5.57, 4.42, 5.57, 3.87, color=NAVY, lw=1.2)
    arrow(ax, 6.6, 3.3, 6.75, 4.42, color=NAVY, lw=1.2)
    arrow(ax, 8.8, 4.98, 9.15, 6.7, color=NAVY, lw=1.2)
    arrow(ax, 6.6, 2.72, 6.75, 2.2, color=ORANGE, lw=1.2)
    arrow(ax, 6.6, 3.3, 9.15, 3.3, color=NAVY, lw=1.2)
    t(ax, 7.0, 0.38, "橙色箭头：置信度低走补拍，不把没把握的高等级写到商品上。", 8.5, SLATE)
    save(fig, "fig13_swimlane.png")


def fig14_chart():
    w, h = 12.8, 6.6
    fig, ax = plt.subplots(figsize=(w, h * 0.72), facecolor="white")
    fig.patch.set_facecolor("white")
    labels = ["成色说不清\n不敢买", "照片看不出\n内页", "价格和成色\n对不上", "面交后发现\n笔记很多", "想卖但懒得\n写描述"]
    vals = [31, 24, 18, 16, 11]
    colors = [NAVY, BLUE, TEAL, ORANGE, GOLD]
    bars = ax.barh(labels[::-1], vals[::-1], color=colors[::-1], height=0.62, zorder=3)
    ax.set_xlim(0, 40)
    ax.set_xlabel("小组访谈中提到该痛点的人次（共 40 人次，可重复）", fontsize=9)
    ax.set_title("校园二手课本：同学们最常抱怨的五件事", fontsize=13, color=NAVY, pad=10, fontweight="bold")
    for b, v in zip(bars, vals[::-1]):
        ax.text(v + 0.5, b.get_y() + b.get_height() / 2, f"{v}", va="center", fontsize=9, color=NAVY)
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)
    ax.grid(axis="x", ls=":", alpha=0.4)
    ax.text(0.0, -0.18, "数据来源：本组扮演甲方时对宿舍同学的结构化访谈纪要（示意统计，用于支撑 BO，不作为论文抽样结论）。",
            transform=ax.transAxes, fontsize=7.5, color=SLATE)
    fig.tight_layout()
    p = OUT / "fig14_pain_chart.png"
    fig.savefig(p, dpi=220, bbox_inches="tight", facecolor="white")
    plt.close(fig)
    print("saved", p)


def fig15_smart():
    w, h = 13.2, 6.2
    fig, ax = canvas(w, h)
    header(ax, "图15  业务目标怎么才算写清楚（SMART）", "Goal Quality Check", w, h)
    letters = [
        ("S", "具体", "Specific", "不要写“提升体验”\n要写“给出成色等级”", BLUE),
        ("M", "可测", "Measurable", "识别结果可抽检\n买卖可问卷打分", GREEN),
        ("A", "可做", "Achievable", "先做课本，不一次\n做全品类人工智能", TEAL),
        ("R", "相关", "Relevant", "直接服务校园闲置\n课本流转这件事", ORANGE),
        ("T", "有时限", "Time-bound", "本学期课程周期内\n完成 V1.0 演示", GOLD),
    ]
    for i, (L, cn, en, desc, c) in enumerate(letters):
        x = 0.4 + i * 2.54
        rbox(ax, x, 1.2, 2.38, 4.05, fc="white", ec=c, lw=1.7)
        ax.add_patch(Circle((x + 1.19, 4.55), 0.42, facecolor=c, edgecolor="none", zorder=5))
        t(ax, x + 1.19, 4.55, L, 16, "white", "bold")
        t(ax, x + 1.19, 3.75, cn, 13, c, "bold")
        t(ax, x + 1.19, 3.35, en, 8, SLATE)
        t(ax, x + 1.19, 2.25, desc, 8.5, INK)
    save(fig, "fig15_smart.png")


if __name__ == "__main__":
    fig1_as_is_to_be()
    fig2_context()
    fig3_vision()
    fig4_pipeline()
    fig5_grades()
    fig6_trace()
    fig7_scope()
    fig8_stakeholder()
    fig9_usecase()
    fig10_risk()
    fig11_roadmap()
    fig12_env()
    fig13_swimlane()
    fig14_chart()
    fig15_smart()
    print("done")
