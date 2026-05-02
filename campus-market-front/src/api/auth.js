import request from './request'

/** 登录 */
export function loginApi(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

/** 注册 */
export function registerApi(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}
