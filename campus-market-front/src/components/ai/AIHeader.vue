<template>
  <header class="sticky top-0 z-50 bg-white/80 backdrop-blur-xl border-b border-ai-border">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-14 sm:h-16">
        <!-- Logo -->
        <router-link to="/home" class="flex items-center gap-2.5 group">
          <div class="w-8 h-8 rounded-xl bg-ai-text flex items-center justify-center transition-transform duration-200 group-hover:scale-105">
            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
            </svg>
          </div>
          <span class="font-display font-semibold text-sm sm:text-base text-ai-text tracking-tight">校园二手</span>
        </router-link>

        <!-- Desktop Nav -->
        <nav class="hidden md:flex items-center gap-1">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="px-3.5 py-1.5 rounded-lg text-sm font-medium transition-all duration-200"
            :class="isActive(item.path)
              ? 'bg-ai-accent-soft text-ai-accent'
              : 'text-ai-text-2 hover:text-ai-text hover:bg-ai-surface-2'"
          >
            {{ item.label }}
          </router-link>
        </nav>

        <!-- Right section -->
        <div class="flex items-center gap-2">
          <!-- Mobile nav toggle -->
          <button
            @click="mobileOpen = !mobileOpen"
            class="md:hidden w-9 h-9 rounded-lg flex items-center justify-center text-ai-text-2 hover:text-ai-text hover:bg-ai-surface-2 transition-all"
          >
            <svg v-if="!mobileOpen" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
            </svg>
            <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>

          <!-- User area -->
          <template v-if="userStore.isLoggedIn">
            <router-link
              to="/agent"
              class="hidden sm:flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold bg-ai-accent-soft text-ai-accent border border-ai-accent-border hover:bg-amber-100 transition-all"
            >
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
              </svg>
              AI 助手
            </router-link>
            <div class="relative" @click.away="dropdownOpen = false">
              <button
                @click="dropdownOpen = !dropdownOpen"
                class="w-8 h-8 rounded-full bg-ai-surface-2 flex items-center justify-center text-sm font-semibold text-ai-text-2 hover:bg-ai-border transition-all"
              >
                {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || '?')[0] }}
              </button>
              <div v-if="dropdownOpen" class="absolute right-0 top-full mt-2 w-44 bg-white border border-ai-border rounded-2xl shadow-warm-lg py-2 overflow-hidden">
                <div class="px-4 py-2 border-b border-ai-border">
                  <p class="text-sm font-medium text-ai-text">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</p>
                  <p class="text-xs text-ai-text-3">{{ userStore.userInfo?.phone || '' }}</p>
                </div>
                <button @click="handleLogout" class="w-full flex items-center gap-2 px-4 py-2 text-sm text-ai-text-2 hover:text-ai-danger hover:bg-ai-danger-soft transition-colors">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
                  </svg>
                  退出登录
                </button>
              </div>
            </div>
          </template>
          <template v-else>
            <router-link to="/login" class="px-4 py-1.5 rounded-lg text-sm font-medium bg-ai-text text-white hover:bg-gray-800 transition-all">
              登录
            </router-link>
          </template>
        </div>
      </div>

      <!-- Mobile nav -->
      <div v-if="mobileOpen" class="md:hidden pb-4 border-t border-ai-border pt-3 animate-scale-in">
        <div class="flex flex-col gap-1">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            @click="mobileOpen = false"
            class="px-3 py-2 rounded-lg text-sm font-medium transition-all"
            :class="isActive(item.path) ? 'bg-ai-accent-soft text-ai-accent' : 'text-ai-text-2 hover:text-ai-text hover:bg-ai-surface-2'"
          >
            {{ item.label }}
          </router-link>
          <router-link
            v-if="userStore.isLoggedIn"
            to="/agent"
            @click="mobileOpen = false"
            class="px-3 py-2 rounded-lg text-sm font-medium text-ai-accent bg-ai-accent-soft"
          >
            AI 智能助手
          </router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const mobileOpen = ref(false)
const dropdownOpen = ref(false)

const navItems = [
  { path: '/home', label: '首页' },
  { path: '/products', label: '商品广场' },
  { path: '/products/add', label: '发布闲置' },
]

function isActive(path) {
  return route.path === path || route.path.startsWith(path + '/')
}

function handleLogout() {
  dropdownOpen.value = false
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/home')
}
</script>
