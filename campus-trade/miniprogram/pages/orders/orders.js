const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { unwrapList, fenToYuan, labelOf } = require('../../utils/format')

Page({
  data: {
    role: 'BUYER',
    status: '',
    statuses: [{ value: '', label: '全部' }],
    list: []
  },
  onShow() {
    if (!session.token()) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    api.ensureMeta().then((meta) => {
      const statuses = [{ value: '', label: '全部' }].concat((meta.enums && meta.enums.orderStatus) || [])
      this.setData({ statuses })
    }).catch(() => {})
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
    api
      .myOrders(q)
      .then((data) => {
        const list = unwrapList(data).list.map((o) => ({
          id: o.id,
          orderNo: o.orderNo,
          goodsTitle: (o.goods && o.goods.title) || '商品',
          statusLabel: labelOf('orderStatus', o.status),
          amountText: fenToYuan(o.amount)
        }))
        this.setData({ list })
      })
      .catch(showError)
  },
  open(e) {
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + e.currentTarget.dataset.id })
  }
})
