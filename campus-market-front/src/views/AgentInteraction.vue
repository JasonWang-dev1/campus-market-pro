<template>
  <div class="h-screen w-full overflow-hidden bg-[#0a0e1a] text-white flex flex-col font-sans relative">
    <!-- ===== ANIMATED BACKGROUND ===== -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none select-none">
      <!-- Gradient orb 1 -->
      <div class="absolute -top-32 -left-32 w-[500px] h-[500px] rounded-full opacity-15 animate-drift-slow"
        style="background: radial-gradient(circle at center, #06b6d4 0%, transparent 70%);">
      </div>
      <!-- Gradient orb 2 -->
      <div class="absolute -bottom-40 -right-20 w-[600px] h-[600px] rounded-full opacity-10 animate-drift"
        style="background: radial-gradient(circle at center, #3b82f6 0%, transparent 70%);">
      </div>
      <!-- Gradient orb 3 -->
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[400px] opacity-[0.04]"
        style="background: radial-gradient(ellipse at center, #8b5cf6 0%, transparent 70%);">
      </div>
      <!-- Grid pattern -->
      <div class="absolute inset-0 opacity-[0.04]"
        style="background-image:
          linear-gradient(rgba(255,255,255,0.15) 1px, transparent 1px),
          linear-gradient(90deg, rgba(255,255,255,0.15) 1px, transparent 1px);
          background-size: 60px 60px;">
      </div>
      <!-- Noise texture -->
      <div class="absolute inset-0 opacity-[0.015] mix-blend-overlay"
        style="background-image: url(&quot;data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E&quot;);
          background-size: 256px 256px;">
      </div>
    </div>

    <!-- ===== HEADER ===== -->
    <header class="flex-shrink-0 flex items-center justify-between px-6 py-4 border-b border-white/5 relative z-10">
      <div class="flex items-center gap-3">
        <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-cyan-500 to-blue-600 flex items-center justify-center shadow-lg shadow-cyan-500/20">
          <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
          </svg>
        </div>
        <div>
          <h1 class="text-sm font-semibold text-white/90">AI 智能助手</h1>
          <p class="text-[11px] text-white/40 flex items-center gap-1.5">
            <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 shadow-lg shadow-emerald-400/40 animate-pulse"></span>
            {{ statusLabel }}
          </p>
        </div>
      </div>
      <div class="flex items-center gap-2 text-white/30 text-xs">
        <router-link to="/home" class="hidden sm:flex items-center gap-1.5 hover:text-white/60 transition-colors px-2 py-1 rounded-lg hover:bg-white/5">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
          </svg>
          返回首页
        </router-link>
        <span class="text-white/10 hidden sm:inline">|</span>
        <button @click="clearChat" class="hover:text-white/60 transition-colors px-2 py-1 rounded-lg hover:bg-white/5">
          清空对话
        </button>
      </div>
    </header>

    <!-- ===== MESSAGES ===== -->
    <div ref="chatRef" class="flex-1 overflow-y-auto px-4 sm:px-8 py-6 space-y-6 agent-scroll relative z-10">
      <!-- Welcome -->
      <div v-if="messages.length === 0" class="flex flex-col items-center justify-center h-full text-center select-none">
        <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-cyan-500/20 to-blue-600/20 border border-white/5 flex items-center justify-center mb-5 welcome-glow">
          <svg class="w-8 h-8 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M13 10V3L4 14h7v7l9-11h-7z"/>
          </svg>
        </div>
        <h2 class="text-lg font-semibold text-white/70 mb-2">有什么我可以帮你的？</h2>
        <p class="text-sm text-white/30 max-w-md leading-relaxed">
          用自然语言指挥平台，我可以帮你搜索商品、发布闲置、生成商品描述
        </p>
        <div class="flex flex-wrap justify-center gap-2 mt-8 max-w-lg">
          <button
            v-for="s in suggestions"
            :key="s"
            @click="sendMessage(s)"
            class="px-3.5 py-2 rounded-xl text-xs font-medium transition-all duration-200 glass text-white/60 hover:text-white/80 hover:bg-white/[0.07] active:scale-95 glow-hover"
          >
            {{ s }}
          </button>
        </div>
      </div>

      <!-- Message Bubbles -->
      <div
        v-for="(msg, idx) in messages"
        :key="idx"
        class="animate-slide-up"
      >
        <!-- User Message -->
        <div v-if="msg.role === 'user'" class="flex justify-end mb-3">
          <div class="max-w-[75%] sm:max-w-[60%]">
            <div class="bg-gradient-to-r from-blue-600 to-blue-500 text-white rounded-2xl rounded-br-md px-4 py-2.5 text-sm leading-relaxed shadow-lg shadow-blue-600/20">
              {{ msg.content }}
            </div>
          </div>
        </div>

        <!-- Agent Message -->
        <div v-if="msg.role === 'agent'" class="flex justify-start mb-6">
          <div class="w-full max-w-2xl">
            <!-- Agent header -->
            <div class="flex items-center gap-2 mb-2.5">
              <div class="w-6 h-6 rounded-lg bg-gradient-to-br from-cyan-500/30 to-blue-600/30 border border-white/5 flex items-center justify-center">
                <svg class="w-3.5 h-3.5 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
              </div>
              <span class="text-xs font-medium text-white/40">智能助手</span>
              <span v-if="msg.explain" class="text-[11px] text-white/25 italic">{{ msg.explain }}</span>
            </div>

            <!-- Summary card -->
            <div
              v-if="msg.summary"
              class="mb-3 p-3 rounded-xl bg-gradient-to-r from-emerald-500/10 to-emerald-400/5 border border-emerald-400/15 animate-bounce-in glow-hover"
            >
              <div class="flex items-start gap-3">
                <div class="w-7 h-7 rounded-lg bg-emerald-400/20 flex items-center justify-center flex-shrink-0 mt-0.5">
                  <svg v-if="msg.status === 'success'" class="w-4 h-4 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"/>
                  </svg>
                  <svg v-else class="w-4 h-4 text-amber-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 9v2m0 4h.01"/>
                  </svg>
                </div>
                <div>
                  <p class="text-sm font-medium text-white/80">{{ msg.summary }}</p>
                  <p class="text-xs text-white/40 mt-0.5">
                    {{ msg.successCount }}/{{ msg.steps?.length }} 步骤成功
                  </p>
                </div>
              </div>
            </div>

            <!-- Steps Timeline -->
            <div v-if="msg.steps?.length" class="space-y-2">
              <div
                v-for="(step, si) in msg.steps"
                :key="step.order"
                class="glass rounded-xl overflow-hidden transition-all duration-300 hover:bg-white/[0.06] animate-step-in glow-hover"
                :style="{ animationDelay: si * 80 + 'ms' }"
              >
                <!-- Step header (collapsible) -->
                <button
                  @click="toggleStep(msg, si)"
                  class="w-full flex items-center gap-3 px-4 py-3 text-left"
                >
                  <!-- Status icon -->
                  <div class="flex-shrink-0 w-8 h-8 rounded-lg flex items-center justify-center transition-all duration-300"
                    :class="stepIconBg(step.status)"
                  >
                    <!-- Running: spinner -->
                    <svg v-if="step.status === 'running'" class="w-4 h-4 text-amber-400 animate-spin" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
                    </svg>
                    <!-- Success -->
                    <svg v-else-if="step.status === 'success'" class="w-4 h-4 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"/>
                    </svg>
                    <!-- Failed -->
                    <svg v-else-if="step.status === 'failed'" class="w-4 h-4 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M6 18L18 6M6 6l12 12"/>
                    </svg>
                    <!-- Pending/Skipped -->
                    <svg v-else class="w-4 h-4 text-white/30" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <circle cx="12" cy="12" r="10" stroke-width="2"/>
                    </svg>
                  </div>

                  <!-- Step info -->
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2">
                      <span class="text-xs font-medium text-white/40 tabular-nums">#{{ step.order }}</span>
                      <code class="text-xs font-mono font-medium px-1.5 py-0.5 rounded-md bg-white/5 text-cyan-300">
                        {{ step.tool }}
                      </code>
                    </div>
                    <p class="text-sm text-white/60 mt-0.5 truncate">{{ step.description || step.tool }}</p>
                  </div>

                  <!-- Status badge -->
                  <span
                    class="flex-shrink-0 text-[11px] font-medium px-2 py-0.5 rounded-full"
                    :class="statusBadgeClass(step.status)"
                  >
                    {{ statusLabelMap[step.status] || step.status }}
                  </span>

                  <!-- Expand icon -->
                  <svg
                    class="w-3.5 h-3.5 text-white/20 transition-transform duration-300"
                    :class="{ 'rotate-180': isStepOpen(msg, si) }"
                    fill="none" stroke="currentColor" viewBox="0 0 24 24"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                  </svg>
                </button>

                <!-- Step details (expandable) -->
                <transition name="step-detail">
                  <div v-if="isStepOpen(msg, si)" class="border-t border-white/5">
                    <!-- Parameters -->
                    <div v-if="hasVisibleParams(step.params)" class="px-4 py-3">
                      <p class="text-[11px] font-medium text-white/30 uppercase tracking-wider mb-2">参数</p>
                      <div class="space-y-1">
                        <div
                          v-for="(val, key) in visibleParams(step.params)"
                          :key="key"
                          class="flex items-center gap-3 text-xs"
                        >
                          <span class="text-white/30 font-mono w-24 flex-shrink-0">{{ key }}</span>
                          <span class="text-white/60 truncate">{{ String(val) }}</span>
                        </div>
                      </div>
                    </div>

                    <!-- Result output -->
                    <div v-if="step.result" class="px-4 pb-3">
                      <p class="text-[11px] font-medium text-white/30 uppercase tracking-wider mb-2">输出</p>
                      <pre class="text-xs text-emerald-300/80 font-mono leading-relaxed bg-black/30 rounded-lg p-3 overflow-x-auto">{{ step.result }}</pre>
                    </div>

                    <!-- Error -->
                    <div v-if="step.error" class="px-4 pb-3">
                      <p class="text-[11px] font-medium text-white/30 uppercase tracking-wider mb-2">错误</p>
                      <pre class="text-xs text-red-300/80 font-mono bg-red-500/10 rounded-lg p-3">{{ step.error }}</pre>
                    </div>
                  </div>
                </transition>

                <!-- Connecting line (between steps) -->
                <div v-if="si < msg.steps.length - 1" class="flex justify-center">
                  <div class="step-connector h-4"></div>
                </div>
              </div>
            </div>

            <!-- Thinking indicator -->
            <div v-if="msg.status === 'thinking'" class="flex items-center gap-3 px-1 py-4">
              <div class="flex gap-1">
                <span class="w-2 h-2 rounded-full bg-cyan-400/60 animate-pulse-dot" style="animation-delay: 0s"></span>
                <span class="w-2 h-2 rounded-full bg-cyan-400/60 animate-pulse-dot" style="animation-delay: 0.2s"></span>
                <span class="w-2 h-2 rounded-full bg-cyan-400/60 animate-pulse-dot" style="animation-delay: 0.4s"></span>
              </div>
              <span class="text-xs text-white/30">AI 正在分析你的指令...</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== INPUT AREA ===== -->
    <div class="flex-shrink-0 border-t border-white/5 bg-[#0a0e1a]/80 backdrop-blur-xl relative z-10">
      <div class="max-w-3xl mx-auto px-4 sm:px-8 py-4">
        <!-- Suggested prompts -->
        <div v-if="messages.length > 0 && !isProcessing" class="flex flex-wrap gap-2 mb-3">
          <button
            v-for="s in suggestions"
            :key="s"
            @click="sendMessage(s)"
            class="px-2.5 py-1.5 rounded-lg text-[11px] font-medium transition-all duration-200 glass text-white/40 hover:text-white/60 hover:bg-white/[0.06] active:scale-95 glow-hover"
          >
            {{ s }}
          </button>
        </div>

        <!-- Input row -->
        <div class="flex items-end gap-2">
          <div class="flex-1 relative">
            <textarea
              v-model="input"
              @keydown.enter.prevent="sendMessage"
              placeholder="输入指令，例如：帮我搜一下二手iPhone..."
              rows="1"
              class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white/80 placeholder-white/20 resize-none outline-none focus:border-cyan-500/40 focus:bg-white/[0.06] transition-all duration-200"
              :disabled="isProcessing"
              @input="autoResize"
            ></textarea>
          </div>
          <button
            @click="sendMessage"
            :disabled="isProcessing || !input.trim()"
            class="flex-shrink-0 w-11 h-11 rounded-xl flex items-center justify-center transition-all duration-200 disabled:opacity-30 disabled:cursor-not-allowed"
            :class="isProcessing || !input.trim() ? 'bg-white/5 text-white/20' : 'bg-gradient-to-r from-cyan-500 to-blue-600 text-white shadow-lg shadow-cyan-500/25 hover:shadow-cyan-500/40 active:scale-95'"
          >
            <svg v-if="!isProcessing" class="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
            <svg v-else class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, computed } from 'vue'
