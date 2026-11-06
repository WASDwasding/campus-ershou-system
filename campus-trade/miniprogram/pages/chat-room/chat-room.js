const api = require('../../utils/api')
const session = require('../../utils/session')
const socket = require('../../utils/socket')
const { showError } = require('../../utils/request')
const { unwrapList, clientId } = require('../../utils/format')

function mapMsg(message, myId) {
  return {
    id: message.id,
    content: message.content || '',
    mine: message.senderId === myId
  }
}

Page({
  data: { messages: [], text: '', canSend: false, hint: '', hasMore: false, anchor: 'end' },
  onLoad(q) {
    this.orderId = q.orderId
    this.off = socket.onEvent((msg) => {
      if (!msg || msg.type !== 'MESSAGE') return
      if (msg.orderId && String(msg.orderId) !== String(this.orderId)) return
      const payload = msg.message || msg
      if (!payload.id) return
      if (this.data.messages.some((item) => item.id === payload.id)) return
      const messages = this.data.messages.concat(mapMsg(payload, this.myId))
      this.setData({ messages, anchor: 'end' })
    })
  },
  onUnload() {
    if (this.off) this.off()
  },
  onShow() {
    this.myId = (session.user() || {}).id
    this.load()
  },
  load() {
    api.orderMessages(this.orderId, { size: 30 }).then((data) => {
      const box = unwrapList(data)
      const messages = box.list.map((item) => mapMsg(item, this.myId))
      const canSend = !!(data && data.canSend)
      let hint = ''
      if (!canSend && !messages.length) hint = '支付完成并进入待面交后才能聊天'
      else if (!canSend) hint = '订单已结束，只能查看记录'
      this.setData({
        messages,
        canSend,
        hint,
        hasMore: !!(data && data.hasMore),
        anchor: 'end'
      })
    }).catch(showError)
  },
  older() {
    const first = this.data.messages[0]
    if (!first) return
    api.orderMessages(this.orderId, { beforeId: first.id, size: 30 }).then((data) => {
      const rows = unwrapList(data).list.map((item) => mapMsg(item, this.myId))
      this.setData({
        messages: rows.concat(this.data.messages),
        hasMore: !!(data && data.hasMore),
        anchor: 'm' + first.id
      })
    }).catch(showError)
  },
  onText(e) { this.setData({ text: e.detail.value }) },
  send() {
    const content = (this.data.text || '').trim()
    if (!content || !this.data.canSend) return
    api.sendOrderMessage(this.orderId, { content, clientMsgId: clientId('m') }).then((message) => {
      const messages = this.data.messages.concat(mapMsg(message, this.myId))
      this.setData({ messages, text: '', anchor: 'end' })
    }).catch(showError)
  }
})
