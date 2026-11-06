const api = require('../../../utils/api')
const adminSession = require('../../../utils/admin-session')
const { showError } = require('../../../utils/request')
const { fenToYuan, labelOf, chars } = require('../../../utils/format')

Page({
  data: { order: {}, messages: [], remark: '', chatHint: '' },
  onLoad(q) {
    this.disputeId = q.id
    this.orderId = q.orderId
  },
  onShow() {
    if (!adminSession.ensure()) return
    api.ensureMeta().catch(() => {}).then(() => this.load())
  },
  onRemark(e) { this.setData({ remark: e.detail.value }) },
  load() {
    api.adminOrder(this.orderId).then((o) => {
      const dispute = o.dispute || {}
      const payment = o.payment || {}
      const handover = o.handover || {}
      const goods = o.goods || {}
      const open = dispute.status === 'OPEN'
      let handoverText = '双方都还没确认面交'
      if (handover.buyerConfirmed && handover.sellerConfirmed) handoverText = '双方都已确认面交'
      else if (handover.buyerConfirmed || handover.sellerConfirmed) handoverText = '只有一方确认了面交'
      this.disputeId = dispute.id || this.disputeId
      this.setData({
        order: {
          orderNo: o.orderNo,
          goodsTitle: goods.title || '商品',
          amountText: fenToYuan(o.amount),
          statusLabel: labelOf('orderStatus', o.status),
          paymentLabel: payment.status ? labelOf('paymentStatus', payment.status) : '',
          reasonLabel: labelOf('disputeReason', dispute.reason),
          description: dispute.description || '',
          status: dispute.status,
          statusLabel: labelOf('disputeStatus', dispute.status),
          resultLabel: dispute.result ? labelOf('disputeResult', dispute.result) : '',
          remark: dispute.remark || '',
          handoverText,
          bothConfirmed: !!(handover.buyerConfirmed && handover.sellerConfirmed),
          open,
          buyerId: o.buyer && o.buyer.id,
          sellerId: o.seller && o.seller.id
        },
        chatHint: open ? '处理中可以查看文字记录，不能代发。' : '只有申诉处理中才能查看聊天。结案后不再打开。'
      })
      if (!open) {
        this.setData({ messages: [] })
        return
      }
      api.adminDisputeMessages(this.disputeId).then((data) => {
        const buyerId = this.data.order.buyerId
        const sellerId = this.data.order.sellerId
        const messages = ((data && data.list) || []).map((item) => ({
          id: item.id,
          content: item.content || '',
          who: item.senderId === buyerId ? '买家' : item.senderId === sellerId ? '卖家' : '用户'
        }))
        this.setData({ messages })
      }).catch((err) => {
        if (err && err.code === 'CHAT_NOT_OPEN') {
          this.setData({ messages: [], chatHint: '申诉已结束，不能再查看聊天记录。' })
          return
        }
        showError(err)
      })
    }).catch(showError)
  },
  resolve(e) {
    const result = e.currentTarget.dataset.result
    const remark = (this.data.remark || '').trim()
    if (chars(remark) < 2 || chars(remark) > 300) {
      wx.showToast({ title: '结案说明 2–300 字', icon: 'none' })
      return
    }
    if (result === 'COMPLETE' && !this.data.order.bothConfirmed) {
      wx.showToast({ title: '双方都确认面交后才能继续完成', icon: 'none' })
      return
    }
    const title = result === 'CANCEL' ? '取消订单？' : result === 'COMPLETE' ? '继续完成？' : '驳回申诉？'
    wx.showModal({
      title,
      content: result === 'CANCEL' ? '已支付会被记为模拟退回。疑似欺诈时还会下架并禁止该卖家再发布。' : '结案说明双方都能在通知里看到。',
      success: (r) => {
        if (!r.confirm) return
        api.resolveDispute(this.disputeId, { result, remark }).then(() => {
          wx.showToast({ title: '已结案', icon: 'success' })
          this.setData({ remark: '' })
          this.load()
        }).catch(showError)
      }
    })
  }
})
