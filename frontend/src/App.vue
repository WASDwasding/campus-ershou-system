<template>
  <div class="layout">
    <header class="topbar">校园智能二手</header>
    <main class="page">
      <section class="card">
        <h1>项目骨架</h1>
        <p class="muted">目录已按模块划好，业务代码尚未实现。</p>
        <p>
          后端健康检查：
          <strong v-if="health" class="status-ok">{{ health }}</strong>
          <strong v-else class="status-bad">{{ healthError || '检测中…' }}</strong>
        </p>
      </section>
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

const health = ref('')
const healthError = ref('')

onMounted(async () => {
  try {
    const res = await fetch('/api/health')
    const data = await res.json()
    health.value = data.status || '未知'
  } catch {
    healthError.value = '后端未启动'
  }
})
</script>
