<template>
  <div class="min-h-screen bg-ai-bg">
    <AIHeader />

    <main class="max-w-2xl mx-auto px-4 sm:px-6 py-8 pb-28">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="font-display font-bold text-2xl sm:text-3xl text-ai-text">发布闲置</h1>
        <p class="text-sm text-ai-text-3 mt-1">填写商品信息，AI 可以帮你自动生成描述</p>
      </div>

      <!-- Form card -->
      <div class="bg-white border border-ai-border rounded-3xl p-5 sm:p-8 shadow-warm">
        <form @submit.prevent="handleSubmit" class="space-y-5 sm:space-y-6">

          <!-- Title with AI assist -->
          <div>
            <label class="block text-sm font-semibold text-ai-text mb-1.5">
              商品标题 <span class="text-ai-danger">*</span>
            </label>
            <div class="flex gap-2">
              <input
                v-model="form.title"
                placeholder="例如：iPhone 13 95新 校园自用"
                maxlength="100"
                class="flex-1 px-4 py-2.5 rounded-xl bg-ai-bg border border-ai-border text-sm text-ai-text outline-none focus:border-ai-accent/40 transition-colors placeholder:text-ai-text-3/50"
              />
              <button
                type="button"
                @click="goAI"
                class="flex-shrink-0 px-3 rounded-xl text-xs font-semibold bg-ai-accent-soft text-ai-accent border border-ai-accent-border hover:bg-amber-100 transition-all flex items-center gap-1.5"
              >
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
                AI 生成
              </button>
            </div>
          </div>

          <!-- Category + Price row -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-semibold text-ai-text mb-1.5">
                分类 <span class="text-ai-danger">*</span>
              </label>
              <select
                v-model="form.category"
                class="w-full px-4 py-2.5 rounded-xl bg-ai-bg border border-ai-border text-sm text-ai-text outline-none focus:border-ai-accent/40 transition-colors appearance-none cursor-pointer"
              >
                <option value="" disabled>选择分类</option>
                <option v-for="c in CATEGORIES" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-semibold text-ai-text mb-1.5">
                价格 (¥) <span class="text-ai-danger">*</span>
              </label>
              <input
                v-model.number="form.price"
                type="number"
                step="0.01"
                min="0.01"
                placeholder="0.00"
                class="w-full px-4 py-2.5 rounded-xl bg-ai-bg border border-ai-border text-sm text-ai-text outline-none focus:border-ai-accent/40 transition-colors placeholder:text-ai-text-3/50"
              />
            </div>
          </div>

          <!-- Original price (optional) -->
          <div>
            <label class="block text-sm font-semibold text-ai-text mb-1.5">
              原价 <span class="text-ai-text-3 font-normal">(可选)</span>
            </label>
            <input
              v-model.number="form.originalPrice"
              type="number"
              step="0.01"
              min="0"
              placeholder="0.00"
              class="w-full px-4 py-2.5 rounded-xl bg-ai-bg border border-ai-border text-sm text-ai-text outline-none focus:border-ai-accent/40 transition-colors placeholder:text-ai-text-3/50 sm:max-w-[200px]"
            />
          </div>

          <!-- Images -->
          <div>
            <label class="block text-sm font-semibold text-ai-text mb-1.5">商品图片</label>
            <p class="text-xs text-ai-text-3 mb-2">输入图片 URL，多个链接请用逗号分隔</p>
            <input
              v-model="form.images"
              placeholder="https://example.com/image1.jpg"
              class="w-full px-4 py-2.5 rounded-xl bg-ai-bg border border-ai-border text-sm text-ai-text outline-none focus:border-ai-accent/40 transition-colors placeholder:text-ai-text-3/50"
            />
            <!-- Image previews -->
            <div v-if="imageUrls.length > 0" class="flex gap-2 mt-3 flex-wrap">
              <div
                v-for="(url, idx) in imageUrls"
                :key="idx"
                class="relative w-16 h-16 rounded-xl overflow-hidden border border-ai-border group"
              >
                <img :src="url" class="w-full h-full object-cover" @error="$event.target.style.display='none'" />
                <button
                  type="button"
                  @click="removeImage(idx)"
                  class="absolute top-0.5 right-0.5 w-5 h-5 rounded-full bg-black/40 text-white flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <!-- Description with AI hint -->
          <div>
            <label class="block text-sm font-semibold text-ai-text mb-1.5">商品描述</label>
            <textarea
              v-model="form.description"
              rows="5"
              placeholder="描述商品的使用情况、成色、交易方式等..."
              class="w-full px-4 py-2.5 rounded-xl bg-ai-bg border border-ai-border text-sm text-ai-text outline-none focus:border-ai-accent/40 transition-colors placeholder:text-ai-text-3/50 resize-y"
            ></textarea>
            <div class="flex items-center justify-between mt-1.5">
              <span class="text-xs text-ai-text-3">建议填写详细描述，有助于更快成交</span>
              <button
                type="button"
                @click="goAI"
                class="text-xs font-medium text-ai-accent hover:underline flex items-center gap-1"
              >
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
                用 AI 生成描述
              </button>
            </div>
          </div>

          <!-- Submit -->
          <div class="flex items-center gap-3 pt-2">
            <button
              type="submit"
              :disabled="submitting"
              class="flex-1 sm:flex-none px-8 py-2.5 rounded-xl text-sm font-semibold bg-ai-text text-white hover:bg-gray-800 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="submitting" class="flex items-center justify-center gap-2">
                <div class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                发布中...
              </span>
              <span v-else>发布商品</span>
            </button>
            <button
              type="button"
              @click="resetForm"
              class="px-6 py-2.5 rounded-xl text-sm font-medium text-ai-text-2 hover:text-ai-text border border-ai-border hover:bg-ai-surface-2 transition-all"
            >
              重置
            </button>
          </div>
        </form>
      </div>
    </main>

    <AICommandBar />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createProduct } from '../api/product'
import { CATEGORIES } from '../utils'
import AIHeader from '../components/ai/AIHeader.vue'
import AICommandBar from '../components/ai/AICommandBar.vue'

const router = useRouter()
const route = useRoute()
const submitting = ref(false)

const form = reactive({
  title: '',
  category: '',
  price: undefined,
  originalPrice: undefined,
  images: '',
  description: '',
})

// Image URL previews
const imageUrls = computed(() => {
  return form.images
    ? form.images.split(/[,，]/).map(s => s.trim()).filter(Boolean)
    : []
})

function removeImage(idx) {
  const urls = form.images.split(/[,，]/).map(s => s.trim()).filter(Boolean)
  urls.splice(idx, 1)
  form.images = urls.join(', ')
}

// Pre-fill from AI page
onMounted(() => {
  if (route.query.title) form.title = route.query.title
  if (route.query.description) form.description = route.query.description
})

async function handleSubmit() {
  // Simple validation
  if (!form.title.trim()) {
    ElMessage.warning('请输入商品标题')
    return
  }
  if (!form.category) {
    ElMessage.warning('请选择商品分类')
    return
  }
  if (!form.price || form.price <= 0) {
    ElMessage.warning('请输入有效价格')
    return
  }

  submitting.value = true
  try {
    await createProduct({
      ...form,
      originalPrice: form.originalPrice || undefined,
    })
    ElMessage.success('发布成功！AI 正在分析商品热度...')
    router.push('/products')
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  form.title = ''
  form.category = ''
  form.price = undefined
  form.originalPrice = undefined
  form.images = ''
  form.description = ''
}

function goAI() {
  router.push({ path: '/ai', query: { title: form.title, description: form.description } })
}
</script>
