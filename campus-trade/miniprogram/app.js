const session = require('./utils/session')
const socket = require('./utils/socket')
const api = require('./utils/api')

App({
  globalData: {
    user: null,
    meta: null
  },
  onLaunch() {
    this.globalData.user = session.user()
    if (session.token()) {
      socket.connect()
      api.ensureMeta().catch(() => {})
      this.refreshMe()
    }
  },
  onShow() {
    if (session.token()) {
      socket.connect()
      this.refreshBadges()
    }
  },
  refreshMe() {
    return api
      .me()
      .then((user) => {
        session.setSession(session.token(), user)
        this.globalData.user = user
        return user
      })
      .catch(() => null)
  },
  refreshBadges() {
    api
      .badges()
      .then((data) => {
        const n = Number((data && (data.imUnread || data.unreadMessages || data.messages)) || 0)
        if (n > 0) wx.setTabBarBadge({ index: 2, text: n > 99 ? '99+' : String(n) })
        else wx.removeTabBarBadge({ index: 2 })
      })
      .catch(() => {})
  }
})
