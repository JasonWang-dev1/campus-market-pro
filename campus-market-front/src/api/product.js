import request from './request'

/** 分页查询商品列表 */
export function getProductList(params) {
  return request({
    url: '/api/products',
    method: 'get',
    params
  })
}

/** 获取商品详情 */
export function getProductDetail(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'get'
  })
}

/** 发布商品 */
export function createProduct(data) {
  return request({
    url: '/api/products',
    method: 'post',
    data
  })
}

/** 更新商品 */
export function updateProduct(id, data) {
  return request({
    url: `/api/products/${id}`,
    method: 'put',
    data
  })
}

/** 删除商品 */
export function deleteProduct(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'delete'
  })
}
