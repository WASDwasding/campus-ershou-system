Component({
  properties: {
    title: { type: String, value: '' },
    back: { type: Boolean, value: false },
    rightText: { type: String, value: '' }
  },
  data: {
    statusBarHeight: 20,
    navBarHeight: 44,
    rightSafe: 96
  },
  lifetimes: {
    attached() {
      let sys = {}
      try {
        sys = wx.getWindowInfo()
      } catch (e) {
        sys = wx.getSystemInfoSync()
      }
      const windowWidth = sys.windowWidth || 375
      const statusBarHeight = sys.statusBarHeight || 20
      let menu = { top: statusBarHeight + 6, height: 32, left: windowWidth - 96 }
      try {
        const rect = wx.getMenuButtonBoundingClientRect()
        if (rect && rect.height) menu = rect
      } catch (e) {}
      const gap = Math.max(0, menu.top - statusBarHeight)
      const rightSafe = menu.left ? Math.max(16, windowWidth - menu.left + 8) : 96
      this.setData({
        statusBarHeight: statusBarHeight,
        navBarHeight: gap * 2 + (menu.height || 32),
        rightSafe: rightSafe
      })
    }
  },
  methods: {
    onBack() {
      const pages = getCurrentPages()
      if (pages && pages.length > 1) {
        wx.navigateBack({ delta: 1 })
      } else {
        wx.switchTab({ url: '/pages/index/index' })
      }
    },
    onRight() {
      this.triggerEvent('right')
    }
  }
})
