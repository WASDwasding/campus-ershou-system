<template>
  <view class="page">
    <view class="card">
      <text class="title">项目骨架</text>
      <text class="muted">uni-app 微信小程序空壳，业务尚未实现。</text>
      <view class="row">
        <text>后端健康检查：</text>
        <text v-if="health" class="ok">{{ health }}</text>
        <text v-else class="bad">{{ healthError || '检测中…' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { API_BASE } from '../../config.js'

const health = ref('')
const healthError = ref('')

onLoad(() => {
  uni.request({
    url: `${API_BASE}/api/health`,
    success(res) {
      const data = res.data || {}
      health.value = data.status || '未知'
    },
    fail() {
      healthError.value = '后端未启动'
    },
  })
})
</script>

<style>
.page {
  padding: 40rpx;
}

.card {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  padding: 32rpx;
  background: #fff;
  border-radius: 16rpx;
}

.title {
  font-size: 36rpx;
  font-weight: 600;
}

.muted {
  color: #8f8f94;
  font-size: 26rpx;
}

.row {
  margin-top: 12rpx;
  font-size: 28rpx;
}

.ok {
  color: #1a7f37;
}

.bad {
  color: #cf222e;
}
</style>
