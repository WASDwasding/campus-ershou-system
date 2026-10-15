# -*- coding: utf-8 -*-
"""Engineering-style figures for the segmentation survey paper."""
from pathlib import Path

import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch, Polygon, Circle, Rectangle
import numpy as np

OUT = Path(r"C:\Users\15736\campus-trade\homework\seg_figs")
OUT.mkdir(parents=True, exist_ok=True)

plt.rcParams["font.sans-serif"] = ["Microsoft YaHei", "SimHei", "SimSun"]
plt.rcParams["axes.unicode_minus"] = False
plt.rcParams["pdf.fonttype"] = 42
plt.rcParams["ps.fonttype"] = 42

NAVY = "#0E1A2B"
CYAN = "#1AA7B8"
ORANGE = "#E36A2E"
GOLD = "#D4A017"
TEAL = "#2E8B7A"
SLATE = "#4A5A6A"
PAPER = "#F7F4EE"
INK = "#1B2430"
BOX = "#E8EEF4"


def save(fig, name):
    p = OUT / name
    fig.savefig(p, dpi=200, bbox_inches="tight", facecolor=fig.get_facecolor())
    plt.close(fig)
    print("saved", p)


def rounded(ax, x, y, w, h, fc, ec, lw=1.4, r=0.08, z=2):
    p = FancyBboxPatch(
        (x, y), w, h, boxstyle=f"round,pad=0.012,rounding_size={r}",
        facecolor=fc, edgecolor=ec, linewidth=lw, zorder=z,
    )
    ax.add_patch(p)
    return p


def fig1_three_tasks():
    fig, axes = plt.subplots(1, 3, figsize=(12.2, 4.6), facecolor="white")
    titles = ["语义分割 Semantic", "实例分割 Instance", "全景分割 Panoptic"]
    notes = [
        "只认类别\n路/车/人涂成同一块色",
        "同类也要拆开\n两辆车两张掩膜",
        "东西 + 背景一起给\n既有实例又有stuff",
    ]
    palettes = [
        [("#7A8B6F", "道路"), ("#C45C26", "车辆"), ("#2F6F8F", "行人")],
        [("#C45C26", "车A"), ("#E6A23C", "车B"), ("#2F6F8F", "人1"), ("#5BA3C9", "人2")],
        [("#7A8B6F", "道路"), ("#C45C26", "车A"), ("#E6A23C", "车B"), ("#2F6F8F", "人"), ("#8FA4B8", "天空")],
    ]
    layouts = [
        [("road", 0.08, 0.08, 0.84, 0.28), ("car", 0.18, 0.28, 0.46, 0.26), ("person", 0.68, 0.22, 0.14, 0.34)],
        [("carA", 0.12, 0.28, 0.32, 0.26), ("carB", 0.42, 0.30, 0.28, 0.22), ("p1", 0.72, 0.22, 0.10, 0.34), ("p2", 0.84, 0.20, 0.08, 0.28)],
        [("sky", 0.08, 0.58, 0.84, 0.30), ("road", 0.08, 0.08, 0.84, 0.22), ("carA", 0.14, 0.26, 0.30, 0.24), ("carB", 0.46, 0.28, 0.26, 0.20), ("p", 0.76, 0.22, 0.12, 0.32)],
    ]
    colors_map = [
        {"road": "#7A8B6F", "car": "#C45C26", "person": "#2F6F8F"},
        {"carA": "#C45C26", "carB": "#E6A23C", "p1": "#2F6F8F", "p2": "#5BA3C9"},
        {"sky": "#8FA4B8", "road": "#7A8B6F", "carA": "#C45C26", "carB": "#E6A23C", "p": "#2F6F8F"},
    ]
    for ax, title, note, layout, cmap, pal in zip(axes, titles, notes, layouts, colors_map, palettes):
        ax.set_xlim(0, 1)
        ax.set_ylim(0, 1.18)
        ax.set_aspect("equal")
        ax.axis("off")
        ax.add_patch(Rectangle((0.04, 0.06), 0.92, 0.86, facecolor="#0E1A2B", edgecolor=CYAN, lw=1.6))
        # exploded offset copies
        for i, (k, x, y, w, h) in enumerate(layout):
            ox, oy = 0.012 * i, 0.018 * i
            ax.add_patch(Rectangle((x + ox + 0.02, y + oy + 0.02), w, h, facecolor="none", edgecolor="white", lw=0.6, linestyle="--", alpha=0.35))
            ax.add_patch(Rectangle((x, y), w, h, facecolor=cmap[k], edgecolor="white", lw=0.8, alpha=0.92))
        ax.set_title(title, fontsize=12, color=NAVY, pad=8, fontweight="bold")
        ax.text(0.5, -0.02, note, ha="center", va="top", fontsize=9, color=SLATE, transform=ax.transAxes)
        # legend chips
        lx = 0.08
        for c, lab in pal:
            ax.add_patch(Rectangle((lx, 0.94), 0.045, 0.045, facecolor=c, transform=ax.transAxes, clip_on=False))
            ax.text(lx + 0.055, 0.96, lab, fontsize=8, va="center", transform=ax.transAxes, color=INK)
            lx += 0.22
    fig.suptitle("图示拆解：三种目标分割到底在分什么", fontsize=13, color=NAVY, y=1.02)
    save(fig, "fig1_three_tasks.png")


