const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError, isBusinessError } = require('../../utils/request')
const { presentGoods, fenToYuan, chars, clientId } = require('../../utils/format')

function buyBlockReason(item) {
  const viewer = item.viewer || {}
  if (viewer.canBuy || viewer.isMine) return ''
  if (item.status === 'SOLD') return '已售出，不能再下单。'
  if (item.status === 'RESERVED' || item.status === 'MEETUP' || item.status === 'DISPUTED') {
    return '交易进行中，暂时不能下单。'
  }
  if (item.status === 'OFF_SHELF' || item.status === 'REMOVED') return '商品已下架，不能下单。'
  if (item.status === 'ON_SALE') return '校园认证通过后才能下单。'
  return '当前不能下单。'
}

Page({
  data: { item: {}, quote: null, blockReason: '', error: '' },
  onLoad(q) {
    this.id = q.id
    this._seq = 0
  },
  onShow() {
    this.load()
  },
  load() {
    this._seq += 1
    const seq = this._seq
    this.setData({ error: '' })
    Promise.all([api.ensureMeta(), api.goodsDetail(this.id)])
      .then(([, g]) => {
        if (seq !== this._seq) return
        const item = presentGoods(g)
        if (!item.viewer) item.viewer = {}
        if (!item.seller) item.seller = {}
        this.setData({ item, blockReason: buyBlockReason(item) })
      })
      .catch((err) => {
        if (seq !== this._seq) return
        this.setData({ error: (err && err.message) || '加载失败' })
        showError(err)
      })
  },
  preview(e) {
    wx.previewImage({ current: e.currentTarget.dataset.src, urls: this.data.item.imageUrls })
  },
  loadQuote() {
    api
      .quote(this.id)
      .then((q) => {
        const available = !!(q && q.available && q.suggestedPrice != null)
        this.setData({
          quote: {
            available,
            priceText: available ? fenToYuan(q.suggestedPrice) : '',
            hint: (q && (q.hint || q.disclaimer)) || '暂无建议价',
            disclaimer: (q && q.disclaimer) || '仅供参考'
          }
        })
      })
      .catch(() => {
        this.setData({ quote: { available: false, priceText: '', hint: '暂无建议价', disclaimer: '仅供参考' } })
      })
  },
  buy() {
    if (this._buying) return
    this._buying = true
    wx.showModal({
      title: '确认下单？',
      content: '将按当前标价生成待支付订单，15 分钟内完成模拟付款。建议价不能作为成交价。',
      success: (r) => {
        if (!r.confirm) {
          this._buying = false
          return
        }
        if (!this._orderKey) this._orderKey = clientId('ord')
        wx.showLoading({ title: '下单中', mask: true })
        api
          .createOrder(this.data.item.id, this._orderKey)
          .then((order) => {
            this._orderKey = ''
            wx.hideLoading()
            const id = order.id || order.orderId
            wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + id })
          })
          .catch((err) => {
            if (isBusinessError(err)) this._orderKey = ''
            wx.hideLoading()
            if (err && err.data && err.data.myOrderId) {
              wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + err.data.myOrderId })
              return
            }
            showError(err)
          })
          .finally(() => {
            this._buying = false
          })
      },
      fail: () => {
        this._buying = false
      }
    })
  },
  openOrder() {
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + this.data.item.viewer.activeOrderId })
  },
  edit() {
    session.setEditGoods(this.data.item.id)
    wx.switchTab({ url: '/pages/publish/publish' })
  },
  openSeller() {
    const id = this.data.item.seller && this.data.item.seller.id
    if (id) wx.navigateTo({ url: '/pages/user/user?id=' + id })
  },
  report() {
    wx.showModal({
      title: '举报商品',
      editable: true,
      placeholderText: '说明 1–300 字',
      success: (m) => {
        if (!m.confirm) return
        const description = (m.content || '').trim()
        if (chars(description) < 1 || chars(description) > 300) {
          wx.showToast({ title: '说明 1–300 字', icon: 'none' })
          return
        }
        wx.showModal({
          title: '上传凭证',
          content: '可以上传 0–3 张图片，每张不超过 5MB。',
          confirmText: '选择图片',
          cancelText: '直接提交',
          success: (ask) => {
            const send = (ids) => {
              api
                .report({
                  goodsId: Number(this.data.item.id),
                  description,
                  evidenceFileIds: ids || []
                })
                .then(() => wx.showToast({ title: '已提交', icon: 'success' }))
                .catch(showError)
            }
            if (!ask.confirm) {
              send([])
              return
            }
            api.pickFiles('EVIDENCE', 3).then(send).catch(showError)
          }
        })
      }
    })
  }
})
