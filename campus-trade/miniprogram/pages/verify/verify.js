const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { labelOf } = require('../../utils/format')

Page({
  data: {
    status: 'UNVERIFIED',
    statusLabel: '',
    rejectReason: '',
    dorms: [],
    dormNames: [],
    dormIndex: 0,
    fileId: ''
  },
  onShow() {
    api.ensureMeta().catch(() => {})
    api.verification().then((v) => {
      const status = (v && v.status) || 'UNVERIFIED'
      this.setData({
        status,
        statusLabel: labelOf('verifyStatus', status),
        rejectReason: (v && v.rejectReason) || ''
      })
    }).catch(showError)
    api.buildings({ type: 'DORM' }).then((data) => {
      const dorms = Array.isArray(data) ? data : (data && data.list) || []
      this.setData({ dorms, dormNames: dorms.map((d) => d.name) })
    }).catch(() => {})
  },
  onDorm(e) { this.setData({ dormIndex: Number(e.detail.value) }) },
  card() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      success: (res) => {
        const f = res.tempFiles && res.tempFiles[0]
        if (!f) return
        if (f.size > 5242880) return wx.showToast({ title: '超过 5MB', icon: 'none' })
        api.upload(f.tempFilePath, 'VERIFICATION').then((data) => {
          this.setData({ fileId: data.fileId })
        }).catch(showError)
      }
    })
  },
  submit() {
    const dorm = this.data.dorms[this.data.dormIndex]
    if (!dorm) return wx.showToast({ title: '请选择宿舍楼', icon: 'none' })
    if (!this.data.fileId) return wx.showToast({ title: '请上传证件照', icon: 'none' })
    api.submitVerification({
      dormBuildingId: dorm.id,
      cardImageFileId: this.data.fileId
    }).then(() => {
      const user = session.user() || {}
      user.verifyStatus = 'PENDING'
      session.setSession(session.token(), user)
      wx.showToast({ title: '已提交', icon: 'success' })
      this.onShow()
    }).catch(showError)
  }
})
