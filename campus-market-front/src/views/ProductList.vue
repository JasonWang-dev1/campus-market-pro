<template>
  <div class="min-h-screen bg-ai-bg">
    <AIHeader />

    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-10 pb-28">
      <!-- Page header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <div>
          <h1 class="font-display font-bold text-2xl sm:text-3xl text-ai-text">商品广场</h1>
          <p v-if="total" class="text-sm text-ai-text-3 mt-1">共 {{ total }} 件商品</p>
        </div>

        <!-- Filters -->
        <div class="flex items-center gap-2">
          <select
            v-model="filters.category"
            @change="onSearch"
            class="px-3.5 py-2 rounded-xl bg-white border border-ai-border text-sm text-ai-text outline-none focus:border-ai-accent/40 transition-colors appearance-none cursor-pointer"
          >
            <option value="">全部分类</option>
            <option v-for="c in CATEGORIES" :key="c" :value="c">{{ c }}</option>
          </select>
          <div class="relative flex-1 sm:flex-initial">
            <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-ai-text-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
            </svg>
            <input
              v-model="filters.keyword"
              placeholder="搜索商品..."
              class="pl-9 pr-3 py-2 rounded-xl bg-white border border-ai-border text-sm text-ai-text outline-none focus:border-ai-accent/40 transition-colors w-full sm:w-52"
              @keydown.enter="onSearch"
            />
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="flex justify-center py-20">
        <div class="w-6 h-6 border-2 border-ai-border border-t-ai-accent rounded-full animate-spin"></div>
      </div>

      <!-- Empty -->
      <div v-else-if="products.length === 0" class="text-center py-20">
        <div class="w-20 h-20 mx-auto mb-5 rounded-3xl bg-ai-surface-2 flex items-center justify-center">
          <svg class="w-10 h-10 text-ai-text-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
          </svg>
        </div>
        <p class="text-sm text-ai-text-3">没有找到匹配的商品</p>
        <button @click="resetFilters" class="mt-3 text-sm font-medium text-ai-accent hover:underline">清除筛选条件</button>
      </div>

      <!-- Product grid -->
      <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-4">
        <ProductCard
          v-for="(item, i) in products"
          :key="item.id"
          :product="item"
          class="animate-card-in"
          :style="{ animationDelay: (i * 60) + 'ms' }"
        />
      </div>

      <!-- Pagination -->
      <div v-if="total > pagination.size" class="flex justify-center mt-10 gap-1.5">
        <button
          :disabled="pagination.page <= 1"
          @click="goPage(pagination.page - 1)"
          class="w-9 h-9 rounded-xl text-sm font-medium border border-ai-border text-ai-text-2 hover:bg-ai-surface-2 transition-all disabled:opacity-30 disabled:cursor-not-allowed"
        >
          ←
        </button>
        <button
          v-for="p in visiblePages"
          :key="p"
          @click="goPage(p)"
          class="min-w-[36px] h-9 px-2 rounded-xl text-sm font-medium transition-all"
          :class="p === pagination.page
            ? 'bg-ai-text text-white'
            : 'border border-ai-border text-ai-text-2 hover:bg-ai-surface-2'"
        >
          {{ p }}
        </button>
        <button
          :disabled="pagination.page >= totalPages"
          @click="goPage(pagination.page + 1)"
          class="w-9 h-9 rounded-xl text-sm font-medium border border-ai-border text-ai-text-2 hover:bg-ai-surface-2 transition-all disabled:opacity-30 disabled:cursor-not-allowed"
        >
          →
        </button>
      </div>
    </main>

    <!-- AI Command Bar -->
    <AICommandBar
      :suggestions="['二手iPhone', '教科书', '数码产品']"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductList } from '../api/product'
import { CATEGORIES } from '../utils'
import AIHeader from '../components/ai/AIHeader.vue'
import ProductCard from '../components/ai/ProductCard.vue'
import AICommandBar from '../components/ai/AICommandBar.vue'

const route = useRoute()
const products = ref([])
const loading = ref(true)
const total = ref(0)

const filters = reactive({
  category: '',
  keyword: '',
})

const pagination = reactive({
  page: 1,
  size: 12,
})

const totalPages = computed(() => Math.ceil(total.value / pagination.size))

const visiblePages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = pagination.page
  let start = Math.max(1, current - 2)
  let end = Math.min(total, current + 2)
  if (end - start < 4) {
    if (start === 1) end = Math.min(total, start + 4)
    else start = Math.max(1, end - 4)
  }
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

async function fetchData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...(filters.category && { category: filters.category }),
      ...(filters.keyword && { keyword: filters.keyword }),
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

function goPage(p) {
  if (p < 1 || p > totalPages.value) return
  pagination.page = p
  fetchData()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function resetFilters() {
  filters.category = ''
  filters.keyword = ''
  onSearch()
}

// Handle query params from search bar
onMounted(() => {
  if (route.query.keyword) {
    filters.keyword = route.query.keyword
  }
  if (route.query.category) {
    filters.category = route.query.category
  }
  fetchData()
})
</script>
