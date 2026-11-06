const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { presentGoods, unwrapList } = require('../../utils/format')

Page({
  data: { profile: {}, goods: [], ready: false },
  onLoad(q) { this.uid = q.id },
  onShow() {
    api.user(this.uid).then((profile) => this.setData({ profile: profile || {}, ready: true })).catch(showError)
    api.ensureMeta().catch(() => {}).then(() => api.userGoods(this.uid, 'ON_SALE')).then((data) => {
      this.setData({ goods: unwrapList(data).list.map(presentGoods) })
    }).catch(() => {})
  },
  open(e) { wx.navigateTo({ url: '/pages/detail/detail?id=' + e.detail.id }) }
})
