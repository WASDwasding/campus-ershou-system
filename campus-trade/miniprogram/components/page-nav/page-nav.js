Component({
  properties: {
    title: { type: String, value: '' }
  },
  data: {
    statusBarHeight: 20,
    navBarHeight: 44
  },
  lifetimes: {
    attached() {
      let sys = {}
      try {
        sys = wx.getWindowInfo()
      } catch (e) {
        sys = wx.getSystemInfoSync()
      }
      const statusBarHeight = sys.statusBarHeight || 20
      let menu = { top: statusBarHeight + 6, height: 32 }
      try {
        const rect = wx.getMenuButtonBoundingClientRect()
        if (rect && rect.height) menu = rect
      } catch (e) {}
      const gap = Math.max(0, menu.top - statusBarHeight)
      this.setData({
        statusBarHeight: statusBarHeight,
        navBarHeight: gap * 2 + (menu.height || 32)
      })
    }
  }
})
