const api = require('../../utils/api')
const { showError } = require('../../utils/request')
const { fenToYuan, labelOf, formatTime, toOffsetIso, chars } = require('../../utils/format')

function canMap(actions) {
  const can = {}
  ;(actions || []).forEach((a) => { can[a] = true })
  return can
}

function todayStr() {
  const d = new Date()
  const p = (n) => (n < 10 ? '0' + n : '' + n)
  return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate())
}

Page({
  data: {
    order: {},
    can: {},
    meetupOpen: false,
    buildings: [],
    buildingNames: [],
    buildingIndex: 0,
    place: '',
    date: '',
    time: '18:00',
    today: todayStr()
  },
  onLoad(q) { this.id = q.id },
  onShow() { this.load() },
  load() {
    api.ensureMeta().catch(() => {}).then(() => api.order(this.id)).then((o) => {
      const meetup = o.meetup || {}
      const building = meetup.building || {}
      const meetupText = [building.name, meetup.place, formatTime(meetup.time)].filter(Boolean).join(' ')
      this.setData({
        order: {
          id: o.id,
          orderNo: o.orderNo,
          statusLabel: labelOf('orderStatus', o.status),
          goodsTitle: (o.goods && o.goods.title) || '商品',
          amountText: fenToYuan(o.amount),
          myRole: o.myRole,
          expireAt: o.expireAt,
          expireText: formatTime(o.expireAt),
          meetupText
        },
        can: canMap(o.allowedActions)
      })
    }).catch(showError)
  },
  pay() {
    api.createPayment(this.id).then((pay) => api.mockPay(pay.paymentNo, 'SUCCESS')).then(() => {
      wx.showToast({ title: '已付款', icon: 'success' })
      this.load()
    }).catch(showError)
  },
  cancel() {
    wx.showModal({
      title: '取消订单',
      editable: true,
      placeholderText: '原因，可留空',
      success: (r) => {
        if (!r.confirm) return
        api.cancelOrder(this.id, (r.content || '').trim()).then(() => this.load()).catch(showError)
      }
    })
  },
  showMeetup() {
    api.buildings({ meetupOnly: true }).then((data) => {
      const buildings = Array.isArray(data) ? data : (data && data.list) || []
      this.setData({
        meetupOpen: true,
        buildings,
        buildingNames: buildings.map((b) => b.name)
      })
    }).catch(showError)
  },
  onBuilding(e) { this.setData({ buildingIndex: Number(e.detail.value) }) },
  onPlace(e) { this.setData({ place: e.detail.value }) },
  onDate(e) { this.setData({ date: e.detail.value }) },
  onTime(e) { this.setData({ time: e.detail.value }) },
  saveMeetup() {
    const building = this.data.buildings[this.data.buildingIndex]
    const place = (this.data.place || '').trim()
    if (!building) return wx.showToast({ title: '请选择面交点', icon: 'none' })
    if (chars(place) < 1 || chars(place) > 40) return wx.showToast({ title: '位置 1–40 字', icon: 'none' })
    const time = toOffsetIso(this.data.date, this.data.time)
    if (!time) return wx.showToast({ title: '请选择时间', icon: 'none' })
    api.meetup(this.id, { buildingId: building.id, place, time }).then(() => {
      this.setData({ meetupOpen: false })
      this.load()
    }).catch(showError)
  },
  handover() {
    api.handover(this.id).then(() => this.load()).catch(showError)
  },
  openDispute() {
    api.ensureMeta().then((meta) => {
      const reasons = (meta.enums && meta.enums.disputeReason) || []
      wx.showActionSheet({
        itemList: reasons.map((x) => x.label),
        success: (r) => {
          wx.showModal({
            title: '申诉说明（10–500 字）',
            editable: true,
            placeholderText: '请描述问题',
            success: (m) => {
              if (!m.confirm) return
              const description = (m.content || '').trim()
              if (chars(description) < 10) return wx.showToast({ title: '说明至少 10 字', icon: 'none' })
              api.dispute(this.id, {
                reason: reasons[r.tapIndex].value,
                description,
                evidenceFileIds: []
              }).then(() => this.load()).catch(showError)
            }
          })
        }
      })
    }).catch(showError)
  },
  withdraw() {
    api.withdrawDispute(this.id).then(() => this.load()).catch(showError)
  },
  review() {
    wx.showActionSheet({
      itemList: ['5 分', '4 分', '3 分', '2 分', '1 分'],
      success: (r) => {
        const score = 5 - r.tapIndex
        wx.showModal({
          title: '评价内容',
          editable: true,
          placeholderText: '200 字以内，可留空',
          success: (m) => {
            if (!m.confirm) return
            api.review(this.id, { score, content: (m.content || '').trim() }).then(() => this.load()).catch(showError)
          }
        })
      }
    })
  }
})
