const api = require('../../../utils/api')
const adminSession = require('../../../utils/admin-session')
const { showError } = require('../../../utils/request')
const { chars } = require('../../../utils/format')

const STATUS = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }

Page({
  data: { item: {} },
  onLoad(q) { this.id = q.id },
  onShow() {
    if (!adminSession.ensure()) return
    this.load()
  },
  load() {
    api.adminVerification(this.id).then((v) => {
      const user = v.user || {}
      const dorm = v.dormBuilding || {}
      const card = v.cardImage || {}
      this.setData({
        item: {
          id: v.id,
          status: v.status,
          statusLabel: STATUS[v.status] || '未知',
          realName: v.realName || '',
          studentNo: v.studentNo || '',
          college: v.college || '',
          dormName: dorm.name || '',
          nickname: user.nickname || user.username || '',
          rejectReason: v.rejectReason || '',
          cardUrl: card.url || ''
        }
      })
    }).catch(showError)
  },
  preview() {
    if (!this.data.item.cardUrl) return
    wx.previewImage({ current: this.data.item.cardUrl, urls: [this.data.item.cardUrl] })
  },
  approve() {
    wx.showModal({
      title: '通过认证？',
      content: '通过后该学生可以发布和下单。',
      success: (r) => {
        if (!r.confirm) return
        api.approveVerification(this.id).then(() => {
          wx.showToast({ title: '已通过', icon: 'success' })
          this.load()
        }).catch(showError)
      }
    })
  },
  reject() {
    wx.showModal({
      title: '驳回原因（2–100 字）',
      editable: true,
      placeholderText: '学生能在认证页看到',
      success: (r) => {
        if (!r.confirm) return
        const reason = (r.content || '').trim()
        if (chars(reason) < 2 || chars(reason) > 100) {
          wx.showToast({ title: '原因 2–100 字', icon: 'none' })
          return
        }
        api.rejectVerification(this.id, reason).then(() => {
          wx.showToast({ title: '已驳回', icon: 'success' })
          this.load()
        }).catch(showError)
      }
    })
  }
})
