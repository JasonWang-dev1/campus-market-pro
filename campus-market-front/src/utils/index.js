/**
 * 通用工具函数
 */

/**
 * 解析商品首图
 * @param {string} images - JSON数组字符串 或 逗号分隔的URL
 * @returns {string} 首图URL，无图返回空字符串
 */
export function firstImage(images) {
  if (!images) return ''
  try {
    const arr = JSON.parse(images)
    return Array.isArray(arr) && arr.length > 0 ? arr[0] : ''
  } catch {
    return images.split(',')[0]?.trim() || ''
  }
}

/**
 * 解析图片列表
 * @param {string} images - JSON数组字符串 或 逗号分隔的URL
 * @returns {string[]}
 */
export function imageList(images) {
  if (!images) return []
  try {
    const arr = JSON.parse(images)
    return Array.isArray(arr) ? arr : []
  } catch {
    return images.split(',').map(s => s.trim()).filter(Boolean)
  }
}

/**
 * 格式化时间
 * @param {string} t - ISO时间字符串
 * @returns {string} 格式: "2026-05-02 21:00"
 */
export function formatTime(t) {
  if (!t) return ''
  return t.slice(0, 16).replace('T', ' ')
}

/**
 * 分类选项（与后端保持一致）
 */
export const CATEGORIES = ['数码', '书籍', '生活用品', '服饰', '体育', '其他']
