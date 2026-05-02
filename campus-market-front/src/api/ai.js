import request from './request'

/** AI 智能生成商品描述 */
export function generateDescription(data) {
  return request({
    url: '/api/ai/generate-description',
    method: 'post',
    data
  })
}