import { executeAgent } from '../api/agent'

// ===== State =====
const input = ref('')
const chatRef = ref(null)
const isProcessing = ref(false)
const messages = reactive([])
const openSteps = reactive(new Set())

const suggestions = [
  '帮我搜一下二手iPhone',
  '搜索耳机并生成描述',
  '有哪些数码商品'
]

const statusLabelMap = {
  pending: '等待',
  running: '执行中',
  success: '成功',
  failed: '失败',
  skipped: '跳过'
}

// ===== Computed =====
const statusLabel = computed(() => {
  if (isProcessing.value) return '正在处理指令...'
  if (messages.length > 0) return '在线 · 随时为你服务'
  return '等待指令'
})

const agentStatusDot = computed(() => {
  return isProcessing.value ? 'bg-amber-400 animate-pulse' : 'bg-emerald-400'
})

// ===== Methods =====
function autoResize(e) {
  const el = e.target
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 120) + 'px'
}

async function sendMessage(text) {
  const content = text || input.value.trim()
  if (!content || isProcessing.value) return
  input.value = ''
  if (text) {
    const ta = document.querySelector('textarea')
    if (ta) ta.style.height = 'auto'
  }

  // Add user message
  messages.push({ role: 'user', content })
  await scrollToBottom()

  // Add thinking message
  const thinkingIdx = messages.length
  messages.push({
    role: 'agent',
    status: 'thinking',
    steps: [],
    content: '',
    explain: '',
    summary: '',
    successCount: 0,
    _expanded: true
  })
  await scrollToBottom()

  // Execute
  isProcessing.value = true
  try {
    const res = await executeAgent({ prompt: content })
    const data = res.data

    // Build steps with execution simulation
    const steps = (data.steps || []).map(s => ({
      ...s,
      status: s.status || 'success'
    }))

    // Replace thinking message with result
    const msg = messages[thinkingIdx]
    msg.status = data.status === 'success' ? 'success' : 'partial'
    msg.steps = steps
    msg.summary = data.summaryMessage || '指令执行完成'
    msg.successCount = data.successCount || 0
    msg.failedCount = data.failedCount || 0
    msg.explain = `共 ${steps.length} 步 · ${msg.successCount} 成功`
    msg._expanded = msg.successCount === steps.length

    // Auto-open all steps
    steps.forEach((_, i) => openSteps.add(`${thinkingIdx}-${i}`))

  } catch (e) {
    const msg = messages[thinkingIdx]
    msg.status = 'failed'
    msg.summary = '指令执行失败'
    msg.explain = e.message || '请重试'
    msg.steps = []
  } finally {
    isProcessing.value = false
    await scrollToBottom()
  }
}

