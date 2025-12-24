<template>
  <el-card class="content-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <el-button link @click="goBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <span class="title">资源详情</span>
        </div>
        <div class="header-right">
          <el-button type="primary" @click="handleEdit" v-if="resource">编辑资源</el-button>
        </div>
      </div>
    </template>

    <div class="detail-container" v-loading="loading">
      <div class="preview-section" v-if="resource">
        <!-- 图片预览 -->
        <div v-if="isImage(resource.fileType)" class="preview-content image-preview">
          <el-image 
            :src="resource.filePath" 
            fit="contain" 
            :preview-src-list="[resource.filePath]"
          />
        </div>
        
        <!-- 视频预览 -->
        <div v-else-if="isVideo(resource.fileType)" class="preview-content video-preview">
          <video controls :src="resource.filePath" style="width: 100%; max-height: 500px;"></video>
        </div>

        <!-- PDF预览 (使用 iframe) -->
        <div v-else-if="isPdf(resource.fileType)" class="preview-content pdf-preview">
          <iframe :src="resource.filePath" width="100%" height="600px" frameborder="0"></iframe>
        </div>

        <!-- 其他文件 -->
        <div v-else class="preview-content other-preview">
          <el-empty description="该文件类型暂不支持在线预览">
            <el-button type="primary" @click="handleDownload">下载文件</el-button>
          </el-empty>
        </div>
      </div>

      <div class="info-section" v-if="resource">
        <h2 class="resource-title">{{ resource.resourceTitle }}</h2>
        
        <div class="meta-info">
          <div class="meta-item">
            <span class="label">所属课程：</span>
            <span class="value">{{ resource.courseName }}</span>
          </div>
          <div class="meta-item">
            <span class="label">文件类型：</span>
            <span class="value">{{ resource.fileType }}</span>
          </div>
          <div class="meta-item">
            <span class="label">文件大小：</span>
            <span class="value">{{ formatFileSize(resource.fileSize) }}</span>
          </div>
          <div class="meta-item">
            <span class="label">可见性：</span>
            <el-tag :type="resource.visibility === 'PUBLIC' ? 'success' : 'warning'" size="small">
              {{ resource.visibility === 'PUBLIC' ? '公开' : '仅课程学生' }}
            </el-tag>
          </div>
          <div class="meta-item">
            <span class="label">下载次数：</span>
            <span class="value">{{ resource.downloadCount }}</span>
          </div>
          <div class="meta-item">
            <span class="label">发布时间：</span>
            <span class="value">{{ resource.createTime }}</span>
          </div>
        </div>

        <div class="description-box">
          <div class="label">资源描述：</div>
          <div class="description-content">{{ resource.description || '暂无描述' }}</div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { getResourceDetail } from '@/api'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const resource = ref(null)

const fetchResourceDetail = async () => {
  const id = route.params.id
  if (!id) return
  
  loading.value = true
  try {
    const res = await getResourceDetail(id)
    if (res.code === 200) {
      resource.value = res.data
    } else {
      ElMessage.error(res.message || '获取资源详情失败')
    }
  } catch (error) {
    console.error('获取资源详情失败:', error)
    ElMessage.error('获取资源详情失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const handleEdit = () => {
  if (!resource.value) return
  router.push({
    path: '/teacher/resources/publish',
    query: {
      id: resource.value.resourceId,
      courseId: resource.value.courseId,
      title: resource.value.resourceTitle,
      description: resource.value.description,
      visibility: resource.value.visibility
    }
  })
}

const handleDownload = () => {
  if (resource.value && resource.value.filePath) {
    window.open(resource.value.filePath, '_blank')
  }
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const isImage = (type) => {
  return type === 'IMAGE'
}

const isVideo = (type) => {
  return type === 'VIDEO'
}

const isPdf = (type) => {
  return type === 'PDF'
}

onMounted(() => {
  fetchResourceDetail()
})
</script>

<style scoped>
.content-card {
  min-height: calc(100vh - 120px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.detail-container {
  display: flex;
  gap: 30px;
  padding: 20px;
}

.preview-section {
  flex: 2;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e4e7ed;
}

.preview-content {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.resource-title {
  font-size: 20px;
  color: #303133;
  margin: 0;
}

.meta-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  background-color: #f9fafc;
  padding: 20px;
  border-radius: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.meta-item .label {
  color: #909399;
  width: 80px;
}

.meta-item .value {
  color: #606266;
}

.description-box {
  flex: 1;
}

.description-box .label {
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.description-content {
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
  white-space: pre-wrap;
}
</style>
