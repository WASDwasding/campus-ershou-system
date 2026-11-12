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
    loading: false,
    error: ''
  },
  onLoad() {
    this._seq = 0
    this._loading = false
    this.measureNav()
  },
  onShow() {
    if (!session.token()) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    getApp().refreshBadges()
    if (!this.data.categories.length) this.loadCategories()
    if (this._shown) wx.pageScrollTo({ scrollTop: 0, duration: 0 })
    this._shown = true
    this.reload()
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
    if (this._catLoading) return
    this._catLoading = true
    api
      .categories()
      .then((data) => {
        const list = Array.isArray(data) ? data : (data && data.list) || []
        this.setData({ categories: list.map(decorateCategory) })
      })
      .catch(showError)
      .finally(() => {
        this._catLoading = false
      })
  },
  query(page) {
    const q = { page: page, size: 20, sort: this.data.sort }
    if (this.data.categoryId) q.categoryId = this.data.categoryId
    return q
  },
  reload() {
    this._seq += 1
    this._loading = false
    this.setData({ page: 1, hasMore: true, list: [], error: '' })
    return this.fetch(1, true)
  },
  loadMore() {
    if (!this.data.hasMore || this._loading) return
    this.fetch(this.data.page + 1, false)
  },
  retry() {
    if (this.data.list.length) this.loadMore()
    else this.reload()
  },
  fetch(page, replace) {
    this._loading = true
    this._seq += 1
    const seq = this._seq
    this.setData({ loading: true, error: '' })
    return Promise.all([api.ensureMeta(), api.goods(this.query(page))])
      .then(([, data]) => {
        if (seq !== this._seq) return
        const box = unwrapList(data)
        const base = replace ? [] : this.data.list
        const seen = {}
        base.forEach((g) => {
          seen[g.id] = true
        })
        const rows = []
        box.list.forEach((g) => {
          if (!g || seen[g.id]) return
          seen[g.id] = true
          rows.push(presentGoods(g))
        })
        this._loading = false
        this.setData({
          list: base.concat(rows),
          page: page,
          hasMore: box.hasMore,
          loading: false
        })
      })
      .catch((err) => {
        if (seq !== this._seq) return
        this._loading = false
        this.setData({ loading: false, error: (err && err.message) || '加载失败' })
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
  goPublish() {
    wx.switchTab({ url: '/pages/publish/publish' })
  },
  open(e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.detail.id })
  }
})
