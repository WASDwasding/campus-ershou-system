const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { unwrapList, formatTime } = require('../../utils/format')

Page({
  data: { list: [], unreadOnly: false, loading: false, error: '' },
  onLoad() {
    this._seq = 0
  },
  onShow() {
    getApp().refreshBadges()
    this.load()
  },
  toggle() {
    this.setData({ unreadOnly: !this.data.unreadOnly })
    this.load()
  },
  load() {
    const q = { page: 1, size: 20 }
    if (this.data.unreadOnly) q.unreadOnly = true
    this._seq += 1
    const seq = this._seq
    this.setData({ loading: true, error: '' })
    api.notifications(q).then((data) => {
      if (seq !== this._seq) return
      const seen = {}
      const list = []
      unwrapList(data).list.forEach((n) => {
        if (!n || (n.id && seen[n.id])) return
        if (n.id) seen[n.id] = true
        list.push(Object.assign({}, n, { timeText: formatTime(n.createdAt) }))
      })
      this.setData({ list, loading: false })
    }).catch((err) => {
      if (seq !== this._seq) return
      this.setData({ list: [], loading: false, error: (err && err.message) || '加载失败' })
      showError(err)
    })
  },
  readAll() {
    api.readNotifications({ all: true }).then(() => {
      getApp().refreshBadges()
      this.load()
    }).catch(showError)
  },
  open(e) {
    const item = this.data.list[e.currentTarget.dataset.i]
    if (!item) return
    if (!item.read && item.id) api.readNotifications({ ids: [item.id] }).catch(() => {})
    const ref = item.ref || {}
    if (item.type === 'CHAT_MESSAGE' && ref.type === 'ORDER' && ref.id) {
      wx.navigateTo({ url: '/pages/chat-room/chat-room?orderId=' + ref.id })
      return
    }
    if (ref.type === 'ORDER' && ref.id) wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + ref.id })
    else if (ref.type === 'GOODS' && ref.id) wx.navigateTo({ url: '/pages/detail/detail?id=' + ref.id })
    else if (ref.type === 'REPORT') wx.navigateTo({ url: '/pages/reports/reports' })
  }
})
