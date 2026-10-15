function pad(n) {
  return n < 10 ? '0' + n : '' + n
}

function formatTime(ts) {
  const d = new Date(ts)
  const now = new Date()
  const sameDay =
    d.getFullYear() === now.getFullYear() &&
    d.getMonth() === now.getMonth() &&
    d.getDate() === now.getDate()
  if (sameDay) return pad(d.getHours()) + ':' + pad(d.getMinutes())
  return d.getMonth() + 1 + '/' + d.getDate()
}

function uid(prefix) {
  return (prefix || 'id') + '_' + Date.now().toString(36) + Math.random().toString(36).slice(2, 6)
}

function clone(obj) {
  return JSON.parse(JSON.stringify(obj))
}

const RISK_WORDS = ['保证金', '点击链接', '先转账', '先付定金', '加微信', '加QQ', '扫码付款', '私聊转账']

function inspectChat(text) {
  const hits = RISK_WORDS.filter((w) => text.indexOf(w) >= 0)
  const phone = /(\+?86)?1[3-9]\d{9}/.test(text)
  const wechat = /微信|wxid_|v[\s:：]{0,2}[a-zA-Z][a-zA-Z0-9_-]{5,}/.test(text)
  const email = /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/.test(text)
  const blocked = hits.length > 0 || phone || wechat || email
  let reason = ''
  if (phone || wechat || email) reason = '请不要在站外交换联系方式，平台内沟通更安全'
  else if (hits.length) reason = '检测到高风险话术：' + hits.join('、')
  return { blocked, reason, hits }
}

function parseNaturalQuery(q) {
  const raw = (q || '').trim()
  const res = { keyword: '', maxPrice: 0, category: '' }
  const m = raw.match(/(\d+)\s*元/)
  if (m) res.maxPrice = Number(m[1])
  const map = [
    ['教材|书|习题', 'books'],
    ['键盘|耳机|平板|电脑|数码|充电', 'electronics'],
    ['灯|冰箱|日用|椅子|桌子', 'daily'],
    ['鞋|衣|包|服装', 'fashion'],
    ['球|车|瑜伽|运动', 'sports'],
    ['美妆|护肤|吹风机', 'beauty'],
    ['免费|赠送', 'free']
  ]
  map.forEach(([reg, id]) => {
    if (new RegExp(reg).test(raw) && !res.category) res.category = id
  })
  res.keyword = raw
    .replace(/\d+\s*元(以内|以下|不超过)?/g, ' ')
    .replace(/预算|以内|以下|不超过|一个|一台|想要|求购|的/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
  return res
}

function conditionLabel(id) {
  return (
    {
      like_new: '几乎全新',
      '90_new': '九成新',
      '80_new': '八成新',
      '70_new': '七成新及以下'
    }[id] || '未知成色'
  )
}

function categoryLabel(id) {
  return (
    {
      books: '教材书籍',
      electronics: '数码配件',
      daily: '宿舍日用',
      fashion: '服饰鞋包',
      sports: '运动户外',
      beauty: '美妆护肤',
      free: '免费赠送',
      other: '其他'
    }[id] || '其他'
  )
}

function statusLabel(id) {
  return (
    {
      available: '在售',
      reserved: '已预约',
      meetup: '待面交',
      completed: '已完成',
      cancelled: '已取消',
      appeal: '申诉中'
    }[id] || id
  )
}

module.exports = {
  formatTime,
  uid,
  clone,
  inspectChat,
  parseNaturalQuery,
  conditionLabel,
  categoryLabel,
  statusLabel
}
