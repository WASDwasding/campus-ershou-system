const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { presentGoods } = require('../../utils/format')

Page({
  data: { item: {} },
  onLoad(q) {
    this.id = q.id
  },
  onShow() {
    this.load()
  },
  load() {
    api
      .ensureMeta()
      .catch(() => {})
      .then(() => api.goodsDetail(this.id))
      .then((g) => {
        const item = presentGoods(g)
        if (!item.viewer) item.viewer = {}
        if (!item.seller) item.seller = {}
        this.setData({ item })
      })
      .catch(showError)
  },
  preview(e) {
    wx.previewImage({ current: e.currentTarget.dataset.src, urls: this.data.item.imageUrls })
  },
  fav() {
    const item = this.data.item
    const req = item.viewer && item.viewer.favorited ? api.unfavorite(item.id) : api.favorite(item.id)
    req.then(() => this.load()).catch(showError)
  },
  chat() {
    api
      .openConversation(this.data.item.id)
      .then((c) => wx.navigateTo({ url: '/pages/chat-room/chat-room?id=' + c.id }))
      .catch(showError)
  },
  buy() {
    wx.showModal({
      title: '确认下单？',
      content: '将按当前标价生成待支付订单，15 分钟内完成模拟付款。',
      success: (r) => {
        if (!r.confirm) return
        api
          .createOrder(this.data.item.id)
          .then((order) => {
            const id = order.id || order.orderId
            wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + id })
          })
          .catch((err) => {
            if (err && err.data && err.data.myOrderId) {
              wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + err.data.myOrderId })
              return
            }
            showError(err)
          })
      }
    })
  },
  openOrder() {
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + this.data.item.viewer.activeOrderId })
  },
  edit() {
    getApp().globalData.editGoodsId = this.data.item.id
    wx.switchTab({ url: '/pages/publish/publish' })
  },
  openSeller() {
    const id = this.data.item.seller && this.data.item.seller.id
    if (id) wx.navigateTo({ url: '/pages/user/user?id=' + id })
  },
  report() {
    api
      .ensureMeta()
      .then((meta) => {
        const reasons = ((meta && meta.enums && meta.enums.reportReason) || []).filter((x) => x && x.value)
        if (!reasons.length) {
          wx.showToast({ title: '举报原因还没下发', icon: 'none' })
          return
        }
        wx.showActionSheet({
          itemList: reasons.map((x) => x.label),
          success: (r) => {
            const reason = reasons[r.tapIndex].value
            wx.showModal({
              title: '举报说明',
              editable: true,
              placeholderText: '选填，300 字以内',
              success: (m) => {
                if (!m.confirm) return
                api
                  .report({
                    targetType: 'GOODS',
                    targetId: Number(this.data.item.id),
                    reason,
                    description: (m.content || '').trim(),
                    evidenceFileIds: []
                  })
                  .then(() => wx.showToast({ title: '已提交', icon: 'success' }))
                  .catch(showError)
              }
            })
          }
        })
      })
      .catch(showError)
  }
})
