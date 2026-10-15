const api = require('../../utils/api')
const session = require('../../utils/session')
const socket = require('../../utils/socket')
const { showError } = require('../../utils/request')
const { unwrapList, clientId, imageUrl } = require('../../utils/format')

function mapMsg(m, myId) {
  return {
    id: m.id,
    type: m.type,
    content: m.content || m.text || '',
    imageUrl: m.type === 'IMAGE' ? imageUrl(m.image || m.file) || m.url || '' : '',
    mine: m.senderId === myId || m.mine === true,
    masked: !!m.masked
  }
}

Page({
  data: { messages: [], text: '', canSend: false, hasMore: false, anchor: 'end' },
  onLoad(q) {
    this.cid = q.id
    this.off = socket.onEvent((msg) => {
      if (!msg || msg.type !== 'MESSAGE') return
      const payload = msg.data || msg.message || msg
      if (String(payload.conversationId || '') !== String(this.cid) && payload.conversationId) return
      if (!payload.id) return
      if (this.data.messages.some((m) => m.id === payload.id)) return
      const mineId = (session.user() || {}).id
      const messages = this.data.messages.concat(mapMsg(payload, mineId))
      this.setData({ messages, anchor: 'end' })
    })
  },
  onUnload() {
    if (this.off) this.off()
  },
  onShow() {
    const user = session.user() || {}
    this.myId = user.id
    this.setData({ canSend: user.verifyStatus === 'VERIFIED' })
    this.load()
  },
  load() {
    api
      .messages(this.cid, { size: 30 })
      .then((data) => {
        const box = unwrapList(data)
        const messages = box.list.map((m) => mapMsg(m, this.myId))
        this.setData({ messages, hasMore: box.hasMore || messages.length >= 30, anchor: 'end' })
        const last = messages[messages.length - 1]
        if (last) api.readConversation(this.cid, last.id).catch(() => {})
      })
      .catch(showError)
  },
  older() {
    const first = this.data.messages[0]
    if (!first) return
    api
      .messages(this.cid, { beforeId: first.id, size: 30 })
      .then((data) => {
        const rows = unwrapList(data).list.map((m) => mapMsg(m, this.myId))
        this.setData({ messages: rows.concat(this.data.messages), hasMore: rows.length >= 30, anchor: 'm' + first.id })
      })
      .catch(showError)
  },
  onText(e) { this.setData({ text: e.detail.value }) },
  send() {
    const content = (this.data.text || '').trim()
    if (!content) return
    if (!this.data.canSend) return showError({ code: 'NOT_VERIFIED', message: '需要校园认证' })
    api
      .sendMessage(this.cid, { type: 'TEXT', content, clientMsgId: clientId('m') })
      .then((m) => {
        const messages = this.data.messages.concat(mapMsg(m, this.myId))
        this.setData({ messages, text: '', anchor: 'end' })
      })
      .catch(showError)
  }
})
