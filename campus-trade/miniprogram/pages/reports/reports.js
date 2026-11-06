const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { unwrapList, formatTime, labelOf } = require('../../utils/format')

Page({
  data: { list: [] },
  onShow() {
    api.ensureMeta().catch(() => {}).then(() => api.myReports({ page: 1, size: 20 })).then((data) => {
      const list = unwrapList(data).list.map((item) => ({
        id: item.id,
        goodsId: item.goodsId,
        statusLabel: labelOf('reportStatus', item.status),
        description: item.description || '',
        reason: item.reason || '',
        timeText: formatTime(item.createdAt),
        handledText: formatTime(item.handledAt)
      }))
      this.setData({ list })
    }).catch(showError)
  },
  open(e) {
    const id = e.currentTarget.dataset.id
    if (id) wx.navigateTo({ url: '/pages/detail/detail?id=' + id })
  }
})