def fig2_timeline():
    fig, ax = plt.subplots(figsize=(12.4, 4.3), facecolor="white")
    ax.set_xlim(2014.4, 2025.2)
    ax.set_ylim(0, 3.2)
    ax.axis("off")
    ax.plot([2015, 2024.6], [1.55, 1.55], color=NAVY, lw=2.4, zorder=1)
    items = [
        (2015, 2.35, "FCN / U-Net", "像素级端到端", CYAN),
        (2017, 0.55, "Mask R-CNN\nPSPNet", "实例 + 金字塔", ORANGE),
        (2018, 2.35, "DeepLabv3+", "空洞卷积编解码", TEAL),
        (2021, 0.55, "SegFormer", "Transformer编码器", GOLD),
        (2022, 2.35, "Mask2Former", "统一三类任务", "#6B4C9A"),
        (2023, 0.55, "SAM", "可提示基础模型", CYAN),
        (2024, 2.35, "SAM 2", "图像+视频记忆", ORANGE),
    ]
    for year, y, title, sub, color in items:
        ax.scatter([year], [1.55], s=70, color=color, zorder=3, edgecolors=NAVY, lw=0.6)
        ax.plot([year, year], [1.55, y], color=color, lw=1.1, ls="--")
        rounded(ax, year - 0.55, y - 0.28 if y > 1.55 else y - 0.08, 1.12, 0.62, color, color, lw=0)
        ax.text(year, y + 0.04 if y > 1.55 else y + 0.24, title, ha="center", va="center", fontsize=8.5, color="white", fontweight="bold")
        ax.text(year, 1.28 if y > 1.55 else 1.82, str(year), ha="center", fontsize=8, color=NAVY)
        ax.text(year, y - 0.18 if y > 1.55 else y - 0.02, sub, ha="center", va="center", fontsize=7.2, color="#F4F7FB")
    ax.set_title("2015—2024 目标分割主线（不是完整编年，只标转折点）", fontsize=12, color=NAVY, pad=6)
    save(fig, "fig2_timeline.png")


