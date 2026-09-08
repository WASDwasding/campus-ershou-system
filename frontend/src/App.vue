<template>
  <div class="layout">
    <header class="topbar">
      <RouterLink to="/" class="brand">校园智能二手</RouterLink>
      <nav>
        <RouterLink to="/goods">商品</RouterLink>
        <RouterLink to="/login">登录</RouterLink>
      </nav>
    </header>
    <main class="page">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>
