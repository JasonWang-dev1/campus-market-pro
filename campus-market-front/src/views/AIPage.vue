<template>
  <div class="ai-page">
    <Header />

    <main class="ai-main">
      <el-card class="ai-card">
        <template #header>
          <div class="ai-card-header">
            <el-icon :size="24" color="#e6a23c"><MagicStick /></el-icon>
            <h2>AI 智能生成商品描述</h2>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          size="large"
        >
          <el-form-item label="商品标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="例如：iPhone 13 校园自用"
            />
          </el-form-item>

          <el-form-item label="额外要求">
            <el-input
              v-model="form.requirements"
              type="textarea"
              :rows="3"
              placeholder="可选：希望强调的内容，如成色、使用时长、配件等"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="warning"
              :icon="MagicStick"
              :loading="generating"
              size="large"
              @click="handleGenerate"
            >
              {{ generating ? 'AI 思考中...' : '智能生成描述' }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 生成结果 -->
        <div v-if="result" class="ai-result">
          <el-divider>
            <el-icon color="#67c23a"><CircleCheckFilled /></el-icon>
          </el-divider>
          <h3>生成结果</h3>
          <div class="result-content">{{ result }}</div>
          <div class="result-actions">
            <el-button type="primary" @click="copyResult">复制描述</el-button>
            <el-button @click="useInPublish">去发布商品</el-button>
            <el-button @click="result = ''">重新生成</el-button>
          </div>
        </div>
      </el-card>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { MagicStick, CircleCheckFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Header from '../components/Header.vue'
import { generateDescription } from '../api/ai'

const router = useRouter()
const route = useRoute()

// ========== 表单 ==========
const formRef = ref(null)
const generating = ref(false)
const result = ref('')

const form = reactive({
  title: '',
  requirements: ''
})

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }]
}

onMounted(() => {
  if (route.query.title) {
    form.title = route.query.title
  }
})

// ========== 操作 ==========
async function handleGenerate() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  generating.value = true
  try {
    const res = await generateDescription({
      title: form.title,
      requirements: form.requirements || undefined
    })
    result.value = res.data
    ElMessage.success('描述生成成功')
  } finally {
    generating.value = false
  }
}

function copyResult() {
  navigator.clipboard.writeText(result.value).then(() => {
    ElMessage.success('已复制到剪贴板')
  })
}

function useInPublish() {
  router.push({
    path: '/products/add',
    query: { description: result.value, title: form.title }
  })
}
</script>

<style scoped>
.ai-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.ai-main {
  max-width: 720px;
  margin: 40px auto;
  padding: 0 20px;
}

.ai-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ai-card-header h2 {
  font-size: 20px;
}

.ai-result {
  padding: 0 20px 20px;
}

.ai-result h3 {
  font-size: 16px;
  margin-bottom: 12px;
  color: #67c23a;
}

.result-content {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
}

.result-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}
</style>
