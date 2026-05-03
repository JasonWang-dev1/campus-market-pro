<template>
  <div class="agent-page">
    <Header />

    <main class="agent-main">
      <el-card class="agent-card">
        <template #header>
          <div class="agent-card-header">
            <el-icon :size="24" color="#409eff"><Cpu /></el-icon>
            <div>
              <h2>AI 智能助手</h2>
              <p class="agent-subtitle">用自然语言指挥平台，自动规划并执行任务</p>
            </div>
          </div>
        </template>

        <!-- 输入区 -->
        <div class="input-section">
          <el-input
            v-model="prompt"
            type="textarea"
            :rows="3"
            placeholder="请输入指令，例如：&#10;· 帮我搜一下有没有二手iPhone&#10;· 搜索耳机并生成商品描述&#10;· 有哪些数码商品在卖"
            :disabled="running"
          />
          <div class="input-actions">
            <div class="suggestions">
              <el-tag
                v-for="s in suggestions"
                :key="s"
                size="small"
                :type="s === activeSuggestion ? 'primary' : 'info'"
                class="suggestion-tag"
                @click="selectSuggestion(s)"
              >
                {{ s }}
              </el-tag>
            </div>
            <el-button
              type="primary"
              :icon="Cpu"
              :loading="running"
              size="large"
              @click="handleExecute"
            >
              {{ running ? '执行中...' : '执行指令' }}
            </el-button>
          </div>
        </div>

        <!-- 错误提示 -->
        <el-alert
          v-if="error"
          :title="error"
          type="error"
          show-icon
          closable
          class="error-alert"
          @close="error = ''"
        />

        <!-- 规划结果 -->
        <div v-if="response" class="result-section">
          <el-divider>
            <el-icon color="#67c23a"><CircleCheckFilled /></el-icon>
          </el-divider>

          <!-- 状态卡片 -->
          <div class="status-bar">
            <el-card
              :class="['status-card', response.status]"
              shadow="never"
            >
              <div class="status-icon">
                <el-icon v-if="response.status === 'success'" :size="32" color="#67c23a">
                  <CircleCheckFilled />
                </el-icon>
                <el-icon v-else-if="response.status === 'partial'" :size="32" color="#e6a23c">
                  <WarningFilled />
                </el-icon>
                <el-icon v-else :size="32" color="#f56c6c">
                  <CloseBold />
                </el-icon>
              </div>
              <div class="status-text">
                <h3>{{ statusTitle }}</h3>
                <p>{{ response.summaryMessage }}</p>
              </div>
            </el-card>
          </div>

          <!-- 任务步骤 -->
          <div class="steps-section">
            <h3>执行步骤（{{ response.successCount }}/{{ response.steps.length }} 成功）</h3>

            <div
              v-for="step in response.steps"
              :key="step.order"
              :class="['step-card', 'step-' + step.status]"
            >
              <div class="step-header">
                <div class="step-order">
                  <el-icon v-if="step.status === 'success'" color="#67c23a">
                    <CircleCheck />
                  </el-icon>
                  <el-icon v-else-if="step.status === 'failed'" color="#f56c6c">
                    <CircleClose />
                  </el-icon>
                  <el-icon v-else-if="step.status === 'skipped'" color="#c0c4cc">
                    <Minus />
                  </el-icon>
                  <el-icon v-else color="#909399">
                    <Clock />
                  </el-icon>
                  <span class="step-num">#{{ step.order }}</span>
                </div>
                <div class="step-tool">
                  <el-tag size="small" effect="plain">{{ step.tool }}</el-tag>
                </div>
              </div>

              <p class="step-desc">{{ step.description }}</p>

              <div v-if="step.params && Object.keys(step.params).length > 0" class="step-params">
                <el-descriptions :column="2" size="small" border>
                  <el-descriptions-item
                    v-for="(val, key) in visibleParams(step.params)"
                    :key="key"
                    :label="key"
                  >
                    {{ val }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>

              <div v-if="step.status === 'success' && step.result" class="step-result success">
                <pre>{{ step.result }}</pre>
              </div>

              <div v-if="step.status === 'failed' && step.error" class="step-result failed">
                <el-icon><WarningFilled /></el-icon>
                {{ step.error }}
              </div>

              <div v-if="step.status === 'skipped' && step.error" class="step-result skipped">
                <el-icon><InfoFilled /></el-icon>
                {{ step.error }}
              </div>
            </div>
          </div>

          <!-- 底部操作 -->
          <div class="result-actions">
            <el-button @click="resetAll">清空结果</el-button>
            <el-button type="primary" @click="handleExecute">再试一次</el-button>
          </div>
        </div>
      </el-card>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  Cpu, CircleCheckFilled, CircleCheck, CircleClose,
  WarningFilled, CloseBold, Clock, Minus, InfoFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Header from '../components/Header.vue'
import { executeAgent } from '../api/agent'

// ========== 状态 ==========
const prompt = ref('')
const running = ref(false)
const error = ref('')
const response = ref(null)

const suggestions = [
  '帮我搜一下iPhone手机',
  '搜索耳机并生成商品描述',
  '有哪些数码商品在卖'
]
const activeSuggestion = ref('')

// ========== 操作 ==========
async function handleExecute() {
  if (!prompt.value.trim()) {
    ElMessage.warning('请输入指令')
    return
  }

  running.value = true
  error.value = ''
  response.value = null

  try {
    const res = await executeAgent({ prompt: prompt.value })
    response.value = res.data
    ElMessage.success('指令执行完成')
  } catch (e) {
    error.value = e.message || '执行失败'
  } finally {
    running.value = false
  }
}

function selectSuggestion(s) {
  prompt.value = s
  activeSuggestion.value = s
}

function resetAll() {
  response.value = null
  error.value = ''
}

// ========== 计算 ==========
function visibleParams(params) {
  if (!params) return {}
  return Object.fromEntries(
    Object.entries(params).filter(([key]) => !key.startsWith('_'))
  )
}

const statusTitle = computed(() => {
  if (!response.value) return ''
  const map = {
    success: '✅ 全部执行成功',
    partial: '⚠️ 部分执行成功',
    failed: '❌ 执行失败'
  }
  return map[response.value.status] || ''
})
</script>

<style scoped>
.agent-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.agent-main {
  max-width: 860px;
  margin: 32px auto;
  padding: 0 20px;
}

.agent-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.agent-card-header h2 {
  font-size: 20px;
  margin: 0;
}

.agent-subtitle {
  font-size: 13px;
  color: #909399;
  margin: 2px 0 0;
}

/* 输入区 */
.input-section {
  padding: 0 4px;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  flex-wrap: wrap;
  gap: 12px;
}

.suggestions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.suggestion-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.error-alert {
  margin-top: 16px;
}

/* 结果区 */
.result-section {
  margin-top: 8px;
}

/* 状态卡片 */
.status-bar {
  margin-bottom: 24px;
}

.status-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-radius: 12px;
}

