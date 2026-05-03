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
    meta: { title: '商品列表' }
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
    name: 'AgentPage',
    component: () => import('../views/AgentPage.vue'),
    meta: { title: 'AI 智能助手', requiresAuth: true }
  }
  /* ---- 后续模块在此扩展 ---- */
  // {
  //   path: '/user',
  //   name: 'UserCenter',
  //   component: () => import('../views/UserCenter.vue'),
  //   meta: { title: '个人中心', requiresAuth: true }
  // }
]

const router = createRouter({
  history: createWebHistory(),
  routes
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
