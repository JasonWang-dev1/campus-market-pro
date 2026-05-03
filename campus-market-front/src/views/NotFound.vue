<template>
  <div class="not-found">
    <div class="not-found-card">
      <div class="nf-icon">
        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
      </div>
      <h1>404</h1>
      <p>页面不存在或已被移除</p>
      <div class="nf-actions">
        <router-link to="/home" class="nf-btn nf-btn-primary">返回首页</router-link>
        <button class="nf-btn nf-btn-outline" @click="router.back()">返回上页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  padding: 20px;
}
.not-found-card {
  text-align: center;
  max-width: 400px;
}
.nf-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  color: #c0c4cc;
}
.nf-icon svg {
  width: 100%;
  height: 100%;
}
.not-found-card h1 {
  font-size: 64px;
  font-weight: 700;
  color: #dcdfe6;
  line-height: 1;
  margin-bottom: 12px;
}
.not-found-card p {
  font-size: 16px;
  color: #909399;
  margin-bottom: 32px;
}
.nf-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
.nf-btn {
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}
.nf-btn-primary {
  background: #409eff;
  color: #fff;
  border: none;
}
.nf-btn-primary:hover {
  background: #337ecc;
}
.nf-btn-outline {
  background: transparent;
  color: #606266;
  border: 1px solid #dcdfe6;
}
.nf-btn-outline:hover {
  color: #409eff;
  border-color: #409eff;
}
</style>
