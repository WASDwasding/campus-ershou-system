Component({
  properties: {
    item: { type: Object, value: {} }
  },
  methods: {
    onTap() {
      const id = this.data.item && (this.data.item.goodsId || this.data.item.id)
      this.triggerEvent('open', { id })
    }
  }
})
