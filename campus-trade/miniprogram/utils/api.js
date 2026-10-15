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
  wxLogin(code) {
    return raw({ url: '/api/auth/wx-login', method: 'POST', data: { code }, auth: false, retry: false })
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
  userReviews(userId) {
    return raw({ url: '/api/users/' + userId + '/reviews' })
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
  favorite(id) {
    return raw({ url: '/api/goods/' + id + '/favorite', method: 'PUT', data: {} })
  },
  unfavorite(id) {
    return raw({ url: '/api/goods/' + id + '/favorite', method: 'DELETE' })
  },
  favorites(query) {
    return raw({ url: '/api/me/favorites', data: query || {} })
  },
  history(query) {
    return raw({ url: '/api/me/history', data: query || {} })
  },
  clearHistory() {
    return raw({ url: '/api/me/history', method: 'DELETE' })
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
  cancelOrder(id, reason) {
    const data = {}
    if (reason) data.reason = reason
    return raw({ url: '/api/orders/' + id + '/cancel', method: 'POST', data })
  },
  meetup(id, body) {
    return raw({ url: '/api/orders/' + id + '/meetup', method: 'PUT', data: body })
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
  review(id, body) {
    return raw({ url: '/api/orders/' + id + '/review', method: 'POST', data: body })
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
  mockPay(paymentNo, result) {
    return raw({
      url: '/api/pay/mock/' + paymentNo + '/confirm',
      method: 'POST',
      data: { result: result || 'SUCCESS' }
    })
  },
  openConversation(goodsId) {
    return raw({ url: '/api/im/conversations', method: 'POST', data: { goodsId: Number(goodsId) } })
  },
  conversations(query) {
    return raw({ url: '/api/im/conversations', data: query || {} })
  },
  conversation(id) {
    return raw({ url: '/api/im/conversations/' + id })
  },
  messages(id, query) {
    return raw({ url: '/api/im/conversations/' + id + '/messages', data: query || {} })
  },
  sendMessage(id, body) {
    return raw({ url: '/api/im/conversations/' + id + '/messages', method: 'POST', data: body })
  },
  readConversation(id, lastReadMessageId) {
    return raw({
      url: '/api/im/conversations/' + id + '/read',
      method: 'POST',
      data: { lastReadMessageId: Number(lastReadMessageId) }
    })
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
  myReports() {
    return raw({ url: '/api/me/reports' })
  }
}

module.exports = api
