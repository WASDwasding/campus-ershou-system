const TOKEN = 'ct_token'
const USER = 'ct_user'

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
}

function verified() {
  const u = user()
  return !!(u && u.verifyStatus === 'VERIFIED')
}

module.exports = { token, user, setSession, clear, verified }
