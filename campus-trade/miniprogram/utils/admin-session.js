const TOKEN = 'ct_admin_token'
const PROFILE = 'ct_admin'

function token() {
  return wx.getStorageSync(TOKEN) || ''
}

function profile() {
  return wx.getStorageSync(PROFILE) || null
}

function setSession(nextToken, nextProfile) {
  if (nextToken) wx.setStorageSync(TOKEN, nextToken)
  if (nextProfile) wx.setStorageSync(PROFILE, nextProfile)
}

function clear() {
  wx.removeStorageSync(TOKEN)
  wx.removeStorageSync(PROFILE)
}

function ensure() {
  if (!token()) {
    wx.redirectTo({ url: '/pages/admin/login/login' })
    return null
  }
  return profile() || {}
}

module.exports = { token, profile, setSession, clear, ensure }
