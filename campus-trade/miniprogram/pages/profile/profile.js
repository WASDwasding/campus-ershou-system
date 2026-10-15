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
          onSale: s.onSaleCount != null ? s.onSaleCount : s.onSale || 0,
          sold: s.soldCount != null ? s.soldCount : s.sold || 0,
          bought: s.boughtCount != null ? s.boughtCount : s.bought || 0,
          rating: s.rating != null ? s.rating : '—'
        }
      })
    }).catch(() => {})
  },
  go(e) { wx.navigateTo({ url: e.currentTarget.dataset.url }) },
  logout() {
    api.logout().catch(() => {}).then(() => {
      socket.close()
      session.clear()
      wx.reLaunch({ url: '/pages/login/login' })
    })
  }
})
