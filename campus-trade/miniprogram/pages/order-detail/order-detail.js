const api = require('../../utils/api')
const { showError, isBusinessError } = require('../../utils/request')
const { fenToYuan, labelOf, formatTime, chars, hasContact, clientId } = require('../../utils/format')

function canMap(actions) {
  const can = {}
  ;(actions || []).forEach((name) => {
    can[name] = true
  })
  return can
}

function orderStatusClass(status) {
  if (status === 'COMPLETED') return 'pill-done'
  if (status === 'CANCELLED') return 'pill-muted'
  if (status === 'PAID') return 'pill-soft'
  return 'pill-cream'
}

Page({
  data: { order: {}, can: {}, error: '' },
  onLoad(q) {
    this.id = q.id
    this._seq = 0
  },
  onShow() { this.load() },
  load() {
    this._seq += 1
    const seq = this._seq
    this.setData({ error: '' })
    return Promise.all([api.ensureMeta(), api.order(this.id)]).then(([, o]) => {
      if (seq !== this._seq) return
      if (o.status !== 'PENDING_PAY') {
        this._payKey = ''
        this._paymentNo = ''
      }
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
          statusClass: orderStatusClass(o.status),
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
    }).catch((err) => {
      if (seq !== this._seq) return
      this.setData({ error: (err && err.message) || '加载失败' })
      showError(err)
    })
  },
  runAction(title, task) {
    if (this._busy) return
    this._busy = true
    wx.showLoading({ title, mask: true })
    task()
      .then((toast) => {
        wx.hideLoading()
        if (toast) wx.showToast({ title: toast, icon: 'success' })
        this.load()
      })
      .catch((err) => {
        wx.hideLoading()
        showError(err)
      })
      .finally(() => {
        this._busy = false
      })
  },
  pay() {
    this.runAction('付款中', () => {
      let created
      if (this._paymentNo) {
        created = Promise.resolve(this._paymentNo)
      } else {
        if (!this._payKey) this._payKey = clientId('pay')
        created = api.createPayment(this.id, this._payKey).then(
          (pay) => {
            this._payKey = ''
            this._paymentNo = pay.paymentNo
            return pay.paymentNo
          },
          (err) => {
            if (isBusinessError(err)) this._payKey = ''
            throw err
          }
        )
      }
      return created.then((paymentNo) =>
        api.mockPay(paymentNo).then(
          () => {
            this._paymentNo = ''
            return '模拟付款已提交'
          },
          (err) => {
            if (isBusinessError(err)) this._paymentNo = ''
            throw err
          }
        )
      )
    })
  },
  cancel() {
    if (this._busy) return
    wx.showModal({
      title: '取消订单',
      content: '只有待支付可以取消。取消后商品回到在售。',
      success: (r) => {
        if (!r.confirm) return
        this.runAction('取消中', () => api.cancelOrder(this.id))
      }
    })
  },
  openChat() {
    wx.navigateTo({ url: '/pages/chat-room/chat-room?orderId=' + this.id })
  },
  handover() {
    this.runAction('提交中', () => api.handover(this.id))
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
