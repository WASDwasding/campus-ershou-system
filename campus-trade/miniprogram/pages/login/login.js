const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError, wxLoginCode } = require('../../utils/request')
const socket = require('../../utils/socket')

Page({
  data: { healthText: '正在连接服务器…' },
  onLoad() {
    if (session.token()) {
      wx.switchTab({ url: '/pages/index/index' })
      return
    }
    api
      .health()
      .then((h) => {
        this.setData({ healthText: h && h.status === 'UP' ? '服务器正常' : '服务器状态未知' })
      })
      .catch(() => this.setData({ healthText: '暂时连不上服务器' }))
  },
  login() {
    wx.showLoading({ title: '登录中' })
    wxLoginCode()
      .then((code) => api.wxLogin(code))
      .then((data) => {
        session.setSession(data.token, data.user)
        const app = getApp()
        app.globalData.user = data.user
        socket.connect()
        api.ensureMeta().catch(() => {})
        wx.hideLoading()
        if (!data.user || !data.user.nickname) {
          wx.redirectTo({ url: '/pages/settings/settings?from=login' })
          return
        }
        wx.switchTab({ url: '/pages/index/index' })
      })
      .catch((err) => {
        wx.hideLoading()
        showError(err)
      })
  }
})
