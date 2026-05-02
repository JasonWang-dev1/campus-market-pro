<template>
  <div class="home">
    <Header />

    <main class="home-main">
      <!-- Banner -->
      <section class="banner">
        <div class="banner-text">
          <h1>校园二手交易平台</h1>
          <p>闲置物品轻松卖，实惠好物放心买</p>
          <div class="banner-actions">
            <el-button type="primary" size="large" @click="router.push('/products')">
              浏览商品
            </el-button>
            <el-button
              v-if="userStore.isLoggedIn"
              size="large"
              @click="router.push('/products/add')"
            >
              发布闲置
            </el-button>
          </div>
        </div>
        <div class="banner-illustration">
          <el-icon :size="180" color="#409eff10"><ShoppingCart /></el-icon>
        </div>
      </section>

      <!-- 快捷入口 -->
      <section class="quick-entry">
        <el-card
          v-for="item in entries"
          :key="item.title"
          shadow="hover"
          class="entry-card"
          @click="router.push(item.path)"
        >
          <el-icon :size="36" :color="item.color">
            <component :is="item.icon" />
          </el-icon>
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
        </el-card>
      </section>

      <!-- 最新商品 -->
      <section class="latest">
        <div class="section-header">
          <h2>最新商品</h2>
          <el-button link @click="router.push('/products')">查看全部 →</el-button>
        </div>

        <div v-if="loading" class="status-center">
          <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        </div>

        <div v-else-if="products.length === 0" class="status-center">
          <el-empty description="暂无商品" />
        </div>

        <div v-else class="product-grid">
          <el-card
            v-for="item in products"
            :key="item.id"
            shadow="hover"
            class="product-card"
            @click="showDetail(item)"
          >
            <div class="product-image">
              <el-image :src="firstImage(item.images)" fit="cover">
                <template #error>
                  <div class="img-placeholder">
                    <el-icon :size="40" color="#c0c4cc"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ item.title }}</h3>
              <p class="product-price">¥{{ item.price }}</p>
              <div class="product-meta">
                <span>{{ item.sellerName || '未知用户' }}</span>
                <span>{{ item.viewCount || 0 }}次浏览</span>
              </div>
            </div>
          </el-card>
        </div>
      </section>
    </main>

    <!-- 商品详情对话框 -->
    <el-dialog v-model="detailVisible" :title="detail.title" width="640px" destroy-on-close>
      <template v-if="detail.id">
        <div class="detail-images">
          <el-image
            v-for="(img, idx) in imageList(detail.images)"
            :key="idx"
            :src="img"
            fit="contain"
            class="detail-img"
          >
            <template #error>
              <div class="img-placeholder">
                <el-icon :size="36" color="#c0c4cc"><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
        <div class="detail-price">¥{{ detail.price }}</div>
        <div class="detail-desc">{{ detail.description || '暂无描述' }}</div>
        <div class="detail-meta">
          <span>卖家：{{ detail.sellerName || '未知' }}</span>
          <span>分类：{{ detail.category || '未分类' }}</span>
          <span>浏览：{{ detail.viewCount || 0 }}次</span>
          <span>{{ formatTime(detail.createTime) }}</span>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingCart, Picture, Loading, Goods, ChatDotSquare, EditPen } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Header from '../components/Header.vue'
import { useUserStore } from '../stores/user'
import { getProductList, getProductDetail } from '../api/product'
import { firstImage, imageList, formatTime } from '../utils'

// ========== 常量 ==========
const entries = [
  { title: '浏览商品', desc: '发现校园好物', icon: Goods, color: '#409eff', path: '/products' },
  { title: '发布闲置', desc: '轻松出售物品', icon: EditPen, color: '#67c23a', path: '/products/add' },
  { title: 'AI 助手', desc: '智能生成描述', icon: ChatDotSquare, color: '#e6a23c', path: '/ai' }
]

// ========== 状态 ==========
const router = useRouter()
const userStore = useUserStore()
const products = ref([])
const loading = ref(true)
const detailVisible = ref(false)
const detail = ref({})

// ========== 数据加载 ==========
async function fetchProducts() {
  loading.value = true
  try {
    const res = await getProductList({ page: 1, size: 8 })
    products.value = res.data?.records || []
  } catch {
    products.value = []
  } finally {
    loading.value = false
  }
}

onMounted(fetchProducts)

// ========== 详情 ==========
async function showDetail(item) {
  try {
    const res = await getProductDetail(item.id)
    detail.value = res.data
  } catch {
    detail.value = item
  }
  detailVisible.value = true
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f7fa;
}

.home-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
}

/* Banner */
.banner {
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border-radius: 16px;
  padding: 48px 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
  margin-bottom: 32px;
}

.banner-text h1 {
  font-size: 36px;
  margin-bottom: 12px;
}

.banner-text p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 24px;
}

.banner-actions {
  display: flex;
  gap: 12px;
}

.banner-actions .el-button--primary {
  background: #fff;
  color: #409eff;
  border: none;
}

.banner-actions .el-button--default {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

/* 快捷入口 */
.quick-entry {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.entry-card {
  cursor: pointer;
  text-align: center;
  padding: 24px;
  transition: transform 0.2s;
}

.entry-card:hover {
  transform: translateY(-4px);
}

.entry-card h3 {
  margin: 12px 0 4px;
  font-size: 18px;
}

.entry-card p {
  font-size: 13px;
  color: #909399;
}

/* 最新商品 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 22px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);
}

.product-image {
  height: 180px;
  overflow: hidden;
  background: #f5f7fa;
}

.product-image .el-image {
  width: 100%;
  height: 100%;
}

.img-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-info {
  padding: 12px 0;
}

.product-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
  margin: 8px 0;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

/* 详情 */
.detail-images {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.detail-img {
  width: 180px;
  height: 180px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.detail-price {
  font-size: 28px;
  font-weight: 700;
  color: #f56c6c;
  margin-bottom: 12px;
}

.detail-desc {
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 16px;
  white-space: pre-wrap;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #909399;
}

.status-center {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}
</style>
