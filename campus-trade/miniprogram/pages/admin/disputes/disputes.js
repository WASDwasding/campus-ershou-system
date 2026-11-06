const api = require('../../../utils/api')
const adminSession = require('../../../utils/admin-session')
const { showError } = require('../../../utils/request')
const { unwrapList, labelOf } = require('../../../utils/format')

Page({
  data: {
    status: 'OPEN',
    tabs: [
      { id: 'OPEN', label: '处理中' },
      { id: 'RESOLVED', label: '已结案' },
      { id: 'REJECTED', label: '已驳回' },
      { id: 'WITHDRAWN', label: '已撤回' }
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
    api.adminDisputes({ status: this.data.status, page: 1, size: 20 }).then((data) => {
      const list = unwrapList(data).list.map((item) => ({
        id: item.id,
        orderId: item.orderId,
        reasonLabel: labelOf('disputeReason', item.reason),
        statusLabel: labelOf('disputeStatus', item.status),
        description: item.description || ''
      }))
      this.setData({ list })
    }).catch(showError)
  },
  open(e) {
    const id = e.currentTarget.dataset.id
    const orderId = e.currentTarget.dataset.order
    wx.navigateTo({ url: '/pages/admin/dispute/dispute?id=' + id + '&orderId=' + orderId })
  }
})
