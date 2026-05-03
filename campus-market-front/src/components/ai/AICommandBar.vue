<template>
  <div class="fixed bottom-6 sm:bottom-8 left-1/2 -translate-x-1/2 z-40 w-[calc(100%-2rem)] sm:w-full sm:max-w-lg">
    <div class="relative group">
      <!-- Glow effect behind -->
      <div class="absolute -inset-1 rounded-2xl bg-gradient-to-r from-ai-accent/20 via-ai-accent/10 to-ai-accent/20 opacity-0 group-hover:opacity-100 blur-xl transition-opacity duration-500"></div>

      <!-- Input -->
      <div class="relative flex items-center bg-white border-2 border-ai-border-2 rounded-2xl shadow-warm-lg transition-all duration-300 group-hover:border-ai-accent/30 group-hover:shadow-warm-xl">
        <svg class="w-5 h-5 text-ai-text-3 ml-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
        <input
          v-model="query"
          type="text"
          :placeholder="placeholder"
          class="flex-1 bg-transparent px-3 py-3.5 sm:py-3 text-sm text-ai-text outline-none placeholder:text-ai-text-3/60"
          @keydown.enter="handleSearch"
        />
        <!-- Spark icon (AI) -->
        <button
          @click="handleAIAssist"
          class="flex items-center gap-1 mr-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-ai-accent bg-ai-accent-soft hover:bg-amber-100 transition-all animate-spark"
          title="AI 智能搜索"
        >
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
          </svg>
          <span class="hidden sm:inline">AI</span>
        </button>
      </div>

      <!-- Suggestions -->
      <div v-if="suggestions.length" class="absolute bottom-full left-0 right-0 mb-2 flex flex-wrap gap-1.5">
        <button
          v-for="s in suggestions"
          :key="s"
          @click="handleSuggestion(s)"
          class="px-3 py-1 rounded-lg text-[11px] font-medium bg-white/80 backdrop-blur-sm border border-ai-border text-ai-text-2 hover:text-ai-accent hover:border-ai-accent/30 transition-all"
        >
          {{ s }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const query = ref('')

defineProps({
  placeholder: { type: String, default: '搜索商品...' },
  suggestions: { type: Array, default: () => [] },
})

function handleSearch() {
  if (!query.value.trim()) return
  router.push({ path: '/products', query: { keyword: query.value.trim() } })
}

function handleSuggestion(text) {
  router.push({ path: '/products', query: { keyword: text } })
}

function handleAIAssist() {
  if (query.value.trim()) {
    router.push({ path: '/agent', query: { prompt: query.value.trim() } })
  } else {
    router.push('/agent')
  }
}
</script>
