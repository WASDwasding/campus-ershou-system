<template>
  <section class="card">
    <h1>登录</h1>
    <p class="muted">登录接口尚未实现。接上 /api/auth/login 后把返回的 token 写入 user store。</p>
    <form @submit.prevent>
      <p>
        <input disabled placeholder="用户名" />
      </p>
      <p>
        <input disabled type="password" placeholder="密码" />
      </p>
      <button class="btn" disabled type="submit">登录（待实现）</button>
    </form>
  </section>
</template>
