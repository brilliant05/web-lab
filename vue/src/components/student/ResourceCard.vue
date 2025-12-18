<template>
  <el-card class="resource-card" shadow="hover" @click="handleCardClick">
    <!-- 文件类型图标 -->
    <div class="resource-icon">
      <el-icon :size="48" :color="getFileTypeColor(resource.fileType)">
        <component :is="getFileTypeIcon(resource.fileType)" />
      </el-icon>
    </div>

    <!-- 资源信息 -->
    <div class="resource-info">
      <div class="resource-title" :title="resource.resourceTitle">
        {{ resource.resourceTitle }}
      </div>
      <div class="resource-description" v-if="resource.description">
        {{ truncateText(resource.description, 60) }}
      </div>
      <div class="resource-meta">
        <span class="meta-item">
          <el-icon><Reading /></el-icon>
          {{ resource.courseName || '未分类' }}
        </span>
        <span class="meta-item">
          <el-icon><User /></el-icon>
          {{ resource.uploaderName || '未知' }}
        </span>
      </div>
      <div class="resource-stats">
        <span class="stat-item">
          <el-icon><Download /></el-icon>
          {{ resource.downloadCount || 0 }}次下载
        </span>
        <span class="stat-item">
          <el-icon><Clock /></el-icon>
          {{ formatTime(resource.createTime) }}
        </span>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="resource-actions" @click.stop>
      <el-button
        :type="resource.isCollected ? 'warning' : 'default'"
        :icon="Star"
        size="small"
        circle
        @click="handleCollect"
        :loading="collectLoading"
      />
    </div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Reading,
  User,
  Download,
  Clock,
  Star,
  Document,
  Picture,
  VideoPlay,
  Folder
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { collectResource, uncollectResource } from '@/api'

const props = defineProps({
  resource: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const emit = defineEmits(['collect-changed'])

const router = useRouter()
const collectLoading = ref(false)

// 获取文件类型图标
const getFileTypeIcon = (fileType) => {
  if (!fileType) return Document
  const type = fileType.toUpperCase()
  if (type.includes('PDF')) return Document
  if (type.includes('IMAGE') || type.includes('JPG') || type.includes('PNG')) return Picture
  if (type.includes('VIDEO') || type.includes('MP4')) return VideoPlay
  return Document
}

// 获取文件类型颜色
const getFileTypeColor = (fileType) => {
  if (!fileType) return '#409eff'
  const type = fileType.toUpperCase()
  if (type.includes('PDF')) return '#f56c6c'
  if (type.includes('WORD') || type.includes('DOC')) return '#409eff'
  if (type.includes('EXCEL') || type.includes('XLS')) return '#67c23a'
  if (type.includes('PPT') || type.includes('POWERPOINT')) return '#e6a23c'
  if (type.includes('IMAGE')) return '#f56c6c'
  if (type.includes('VIDEO')) return '#909399'
  return '#409eff'
}

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / 86400000)
  const hours = Math.floor(diff / 3600000)
  const minutes = Math.floor(diff / 60000)

  if (days > 7) {
    return date.toLocaleDateString('zh-CN')
  } else if (days > 0) {
    return `${days}天前`
  } else if (hours > 0) {
    return `${hours}小时前`
  } else if (minutes > 0) {
    return `${minutes}分钟前`
  } else {
    return '刚刚'
  }
}

// 点击卡片
const handleCardClick = () => {
  router.push(`/student/resources/${props.resource.resourceId}`)
}

// 收藏/取消收藏
const handleCollect = async () => {
  if (collectLoading.value) return

  collectLoading.value = true
  try {
    if (props.resource.isCollected) {
      // 取消收藏
      await uncollectResource(props.resource.resourceId)
      props.resource.isCollected = false
      ElMessage.success('已取消收藏')
    } else {
      // 收藏
      await collectResource(props.resource.resourceId)
      props.resource.isCollected = true
      ElMessage.success('收藏成功')
    }
    emit('collect-changed', props.resource)
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(error?.response?.data?.message || '操作失败')
  } finally {
    collectLoading.value = false
  }
}
</script>

<style scoped>
.resource-card {
  cursor: pointer;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
}

.resource-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.resource-icon {
  width: 100%;
  height: 120px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #ebeef5;
}

.resource-info {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.resource-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  min-height: 44px;
}

.resource-description {
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.resource-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 4px;
}

.meta-item {
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-item .el-icon {
  font-size: 14px;
  color: #909399;
}

.resource-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 8px;
  border-top: 1px solid #f5f7fa;
}

.stat-item {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item .el-icon {
  font-size: 14px;
}

.resource-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 10;
}

.resource-actions .el-button {
  background-color: rgba(255, 255, 255, 0.9);
  border: 1px solid #dcdfe6;
}

.resource-actions .el-button:hover {
  background-color: #fff;
}
</style>
