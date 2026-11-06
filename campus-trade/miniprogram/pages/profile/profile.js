const api = require('../../utils/api')
const session = require('../../utils/session')
const socket = require('../../utils/socket')
const { showError } = require('../../utils/request')
const { labelOf } = require('../../utils/format')

Page({
  data: { user: null, stats: {} },
  onShow() {
    if (!session.token()) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    api.ensureMeta().catch(() => {})
    api.me().then((user) => {
      session.setSession(session.token(), user)
      const dorm = user.dormBuilding || {}
      this.setData({
        user: Object.assign({}, user, {
          nickname0: (user.nickname || '同').slice(0, 1),
          verifyLabel: labelOf('verifyStatus', user.verifyStatus) === '未知' ? user.verifyStatus : labelOf('verifyStatus', user.verifyStatus),
          dormName: dorm.name || ''
        })
      })
    }).catch(showError)
    api.stats().then((stats) => {
      const s = stats || {}
      this.setData({
        stats: {
          onSale: s.onSaleCount || 0,
          sold: s.soldCount || 0,
          pending: s.pendingOrderCount || 0,
          selling: s.sellingOrderCount || 0
        }
      })
    }).catch(() => {})
  },
  go(e) { wx.navigateTo({ url: e.currentTarget.dataset.url }) },
  bindWx() {
    wx.login({
      success: (res) => {
        if (!res.code) return wx.showToast({ title: '没有拿到微信登录凭证', icon: 'none' })
        api.wxBind({ code: res.code }).then(() => {
          wx.showToast({ title: '已绑定微信', icon: 'success' })
          this.onShow()
        }).catch(showError)
      },
      fail: () => wx.showToast({ title: '微信登录失败', icon: 'none' })
    })
  },
  logout() {
    api.logout().catch(() => {}).then(() => {
      socket.close()
      session.clear()
      wx.reLaunch({ url: '/pages/login/login' })
    })
  }
})
