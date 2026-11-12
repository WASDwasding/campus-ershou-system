const TOKEN = 'ct_token'
const USER = 'ct_user'
const EDIT_GOODS = 'ct_edit_goods'

function token() {
  return wx.getStorageSync(TOKEN) || ''
}

function user() {
  return wx.getStorageSync(USER) || null
}

function setSession(nextToken, nextUser) {
  if (nextToken) wx.setStorageSync(TOKEN, nextToken)
  if (nextUser) wx.setStorageSync(USER, nextUser)
}

function clear() {
  wx.removeStorageSync(TOKEN)
  wx.removeStorageSync(USER)
  wx.removeStorageSync(EDIT_GOODS)
}

function verified() {
  const u = user()
  return !!(u && u.verifyStatus === 'VERIFIED')
}

function setEditGoods(id) {
  if (id) wx.setStorageSync(EDIT_GOODS, String(id))
}

function takeEditGoods() {
  const id = wx.getStorageSync(EDIT_GOODS) || ''
  if (id) wx.removeStorageSync(EDIT_GOODS)
  return id
}

module.exports = { token, user, setSession, clear, verified, setEditGoods, takeEditGoods }
