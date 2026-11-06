const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { presentGoods, unwrapList, yuanToFen, chars } = require('../../utils/format')

Page({
  data: {
    keyword: '',
    minYuan: '',
    maxYuan: '',
    meetups: [],
    meetupNames: ['全部面交点'],
    meetupIndex: 0,
    list: [],
    searched: false
  },
  onShow() {
    if (this.data.meetups.length) return
    api.buildings({ meetupOnly: true }).then((data) => {
      const meetups = Array.isArray(data) ? data : (data && data.list) || []
      this.setData({
        meetups,
        meetupNames: ['全部面交点'].concat(meetups.map((b) => b.name))
      })
    }).catch(() => {})
  },
  onQ(e) { this.setData({ keyword: e.detail.value }) },
  onMin(e) { this.setData({ minYuan: e.detail.value }) },
  onMax(e) { this.setData({ maxYuan: e.detail.value }) },
  onMeetup(e) { this.setData({ meetupIndex: Number(e.detail.value) }) },
  go() {
    const keyword = (this.data.keyword || '').trim()
    if (keyword && (chars(keyword) < 1 || chars(keyword) > 30)) {
      wx.showToast({ title: '关键词 1–30 字', icon: 'none' })
      return
    }
    const q = { page: 1, size: 20, sort: 'latest' }
    if (keyword) q.keyword = keyword
    if (this.data.minYuan) {
      const fen = yuanToFen(this.data.minYuan)
      if (fen == null) return wx.showToast({ title: '最低价格式不对', icon: 'none' })
      q.minPrice = fen
    }
    if (this.data.maxYuan) {
      const fen = yuanToFen(this.data.maxYuan)
      if (fen == null) return wx.showToast({ title: '最高价格式不对', icon: 'none' })
      q.maxPrice = fen
    }
    const meetup = this.data.meetupIndex > 0 ? this.data.meetups[this.data.meetupIndex - 1] : null
    if (meetup) q.meetupBuildingId = meetup.id
    api
      .ensureMeta()
      .catch(() => {})
      .then(() => api.goods(q))
      .then((data) => this.setData({ list: unwrapList(data).list.map(presentGoods), searched: true }))
      .catch(showError)
  },
  open(e) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id })
  }
})
