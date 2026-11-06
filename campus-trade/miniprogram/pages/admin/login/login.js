const api = require('../../../utils/api')
const adminSession = require('../../../utils/admin-session')
const { showError } = require('../../../utils/request')

Page({
  data: { username: '', password: '' },
  onShow() {
    if (!adminSession.token()) return
    const admin = adminSession.profile() || {}
    if (admin.mustChangePassword) {
      wx.redirectTo({ url: '/pages/admin/password/password' })
      return
    }
    wx.redirectTo({ url: '/pages/admin/home/home' })
  },
  onUser(e) { this.setData({ username: e.detail.value }) },
  onPassword(e) { this.setData({ password: e.detail.value }) },
  submit() {
    const username = (this.data.username || '').trim()
    const password = this.data.password || ''
    if (!username || !password) return wx.showToast({ title: '请输入用户名和密码', icon: 'none' })
    wx.showLoading({ title: '登录中' })
    api.adminLogin({ username, password }).then((data) => {
      wx.hideLoading()
      const admin = Object.assign({}, data.admin || {}, { mustChangePassword: !!data.mustChangePassword })
      adminSession.setSession(data.token, admin)
      if (admin.mustChangePassword) {
        wx.redirectTo({ url: '/pages/admin/password/password' })
        return
      }
      wx.redirectTo({ url: '/pages/admin/home/home' })
    }).catch((err) => {
      wx.hideLoading()
      showError(err)
    })
  }
})
