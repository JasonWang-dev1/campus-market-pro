<template>
  <div class="add-page">
    <Header />

    <main class="add-main">
      <el-card class="add-card">
        <template #header>
          <h2>发布商品</h2>
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
              placeholder="请输入商品标题"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="商品分类" prop="category">
            <el-select v-model="form.category" placeholder="请选择分类" style="width:100%">
              <el-option v-for="c in CATEGORIES" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>

          <el-form-item label="价格" prop="price">
            <el-input-number
              v-model="form.price"
              :min="0.01"
              :precision="2"
              :step="10"
              style="width:200px"
            />
          </el-form-item>

          <el-form-item label="原价">
            <el-input-number
              v-model="form.originalPrice"
              :min="0"
              :precision="2"
              :step="10"
              style="width:200px"
            />
          </el-form-item>

          <el-form-item label="商品图片">
            <div class="upload-tip">
              <el-icon><Picture /></el-icon>
              <span>输入图片URL，多个URL用逗号分隔</span>
            </div>
            <el-input
              v-model="form.images"
              type="textarea"
              :rows="2"
              placeholder="https://example.com/image1.jpg, https://example.com/image2.jpg"
            />
          </el-form-item>

          <el-form-item label="商品描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="6"
              placeholder="请描述商品的使用情况、成色、交易方式等"
            />
            <div class="ai-hint">
              <el-button link type="primary" :icon="MagicStick" @click="goAI">
                使用 AI 智能生成描述
              </el-button>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">
              {{ submitting ? '发布中...' : '立即发布' }}
            </el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Picture, MagicStick } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Header from '../components/Header.vue'
import { createProduct } from '../api/product'
import { CATEGORIES } from '../utils'

const router = useRouter()
const route = useRoute()

// ========== 表单 ==========
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  title: '',
  category: '',
  price: undefined,
  originalPrice: undefined,
  images: '',
  description: ''
})

const rules = {
  title: [
    { required: true, message: '请输入商品标题', trigger: 'blur' },
    { max: 100, message: '标题不超过100字', trigger: 'blur' }
  ],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
  ]
}

// 从 AI 页跳转回来时填充
onMounted(() => {
  if (route.query.title) {
    form.title = route.query.title
  }
  if (route.query.description) {
    form.description = route.query.description
  }
})

// ========== 操作 ==========
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await createProduct({
      ...form,
      originalPrice: form.originalPrice || undefined
    })
    ElMessage.success('发布成功')
    router.push('/products')
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  formRef.value.resetFields()
  form.originalPrice = undefined
}

function goAI() {
  router.push({ path: '/ai', query: { title: form.title } })
}
</script>

<style scoped>
.add-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.add-main {
  max-width: 720px;
  margin: 24px auto;
  padding: 0 20px;
}

.add-card h2 {
  font-size: 20px;
}

.upload-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.ai-hint {
  margin-top: 8px;
}
</style>
