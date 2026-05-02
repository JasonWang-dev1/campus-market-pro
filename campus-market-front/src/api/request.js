import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '../utils/token'
import router from '../router'

/**
 * Axios 实例
 * - baseURL: 后端接口地址
 * - 请求拦截器: 自动携带 JWT Token
 * - 响应拦截器: 统一错误处理
 */
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000
})

// ========== 请求拦截器 ==========
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ========== 响应拦截器 ==========
request.interceptors.response.use(
  (response) => {
    const body = response.data

    // 后端统一返回 { code, message, data }
    if (body.code === undefined) {
      // 非标准响应，直接返回原始数据
      return body
    }

    if (body.code !== 200) {
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message))
    }

    return body
  },
  (error) => {
    if (error.response) {
      const { status } = error.response
      const msg = error.response.data?.message

      switch (status) {
        case 401:
          removeToken()
          ElMessage.error('登录已过期，请重新登录')
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器繁忙，请稍后重试')
          break
        default:
          ElMessage.error(msg || `请求失败(${status})`)
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查网络')
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }

    return Promise.reject(error)
  }
)

export default request
