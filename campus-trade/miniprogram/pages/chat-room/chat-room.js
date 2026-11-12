const api = require('../../utils/api')
const session = require('../../utils/session')
const socket = require('../../utils/socket')
const { showError } = require('../../utils/request')
const { unwrapList, clientId } = require('../../utils/format')

function firstChar(name, fallback) {
  const arr = Array.from(String(name || '').trim())
  return arr[0] || fallback
}

function mapMsg(message, myId) {
  return {
    id: message.id,
    content: message.content || '',
    mine: message.senderId === myId
  }
}

Page({
  data: {
    messages: [],
    text: '',
    canSend: false,
    hint: '',
    hasMore: false,
    anchor: '',
    kbHeight: 0,
    myAvatar: '',
    myInitial: '我',
    peerAvatar: '',
    peerInitial: '同'
  },
  onLoad(q) {
    this.orderId = q.orderId
    this._kbListener = (res) => {
      this.applyKeyboard(res && res.height)
    }
    if (wx.onKeyboardHeightChange) {
      wx.onKeyboardHeightChange(this._kbListener)
    }
    this.off = socket.onEvent((msg) => {
      if (!msg || msg.type !== 'MESSAGE') return
      if (msg.orderId && String(msg.orderId) !== String(this.orderId)) return
      const payload = msg.message || msg
      if (!payload.id) return
      if (this.data.messages.some((item) => item.id === payload.id)) return
      const messages = this.data.messages.concat(mapMsg(payload, this.myId))
      this.setData({ messages }, () => this.scrollToEnd())
    })
  },
  onUnload() {
    if (this.off) this.off()
    if (wx.offKeyboardHeightChange && this._kbListener) {
      wx.offKeyboardHeightChange(this._kbListener)
    }
  },
  onHide() {
    if (this.data.kbHeight) this.setData({ kbHeight: 0 })
  },
  onShow() {
    const me = session.user() || {}
    this.myId = me.id
    this.setData({
      myAvatar: me.avatarUrl || this.data.myAvatar || '',
      myInitial: firstChar(me.nickname, '我')
    })
    this.loadParticipants()
    this.load()
  },
  noop() {},
  onMyAvatarErr() {
    this.setData({ myAvatar: '' })
  },
  onPeerAvatarErr() {
    this.setData({ peerAvatar: '' })
  },
  onKeyboard(e) {
    this.applyKeyboard(e && e.detail && e.detail.height)
  },
  applyKeyboard(rawHeight) {
    const h = Math.max(0, Math.round(Number(rawHeight) || 0))
    if (h === this.data.kbHeight) return
    this.setData({ kbHeight: h }, () => {
      if (h > 0) this.scrollToEnd()
    })
  },
  dismissKeyboard() {
    if (this.data.kbHeight > 0) wx.hideKeyboard()
  },
  scrollToEnd() {
    if (!this.data.anchor) {
      this.setData({ anchor: 'end' })
      return
    }
    this.setData({ anchor: '' }, () => {
      this.setData({ anchor: 'end' })
    })
  },
  loadParticipants() {
    if (!this.orderId) return
    api
      .order(this.orderId)
      .then((o) => {
        if (!o) return
        const buyer = o.buyer || {}
        const seller = o.seller || {}
        const isBuyer = o.myRole === 'BUYER' || (this.myId != null && buyer.id === this.myId)
        const meInfo = isBuyer ? buyer : seller
        const peerInfo = isBuyer ? seller : buyer
        const next = {
          peerAvatar: peerInfo.avatarUrl || '',
          peerInitial: firstChar(peerInfo.nickname, '同')
        }
        if (meInfo.avatarUrl) next.myAvatar = meInfo.avatarUrl
        if (meInfo.nickname) next.myInitial = firstChar(meInfo.nickname, '我')
        this.setData(next)
      })
      .catch(() => {})
  },
  load() {
    api
      .orderMessages(this.orderId, { size: 30 })
      .then((data) => {
        const box = unwrapList(data)
        const messages = box.list.map((item) => mapMsg(item, this.myId))
        const canSend = !!(data && data.canSend)
        let hint = ''
        if (!canSend && !messages.length) hint = '支付完成并进入待面交后才能聊天'
        else if (!canSend) hint = '订单已结束，只能查看记录'
        this.setData(
          {
            messages,
            canSend,
            hint,
            hasMore: !!(data && data.hasMore)
          },
          () => this.scrollToEnd()
        )
      })
      .catch(showError)
  },
  older() {
    const first = this.data.messages[0]
    if (!first) return
    api
      .orderMessages(this.orderId, { beforeId: first.id, size: 30 })
      .then((data) => {
        const rows = unwrapList(data).list.map((item) => mapMsg(item, this.myId))
        this.setData({
          messages: rows.concat(this.data.messages),
          hasMore: !!(data && data.hasMore),
          anchor: 'm' + first.id
        })
      })
      .catch(showError)
  },
  onText(e) {
    this.setData({ text: e.detail.value })
  },
  send() {
    const content = (this.data.text || '').trim()
    if (!content || !this.data.canSend) return
    api
      .sendOrderMessage(this.orderId, { content, clientMsgId: clientId('m') })
      .then((message) => {
        const messages = this.data.messages.concat(mapMsg(message, this.myId))
        this.setData({ messages, text: '' }, () => this.scrollToEnd())
      })
      .catch(showError)
  }
})