def fig3_explode_fcn():
    fig, ax = plt.subplots(figsize=(12.0, 5.0), facecolor="white")
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 5.2)
    ax.axis("off")
    ax.set_title("FCN / U-Net 结构爆炸图：下采样抽特征，再把分辨率拼回去", fontsize=12, color=NAVY, pad=4)

    # exploded encoder blocks
    enc = [(0.4, 1.6, 1.5, 2.0, "#123047", "输入\nHxWx3"),
           (2.2, 1.3, 1.5, 2.6, "#1A4A63", "Conv1\n浅层边缘"),
           (4.0, 1.0, 1.5, 3.2, "#1F5F7A", "Conv2-3\n纹理/部件"),
           (5.8, 0.7, 1.5, 3.8, "#1AA7B8", "深层语义\n低分辨率")]
    for x, y, w, h, c, t in enc:
        rounded(ax, x, y, w, h, c, "white", lw=0.8, r=0.12)
        ax.text(x + w / 2, y + h / 2, t, ha="center", va="center", color="white", fontsize=9)
        ax.annotate("", xy=(x + w + 0.12, y + h / 2), xytext=(x + w + 0.28, y + h / 2 + 0.55),
                    arrowprops=dict(arrowstyle="-", color=SLATE, lw=0.7, ls=":"))
    # skip
    ax.annotate("skip / 跳跃连接\n把细节送回解码端", xy=(3.0, 4.15), xytext=(3.0, 4.7),
                ha="center", fontsize=8, color=ORANGE,
                arrowprops=dict(arrowstyle="->", color=ORANGE, lw=1.2))
    # decoder
    rounded(ax, 8.0, 1.4, 1.7, 2.4, ORANGE, "white", lw=0.8, r=0.12)
    ax.text(8.85, 2.6, "上采样\n+融合", ha="center", va="center", color="white", fontsize=10)
    rounded(ax, 10.1, 1.7, 1.6, 1.8, NAVY, CYAN, lw=1.4, r=0.12)
    ax.text(10.9, 2.6, "像素图\n类别/掩膜", ha="center", va="center", color="white", fontsize=10)
    ax.annotate("", xy=(8.0, 2.6), xytext=(7.4, 2.6), arrowprops=dict(arrowstyle="->", color=NAVY, lw=1.6))
    ax.annotate("", xy=(10.1, 2.6), xytext=(9.75, 2.6), arrowprops=dict(arrowstyle="->", color=NAVY, lw=1.6))
    ax.text(6.0, 0.28, "读论文时容易忽略：真正拉开差距的往往不是“层数更多”，而是怎么把丢掉的边界补回来。",
            ha="center", fontsize=8.5, color=SLATE)
    save(fig, "fig3_fcn_explode.png")


def fig4_sam_explode():
    fig, ax = plt.subplots(figsize=(12.0, 5.4), facecolor="white")
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 6)
    ax.axis("off")
    ax.set_title("SAM 模块爆炸图：图像编码一次，提示随时插，解码器吐掩膜", fontsize=12, color=NAVY, pad=2)

    rounded(ax, 0.35, 3.35, 3.3, 2.15, "#123047", CYAN, lw=1.5, r=0.1)
    ax.text(2.0, 4.85, "Image Encoder", ha="center", color=CYAN, fontsize=11, fontweight="bold")
    ax.text(2.0, 4.25, "ViT 骨干（MAE预训练）", ha="center", color="white", fontsize=9)
    ax.text(2.0, 3.75, "图像只跑一遍\n得到稠密嵌入", ha="center", color="#D7EEF2", fontsize=8.5)

    rounded(ax, 0.35, 0.55, 3.3, 2.15, "#3A2416", ORANGE, lw=1.5, r=0.1)
    ax.text(2.0, 2.05, "Prompt Encoder", ha="center", color=ORANGE, fontsize=11, fontweight="bold")
    ax.text(2.0, 1.45, "点 / 框 / 粗掩膜 / 文本", ha="center", color="white", fontsize=9)
    ax.text(2.0, 0.95, "交互信号被编成token", ha="center", color="#F3E2D4", fontsize=8.5)

    rounded(ax, 4.6, 1.55, 3.5, 2.9, "#1F3D4A", GOLD, lw=1.6, r=0.1)
    ax.text(6.35, 3.95, "Mask Decoder", ha="center", color=GOLD, fontsize=12, fontweight="bold")
    ax.text(6.35, 3.25, "轻量Transformer解码", ha="center", color="white", fontsize=9)
    ax.text(6.35, 2.55, "一次给出最多3个候选掩膜\n并打IoU置信分", ha="center", color="#F4E8C1", fontsize=8.5)

    rounded(ax, 8.9, 3.5, 2.7, 1.6, TEAL, "white", lw=0.8, r=0.1)
    ax.text(10.25, 4.3, "掩膜 A  整体", ha="center", color="white", fontsize=9)
    rounded(ax, 8.9, 2.2, 2.7, 1.15, "#2F6F8F", "white", lw=0.8, r=0.1)
    ax.text(10.25, 2.78, "掩膜 B  部件", ha="center", color="white", fontsize=9)
    rounded(ax, 8.9, 0.9, 2.7, 1.15, ORANGE, "white", lw=0.8, r=0.1)
    ax.text(10.25, 1.48, "掩膜 C  子部件", ha="center", color="white", fontsize=9)

    ax.annotate("", xy=(4.6, 3.7), xytext=(3.7, 4.3), arrowprops=dict(arrowstyle="->", color=CYAN, lw=1.7))
    ax.annotate("", xy=(4.6, 2.3), xytext=(3.7, 1.6), arrowprops=dict(arrowstyle="->", color=ORANGE, lw=1.7))
    ax.annotate("", xy=(8.9, 3.0), xytext=(8.15, 3.0), arrowprops=dict(arrowstyle="->", color=GOLD, lw=1.7))
    ax.text(6.35, 0.28, "和分类网络最大的差别：SAM默认不输出“这是猫/车”，只输出“你点的那一块”。业务系统还要再接标签头。",
            ha="center", fontsize=8.5, color=SLATE)
    save(fig, "fig4_sam_explode.png")


