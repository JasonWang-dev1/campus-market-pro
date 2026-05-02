const TOKEN_KEY = 'campus_token'
const USER_KEY = 'campus_user'

/**
 * Token 管理工具
 * 统一管理 JWT 令牌的存取
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export function getUser() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export function setUser(info) {
  localStorage.setItem(USER_KEY, JSON.stringify(info))
}

/** 是否已登录 */
export function isLogged() {
  return !!getToken()
}
