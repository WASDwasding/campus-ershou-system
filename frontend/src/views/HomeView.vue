<template>
  <section class="card">
    <h1>校园智能二手交易平台</h1>
    <p class="muted">骨架已就绪。业务接口目前返回 501，按模块在 backend 对应包里实现即可。</p>
    <p>
      后端健康检查：
      <strong v-if="health" class="status-ok">{{ health }}</strong>
      <strong v-else class="status-bad">{{ healthError || '检测中…' }}</strong>
    </p>
    <p class="muted">启动后端后刷新本页，应显示 UP。</p>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'

const health = ref('')
const healthError = ref('')

onMounted(async () => {
  try {
    const { data } = await http.get('/health')
    health.value = data?.data?.status || '未知'
  } catch {
    healthError.value = '后端未启动或数据库未连通'
  }
})
</script>
