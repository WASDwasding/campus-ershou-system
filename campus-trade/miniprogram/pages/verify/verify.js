const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { labelOf, chars } = require('../../utils/format')

Page({
  data: {
    status: 'UNVERIFIED',
    statusLabel: '',
    rejectReason: '',
    realName: '',
    studentNo: '',
    college: '',
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
  onRealName(e) { this.setData({ realName: e.detail.value }) },
  onStudentNo(e) { this.setData({ studentNo: e.detail.value }) },
  onCollege(e) { this.setData({ college: e.detail.value }) },
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
    const realName = (this.data.realName || '').trim()
    const studentNo = (this.data.studentNo || '').trim()
    const college = (this.data.college || '').trim()
    const dorm = this.data.dorms[this.data.dormIndex]
    if (chars(realName) < 1 || chars(realName) > 20) return wx.showToast({ title: '姓名 1–20 字', icon: 'none' })
    if (!/^\d{8,12}$/.test(studentNo)) return wx.showToast({ title: '学号为 8–12 位数字', icon: 'none' })
    if (chars(college) < 1 || chars(college) > 30) return wx.showToast({ title: '学院 1–30 字', icon: 'none' })
    if (!dorm) return wx.showToast({ title: '请选择宿舍楼', icon: 'none' })
    if (!this.data.fileId) return wx.showToast({ title: '请上传证件照', icon: 'none' })
    api.submitVerification({
      realName,
      studentNo,
      college,
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
