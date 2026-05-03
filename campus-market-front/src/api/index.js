/**
 * API 统一出口
 * 各业务模块 API 统一从这里导出
 * 新增模块时在此文件添加 export
 */
export { loginApi, registerApi } from './auth'
export {
  getProductList,
  getProductDetail,
  createProduct,
  updateProduct,
  deleteProduct
} from './product'
export { generateDescription } from './ai'
export { executeAgent } from './agent'

/* ---- 后续模块在此扩展 ---- */
// export { ... } from './user-center'
// export { ... } from './recommend'
