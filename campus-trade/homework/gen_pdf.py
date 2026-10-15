# -*- coding: utf-8 -*-
from pathlib import Path

from fpdf import FPDF

TITLE = "商业银行客户数据挖掘应用分析"
OUT = Path(r"C:\Users\15736\campus-trade\homework\商业银行客户数据挖掘作业.pdf")

SECTIONS = [
    (
        "一、两个可利用数据挖掘解决的业务问题",
        [
            "问题1：个人信贷违约预测。根据年龄、收入、资产负债、贷款与还款记录，判断客户未来是否违约，用于贷前审批。属于分类任务：目标是“违约/不违约”两类，且历史已有标签，可监督学习。",
            "问题2：金融产品交叉销售。利用理财、保险、信用卡等购买记录，发现产品间的伴随购买关系。属于关联分析：目标不是预测单一标签，而是挖掘“买A常买B”这类规则。",
        ],
    ),
    (
        "二、以问题1为例的实施流程",
        [
            "数据采集：从核心、信贷、信用卡系统抽取客户基本信息、资产负债、贷款还款与消费流水，并以历史逾期或坏账作为标签。",
            "数据预处理：处理缺失与异常值，统一口径；对类别变量编码；构造负债收入比、近12个月逾期次数等特征；按时间划分训练/测试集，避免信息泄露。",
            "数据挖掘建模：以是否违约为目标建立分类模型（如逻辑回归），输出违约概率，为审批提供量化依据。",
            "模型评估：用精确率、召回率、AUC评价。风控更关注召回率，减少漏判高风险客户；并结合误判成本设定阈值，检验模型稳定性。",
            "结果应用：嵌入贷前审批：高风险拒绝或加价，中风险人工复核，低风险快速放贷。上线后定期回测不良率并更新模型，持续降低风险、提升效率。",
        ],
    ),
]


def count_chars() -> int:
    parts = []
    for title, paras in SECTIONS:
        parts.append(title)
        parts.extend(paras)
    return sum(1 for ch in "".join(parts) if not ch.isspace())


class HomeworkPDF(FPDF):
    def footer(self):
        self.set_y(-14)
        self.set_font("Song", size=9)
        self.set_text_color(120, 120, 120)
        self.cell(0, 8, "课程作业  |  数据挖掘", align="C")


def main():
    n = count_chars()
    print(f"body_chars={n}")

    font_hei = Path(r"C:\Windows\Fonts\simhei.ttf")
    if not font_hei.exists():
        font_hei = Path(r"C:\Windows\Fonts\msyh.ttc")

    pdf = HomeworkPDF(format="A4", unit="mm")
    pdf.set_auto_page_break(auto=True, margin=18)
    pdf.set_margins(18, 16, 18)
    pdf.add_page()
    pdf.add_font("Song", fname=str(font_hei))
    pdf.add_font("Hei", fname=str(font_hei))

    pdf.set_font("Hei", size=18)
    pdf.set_text_color(24, 32, 48)
    pdf.cell(0, 12, TITLE, align="C", new_x="LMARGIN", new_y="NEXT")

    pdf.set_font("Song", size=11)
    pdf.set_text_color(90, 90, 90)
    pdf.cell(0, 8, "课程作业", align="C", new_x="LMARGIN", new_y="NEXT")
    pdf.ln(2)

    pdf.set_draw_color(180, 180, 180)
    pdf.set_fill_color(248, 249, 252)
    pdf.set_text_color(30, 30, 30)
    pdf.set_font("Song", size=12)
    y = pdf.get_y()
    pdf.rect(18, y, 174, 14, style="DF")
    pdf.set_xy(22, y + 3.5)
    pdf.cell(58, 7, "姓名：____________")
    pdf.cell(58, 7, "班级：____________")
    pdf.cell(50, 7, "学号：____________")
    pdf.set_y(y + 20)

    for title, paras in SECTIONS:
        pdf.set_font("Hei", size=13)
        pdf.set_text_color(24, 32, 48)
        pdf.multi_cell(174, 8, title)
        pdf.ln(1.5)
        pdf.set_font("Song", size=12)
        pdf.set_text_color(32, 32, 32)
        for para in paras:
            pdf.multi_cell(174, 7.4, para, align="L")
            pdf.ln(2.2)
        pdf.ln(2)

    OUT.parent.mkdir(parents=True, exist_ok=True)
    pdf.output(str(OUT))
    print(OUT)


if __name__ == "__main__":
    main()