def fig5_bars():
    fig, axes = plt.subplots(1, 2, figsize=(12.2, 4.8), facecolor="white")

    ax = axes[0]
    names = ["FCN-8s\n2015", "PSPNet\n2017", "DeepLabv3+\n2018"]
    vals = [67.2, 85.4, 89.0]
    colors = [SLATE, TEAL, CYAN]
    bars = ax.bar(names, vals, color=colors, width=0.62, zorder=3)
    ax.set_ylim(55, 95)
    ax.set_ylabel("mIoU (%)")
    ax.set_title("PASCAL VOC 2012 test（语义分割）", fontsize=11)
    ax.axhline(67.2, color=SLATE, ls=":", lw=0.8)
    for b, v in zip(bars, vals):
        ax.text(b.get_x() + b.get_width() / 2, v + 0.8, f"{v:.1f}", ha="center", fontsize=9, color=NAVY)
    ax.text(0.5, -0.22, "数据分别引自 Long 等 CVPR2015、Zhao 等 CVPR2017、Chen 等 ECCV2018。\n预训练和额外数据设置并不完全一致，只能看趋势。",
            transform=ax.transAxes, ha="center", fontsize=7.5, color=SLATE)
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)
    ax.grid(axis="y", ls=":", alpha=0.4)

    ax = axes[1]
    names = ["DeepLabv3+\nCityscapes", "SegFormer\nCityscapes", "Mask2Former\nADE20K", "Mask R-CNN\nCOCO mask"]
    vals = [82.1, 84.0, 57.7, 37.1]
    units = ["mIoU", "mIoU", "mIoU", "AP"]
    colors = [TEAL, CYAN, "#6B4C9A", ORANGE]
    bars = ax.bar(names, vals, color=colors, width=0.62, zorder=3)
    ax.set_ylim(0, 100)
    ax.set_ylabel("指标数值")
    ax.set_title("不同基准，不要直接横比", fontsize=11)
    for b, v, u in zip(bars, vals, units):
        ax.text(b.get_x() + b.get_width() / 2, v + 1.5, f"{v}\n{u}", ha="center", fontsize=8, color=NAVY)
    ax.text(0.5, -0.22, "Cityscapes 街景、ADE20K 室内外场景、COCO 实例AP，任务和指标都不是一回事。",
            transform=ax.transAxes, ha="center", fontsize=7.5, color=SLATE)
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)
    ax.grid(axis="y", ls=":", alpha=0.4)
    fig.suptitle("公开论文里的代表性成绩（核对原文后摘录）", fontsize=12, color=NAVY)
    fig.tight_layout()
    save(fig, "fig5_benchmarks.png")


