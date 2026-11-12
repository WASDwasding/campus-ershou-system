const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { unwrapList, presentGoods } = require('../../utils/format')

Page({
  data: {
    status: 'ON_SALE',
    statuses: [
      { value: 'ON_SALE', label: '在售' },
      { value: 'RESERVED', label: '已预约' },
      { value: 'MEETUP', label: '待面交' },
      { value: 'DISPUTED', label: '申诉中' },
      { value: 'SOLD', label: '已售出' },
      { value: 'OFF_SHELF', label: '已下架' },
      { value: 'REMOVED', label: '被下架' }
    ],
    list: [],
    loading: false,
    error: ''
  },
  onLoad() {
    this._seq = 0
  },
  onShow() {
    this.load()
  },
  setStatus(e) {
    this.setData({ status: e.currentTarget.dataset.id })
    this.load()
  },
  load() {
    this._seq += 1
    const seq = this._seq
    this.setData({ loading: true, error: '' })
    Promise.all([api.ensureMeta(), api.myGoods(this.data.status)]).then(([, data]) => {
      if (seq !== this._seq) return
      const seen = {}
      const list = []
      unwrapList(data).list.forEach((g) => {
        if (!g || seen[g.id]) return
        seen[g.id] = true
        list.push(presentGoods(g))
      })
      this.setData({ list, loading: false })
    }).catch((err) => {
      if (seq !== this._seq) return
      this.setData({ list: [], loading: false, error: (err && err.message) || '加载失败' })
      showError(err)
    })
  },
  open(e) {
    const id = (e.detail && e.detail.id) || (e.currentTarget && e.currentTarget.dataset && e.currentTarget.dataset.id)
    if (!id) return
    wx.navigateTo({ url: '/pages/detail/detail?id=' + id })
  },
  edit(e) {
    session.setEditGoods(e.currentTarget.dataset.id)
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
