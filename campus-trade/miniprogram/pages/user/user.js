const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { presentGoods, unwrapList } = require('../../utils/format')

Page({
  data: { profile: {}, goods: [], ready: false, goodsError: '' },
  onLoad(q) { this.uid = q.id },
  onShow() {
    api.user(this.uid).then((profile) => {
      const p = profile || {}
      p.initial = (p.nickname || '同').slice(0, 1)
      this.setData({ profile: p, ready: true })
    }).catch(showError)
    this.loadGoods()
  },
  loadGoods() {
    this.setData({ goodsError: '' })
    Promise.all([api.ensureMeta(), api.userGoods(this.uid, 'ON_SALE')]).then(([, data]) => {
      this.setData({ goods: unwrapList(data).list.map(presentGoods) })
    }).catch((err) => {
      this.setData({ goodsError: (err && err.message) || '加载失败' })
      showError(err)
    })
  },
  open(e) { wx.navigateTo({ url: '/pages/detail/detail?id=' + e.detail.id }) },
  onAvatarErr() {
    if (this.data.profile && this.data.profile.avatarUrl) {
      this.setData({ 'profile.avatarUrl': '' })
    }
  }
})
