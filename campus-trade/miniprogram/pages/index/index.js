const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { presentGoods, unwrapList } = require('../../utils/format')

const ICONS = ['books', 'digital', 'appliance', 'daily', 'sports', 'clothing', 'exam', 'other']

function decorateCategory(item) {
  const icon = ICONS.indexOf(item.icon) >= 0 ? item.icon : 'other'
  return Object.assign({}, item, { icon: icon })
}

Page({
  data: {
    statusBarHeight: 20,
    navBarHeight: 44,
    navSide: 96,
    categories: [],
    categoryId: 0,
    sort: 'latest',
    sorts: [
      { id: 'latest', name: '最新发布' },
      { id: 'nearby', name: '离我最近' },
      { id: 'price_asc', name: '价格从低到高' }
    ],
    list: [],
    page: 1,
    hasMore: true,
    loading: false
  },
  onLoad() {
    this.measureNav()
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
  measureNav() {
    let sys = {}
    try {
      sys = wx.getWindowInfo()
    } catch (e) {
      sys = wx.getSystemInfoSync()
    }
    const windowWidth = sys.windowWidth || 375
    let menu = { top: (sys.statusBarHeight || 20) + 6, height: 32, left: windowWidth - 96 }
    try {
      const rect = wx.getMenuButtonBoundingClientRect()
      if (rect && rect.left) menu = rect
    } catch (e) {}
    const statusBarHeight = sys.statusBarHeight || 20
    const gap = Math.max(0, menu.top - statusBarHeight)
    this.setData({
      statusBarHeight: statusBarHeight,
      navBarHeight: gap * 2 + (menu.height || 32),
      navSide: Math.max(12, windowWidth - menu.left + 8)
    })
  },
  loadCategories() {
    api
      .categories()
      .then((data) => {
        const list = Array.isArray(data) ? data : (data && data.list) || []
        this.setData({ categories: list.map(decorateCategory) })
      })
      .catch(() => {})
  },
  query(page) {
    const q = { page: page, size: 20, sort: this.data.sort }
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
          page: page,
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
    const id = Number(e.currentTarget.dataset.id) || 0
    this.setData({ categoryId: this.data.categoryId === id ? 0 : id })
    this.reload()
  },
  setSort(e) {
    const sort = e.currentTarget.dataset.id
    if (!sort || sort === this.data.sort) return
    this.setData({ sort: sort })
    this.reload()
  },
  goNearby() {
    if (this.data.sort === 'nearby') return
    this.setData({ sort: 'nearby' })
    this.reload()
  },
  goSearch() {
    wx.navigateTo({ url: '/pages/search/search' })
  },
  open(e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.detail.id })
  }
})
