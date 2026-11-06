const api = require('../../../utils/api')
const adminSession = require('../../../utils/admin-session')
const { showError } = require('../../../utils/request')
const { unwrapList, formatTime } = require('../../../utils/format')

const STATUS = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }

Page({
  data: {
    status: 'PENDING',
    tabs: [
      { id: 'PENDING', label: '待审核' },
      { id: 'APPROVED', label: '已通过' },
      { id: 'REJECTED', label: '已驳回' }
    ],
    list: []
  },
  onShow() {
    if (!adminSession.ensure()) return
    this.load()
  },
  setStatus(e) {
    this.setData({ status: e.currentTarget.dataset.id })
    this.load()
  },
  load() {
    api.adminVerifications({ status: this.data.status, page: 1, size: 20 }).then((data) => {
      const list = unwrapList(data).list.map((item) => {
        const user = item.user || {}
        const dorm = item.dormBuilding || {}
        return {
          id: item.id,
          name: user.nickname || user.username || '同学',
          statusLabel: STATUS[item.status] || '未知',
          dormName: dorm.name || '',
          timeText: formatTime(item.submittedAt)
        }
      })
      this.setData({ list })
    }).catch(showError)
  },
  open(e) {
    wx.navigateTo({ url: '/pages/admin/verification/verification?id=' + e.currentTarget.dataset.id })
  }
})
