<template>
  <div class="min-h-screen bg-ai-bg">
    <AIHeader />

    <main class="pb-28 sm:pb-32">
      <!-- Hero -->
      <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-12 sm:pt-20 pb-10 sm:pb-16 text-center">
        <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-ai-accent-soft border border-ai-accent-border text-ai-accent text-xs font-semibold mb-6 animate-fade-in">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
          </svg>
          AI 驱动的校园二手平台
        </div>
        <h1 class="font-display font-bold text-4xl sm:text-5xl lg:text-6xl text-ai-text leading-[1.1] tracking-tight">
          买卖闲置
          <span class="text-ai-accent">更聪明</span>
        </h1>
        <p class="mt-4 text-base sm:text-lg text-ai-text-2 max-w-md mx-auto leading-relaxed">
          用自然语言告诉 AI 你想买什么或卖什么，剩下的交给我们
        </p>

        <!-- Category quick links -->
        <div class="flex flex-wrap justify-center gap-2 mt-8">
          <button
            v-for="cat in categories"
            :key="cat"
            @click="router.push({ path: '/products', query: { category: cat } })"
            class="px-4 py-2 rounded-xl text-sm font-medium bg-white border border-ai-border text-ai-text-2 hover:text-ai-text hover:border-ai-border-2 hover:shadow-warm transition-all"
          >
            {{ cat }}
          </button>
        </div>
      </section>

      <!-- AI Recommended -->
      <section v-if="aiProducts.length" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mb-10 sm:mb-14">
        <div class="flex items-center gap-2.5 mb-5">
          <div class="w-7 h-7 rounded-lg bg-ai-accent-soft flex items-center justify-center">
            <svg class="w-4 h-4 text-ai-accent" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
            </svg>
          </div>
          <h2 class="font-display font-semibold text-lg text-ai-text">AI 为你推荐</h2>
        </div>
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-4">
          <ProductCard
            v-for="(item, i) in aiProducts"
            :key="item.id"
            :product="item"
            size="md"
            class="animate-card-in"
            :style="{ animationDelay: (i * 80) + 'ms' }"
          />
        </div>
      </section>

      <!-- Latest listings -->
      <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between mb-5">
          <h2 class="font-display font-semibold text-lg text-ai-text">最新发布</h2>
          <router-link to="/products" class="text-sm font-medium text-ai-text-2 hover:text-ai-accent transition-colors">
            查看全部 →
          </router-link>
        </div>

        <div v-if="loading" class="flex justify-center py-16">
          <div class="w-6 h-6 border-2 border-ai-border border-t-ai-accent rounded-full animate-spin"></div>
        </div>

        <div v-else-if="products.length === 0" class="text-center py-16">
          <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-ai-surface-2 flex items-center justify-center">
            <svg class="w-8 h-8 text-ai-text-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
            </svg>
          </div>
          <p class="text-sm text-ai-text-3">还没有商品，快来发布第一件闲置吧</p>
          <router-link v-if="userStore.isLoggedIn" to="/products/add" class="inline-block mt-4 px-5 py-2 rounded-xl text-sm font-semibold bg-ai-text text-white hover:bg-gray-800 transition-all">
            发布闲置
          </router-link>
        </div>

        <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-4">
          <ProductCard
            v-for="(item, i) in products"
            :key="item.id"
            :product="item"
            size="md"
            class="animate-card-in"
            :style="{ animationDelay: (i * 60) + 'ms' }"
          />
        </div>
      </section>
    </main>

    <!-- Floating AI Command Bar -->
    <AICommandBar
      :suggestions="['二手iPhone', '教科书', '数码产品']"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getProductList } from '../api/product'
import AIHeader from '../components/ai/AIHeader.vue'
import ProductCard from '../components/ai/ProductCard.vue'
import AICommandBar from '../components/ai/AICommandBar.vue'

const router = useRouter()
const userStore = useUserStore()
const products = ref([])
const aiProducts = ref([])
const loading = ref(true)
const categories = ['数码', '书籍', '生活用品', '服饰']

async function fetchData() {
  loading.value = true
  try {
    // Latest products
    const res = await getProductList({ page: 1, size: 8, sort: 'newest' })
    products.value = res.data?.records || []

    // AI recommended (same API for now, could use a dedicated AI endpoint)
    const aiRes = await getProductList({ page: 1, size: 4, sort: 'views' })
    aiProducts.value = aiRes.data?.records || []
  } catch {
    // Silent fallback
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>
