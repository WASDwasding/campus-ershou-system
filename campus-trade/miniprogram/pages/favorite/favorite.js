const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { unwrapList, presentGoods } = require('../../utils/format')

Page({
  data: { list: [] },
  onShow() {
    api.ensureMeta().catch(() => {}).then(() => api.favorites({ page: 1, size: 50 })).then((data) => {
      const list = unwrapList(data).list.map((row) => presentGoods(row.goods || row))
      this.setData({ list })
    }).catch(showError)
  },
  open(e) { wx.navigateTo({ url: '/pages/detail/detail?id=' + e.detail.id }) }
})
