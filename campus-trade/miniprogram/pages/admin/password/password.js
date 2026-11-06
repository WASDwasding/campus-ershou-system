const api = require('../../../utils/api')
const adminSession = require('../../../utils/admin-session')
const { showError } = require('../../../utils/request')

Page({
  data: { oldPassword: '', newPassword: '', forced: false },
  onShow() {
    if (!adminSession.ensure()) return
    const admin = adminSession.profile() || {}
    this.setData({ forced: !!admin.mustChangePassword })
  },
  onOld(e) { this.setData({ oldPassword: e.detail.value }) },
  onNew(e) { this.setData({ newPassword: e.detail.value }) },
  submit() {
    const oldPassword = this.data.oldPassword || ''
    const newPassword = this.data.newPassword || ''
    if (!oldPassword) return wx.showToast({ title: '请输入原密码', icon: 'none' })
    if (newPassword.length < 12 || newPassword.length > 64) {
      return wx.showToast({ title: '新密码 12–64 位', icon: 'none' })
    }
    api.adminPassword({ oldPassword, newPassword }).then(() => {
      adminSession.clear()
      wx.showToast({ title: '已修改，请重新登录', icon: 'none' })
      setTimeout(() => wx.reLaunch({ url: '/pages/admin/login/login' }), 400)
    }).catch(showError)
  }
})
