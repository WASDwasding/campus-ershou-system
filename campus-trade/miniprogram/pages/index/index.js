const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { presentGoods, unwrapList } = require('../../utils/format')

Page({
  data: {
    categories: [],
    categoryId: 0,
    sort: 'latest',
    sorts: [
      { id: 'latest', name: '最新' },
      { id: 'nearby', name: '就近' },
      { id: 'price_asc', name: '价格升' },
      { id: 'price_desc', name: '价格降' }
    ],
    list: [],
    page: 1,
    hasMore: true,
    loading: false
  },
  onShow() {
    if (!session.token()) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    api.ensureMeta().catch(() => {})
    if (!this.data.categories.length) this.loadCategories()
    if (!this.data.list.length) this.reload()
  },
  onPullDownRefresh() {
    this.reload().finally(() => wx.stopPullDownRefresh())
  },
  onReachBottom() {
    this.loadMore()
  },
  loadCategories() {
    api
      .categories()
      .then((data) => {
        const list = Array.isArray(data) ? data : (data && data.list) || []
        this.setData({ categories: list })
      })
      .catch(() => {})
  },
  query(page) {
    const q = { page, size: 20, sort: this.data.sort }
    if (this.data.categoryId) q.categoryId = this.data.categoryId
    return q
  },
  reload() {
    this.setData({ page: 1, hasMore: true, list: [] })
    return this.fetch(1, true)
  },
  loadMore() {
    if (!this.data.hasMore || this.data.loading) return
    this.fetch(this.data.page + 1, false)
  },
  fetch(page, replace) {
    this.setData({ loading: true })
    return api
      .goods(this.query(page))
      .then((data) => {
        const box = unwrapList(data)
        const rows = box.list.map(presentGoods)
        this.setData({
          list: replace ? rows : this.data.list.concat(rows),
          page,
          hasMore: box.hasMore,
          loading: false
        })
      })
      .catch((err) => {
        this.setData({ loading: false })
        showError(err)
      })
  },
  setCat(e) {
    this.setData({ categoryId: Number(e.currentTarget.dataset.id) || 0 })
    this.reload()
  },
  setSort(e) {
    this.setData({ sort: e.currentTarget.dataset.id })
    this.reload()
  },
  goSearch() {
    wx.navigateTo({ url: '/pages/search/search' })
  },
  open(e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.detail.id })
  }
})
