import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../utils/token'

/**
 * 路由配置
 *
 * meta.requiresAuth - true 表示必须登录才能访问
 * meta.title        - 页面标题
 */
const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('../views/ProductList.vue'),
    meta: { title: '商品广场' }
  },
  {
    path: '/products/:id(\\d+)',
    name: 'ProductDetail',
    component: () => import('../views/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/products/add',
    name: 'ProductAdd',
    component: () => import('../views/ProductAdd.vue'),
    meta: { title: '发布商品', requiresAuth: true }
  },
  {
    path: '/ai',
    name: 'AIPage',
    component: () => import('../views/AIPage.vue'),
    meta: { title: 'AI 生成描述', requiresAuth: true }
  },
  {
    path: '/agent',
    name: 'AgentInteraction',
    component: () => import('../views/AgentInteraction.vue'),
    meta: { title: 'AI 智能助手', requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// ========== 路由守卫 ==========
router.beforeEach((to, from, next) => {
  // 标题
  if (to.meta.title) {
    document.title = `${to.meta.title} · 校园二手`
  }

  // 鉴权
  if (to.meta.requiresAuth && !getToken()) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  next()
})

export default router
