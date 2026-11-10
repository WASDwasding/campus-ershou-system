const api = require('../../../utils/api')
const adminSession = require('../../../utils/admin-session')
const { showError } = require('../../../utils/request')
const { labelOf, chars } = require('../../../utils/format')

Page({
  data: { item: {}, reason: '' },
  onLoad(q) { this.id = q.id },
  onShow() {
    if (!adminSession.ensure()) return
    api.ensureMeta().catch(() => {}).then(() => this.load())
  },
  onReason(e) { this.setData({ reason: e.detail.value }) },
  preview(e) {
    const urls = (this.data.item.evidence || []).map((item) => item.url || item.thumbUrl).filter(Boolean)
    const current = e.currentTarget.dataset.src
    if (!current || !urls.length) return
    wx.previewImage({ current, urls })
  },
  load() {
    api.adminReport(this.id).then((item) => {
      const files = (item.evidence || []).filter((file) => file && (file.url || file.thumbUrl))
      this.setData({
        item: {
          id: item.id,
          goodsId: item.goodsId,
          status: item.status,
          statusLabel: labelOf('reportStatus', item.status),
          description: item.description || '',
          handleReason: item.reason || '',
          evidence: files,
          pending: item.status === 'PENDING'
        }
      })
    }).catch(showError)
  },
  handle(e) {
    const action = e.currentTarget.dataset.action
    const reason = (this.data.reason || '').trim()
    if (chars(reason) < 2 || chars(reason) > 100) {
      wx.showToast({ title: '理由 2–100 字', icon: 'none' })
      return
    }
    wx.showModal({
      title: action === 'REMOVE_GOODS' ? '下架商品？' : '驳回举报？',
      content: action === 'REMOVE_GOODS' ? '只下架商品，不取消未结束订单。理由会发给举报人。' : '商品状态不变。理由会发给举报人。',
      success: (r) => {
        if (!r.confirm) return
        api.handleReport(this.id, { action, reason }).then(() => {
          wx.showToast({ title: '已处理', icon: 'success' })
          this.setData({ reason: '' })
          this.load()
        }).catch(showError)
      }
    })
  }
})