function toggleStep(msg, idx) {
  const key = `${messages.indexOf(msg)}-${idx}`
  if (openSteps.has(key)) openSteps.delete(key)
  else openSteps.add(key)
}

function isStepOpen(msg, idx) {
  return openSteps.has(`${messages.indexOf(msg)}-${idx}`)
}

function hasVisibleParams(params) {
  return params && Object.keys(params).some(k => !k.startsWith('_'))
}

function visibleParams(params) {
  if (!params) return {}
  return Object.fromEntries(
    Object.entries(params).filter(([k]) => !k.startsWith('_'))
  )
}

function clearChat() {
  messages.splice(0)
  openSteps.clear()
}

function stepIconBg(status) {
  const map = {
    running: 'bg-amber-400/20',
    success: 'bg-emerald-400/20',
    failed: 'bg-red-400/20',
    pending: 'bg-white/5',
    skipped: 'bg-white/5'
  }
  return map[status] || 'bg-white/5'
}

function statusBadgeClass(status) {
  const map = {
    running: 'bg-amber-400/15 text-amber-300',
    success: 'bg-emerald-400/15 text-emerald-300',
    failed: 'bg-red-400/15 text-red-300',
    pending: 'bg-white/5 text-white/30',
    skipped: 'bg-white/5 text-white/20'
  }
  return map[status] || 'bg-white/5 text-white/30'
}

