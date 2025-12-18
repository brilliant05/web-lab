<template>
  <div class="resource-detail-page" v-loading="loading">
    <!-- 标题和标签区域 -->
    <div class="detail-header">
      <h1 class="resource-title">{{ resource.resourceTitle || '加载中...' }}</h1>
      <div class="resource-tags" v-if="resource.tags">
        <el-tag
          v-for="(tag, index) in tagList"
          :key="index"
          type="info"
          size="large"
          class="tag-item"
        >
          {{ tag }}
        </el-tag>
      </div>
    </div>

    <div class="detail-content">
      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 统计信息卡片 -->
        <el-card class="stats-card" shadow="never">
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-label">下载次数</div>
              <div class="stat-value">{{ resource.downloadCount || 0 }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">文件大小</div>
              <div class="stat-value">{{ formatFileSize(resource.fileSize) }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">文件类型</div>
              <div class="stat-value">{{ resource.fileType || '未知' }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">浏览次数</div>
              <div class="stat-value">{{ resource.viewCount || 0 }}</div>
            </div>
          </div>
        </el-card>

        <!-- 资源简介 -->
        <el-card class="description-card" shadow="never">
          <template #header>
            <div class="card-title">资源简介</div>
          </template>
          <div class="description-content">
            {{ resource.description || '暂无简介' }}
          </div>
        </el-card>

        <!-- 上传者信息 -->
        <el-card class="uploader-card" shadow="never">
          <template #header>
            <div class="card-title">上传信息</div>
          </template>
          <div class="uploader-info">
            <div class="info-item">
              <span class="info-label">上传者：</span>
              <span class="info-value">{{ resource.uploaderName || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">所属课程：</span>
              <el-button
                v-if="resource.courseName"
                link
                type="primary"
                @click="goToCourseResources"
              >
                {{ resource.courseName }}
              </el-button>
              <span v-else class="info-value">未分类</span>
            </div>
            <div class="info-item">
              <span class="info-label">上传时间：</span>
              <span class="info-value">{{ formatDateTime(resource.createTime) }}</span>
            </div>
          </div>
        </el-card>

        <!-- 预览区 -->
        <el-card class="preview-card" shadow="never" v-if="canPreview">
          <template #header>
            <div class="card-title">在线预览</div>
          </template>
          <div class="preview-content">
            <iframe
              v-if="isPdf"
              :src="resource.filePath"
              class="preview-iframe"
              frameborder="0"
            ></iframe>
            <img
              v-else-if="isImage"
              :src="resource.filePath"
              alt="资源预览"
              class="preview-image"
            />
            <div v-else class="preview-placeholder">
              <el-icon :size="48"><Document /></el-icon>
              <p>该文件类型暂不支持在线预览</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 操作区（固定右侧） -->
      <div class="action-sidebar">
        <el-card class="action-card" shadow="never">
          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              :icon="Download"
              @click="handleDownload"
              :loading="downloadLoading"
              class="action-btn"
            >
              下载资源
            </el-button>
            <el-button
              :type="resource.isCollected ? 'warning' : 'default'"
              size="large"
              :icon="Star"
              @click="handleCollect"
              :loading="collectLoading"
              class="action-btn"
            >
              {{ resource.isCollected ? '已收藏' : '收藏' }}
            </el-button>
            <el-button
              size="large"
              :icon="Share"
              @click="handleShare"
              class="action-btn"
            >
              分享
            </el-button>
            <el-button
              size="large"
              :icon="Warning"
              @click="handleReport"
              class="action-btn"
            >
              举报
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Download,
  Star,
  Share,
  Warning,
  Document
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getResourceDetail,
  downloadResource,
  collectResource,
  uncollectResource
} from '@/api'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const downloadLoading = ref(false)
const collectLoading = ref(false)

// 资源数据
const resource = ref({})

// 标签列表
const tagList = computed(() => {
  if (!resource.value.tags) return []
  return resource.value.tags.split(',').filter(tag => tag.trim())
})

// 判断是否可以预览
const canPreview = computed(() => {
  return resource.value.filePath && (isPdf.value || isImage.value)
})

// 判断是否为PDF
const isPdf = computed(() => {
  const fileType = resource.value.fileType?.toUpperCase()
  return fileType === 'PDF' || resource.value.filePath?.toLowerCase().endsWith('.pdf')
})

// 判断是否为图片
const isImage = computed(() => {
  const fileType = resource.value.fileType?.toUpperCase()
  const imageTypes = ['IMAGE', 'JPG', 'JPEG', 'PNG', 'GIF', 'BMP']
  return imageTypes.includes(fileType) || 
         /\.(jpg|jpeg|png|gif|bmp)$/i.test(resource.value.filePath || '')
})

// 加载资源详情
const loadResourceDetail = async () => {
  loading.value = true
  try {
    const resourceId = route.params.id
    const response = await getResourceDetail(resourceId)
    if (response && response.code === 200 && response.data) {
      resource.value = response.data
    } else {
      ElMessage.error(response?.message || '获取资源详情失败')
      router.back()
    }
  } catch (error) {
    console.error('加载资源详情失败:', error)
    ElMessage.error('加载资源详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '未知'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

// 格式化日期时间
const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 下载资源
const handleDownload = async () => {
  if (downloadLoading.value) return

  downloadLoading.value = true
  try {
    const resourceId = route.params.id
    const response = await downloadResource(resourceId)
    if (response && response.code === 200 && response.data && response.data.url) {
      // 打开下载链接
      window.open(response.data.url, '_blank')
      ElMessage.success('开始下载资源')
      // 更新下载次数（前端预估）
      resource.value.downloadCount = (resource.value.downloadCount || 0) + 1
    } else {
      ElMessage.error(response?.message || '下载失败')
    }
  } catch (error) {
    console.error('下载资源失败:', error)
    ElMessage.error(error?.response?.data?.message || '下载失败')
  } finally {
    downloadLoading.value = false
  }
}

// 收藏/取消收藏
const handleCollect = async () => {
  if (collectLoading.value) return

  collectLoading.value = true
  try {
    const resourceId = route.params.id
    if (resource.value.isCollected) {
      // 取消收藏
      await uncollectResource(resourceId)
      resource.value.isCollected = false
      ElMessage.success('已取消收藏')
    } else {
      // 收藏
      await collectResource(resourceId)
      resource.value.isCollected = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(error?.response?.data?.message || '操作失败')
  } finally {
    collectLoading.value = false
  }
}

// 分享
const handleShare = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    // 降级方案
    const textarea = document.createElement('textarea')
    textarea.value = url
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success('链接已复制到剪贴板')
  })
}

// 举报
const handleReport = () => {
  ElMessageBox.prompt('请输入举报原因', '举报资源', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请描述举报原因...'
  }).then(({ value }) => {
    if (value) {
      // TODO: 实现举报接口
      ElMessage.success('举报已提交，我们会尽快处理')
    }
  }).catch(() => {})
}

// 跳转到课程资源列表
const goToCourseResources = () => {
  if (resource.value.courseId) {
    router.push({
      path: '/student/resources',
      query: { courseId: resource.value.courseId }
    })
  }
}

onMounted(() => {
  loadResourceDetail()
})
</script>

<style scoped>
.resource-detail-page {
  max-width: 1400px;
  margin: 0 auto;
}

.detail-header {
  margin-bottom: 24px;
}

.resource-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 16px 0;
  line-height: 1.4;
}

.resource-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-item {
  font-size: 14px;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-card,
.description-card,
.uploader-card,
.preview-card {
  border-radius: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.description-content {
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}

.uploader-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 15px;
}

.info-label {
  color: #909399;
  min-width: 80px;
}

.info-value {
  color: #303133;
}

.preview-content {
  min-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.preview-iframe {
  width: 100%;
  height: 600px;
  border: none;
}

.preview-image {
  max-width: 100%;
  max-height: 600px;
  object-fit: contain;
}

.preview-placeholder {
  text-align: center;
  color: #909399;
}

.preview-placeholder p {
  margin-top: 16px;
  font-size: 14px;
}

.action-sidebar {
  position: sticky;
  top: 20px;
  height: fit-content;
}

.action-card {
  border-radius: 16px;
  position: sticky;
  top: 20px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  width: 100%;
}

@media screen and (max-width: 1024px) {
  .detail-content {
    grid-template-columns: 1fr;
  }

  .action-sidebar {
    position: static;
  }

  .action-card {
    position: static;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .resource-title {
    font-size: 24px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .preview-iframe,
  .preview-image {
    height: 400px;
  }
}
</style>