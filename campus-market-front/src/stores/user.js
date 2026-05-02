import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi } from '../api/auth'
import { getToken, setToken, removeToken, setUser, getUser, isLogged } from '../utils/token'

/**
 * 用户状态管理
 *
 * 职责：
 * - 维护 token 与用户信息
 * - 提供 login / logout 方法
 * - 持久化到 localStorage
 *
 * 后续扩展：
 * - 用户信息增补（头像上传、资料修改）
 * - 多角色判断（普通用户 / 管理员）
 */
export const useUserStore = defineStore('user', () => {
  // ========== State ==========
  const token = ref(getToken())
  const userInfo = ref(getUser())

  // ========== Getters ==========
  const isLoggedIn = computed(() => !!token.value)

  // ========== Actions ==========

  /** 登录并持久化 */
  async function login(loginDTO) {
    const res = await loginApi(loginDTO)
    const { token: jwt, ...info } = res.data
    setToken(jwt)
    setUser(info)
    token.value = jwt
    userInfo.value = info
    return res
  }

  /** 退出登录 */
  function logout() {
    removeToken()
    token.value = ''
    userInfo.value = null
  }

  /** 更新用户信息（后续个人中心使用） */
  function updateUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
    setUser(userInfo.value)
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    logout,
    updateUserInfo
  }
})
