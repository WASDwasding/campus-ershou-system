const { raw, upload } = require('./request')
const { clientId } = require('./format')

let metaTask = null

function meta() {
  return raw({ url: '/api/meta', auth: false })
}

function ensureMeta() {
  const app = getApp()
  if (app && app.globalData && app.globalData.meta) return Promise.resolve(app.globalData.meta)
  if (metaTask) return metaTask
  metaTask = meta()
    .then((data) => {
      if (app && app.globalData) app.globalData.meta = data
      return data
    })
    .catch((err) => {
      metaTask = null
      throw err
    })
  return metaTask
}

function health() {
  return new Promise((resolve, reject) => {
    wx.request({
      url: require('./config').baseUrl + '/api/health',
      success(res) {
        if (res.statusCode === 200) resolve(res.data)
        else reject(res)
      },
      fail: reject
    })
  })
}

const api = {
  health,
  meta,
  ensureMeta,
  buildings(query) {
    return raw({ url: '/api/campus/buildings', data: query || {} })
  },
  categories() {
    return raw({ url: '/api/categories' })
  },
  register(body) {
    return raw({ url: '/api/auth/register', method: 'POST', data: body, auth: false })
  },
  verifyRegisterCode(body) {
    return raw({ url: '/api/auth/register/verify-code', method: 'POST', data: body, auth: false })
  },
  setPassword(body) {
    return raw({ url: '/api/auth/register/set-password', method: 'POST', data: body, auth: false })
  },
  login(body) {
    return raw({ url: '/api/auth/login', method: 'POST', data: body, auth: false })
  },
  wxLogin(body) {
    return raw({ url: '/api/auth/wx-login', method: 'POST', data: body, auth: false })
  },
  wxBind(body) {
    return raw({ url: '/api/auth/wx-bind', method: 'POST', data: body })
  },
  logout() {
    return raw({ url: '/api/auth/logout', method: 'POST', data: {} })
  },
  submitVerification(body) {
    return raw({ url: '/api/auth/campus-verification', method: 'POST', data: body })
  },
  verification() {
    return raw({ url: '/api/auth/campus-verification' })
  },
  me() {
    return raw({ url: '/api/me' })
  },
  updateProfile(body) {
    return raw({ url: '/api/me/profile', method: 'PUT', data: body })
  },
  stats() {
    return raw({ url: '/api/me/stats' })
  },
  badges() {
    return raw({ url: '/api/me/badges' })
  },
  user(userId) {
    return raw({ url: '/api/users/' + userId })
  },
  userGoods(userId, status) {
    return raw({ url: '/api/users/' + userId + '/goods', data: status ? { status } : {} })
  },
  upload,
  goods(query) {
    return raw({ url: '/api/goods', data: query })
  },
  goodsDetail(id) {
    return raw({ url: '/api/goods/' + id })
  },
  createGoods(body) {
    return raw({ url: '/api/goods', method: 'POST', data: body })
  },
  updateGoods(id, body) {
    return raw({ url: '/api/goods/' + id, method: 'PUT', data: body })
  },
  offShelf(id) {
    return raw({ url: '/api/goods/' + id + '/off-shelf', method: 'POST', data: {} })
  },
  onShelf(id) {
    return raw({ url: '/api/goods/' + id + '/on-shelf', method: 'POST', data: {} })
  },
  deleteGoods(id) {
    return raw({ url: '/api/goods/' + id, method: 'DELETE' })
  },
  myGoods(status) {
    return raw({ url: '/api/me/goods', data: status ? { status } : {} })
  },
  createOrder(goodsId) {
    return raw({
      url: '/api/orders',
      method: 'POST',
      data: { goodsId: Number(goodsId), quantity: 1 },
      idempotencyKey: clientId('ord')
    })
  },
  order(id) {
    return raw({ url: '/api/orders/' + id })
  },
  myOrders(query) {
    return raw({ url: '/api/me/orders', data: query || {} })
  },
  cancelOrder(id) {
    return raw({ url: '/api/orders/' + id + '/cancel', method: 'POST', data: {} })
  },
  handover(id) {
    return raw({ url: '/api/orders/' + id + '/handover-confirm', method: 'POST', data: {} })
  },
  dispute(id, body) {
    return raw({ url: '/api/orders/' + id + '/dispute', method: 'POST', data: body })
  },
  withdrawDispute(id) {
    return raw({ url: '/api/orders/' + id + '/dispute/withdraw', method: 'POST', data: {} })
  },
  orderMessages(orderId, query) {
    return raw({ url: '/api/orders/' + orderId + '/messages', data: query || {} })
  },
  sendOrderMessage(orderId, body) {
    return raw({ url: '/api/orders/' + orderId + '/messages', method: 'POST', data: body })
  },
  createPayment(orderId) {
    return raw({
      url: '/api/orders/' + orderId + '/payments',
      method: 'POST',
      data: { channel: 'MOCK' },
      idempotencyKey: clientId('pay')
    })
  },
  payment(paymentNo) {
    return raw({ url: '/api/payments/' + paymentNo })
  },
  mockPay(paymentNo) {
    return raw({
      url: '/api/pay/mock/' + paymentNo + '/confirm',
      method: 'POST',
      data: {}
    })
  },
  goodsDraft(body) {
    return raw({ url: '/api/ai/goods-draft', method: 'POST', data: body })
  },
  quote(goodsId) {
    return raw({ url: '/api/ai/quotes', method: 'POST', data: { goodsId: Number(goodsId) } })
  },
  notifications(query) {
    return raw({ url: '/api/notifications', data: query || {} })
  },
  readNotifications(body) {
    return raw({ url: '/api/notifications/read', method: 'POST', data: body })
  },
  report(body) {
    return raw({ url: '/api/reports', method: 'POST', data: body })
  },
  myReports(query) {
    return raw({ url: '/api/me/reports', data: query || {} })
  },
  adminLogin(body) {
    return raw({ url: '/api/admin/auth/login', method: 'POST', data: body, auth: false })
  },
  adminLogout() {
    return raw({ url: '/api/admin/auth/logout', method: 'POST', data: {}, auth: 'admin' })
  },
  adminMe() {
    return raw({ url: '/api/admin/me', auth: 'admin' })
  },
  adminPassword(body) {
    return raw({ url: '/api/admin/auth/password', method: 'POST', data: body, auth: 'admin' })
  },
  adminVerifications(query) {
    return raw({ url: '/api/admin/verifications', data: query || {}, auth: 'admin' })
  },
  adminVerification(id) {
    return raw({ url: '/api/admin/verifications/' + id, auth: 'admin' })
  },
  approveVerification(id) {
    return raw({ url: '/api/admin/verifications/' + id + '/approve', method: 'POST', data: {}, auth: 'admin' })
  },
  rejectVerification(id, reason) {
    return raw({ url: '/api/admin/verifications/' + id + '/reject', method: 'POST', data: { reason }, auth: 'admin' })
  },
  adminDisputes(query) {
    return raw({ url: '/api/admin/disputes', data: query || {}, auth: 'admin' })
  },
  adminOrder(id) {
    return raw({ url: '/api/admin/orders/' + id, auth: 'admin' })
  },
  adminDisputeMessages(id) {
    return raw({ url: '/api/admin/disputes/' + id + '/messages', auth: 'admin' })
  },
  resolveDispute(id, body) {
    return raw({ url: '/api/admin/disputes/' + id + '/resolve', method: 'POST', data: body, auth: 'admin' })
  },
  adminReports(query) {
    return raw({ url: '/api/admin/reports', data: query || {}, auth: 'admin' })
  },
  adminReport(id) {
    return raw({ url: '/api/admin/reports/' + id, auth: 'admin' })
  },
  handleReport(id, body) {
    return raw({ url: '/api/admin/reports/' + id + '/handle', method: 'POST', data: body, auth: 'admin' })
  },
  pickFiles(purpose, max) {
    return pickFiles(purpose, max)
  }
}

function pickFiles(purpose, max) {
  return new Promise((resolve, reject) => {
    wx.chooseMedia({
      count: max,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success(res) {
        const files = res.tempFiles || []
        const ids = []
        const run = (i) => {
          if (i >= files.length) {
            wx.hideLoading()
            resolve(ids)
            return
          }
          const file = files[i]
          if (file.size > 5242880) {
            wx.hideLoading()
            reject({ code: 'FILE_TOO_LARGE', message: '有图片超过 5MB' })
            return
          }
          wx.showLoading({ title: '上传中' })
          upload(file.tempFilePath, purpose)
            .then((data) => {
              ids.push(data.fileId)
              run(i + 1)
            })
            .catch((err) => {
              wx.hideLoading()
              reject(err)
            })
        }
        if (!files.length) {
          resolve([])
          return
        }
        run(0)
      },
      fail(err) {
        const msg = (err && err.errMsg) || ''
        if (msg.indexOf('cancel') >= 0) resolve([])
        else reject({ message: '没有选到图片' })
      }
    })
  })
}

module.exports = api
