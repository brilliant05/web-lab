<template>
  <div class="upload-resource-page">
    <el-card class="upload-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h2 class="card-title">上传资源</h2>
        </div>
      </template>

      <!-- 文件上传区域 -->
      <div class="upload-section">
        <el-upload
          ref="uploadRef"
          v-model:file-list="fileList"
          class="upload-dragger"
          :auto-upload="false"
          :limit="1"
          :on-exceed="handleExceed"
          :before-upload="beforeUpload"
          drag
        >
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">
            <p>将文件拖到此处，或<em>点击上传</em></p>
            <p class="upload-tip">支持 PDF、Word、Excel、PPT、图片、压缩包，单文件不超过 50MB</p>
          </div>
        </el-upload>

        <!-- 文件列表 -->
        <div v-if="fileList.length > 0" class="file-list">
          <div
            v-for="(file, index) in fileList"
            :key="index"
            class="file-item"
          >
            <div class="file-info">
              <el-icon class="file-icon"><Document /></el-icon>
              <div class="file-details">
                <div class="file-name">{{ file.name }}</div>
                <div class="file-size">{{ formatFileSize(file.size) }}</div>
              </div>
            </div>
            <el-button
              link
              type="danger"
              :icon="Delete"
              @click="handleRemoveFile(index)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 表单区域 -->
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="upload-form"
      >
        <el-form-item label="资源标题" prop="resourceTitle">
          <el-input
            v-model="formData.resourceTitle"
            placeholder="请输入资源标题（最多50个字符）"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="资源简介" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入资源简介（最多500个字符）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="所属课程" prop="courseId">
          <el-select
            v-model="formData.courseId"
            placeholder="请选择课程"
            filterable
            style="width: 100%"
            :loading="coursesLoading"
          >
            <el-option
              v-for="course in courses"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            >
              <span>{{ course.courseName }}</span>
              <span style="color: #909399; font-size: 12px; margin-left: 8px">
                {{ course.college }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="资源标签" prop="tags">
          <el-input
            v-model="formData.tags"
            placeholder="请输入标签，多个标签用逗号分隔（可选）"
          />
          <div class="form-tip">例如：复习资料,期末考试,重点整理</div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="submitting"
            @click="handleSubmit"
          >
            {{ submitting ? '上传中...' : '提交上传' }}
          </el-button>
          <el-button size="large" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { UploadFilled, Document, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { uploadResource, getCourseList } from '@/api'

const router = useRouter()

const uploadRef = ref(null)
const formRef = ref(null)
const fileList = ref([])
const courses = ref([])
const coursesLoading = ref(false)
const submitting = ref(false)

// 表单数据
const formData = reactive({
  resourceTitle: '',
  description: '',
  courseId: null,
  tags: ''
})

// 表单验证规则
const formRules = {
  resourceTitle: [
    { required: true, message: '请输入资源标题', trigger: 'blur' },
    { max: 50, message: '标题长度不能超过50个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入资源简介', trigger: 'blur' },
    { max: 500, message: '简介长度不能超过500个字符', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'change' }
  ]
}

// 加载课程列表
const loadCourses = async () => {
  coursesLoading.value = true
  try {
    const response = await getCourseList({ pageNum: 1, pageSize: 100, status: 1 })
    if (response && response.code === 200 && response.data) {
      courses.value = response.data.records || []
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  } finally {
    coursesLoading.value = false
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

// 文件超出限制
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先删除已有文件')
}

// 上传前校验
const beforeUpload = (file) => {
  // 文件大小限制 50MB
  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }

  // 文件类型检查
  const allowedTypes = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.ms-powerpoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'image/jpeg',
    'image/png',
    'image/gif',
    'application/zip',
    'application/x-rar-compressed'
  ]

  if (!allowedTypes.includes(file.type) && !file.name.match(/\.(pdf|doc|docx|xls|xlsx|ppt|pptx|jpg|jpeg|png|gif|zip|rar)$/i)) {
    ElMessage.error('不支持的文件类型，请上传 PDF、Word、Excel、PPT、图片或压缩包')
    return false
  }

  return true
}

// 删除文件
const handleRemoveFile = (index) => {
  fileList.value.splice(index, 1)
}

// 提交上传
const handleSubmit = async () => {
  // 表单验证
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (!valid) return false
  })

  // 检查文件
  if (fileList.value.length === 0) {
    ElMessage.warning('请先上传文件')
    return
  }

  const file = fileList.value[0].raw || fileList.value[0]
  if (!file) {
    ElMessage.warning('文件无效，请重新上传')
    return
  }

  submitting.value = true
  try {
    // 构建 FormData
    const formDataToSubmit = new FormData()
    formDataToSubmit.append('file', file)
    formDataToSubmit.append('resourceTitle', formData.resourceTitle.trim())
    formDataToSubmit.append('description', formData.description.trim())
    formDataToSubmit.append('courseId', formData.courseId)
    
    if (formData.tags && formData.tags.trim()) {
      formDataToSubmit.append('tags', formData.tags.trim())
    }

    const response = await uploadResource(formDataToSubmit)
    if (response && response.code === 200) {
      ElMessage.success('资源上传成功')
      // 跳转到资源列表
      setTimeout(() => {
        router.push('/student/resources')
      }, 1500)
    } else {
      ElMessage.error(response?.message || '上传失败')
    }
  } catch (error) {
    console.error('上传资源失败:', error)
    ElMessage.error(error?.response?.data?.message || '上传失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  fileList.value = []
  formData.resourceTitle = ''
  formData.description = ''
  formData.courseId = null
  formData.tags = ''
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.upload-resource-page {
  max-width: 1000px;
  margin: 0 auto;
}

.upload-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.upload-section {
  margin-bottom: 32px;
}

.upload-dragger {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  padding: 60px 20px;
  border-radius: 12px;
  background-color: #fafafa;
  border: 2px dashed #dcdfe6;
  transition: all 0.3s;
}

:deep(.el-upload-dragger:hover) {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.upload-icon {
  font-size: 64px;
  color: #409eff;
  margin-bottom: 16px;
}

.upload-text {
  text-align: center;
  color: #606266;
}

.upload-text em {
  color: #409eff;
  font-style: normal;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.file-list {
  margin-top: 20px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 12px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.file-icon {
  font-size: 32px;
  color: #409eff;
  flex-shrink: 0;
}

.file-details {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.upload-form {
  margin-top: 32px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

@media screen and (max-width: 768px) {
  .upload-card {
    padding: 16px;
  }

  :deep(.el-upload-dragger) {
    padding: 40px 16px;
  }

  .upload-icon {
    font-size: 48px;
  }
}
</style>