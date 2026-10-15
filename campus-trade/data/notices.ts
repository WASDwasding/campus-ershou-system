export type CampusNotice = {
  id: string;
  emoji: string;
  title: string;
  body: string;
  tone: 'blue' | 'peach' | 'mint' | 'yellow';
};

export const campusNotices: CampusNotice[] = [
  {
    id: 'n1',
    emoji: '🌊',
    title: '湖边闲置周',
    body: '本周同楼交易优先展示，见面交易记得互评哦～',
    tone: 'blue',
  },
  {
    id: 'n2',
    emoji: '📷',
    title: '智能低光发帖',
    body: '宿舍灯光也能拍清楚！自动去噪+划痕标注',
    tone: 'peach',
  },
  {
    id: 'n3',
    emoji: '🛡️',
    title: '仅校园私聊',
    body: '微信号/手机号会被自动屏蔽，安心闲逛',
    tone: 'mint',
  },
];

export const emptyTips = [
  '贴一张手帐便签，开始第一笔闲置吧 ✿',
  '试试切换分类，或者放宽价格筛选～',
  '同楼好物通常更快成交哦',
];
