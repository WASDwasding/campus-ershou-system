const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { fenToYuan, labelOf, formatTime, chars, hasContact } = require('../../utils/format')

function canMap(actions) {
  const can = {}
  ;(actions || []).forEach((name) => {
    can[name] = true
  })
  return can
}

Page({
  data: { order: {}, can: {} },
  onLoad(q) { this.id = q.id },
  onShow() { this.load() },
  load() {
    api.ensureMeta().catch(() => {}).then(() => api.order(this.id)).then((o) => {
      const payment = o.payment || {}
      const handover = o.handover || {}
      const dispute = o.dispute || {}
      const goods = o.goods || {}
      let handoverText = ''
      if (handover.buyerConfirmed && handover.sellerConfirmed) handoverText = '双方都已确认面交'
      else if (handover.buyerConfirmed || handover.sellerConfirmed) handoverText = '已有一方确认，等待对方确认'
      this.setData({
        order: {
          id: o.id,
          orderNo: o.orderNo,
          status: o.status,
          statusLabel: labelOf('orderStatus', o.status),
          goodsTitle: goods.title || '商品',
          amountText: fenToYuan(o.amount),
          myRole: o.myRole,
          expireAt: o.expireAt,
          expireText: formatTime(o.expireAt),
          paymentLabel: payment.status ? labelOf('paymentStatus', payment.status) : '',
          paymentReason: payment.reason || '',
          handoverText,
          disputeText: dispute.remark || dispute.description || ''
        },
        can: canMap(o.allowedActions)
      })
    }).catch(showError)
  },
  pay() {
    api.createPayment(this.id).then((pay) => api.mockPay(pay.paymentNo)).then(() => {
      wx.showToast({ title: '模拟付款已提交', icon: 'success' })
      this.load()
    }).catch(showError)
  },
  cancel() {
    wx.showModal({
      title: '取消订单',
      content: '只有待支付可以取消。取消后商品回到在售。',
      success: (r) => {
        if (!r.confirm) return
        api.cancelOrder(this.id).then(() => this.load()).catch(showError)
      }
    })
  },
  openChat() {
    wx.navigateTo({ url: '/pages/chat-room/chat-room?orderId=' + this.id })
  },
  handover() {
    api.handover(this.id).then(() => this.load()).catch(showError)
  },
  openDispute() {
    api.ensureMeta().then((meta) => {
      const reasons = (meta.enums && meta.enums.disputeReason) || []
      if (!reasons.length) return wx.showToast({ title: '申诉原因还没下发', icon: 'none' })
      wx.showActionSheet({
        itemList: reasons.map((item) => item.label),
        success: (r) => {
          const reason = reasons[r.tapIndex]
          if (!reason) return
          wx.showModal({
            title: '申诉说明（10–500 字）',
            editable: true,
            placeholderText: '请描述问题，不要填写联系方式',
            success: (m) => {
              if (!m.confirm) return
              const description = (m.content || '').trim()
              if (chars(description) < 10 || chars(description) > 500) {
                wx.showToast({ title: '说明 10–500 字', icon: 'none' })
                return
              }
              if (hasContact(description)) {
                wx.showToast({ title: '说明不能含联系方式', icon: 'none' })
                return
              }
              const needImage = reason.value === 'GOODS_MISMATCH' || reason.value === 'FALSE_DESCRIPTION'
              const submit = (ids) => {
                if (needImage && (!ids || !ids.length)) {
                  wx.showToast({ title: '这类申诉至少上传 1 张图片', icon: 'none' })
                  return
                }
                api.dispute(this.id, {
                  reason: reason.value,
                  description,
                  evidenceFileIds: (ids || []).slice(0, 3)
                }).then(() => this.load()).catch(showError)
              }
              wx.showModal({
                title: needImage ? '请上传图片' : '上传凭证',
                content: needImage ? '货不对版和虚假描述至少 1 张，最多 3 张。' : '最多 3 张，可以不传。',
                confirmText: '选择图片',
                cancelText: needImage ? '取消' : '直接提交',
                success: (ask) => {
                  if (!ask.confirm) {
                    if (!needImage) submit([])
                    return
                  }
                  api.pickFiles('EVIDENCE', 3).then(submit).catch(showError)
                }
              })
            }
          })
        }
      })
    }).catch(showError)
  },
  withdraw() {
    api.withdrawDispute(this.id).then(() => this.load()).catch(showError)
  }
})
