<template>
  <div class="product-list-page">
    <Header />

    <main class="pl-main">
      <!-- 筛选栏 -->
      <div class="pl-header">
        <h1>商品列表</h1>
        <div class="pl-filters">
          <el-select
            v-model="filters.category"
            placeholder="全部分类"
            clearable
            @change="onSearch"
          >
            <el-option label="全部分类" value="" />
            <el-option v-for="c in CATEGORIES" :key="c" :label="c" :value="c" />
          </el-select>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索商品..."
            :prefix-icon="Search"
            clearable
            class="search-input"
            @clear="onSearch"
            @keyup.enter="onSearch"
          />
          <el-button type="primary" :icon="Search" @click="onSearch">搜索</el-button>
        </div>
      </div>

      <!-- 加载态 -->
      <div v-if="loading" class="status-center">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      </div>

      <!-- 空态 -->
      <div v-else-if="products.length === 0" class="status-center">
        <el-empty description="暂无符合条件的商品" />
      </div>

      <!-- 商品网格 -->
      <div v-else class="pl-grid">
        <el-card
          v-for="item in products"
          :key="item.id"
          shadow="hover"
          class="pl-card"
          @click="showDetail(item)"
        >
          <div class="pl-card-img">
            <el-image :src="firstImage(item.images)" fit="cover">
              <template #error>
                <div class="img-placeholder">
                  <el-icon :size="36" color="#c0c4cc"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          <div class="pl-card-body">
            <h3>{{ item.title }}</h3>
            <p class="pl-price">¥{{ item.price }}</p>
            <div class="pl-meta">
              <span>{{ item.sellerName || '未知' }}</span>
              <span>{{ item.viewCount || 0 }}浏览</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pl-pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="fetchData"
        />
      </div>
    </main>

    <!-- 详情弹窗 -->
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
import { ref, reactive, onMounted } from 'vue'
import { Search, Picture, Loading } from '@element-plus/icons-vue'
import Header from '../components/Header.vue'
import { getProductList, getProductDetail } from '../api/product'
import { firstImage, imageList, formatTime, CATEGORIES } from '../utils'

// ========== 状态 ==========
const products = ref([])
const loading = ref(true)
const total = ref(0)

const filters = reactive({
  category: '',
  keyword: ''
})

const pagination = reactive({
  page: 1,
  size: 12
})

// ========== 数据加载 ==========
async function fetchData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...(filters.category && { category: filters.category }),
      ...(filters.keyword && { keyword: filters.keyword })
    }
    const res = await getProductList(params)
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pagination.page = 1
  fetchData()
}

onMounted(fetchData)

// ========== 详情 ==========
const detailVisible = ref(false)
const detail = ref({})

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
.product-list-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.pl-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
}

.pl-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.pl-header h1 {
  font-size: 24px;
}

.pl-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.search-input {
  width: 240px;
}

.pl-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.pl-card {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
}

.pl-card:hover {
  transform: translateY(-4px);
}

.pl-card-img {
  height: 180px;
  background: #f5f7fa;
}

.pl-card-img .el-image {
  width: 100%;
  height: 100%;
}

.img-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pl-card-body {
  padding: 12px 0;
}

.pl-card-body h3 {
  font-size: 15px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pl-price {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
  margin: 8px 0;
}

.pl-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

.pl-pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.status-center {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

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
</style>
