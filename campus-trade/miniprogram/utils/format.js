function chars(s) {
  return Array.from(String(s || '')).length
}

function fenToYuan(fen) {
  const n = Number(fen)
  if (!isFinite(n)) return '0.00'
  const sign = n < 0 ? '-' : ''
  const abs = Math.abs(Math.trunc(n))
  const yuan = Math.floor(abs / 100)
  const cent = String(abs % 100).padStart(2, '0')
  return sign + yuan + '.' + cent
}

function yuanToFen(input) {
  const s = String(input == null ? '' : input).trim()
  if (!/^\d+(\.\d{1,2})?$/.test(s)) return null
  const parts = s.split('.')
  const yuan = parseInt(parts[0], 10)
  const cent = parseInt(((parts[1] || '') + '00').slice(0, 2), 10)
  return yuan * 100 + cent
}

function formatTime(iso) {
  if (!iso) return ''
  const m = String(iso).match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})/)
  if (!m) return String(iso)
  return m[2] + '-' + m[3] + ' ' + m[4] + ':' + m[5]
}

function toOffsetIso(dateStr, timeStr) {
  if (!dateStr || !timeStr) return ''
  const hm = String(timeStr).slice(0, 5)
  return dateStr + 'T' + hm + ':00+08:00'
}

function labelOf(enumName, value) {
  if (value == null || value === '') return ''
  let meta = null
  try {
    meta = getApp().globalData.meta
  } catch (e) {
    meta = null
  }
  const list = meta && meta.enums && meta.enums[enumName]
  if (!Array.isArray(list)) return '未知'
  const hit = list.find((x) => x && x.value === value)
  return hit ? hit.label : '未知'
}

function imageUrl(img) {
  if (!img) return ''
  if (typeof img === 'string') return img
  return img.url || img.thumbUrl || ''
}

function presentGoods(g) {
  if (!g) return {}
  const meetup = g.meetupBuilding || {}
  const seller = g.seller || {}
  const images = (g.images || []).map(imageUrl).filter(Boolean)
  return Object.assign({}, g, {
    coverUrl: g.coverUrl || images[0] || '',
    imageUrls: images,
    priceText: fenToYuan(g.price),
    originalPriceText: g.originalPrice ? fenToYuan(g.originalPrice) : '',
    conditionLabel: labelOf('goodsCondition', g.condition),
    statusLabel: labelOf('goodsStatus', g.status),
    meetupName: meetup.name || '',
    sellerName: seller.nickname || '同学',
    categoryName: (g.category && g.category.name) || ''
  })
}

function unwrapList(data) {
  if (Array.isArray(data)) {
    return { list: data, page: 1, size: data.length, total: data.length, hasMore: false }
  }
  const list = (data && data.list) || []
  return {
    list,
    page: data && data.page ? data.page : 1,
    size: data && data.size ? data.size : list.length,
    total: data && data.total != null ? data.total : list.length,
    hasMore: !!(data && data.hasMore)
  }
}

function hasContact(text) {
  const s = String(text || '').toLowerCase()
  const phone = /(?:^|[^\d])1[3-9]\d(?:[\s-]?\d){8}(?!\d)/
  const wxid = /(?:微信|威信|薇信|v信|vx|wx|weixin|wechat|加v|\+v)[\s:：]*[a-z][-_a-z0-9]{5,19}/
  const qq = /(?:qq|扣扣|企鹅)[\s:：]*[1-9]\d{4,10}/
  const email = /[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}/
  return phone.test(s) || wxid.test(s) || qq.test(s) || email.test(s)
}

function clientId(prefix) {
  const raw = (prefix || 'c') + Date.now().toString(36) + Math.random().toString(36).slice(2, 10)
  return raw.slice(0, 40)
}

module.exports = {
  chars,
  fenToYuan,
  yuanToFen,
  formatTime,
  toOffsetIso,
  labelOf,
  imageUrl,
  presentGoods,
  unwrapList,
  hasContact,
  clientId
}
