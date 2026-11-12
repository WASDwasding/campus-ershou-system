const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { unwrapList, fenToYuan, labelOf, imageUrl, thumbFrom } = require('../../utils/format')

function orderStatusClass(status) {
  if (status === 'COMPLETED') return 'pill-done'
  if (status === 'CANCELLED') return 'pill-muted'
  if (status === 'PAID') return 'pill-soft'
  return 'pill-cream'
}

Page({
  data: {
    role: 'BUYER',
    status: '',
    statuses: [{ value: '', label: '全部' }],
    list: [],
    loading: false,
    error: ''
  },
  onLoad() {
    this._seq = 0
  },
  onShow() {
    if (!session.token()) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    this.load()
  },
  setRole(e) {
    this.setData({ role: e.currentTarget.dataset.id })
    this.load()
  },
  setStatus(e) {
    this.setData({ status: e.currentTarget.dataset.id })
    this.load()
  },
  load() {
    const q = { role: this.data.role, page: 1, size: 20 }
    if (this.data.status) q.status = this.data.status
    this._seq += 1
    const seq = this._seq
    this.setData({ loading: true, error: '' })
    Promise.all([api.ensureMeta(), api.myOrders(q)])
      .then(([meta, data]) => {
        if (seq !== this._seq) return
        const seen = {}
        const list = []
        unwrapList(data).list.forEach((o) => {
          if (!o || seen[o.id]) return
          seen[o.id] = true
          const goods = o.goods || {}
          const firstImg = Array.isArray(goods.images) && goods.images.length ? imageUrl(goods.images[0]) : ''
          list.push({
            id: o.id,
            orderNo: o.orderNo,
            goodsTitle: goods.title || '商品',
            coverUrl: thumbFrom(goods.coverUrl || firstImg || ''),
            status: o.status,
            statusLabel: labelOf('orderStatus', o.status),
            statusClass: orderStatusClass(o.status),
            amountText: fenToYuan(o.amount)
          })
        })
        const statuses = [{ value: '', label: '全部' }].concat((meta.enums && meta.enums.orderStatus) || [])
        this.setData({ list, statuses, loading: false })
      })
      .catch((err) => {
        if (seq !== this._seq) return
        this.setData({ list: [], loading: false, error: (err && err.message) || '加载失败' })
        showError(err)
      })
  },
  open(e) {
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + e.currentTarget.dataset.id })
  }
})
