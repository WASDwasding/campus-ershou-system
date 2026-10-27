const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const socket = require('../../utils/socket')

const USERNAME = /^[\u4e00-\u9fa5a-zA-Z0-9_]{1,16}$/
const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function chars(text) {
  return Array.from(text || '').length
}

Page({
  data: {
    mode: 'login',
    healthText: '正在连接服务器…',
    studentNo: '',
    username: '',
    email: '',
    password: '',
    confirm: '',
    code: '',
    emailMasked: '',
    debugCode: '',
    cooldown: 0
  },
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
  onUnload() {
    this.clearCooldown()
  },
  onStudentNo(e) { this.setData({ studentNo: e.detail.value }) },
  onUsername(e) { this.setData({ username: e.detail.value }) },
  onEmail(e) { this.setData({ email: e.detail.value }) },
  onPassword(e) { this.setData({ password: e.detail.value }) },
  onConfirm(e) { this.setData({ confirm: e.detail.value }) },
  onCode(e) { this.setData({ code: e.detail.value }) },
  showLogin() { this.setData({ mode: 'login' }) },
  showRegister() { this.setData({ mode: 'register' }) },
  login() {
    const studentNo = (this.data.studentNo || '').trim()
    const password = this.data.password || ''
    if (!/^\d{8,12}$/.test(studentNo)) return wx.showToast({ title: '学号为 8–12 位数字', icon: 'none' })
    if (!password || password.length > 64) return wx.showToast({ title: '请输入密码', icon: 'none' })
    if (this._busy) return
    this._busy = true
    wx.showLoading({ title: '登录中' })
    api
      .login({ studentNo, password })
      .then((data) => this.enter(data))
      .catch((err) => {
        wx.hideLoading()
        showError(err)
      })
      .finally(() => {
        this._busy = false
      })
  },
  sendCode() {
    const studentNo = (this.data.studentNo || '').trim()
    const username = (this.data.username || '').trim()
    const email = (this.data.email || '').trim().toLowerCase()
    if (!/^\d{8,12}$/.test(studentNo)) return wx.showToast({ title: '学号为 8–12 位数字', icon: 'none' })
    if (!USERNAME.test(username) || chars(username) > 16) {
      return wx.showToast({ title: '用户名格式不正确', icon: 'none' })
    }
    if (!EMAIL.test(email) || email.length > 128) return wx.showToast({ title: '邮箱格式不正确', icon: 'none' })
    if (this.data.cooldown > 0) return wx.showToast({ title: '请稍后再发送', icon: 'none' })
    if (this._busy) return
    this._busy = true
    wx.showLoading({ title: '发送中' })
    api
      .register({ studentNo, username, email })
      .then((data) => {
        wx.hideLoading()
        this.setData({
          mode: 'code',
          email,
          emailMasked: (data && data.emailMasked) || email,
          debugCode: (data && data.debugCode) || '',
          code: (data && data.debugCode) || this.data.code
        })
        this.startCooldown(60)
      })
      .catch((err) => {
        wx.hideLoading()
        showError(err)
      })
      .finally(() => {
        this._busy = false
      })
  },
  verifyCode() {
    const email = (this.data.email || '').trim().toLowerCase()
    const code = (this.data.code || '').trim()
    if (!/^\d{6}$/.test(code)) return wx.showToast({ title: '验证码为 6 位数字', icon: 'none' })
    if (this._busy) return
    this._busy = true
    wx.showLoading({ title: '验证中' })
    api
      .verifyRegisterCode({ email, code })
      .then((data) => {
        wx.hideLoading()
        this.setupToken = data && data.setupToken
        if (!this.setupToken) {
          wx.showToast({ title: '没有拿到设置密码凭证', icon: 'none' })
          return
        }
        this.setData({ mode: 'password', password: '', confirm: '' })
      })
      .catch((err) => {
        wx.hideLoading()
        showError(err)
      })
      .finally(() => {
        this._busy = false
      })
  },
  finishRegister() {
    const password = this.data.password || ''
    const confirm = this.data.confirm || ''
    if (password.length < 8 || password.length > 64) return wx.showToast({ title: '密码 8–64 位', icon: 'none' })
    if (password !== confirm) return wx.showToast({ title: '两次密码不一致', icon: 'none' })
    if (!this.setupToken) return wx.showToast({ title: '请先完成验证码验证', icon: 'none' })
    if (this._busy) return
    this._busy = true
    wx.showLoading({ title: '注册中' })
    api
      .setPassword({ setupToken: this.setupToken, password })
      .then((data) => this.enter(data))
      .catch((err) => {
        wx.hideLoading()
        showError(err)
      })
      .finally(() => {
        this._busy = false
      })
  },
  enter(data) {
    session.setSession(data.token, data.user)
    const app = getApp()
    if (app && app.globalData) app.globalData.user = data.user
    socket.connect()
    api.ensureMeta().catch(() => {})
    wx.hideLoading()
    if (!data.user || !data.user.nickname) {
      wx.redirectTo({ url: '/pages/settings/settings?from=login' })
      return
    }
    wx.switchTab({ url: '/pages/index/index' })
  },
  startCooldown(seconds) {
    this.clearCooldown()
    const until = Date.now() + seconds * 1000
    const tick = () => {
      const left = Math.max(0, Math.ceil((until - Date.now()) / 1000))
      this.setData({ cooldown: left })
      if (left <= 0) this.clearCooldown()
    }
    tick()
    this._timer = setInterval(tick, 1000)
  },
  clearCooldown() {
    if (this._timer) {
      clearInterval(this._timer)
      this._timer = null
    }
  }
})
