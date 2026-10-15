const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { unwrapList, formatTime } = require('../../utils/format')

Page({
  data: { list: [], unreadOnly: false },
  onShow() { this.load() },
  toggle() {
    this.setData({ unreadOnly: !this.data.unreadOnly })
    this.load()
  },
  load() {
    const q = { page: 1, size: 20 }
    if (this.data.unreadOnly) q.unreadOnly = true
    api.notifications(q).then((data) => {
      const list = unwrapList(data).list.map((n) => Object.assign({}, n, { timeText: formatTime(n.createdAt) }))
      this.setData({ list })
    }).catch(showError)
  },
  readAll() {
    api.readNotifications({ all: true }).then(() => this.load()).catch(showError)
  },
  open(e) {
    const item = this.data.list[e.currentTarget.dataset.i]
    if (!item) return
    if (!item.read && item.id) api.readNotifications({ ids: [item.id] }).catch(() => {})
    const ref = item.ref || {}
    if (ref.type === 'ORDER' && ref.id) wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + ref.id })
    else if (ref.type === 'GOODS' && ref.id) wx.navigateTo({ url: '/pages/detail/detail?id=' + ref.id })
  }
})
