const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { unwrapList, formatTime } = require('../../utils/format')

function peerName(c) {
  const peer = c.peer || c.seller || c.buyer || {}
  const name = peer.nickname || c.goodsTitle || '同学'
  return name.slice(0, 1)
}

Page({
  data: { list: [] },
  onShow() {
    if (!session.token()) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    api
      .conversations({ page: 1, size: 50 })
      .then((data) => {
        const list = unwrapList(data).list.map((c) => ({
          id: c.id,
          peerName: peerName(c),
          title: (c.goods && c.goods.title) || c.goodsTitle || '商品咨询',
          preview: (c.lastMessage && (c.lastMessage.content || c.lastMessage.text)) || '',
          timeText: formatTime(c.updatedAt || c.lastMessageAt || ''),
          unreadCount: c.unreadCount || 0
        }))
        this.setData({ list })
      })
      .catch(showError)
  },
  open(e) {
    wx.navigateTo({ url: '/pages/chat-room/chat-room?id=' + e.currentTarget.dataset.id })
  }
})
