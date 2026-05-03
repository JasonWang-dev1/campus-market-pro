<template>
  <article
    class="card-warm cursor-pointer group overflow-hidden"
    :class="[size === 'sm' ? 'rounded-xl' : 'rounded-2xl']"
    @click="$router.push(`/products/${product.id}`)"
  >
    <!-- Image -->
    <div
      class="relative overflow-hidden bg-ai-surface-2"
      :class="size === 'sm' ? 'h-32' : 'h-44 sm:h-52'"
    >
      <img
        v-if="imgSrc"
        :src="imgSrc"
        :alt="product.title"
        class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
        loading="lazy"
      />
      <div v-else class="w-full h-full flex items-center justify-center text-ai-text-3">
        <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
        </svg>
      </div>
      <!-- Category tag -->
      <span v-if="product.category" class="absolute top-2.5 left-2.5 px-2 py-0.5 rounded-md text-[10px] font-semibold bg-white/80 backdrop-blur-sm text-ai-text-2">
        {{ product.category }}
      </span>
    </div>

    <!-- Info -->
    <div class="p-3 sm:p-4">
      <h3 class="font-semibold text-ai-text text-sm sm:text-base leading-snug text-truncate group-hover:text-ai-accent transition-colors">
        {{ product.title }}
      </h3>
      <div class="flex items-baseline gap-1.5 mt-1.5">
        <span class="text-lg sm:text-xl font-bold text-ai-danger">¥{{ product.price }}</span>
        <span v-if="product.originalPrice" class="text-xs text-ai-text-3 line-through">¥{{ product.originalPrice }}</span>
      </div>
      <div class="flex items-center justify-between mt-2 text-xs text-ai-text-3">
        <span>{{ product.sellerName || '校园用户' }}</span>
        <span>{{ product.viewCount || 0 }} 次浏览</span>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  product: { type: Object, required: true },
  size: { type: String, default: 'md' }, // 'sm' | 'md'
})

const router = useRouter()

const imgSrc = computed(() => {
  if (!props.product.images) return ''
  try {
    const arr = JSON.parse(props.product.images)
    return Array.isArray(arr) && arr.length > 0 ? arr[0] : ''
  } catch {
    return props.product.images.split(',')[0]?.trim() || ''
  }
})
</script>
