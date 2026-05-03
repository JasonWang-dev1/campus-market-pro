<template>
  <div class="min-h-screen bg-ai-bg">
    <AIHeader />

    <main class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 pb-28">
      <!-- Back link -->
      <router-link to="/products" class="inline-flex items-center gap-1.5 text-sm text-ai-text-2 hover:text-ai-accent transition-colors mb-6">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
        </svg>
        返回列表
      </router-link>

      <!-- Loading -->
      <div v-if="loading" class="flex justify-center py-24">
        <div class="w-6 h-6 border-2 border-ai-border border-t-ai-accent rounded-full animate-spin"></div>
      </div>

      <!-- Not found -->
      <div v-else-if="!product.id" class="text-center py-24">
        <div class="w-20 h-20 mx-auto mb-5 rounded-3xl bg-ai-surface-2 flex items-center justify-center">
          <svg class="w-10 h-10 text-ai-text-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <p class="text-sm text-ai-text-3">商品不存在或已下架</p>
        <router-link to="/products" class="inline-block mt-4 text-sm font-medium text-ai-accent hover:underline">浏览其他商品</router-link>
      </div>

      <!-- Content -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-5 gap-8 lg:gap-10">
        <!-- Images column -->
        <div class="lg:col-span-3">
          <!-- Main image -->
          <div class="aspect-[4/3] rounded-2xl overflow-hidden bg-ai-surface-2 border border-ai-border mb-3">
            <img
              v-if="mainImage"
              :src="mainImage"
              :alt="product.title"
              class="w-full h-full object-contain"
            />
            <div v-else class="w-full h-full flex items-center justify-center text-ai-text-3">
              <svg class="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
              </svg>
            </div>
          </div>
          <!-- Thumbnails -->
          <div v-if="allImages.length > 1" class="flex gap-2 overflow-x-auto">
            <button
              v-for="(img, idx) in allImages"
              :key="idx"
              @click="currentImage = idx"
              class="flex-shrink-0 w-16 h-16 rounded-xl overflow-hidden border-2 transition-all"
              :class="idx === currentImage ? 'border-ai-accent' : 'border-transparent opacity-60 hover:opacity-100'"
            >
              <img :src="img" class="w-full h-full object-cover" />
            </button>
          </div>
        </div>

        <!-- Info column -->
        <div class="lg:col-span-2">
          <!-- Category + AI badge -->
          <div class="flex items-center gap-2 mb-3">
            <span class="px-2.5 py-0.5 rounded-md text-[11px] font-semibold bg-ai-surface-2 text-ai-text-2">{{ product.category || '未分类' }}</span>
            <span v-if="product.aiGenerated" class="ai-badge">
              <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
              </svg>
              AI 生成
            </span>
          </div>

          <!-- Title -->
          <h1 class="font-display font-bold text-2xl sm:text-3xl text-ai-text leading-tight">{{ product.title }}</h1>

          <!-- Price -->
          <div class="flex items-baseline gap-2 mt-4">
            <span class="text-3xl font-bold text-ai-danger">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="text-sm text-ai-text-3 line-through">¥{{ product.originalPrice }}</span>
          </div>

          <hr class="my-5 border-ai-border" />

          <!-- Description -->
          <div>
            <h3 class="text-sm font-semibold text-ai-text mb-2">商品描述</h3>
            <p class="text-sm text-ai-text-2 leading-relaxed whitespace-pre-wrap">{{ product.description || '暂无描述' }}</p>
          </div>

          <hr class="my-5 border-ai-border" />

          <!-- Meta info -->
          <div class="space-y-2.5">
            <div class="flex items-center justify-between text-sm">
              <span class="text-ai-text-3">卖家</span>
              <span class="font-medium text-ai-text">{{ product.sellerName || '校园用户' }}</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-ai-text-3">分类</span>
              <span class="text-ai-text-2">{{ product.category || '未分类' }}</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-ai-text-3">浏览</span>
              <span class="text-ai-text-2">{{ product.viewCount || 0 }} 次</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-ai-text-3">发布时间</span>
              <span class="text-ai-text-2">{{ formatTime(product.createTime) }}</span>
            </div>
          </div>

          <!-- AI Actions -->
          <div class="mt-6 flex flex-col gap-2">
            <router-link
              to="/agent"
              class="flex items-center justify-center gap-2 py-2.5 rounded-xl text-sm font-semibold bg-ai-accent-soft text-ai-accent border border-ai-accent-border hover:bg-amber-100 transition-all"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
              </svg>
              向 AI 助手咨询
            </router-link>
          </div>
        </div>
      </div>
    </main>

    <AICommandBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductDetail } from '../api/product'
import { imageList, formatTime } from '../utils'
import AIHeader from '../components/ai/AIHeader.vue'
import AICommandBar from '../components/ai/AICommandBar.vue'

const route = useRoute()
const product = ref({})
const loading = ref(true)
const currentImage = ref(0)

const allImages = computed(() => imageList(product.value.images))
const mainImage = computed(() => allImages.value[currentImage.value] || allImages.value[0] || '')

onMounted(async () => {
  const id = route.params.id
  if (!id) {
    loading.value = false
    return
  }
  try {
    const res = await getProductDetail(id)
    product.value = res.data || {}
  } catch {
    product.value = {}
  } finally {
    loading.value = false
  }
})
</script>
