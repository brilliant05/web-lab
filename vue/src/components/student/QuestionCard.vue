<template>
  <el-card class="question-card" shadow="hover" @click="handleCardClick">
    <!-- 问题标题和状态 -->
    <div class="question-header">
      <div class="question-title" :title="question.questionTitle">
        {{ question.questionTitle }}
      </div>
      <el-tag
        :type="question.isAnswered === 1 ? 'success' : 'warning'"
        size="small"
        class="status-tag"
      >
        {{ question.isAnswered === 1 ? '已回答' : '待回答' }}
      </el-tag>
    </div>

    <!-- 问题内容摘要 -->
    <div class="question-content" v-if="question.questionContent">
      {{ truncateText(question.questionContent, 100) }}
    </div>

    <!-- 标签 -->
    <div class="question-tags" v-if="tagList.length > 0">
      <el-tag
        v-for="(tag, index) in tagList"
        :key="index"
        type="info"
        size="small"
        class="tag-item"
      >
        {{ tag }}
      </el-tag>
    </div>

    <!-- 问题信息 -->
    <div class="question-info">
      <div class="info-row">
        <span class="info-item">
          <el-icon><Reading /></el-icon>
          {{ question.courseName || '未分类' }}
        </span>
        <span class="info-item">
          <el-icon><UserFilled /></el-icon>
          {{ question.teacherName || '未知教师' }}
        </span>
      </div>
      <div class="info-row">
        <span class="info-item">
          <el-icon><User /></el-icon>
          {{ question.studentName || '未知' }}
        </span>
        <span class="info-item">
          <el-icon><Clock /></el-icon>
          {{ formatTime(question.createTime) }}
        </span>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="question-stats">
      <span class="stat-item">
        <el-icon><View /></el-icon>
        {{ question.viewCount || 0 }} 次浏览
      </span>
      <span class="stat-item">
        <el-icon><ChatDotRound /></el-icon>
        {{ question.answerCount || 0 }} 个回答
      </span>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  Reading,
  UserFilled,
  User,
  Clock,
  View,
  ChatDotRound
} from '@element-plus/icons-vue'

const props = defineProps({
  question: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const router = useRouter()

// 标签列表
const tagList = computed(() => {
  if (!props.question.tags) return []
  return props.question.tags.split(',').filter(tag => tag.trim())
})

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
  router.push(`/student/questions/${props.question.questionId}`)
}
</script>

<style scoped>
.question-card {
  cursor: pointer;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.question-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.question-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  min-height: 50px;
}

.status-tag {
  flex-shrink: 0;
}

.question-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  min-height: 66px;
}

.question-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.tag-item {
  font-size: 12px;
}

.question-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #f5f7fa;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.info-item {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-item .el-icon {
  font-size: 14px;
  color: #c0c4cc;
}

.question-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f5f7fa;
}

.stat-item {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

.stat-item .el-icon {
  font-size: 14px;
  color: #909399;
}
</style>
