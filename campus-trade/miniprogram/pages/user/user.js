const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { presentGoods, unwrapList } = require('../../utils/format')

Page({
  data: { profile: {}, goods: [], reviews: [], ready: false },
  onLoad(q) { this.uid = q.id },
  onShow() {
    api.user(this.uid).then((profile) => this.setData({ profile: profile || {}, ready: true })).catch(showError)
    api.ensureMeta().catch(() => {}).then(() => api.userGoods(this.uid, 'ON_SALE')).then((data) => {
      this.setData({ goods: unwrapList(data).list.map(presentGoods) })
    }).catch(() => {})
    api.userReviews(this.uid).then((data) => {
      this.setData({ reviews: unwrapList(data).list })
    }).catch(() => {})
  },
  open(e) { wx.navigateTo({ url: '/pages/detail/detail?id=' + e.detail.id }) },
  report() {
    api.ensureMeta().then((meta) => {
      const reasons = (meta.enums && meta.enums.reportReason) || []
      wx.showActionSheet({
        itemList: reasons.map((x) => x.label),
        success: (r) => {
          api.report({
            targetType: 'USER',
            targetId: Number(this.uid),
            reason: reasons[r.tapIndex].value,
            description: '',
            evidenceFileIds: []
          }).then(() => wx.showToast({ title: '已提交', icon: 'success' })).catch(showError)
        }
      })
    }).catch(showError)
  }
})
