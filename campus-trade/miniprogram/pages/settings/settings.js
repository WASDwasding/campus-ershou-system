const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { chars, hasContact } = require('../../utils/format')

Page({
  data: { nickname: '', bio: '', dorms: [], dormNames: [], dormIndex: 0, avatarFileId: '' },
  onLoad(q) { this.from = q.from || '' },
  onShow() {
    api.buildings({ type: 'DORM' }).then((data) => {
      const dorms = Array.isArray(data) ? data : (data && data.list) || []
      const user = session.user() || {}
      const dormIndex = Math.max(0, dorms.findIndex((d) => d.id === (user.dormBuilding && user.dormBuilding.id)))
      this.setData({
        dorms,
        dormNames: dorms.map((d) => d.name),
        dormIndex,
        nickname: user.nickname || '',
        bio: user.bio || ''
      })
    }).catch(showError)
  },
  onNick(e) { this.setData({ nickname: e.detail.value }) },
  onBio(e) { this.setData({ bio: e.detail.value }) },
  onDorm(e) { this.setData({ dormIndex: Number(e.detail.value) }) },
  avatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      success: (res) => {
        const f = res.tempFiles && res.tempFiles[0]
        if (!f) return
        if (f.size > 5242880) return wx.showToast({ title: '超过 5MB', icon: 'none' })
        api.upload(f.tempFilePath, 'AVATAR').then((data) => {
          this.setData({ avatarFileId: data.fileId })
          wx.showToast({ title: '头像已上传', icon: 'success' })
        }).catch(showError)
      }
    })
  },
  save() {
    const nickname = (this.data.nickname || '').trim()
    const bio = (this.data.bio || '').trim()
    if (chars(nickname) < 1 || chars(nickname) > 16) return wx.showToast({ title: '昵称 1–16 字', icon: 'none' })
    if (chars(bio) > 60) return wx.showToast({ title: '简介最多 60 字', icon: 'none' })
    if (hasContact(nickname + bio)) return wx.showToast({ title: '不能填写联系方式', icon: 'none' })
    const dorm = this.data.dorms[this.data.dormIndex]
    const body = { nickname, bio }
    if (dorm) body.dormBuildingId = dorm.id
    if (this.data.avatarFileId) body.avatarFileId = this.data.avatarFileId
    api.updateProfile(body).then((user) => {
      session.setSession(session.token(), user || Object.assign({}, session.user(), body))
      wx.showToast({ title: '已保存', icon: 'success' })
      if (this.from === 'login') wx.switchTab({ url: '/pages/index/index' })
      else wx.navigateBack()
    }).catch(showError)
  }
})
