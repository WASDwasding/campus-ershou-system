const api = require('../../utils/api')
const session = require('../../utils/session')
const { showError } = require('../../utils/request')
const { yuanToFen, fenToYuan, chars, hasContact, imageUrl } = require('../../utils/format')

Page({
  data: {
    verified: false,
    goodsId: '',
    images: [],
    categories: [],
    categoryNames: [],
    catIndex: 0,
    conditions: [],
    conditionNames: [],
    condIndex: 0,
    meetups: [],
    meetupNames: [],
    meetupIndex: 0,
    form: { title: '', description: '', priceYuan: '', originalYuan: '', tradeNote: '' }
  },
  onLoad() {},
  onShow() {
    if (!session.token()) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    const app = getApp()
    const editId = (app.globalData && app.globalData.editGoodsId) || ''
    if (app.globalData) app.globalData.editGoodsId = ''
    if (!editId && this.wasEditing) {
      this.setData({
        images: [],
        form: { title: '', description: '', priceYuan: '', originalYuan: '', tradeNote: '' }
      })
    }
    this.wasEditing = !!editId
    this.editId = editId
    this.setData({ goodsId: editId })
    const user = session.user()
    const verified = !!(user && user.verifyStatus === 'VERIFIED')
    this.setData({ verified })
    if (!verified) return
    this.prepare()
  },
  prepare() {
    Promise.all([api.ensureMeta(), api.categories(), api.buildings({ meetupOnly: true })])
      .then(([meta, cats, buildings]) => {
        const categories = Array.isArray(cats) ? cats : (cats && cats.list) || []
        const meetups = Array.isArray(buildings) ? buildings : (buildings && buildings.list) || []
        const conditions = (meta.enums && meta.enums.goodsCondition) || []
        this.setData({
          categories,
          categoryNames: categories.map((c) => c.name),
          meetups,
          meetupNames: meetups.map((b) => b.name),
          conditions,
          conditionNames: conditions.map((c) => c.label)
        })
        if (this.editId) this.fill(this.editId)
      })
      .catch(showError)
  },
  fill(id) {
    api.goodsDetail(id).then((g) => {
      const images = (g.images || []).map((img) => ({
        fileId: img.fileId || img.id,
        url: imageUrl(img)
      }))
      const catIndex = Math.max(0, this.data.categories.findIndex((c) => c.id === (g.category && g.category.id)))
      const condIndex = Math.max(0, this.data.conditions.findIndex((c) => c.value === g.condition))
      const meetupIndex = Math.max(
        0,
        this.data.meetups.findIndex((b) => b.id === (g.meetupBuilding && g.meetupBuilding.id))
      )
      this.setData({
        images,
        catIndex,
        condIndex,
        meetupIndex,
        form: {
          title: g.title || '',
          description: g.description || '',
          priceYuan: fenToYuan(g.price),
          originalYuan: g.originalPrice ? fenToYuan(g.originalPrice) : '',
          tradeNote: g.tradeNote || ''
        }
      })
    }).catch(showError)
  },
  setF(e) {
    this.setData({ ['form.' + e.currentTarget.dataset.k]: e.detail.value })
  },
  onCat(e) { this.setData({ catIndex: Number(e.detail.value) }) },
  onCond(e) { this.setData({ condIndex: Number(e.detail.value) }) },
  onMeetup(e) { this.setData({ meetupIndex: Number(e.detail.value) }) },
  goVerify() { wx.navigateTo({ url: '/pages/verify/verify' }) },
  choose() {
    wx.chooseMedia({
      count: 9 - this.data.images.length,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success: (res) => {
        const files = res.tempFiles || []
        const next = this.data.images.slice()
        const run = (i) => {
          if (i >= files.length) {
            this.setData({ images: next.slice(0, 9) })
            wx.hideLoading()
            return
          }
          const f = files[i]
          if (f.size > 5242880) {
            wx.showToast({ title: '有图片超过 5MB', icon: 'none' })
            run(i + 1)
            return
          }
          wx.showLoading({ title: '上传中' })
          api
            .upload(f.tempFilePath, 'GOODS')
            .then((data) => {
              next.push({ fileId: data.fileId, url: data.url || f.tempFilePath })
              run(i + 1)
            })
            .catch((err) => {
              wx.hideLoading()
              showError(err)
            })
        }
        run(0)
      }
    })
  },
  delImg(e) {
    const images = this.data.images.slice()
    images.splice(e.currentTarget.dataset.i, 1)
    this.setData({ images })
  },
  submit() {
    const form = this.data.form
    const title = (form.title || '').trim()
    const description = (form.description || '').trim()
    const tradeNote = (form.tradeNote || '').trim()
    if (chars(title) < 2 || chars(title) > 30) return wx.showToast({ title: '标题 2–30 字', icon: 'none' })
    if (chars(description) > 500) return wx.showToast({ title: '描述最多 500 字', icon: 'none' })
    if (chars(tradeNote) > 100) return wx.showToast({ title: '交易说明最多 100 字', icon: 'none' })
    if (hasContact(title + description + tradeNote)) {
      return wx.showToast({ title: '不能填写联系方式', icon: 'none' })
    }
    const price = yuanToFen(form.priceYuan)
    if (price == null || price < 1 || price > 9999900) {
      return wx.showToast({ title: '标价需在 0.01–99999 元', icon: 'none' })
    }
    let originalPrice
    if ((form.originalYuan || '').trim()) {
      originalPrice = yuanToFen(form.originalYuan)
      if (originalPrice == null || originalPrice < 1) return wx.showToast({ title: '原价格式不对', icon: 'none' })
    }
    if (!this.data.images.length) return wx.showToast({ title: '至少 1 张图片', icon: 'none' })
    const category = this.data.categories[this.data.catIndex]
    const condition = this.data.conditions[this.data.condIndex]
    const meetup = this.data.meetups[this.data.meetupIndex]
    if (!category || !condition || !meetup) return wx.showToast({ title: '请选择分类、成色和面交点', icon: 'none' })
    const body = {
      title,
      description: description || (this.editId ? null : ''),
      categoryId: category.id,
      condition: condition.value,
      price,
      imageFileIds: this.data.images.map((x) => x.fileId),
      meetupBuildingId: meetup.id,
      tradeNote
    }
    if (originalPrice != null) body.originalPrice = originalPrice
    else if (this.editId) body.originalPrice = null
    const req = this.editId ? api.updateGoods(this.editId, body) : api.createGoods(body)
    req
      .then((g) => {
        wx.showToast({ title: this.editId ? '已保存' : '已发布', icon: 'success' })
        setTimeout(() => {
          wx.navigateTo({ url: '/pages/detail/detail?id=' + (g.id || this.editId) })
          this.editId = ''
          this.setData({
            goodsId: '',
            images: [],
            form: { title: '', description: '', priceYuan: '', originalYuan: '', tradeNote: '' }
          })
        }, 400)
      })
      .catch(showError)
  }
})
