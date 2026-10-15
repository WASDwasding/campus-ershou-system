const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { unwrapList, presentGoods } = require('../../utils/format')

Page({
  data: {
    status: 'ON_SALE',
    statuses: [
      { value: 'ON_SALE', label: '在售' },
      { value: 'RESERVED', label: '已预约' },
      { value: 'SOLD', label: '已售出' },
      { value: 'OFF_SHELF', label: '已下架' },
      { value: 'REMOVED', label: '被下架' }
    ],
    list: []
  },
  onShow() {
    api.ensureMeta().catch(() => {})
    this.load()
  },
  setStatus(e) {
    this.setData({ status: e.currentTarget.dataset.id })
    this.load()
  },
  load() {
    api.myGoods(this.data.status).then((data) => {
      this.setData({ list: unwrapList(data).list.map(presentGoods) })
    }).catch(showError)
  },
  open(e) { wx.navigateTo({ url: '/pages/detail/detail?id=' + e.detail.id }) },
  edit(e) {
    getApp().globalData.editGoodsId = e.currentTarget.dataset.id
    wx.switchTab({ url: '/pages/publish/publish' })
  },
  off(e) { api.offShelf(e.currentTarget.dataset.id).then(() => this.load()).catch(showError) },
  on(e) { api.onShelf(e.currentTarget.dataset.id).then(() => this.load()).catch(showError) },
  del(e) {
    wx.showModal({
      title: '删除商品？',
      content: '删除后不能再看到，已有订单快照会保留。',
      success: (r) => {
        if (!r.confirm) return
        api.deleteGoods(e.currentTarget.dataset.id).then(() => this.load()).catch(showError)
      }
    })
  }
})
