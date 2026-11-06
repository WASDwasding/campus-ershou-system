const api = require('../../../utils/api')
const adminSession = require('../../../utils/admin-session')
const { showError } = require('../../../utils/request')
const { unwrapList, labelOf, formatTime } = require('../../../utils/format')

Page({
  data: {
    status: 'PENDING',
    tabs: [
      { id: 'PENDING', label: '待处理' },
      { id: 'DISMISSED', label: '已驳回' },
      { id: 'REMOVED', label: '已下架' }
    ],
    list: []
  },
  onShow() {
    if (!adminSession.ensure()) return
    api.ensureMeta().catch(() => {}).then(() => this.load())
  },
  setStatus(e) {
    this.setData({ status: e.currentTarget.dataset.id })
    this.load()
  },
  load() {
    api.adminReports({ status: this.data.status, page: 1, size: 20 }).then((data) => {
      const list = unwrapList(data).list.map((item) => ({
        id: item.id,
        goodsId: item.goodsId,
        statusLabel: labelOf('reportStatus', item.status),
        description: item.description || '',
        timeText: formatTime(item.createdAt)
      }))
      this.setData({ list })
    }).catch(showError)
  },
  open(e) {
    wx.navigateTo({ url: '/pages/admin/report/report?id=' + e.currentTarget.dataset.id })
  }
})