.status-card.success {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
}

.status-card.partial {
  background: #fdf6ec;
  border: 1px solid #faecd8;
}

.status-card.failed {
  background: #fef0f0;
  border: 1px solid #fde2e2;
}

.status-icon {
  flex-shrink: 0;
}

.status-text h3 {
  font-size: 16px;
  margin: 0 0 4px;
}

.status-text p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

/* 步骤 */
.steps-section {
  margin-bottom: 20px;
}

.steps-section h3 {
  font-size: 16px;
  margin-bottom: 16px;
}

.step-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  background: #fff;
  transition: box-shadow 0.2s;
}

.step-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.step-card.step-success {
  border-left: 4px solid #67c23a;
}

.step-card.step-failed {
  border-left: 4px solid #f56c6c;
}

.step-card.step-skipped {
  border-left: 4px solid #c0c4cc;
  opacity: 0.7;
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.step-order {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  font-size: 14px;
}

.step-desc {
  font-size: 14px;
  color: #606266;
  margin: 4px 0 12px;
}

.step-params {
  margin-bottom: 12px;
}

.step-result {
  padding: 12px;
  border-radius: 6px;
  font-size: 13px;
  line-height: 1.6;
  margin-top: 8px;
}

.step-result.success {
  background: #f0f9eb;
  color: #303133;
}

.step-result.success pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: 'Courier New', monospace;
}

.step-result.failed,
.step-result.skipped {
  background: #fef0f0;
  color: #f56c6c;
  display: flex;
  align-items: center;
  gap: 6px;
}

.step-result.skipped {
  background: #f5f7fa;
  color: #909399;
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 20px;
}
</style>
