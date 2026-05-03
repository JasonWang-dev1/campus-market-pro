<template>
  <header class="app-header">
    <div class="header-inner">
      <div class="logo" @click="router.push('/home')">
        <el-icon :size="26" color="#409eff"><School /></el-icon>
        <span>校园二手</span>
      </div>

      <nav class="nav">
        <router-link to="/home">首页</router-link>
        <router-link to="/products">商品列表</router-link>
        <router-link v-if="userStore.isLoggedIn" to="/products/add">发布商品</router-link>
        <router-link v-if="userStore.isLoggedIn" to="/agent">AI 智能助手</router-link>
        <router-link v-if="userStore.isLoggedIn" to="/ai">AI 描述</router-link>
      </nav>

      <div class="user-area">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="30" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-item @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" size="small" @click="router.push('/login')">登录</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { UserFilled, SwitchButton, School } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/home')
}
</script>

<style scoped>
.app-header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  cursor: pointer;
}

.nav {
  flex: 1;
  display: flex;
  gap: 24px;
  margin-left: 40px;
}

.nav a {
  font-size: 15px;
  color: #606266;
  transition: color 0.2s;
}

.nav a:hover,
.nav a.router-link-active {
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #303133;
}
</style>