def fig6_scale():
    fig, ax = plt.subplots(figsize=(11.6, 4.6), facecolor="white")
    names = ["PASCAL VOC\n约 10k 级", "Cityscapes\n5k 精细标注", "COCO\n约 118k 训练图", "ADE20K\n20k+", "SA-1B\n11M 图 / 1.1B 掩膜"]
    masks = [2.9e4, 2.5e4, 8.5e5, 7e5, 1.1e9]  # order-of-magnitude illustrative except SA-1B exact
    # Use exact-ish published magnitudes; VOC/Cityscapes mask counts are approximate order, label as 量级
    x = np.arange(len(names))
    colors = [SLATE, TEAL, "#2F6F8F", GOLD, ORANGE]
    ax.bar(x, masks, color=colors, width=0.62, zorder=3)
    ax.set_yscale("log")
    ax.set_xticks(x)
    ax.set_xticklabels(names, fontsize=9)
    ax.set_ylabel("掩膜规模（对数坐标）")
    ax.set_title("数据集体量差了几个数量级：SAM 的 SA-1B 把规模直接拉到十亿级掩膜", fontsize=11, color=NAVY)
    ax.text(4, 1.1e9 * 1.15, "1.1B（论文给出）", ha="center", fontsize=8, color=ORANGE)
    ax.text(0.5, -0.18, "VOC/Cityscapes/COCO/ADE20K 柱高按公开数据集量级示意；SA-1B 的 11M 图像、1.1B 掩膜来自 Kirillov 等 ICCV 2023。",
            transform=ax.transAxes, ha="center", fontsize=8, color=SLATE)
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)
    ax.grid(axis="y", ls=":", alpha=0.4)
    save(fig, "fig6_dataset_scale.png")


def fig7_pipeline():
    fig, ax = plt.subplots(figsize=(12.2, 4.2), facecolor="white")
    ax.set_xlim(0, 12.2)
    ax.set_ylim(0, 4.2)
    ax.axis("off")
    ax.set_title("接到软件工程里，分割模型只是流水线中间一截", fontsize=12, color=NAVY)
    steps = [
        (0.3, "数据采集\n与标注规范"),
        (2.3, "版本化数据集\n+质量抽检"),
        (4.3, "训练/微调\n与阈值标定"),
        (6.3, "封装推理服务\n延迟·显存·日志"),
        (8.3, "业务规则\n人机复核"),
        (10.3, "线上监控\n漂移与回滚"),
    ]
    for i, (x, t) in enumerate(steps):
        rounded(ax, x, 1.45, 1.75, 1.35, NAVY if i in (3, 5) else "#1F4E5F", CYAN, lw=1.3, r=0.1)
        ax.text(x + 0.88, 2.12, t, ha="center", va="center", color="white", fontsize=8.5)
        if i < len(steps) - 1:
            ax.annotate("", xy=(x + 1.92, 2.12), xytext=(x + 1.78, 2.12),
                        arrowprops=dict(arrowstyle="->", color=ORANGE, lw=1.6))
    ax.text(6.1, 0.55, "课上讲需求工程时强调可追溯：掩膜错了要能回到哪一批数据、哪次模型和哪条规则。分割准确率 alone 交不了工。",
            ha="center", fontsize=8.5, color=SLATE)
    save(fig, "fig7_se_pipeline.png")


def fig8_iou():
    fig, ax = plt.subplots(figsize=(8.6, 4.4), facecolor="white")
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 5)
    ax.axis("off")
    ax.set_title("IoU 其实就是两块橡皮泥重合了多少", fontsize=12, color=NAVY)
    c1 = Circle((3.2, 2.5), 1.35, facecolor="#1AA7B855", edgecolor=CYAN, lw=2)
    c2 = Circle((4.35, 2.5), 1.35, facecolor="#E36A2E55", edgecolor=ORANGE, lw=2)
    ax.add_patch(c1)
    ax.add_patch(c2)
    ax.text(2.3, 2.5, "预测", color=CYAN, fontsize=10, ha="center")
    ax.text(5.3, 2.5, "真值", color=ORANGE, fontsize=10, ha="center")
    ax.text(7.7, 3.3, "IoU = 交集 / 并集", fontsize=12, color=NAVY, fontweight="bold")
    ax.text(7.7, 2.4, "mIoU：各类 IoU 再平均\n实例分割常用 mask AP\n全景分割常用 PQ", fontsize=9, color=SLATE, ha="center")
    ax.text(5.0, 0.45, "边界差 3 个像素，肉眼看着还行，IoU 可能已经掉一截。这就是为啥分割比框检测更“较真”。",
            ha="center", fontsize=8.5, color=SLATE)
    save(fig, "fig8_iou.png")


if __name__ == "__main__":
    fig1_three_tasks()
    fig2_timeline()
    fig3_explode_fcn()
    fig4_sam_explode()
    fig5_bars()
    fig6_scale()
    fig7_pipeline()
    fig8_iou()
    print("done")
