<template>
  <el-card class="my-resource-card" shadow="hover">
    <div class="resource-content">
      <!-- 文件类型图标 -->
      <div class="resource-icon">
        <el-icon :size="40" :color="getFileTypeColor(resource.fileType)">
          <component :is="getFileTypeIcon(resource.fileType)" />
        </el-icon>
      </div>

      <!-- 资源信息 -->
      <div class="resource-info">
        <div class="resource-title" :title="resource.resourceTitle">
          {{ resource.resourceTitle }}
        </div>
        <div class="resource-description" v-if="resource.description">
          {{ truncateText(resource.description, 80) }}
        </div>
        <div class="resource-meta">
          <span class="meta-item">
            <el-icon><Reading /></el-icon>
            {{ resource.courseName || '未分类' }}
          </span>
          <span class="meta-item">
            <el-icon><Download /></el-icon>
            {{ resource.downloadCount || 0 }}次下载
          </span>
          <span class="meta-item">
            <el-icon><Clock /></el-icon>
            {{ formatTime(resource.createTime) }}
          </span>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="resource-actions">
        <el-button
          type="primary"
          size="small"
          :icon="Edit"
          @click="handleEdit"
        >
          编辑
        </el-button>
        <el-button
          type="danger"
          size="small"
          :icon="Delete"
          @click="handleDelete"
        >
          删除
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Reading,
  Download,
  Clock,
  Edit,
  Delete,
  Document,
  Picture,
  VideoPlay
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  resource: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const emit = defineEmits(['edit', 'delete'])

const router = useRouter()

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

// 编辑资源
const handleEdit = () => {
  emit('edit', props.resource)
}

// 删除资源
const handleDelete = () => {
  ElMessageBox.confirm('确定要删除这个资源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    emit('delete', props.resource)
  }).catch(() => {
    // 用户取消，不需要处理
  })
}
</script>

<style scoped>
.my-resource-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.resource-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.resource-icon {
  flex-shrink: 0;
}

.resource-info {
  flex: 1;
  min-width: 0;
}

.resource-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-description {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
  line-height: 1.5;
}

.resource-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-item .el-icon {
  font-size: 14px;
}

.resource-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

@media screen and (max-width: 768px) {
  .resource-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .resource-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
