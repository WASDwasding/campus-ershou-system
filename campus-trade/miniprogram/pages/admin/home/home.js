const api = require('../../../utils/api')
const adminSession = require('../../../utils/admin-session')
const { showError } = require('../../../utils/request')

Page({
  data: { name: '' },
  onShow() {
    const admin = adminSession.ensure()
    if (!admin) return
    if (admin.mustChangePassword) {
      wx.redirectTo({ url: '/pages/admin/password/password' })
      return
    }
    this.setData({ name: admin.username || '' })
    api.adminMe().then((me) => {
      this.setData({ name: (me && me.username) || this.data.name })
    }).catch(showError)
  },
  go(e) { wx.navigateTo({ url: e.currentTarget.dataset.url }) },
  logout() {
    api.adminLogout().catch(() => {}).then(() => {
      adminSession.clear()
      wx.reLaunch({ url: '/pages/admin/login/login' })
    })
  }
})