async function scrollToBottom() {
  await nextTick()
  if (chatRef.value) {
    chatRef.value.scrollTo({ top: chatRef.value.scrollHeight, behavior: 'smooth' })
  }
}
</script>

<style scoped>
.step-detail-enter-active {
  animation: step-detail-in 0.25s ease-out;
}
.step-detail-leave-active {
  animation: step-detail-in 0.2s ease-in reverse;
}
@keyframes step-detail-in {
  0% { opacity: 0; max-height: 0; }
  100% { opacity: 1; max-height: 300px; }
}

/* Glass card hover glow */
.glow-hover {
  transition: box-shadow 0.3s ease, border-color 0.3s ease, transform 0.2s ease;
}
.glow-hover:hover {
  border-color: rgba(6, 182, 212, 0.2);
  box-shadow: 0 0 20px rgba(6, 182, 212, 0.06), inset 0 0 20px rgba(6, 182, 212, 0.03);
  transform: translateY(-1px);
}

/* Smooth textarea */
textarea {
  transition: height 0.15s ease;
}

/* Welcome icon shimmer */
.welcome-glow {
  animation: welcome-pulse 3s ease-in-out infinite;
}
@keyframes welcome-pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(6, 182, 212, 0.1); }
  50% { box-shadow: 0 0 30px 10px rgba(6, 182, 212, 0.15); }
}

/* Step timeline connecting line */
.step-connector {
  width: 1px;
  background: linear-gradient(to bottom, rgba(255,255,255,0.08), rgba(255,255,255,0.02));
}
</style>
