<template>
  <div class="question-detail-page" v-loading="loading">
    <!-- 问题区域 -->
    <el-card class="question-card" shadow="never">
      <div class="question-header">
        <h1 class="question-title">{{ question.questionTitle || '加载中...' }}</h1>
        <div class="question-tags" v-if="tagList.length > 0">
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

      <!-- 提问者信息 -->
      <div class="question-author">
        <div class="author-info">
          <el-avatar :size="40" :src="question.studentAvatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="author-details">
            <div class="author-name">{{ question.studentName || '未知' }}</div>
            <div class="author-meta">{{ formatDateTime(question.createTime) }}</div>
          </div>
        </div>
        <div class="question-meta">
          <el-tag :type="question.isAnswered === 1 ? 'success' : 'warning'" size="large">
            {{ question.isAnswered === 1 ? '已回答' : '待回答' }}
          </el-tag>
        </div>
      </div>

      <!-- 问题内容 -->
      <div class="question-content">
        <div class="content-text">{{ question.questionContent }}</div>
        <!-- 问题图片 -->
        <div class="question-images" v-if="questionImages.length > 0">
          <el-image
            v-for="(img, index) in questionImages"
            :key="index"
            :src="img"
            :preview-src-list="questionImages"
            :initial-index="index"
            fit="cover"
            class="content-image"
            :preview-teleported="true"
          />
        </div>
      </div>

      <!-- 课程和教师信息 -->
      <div class="question-info">
        <div class="info-item">
          <span class="info-label">所属课程：</span>
          <el-button
            v-if="question.courseName"
            link
            type="primary"
            @click="goToCourseResources"
          >
            {{ question.courseName }}
          </el-button>
          <span v-else class="info-value">未分类</span>
        </div>
        <div class="info-item">
          <span class="info-label">被提问教师：</span>
          <span class="info-value">{{ question.teacherName || '未知' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">浏览次数：</span>
          <span class="info-value">{{ question.viewCount || 0 }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">回答数量：</span>
          <span class="info-value">{{ question.answerCount || 0 }}</span>
        </div>
      </div>

      <!-- 操作按钮（仅问题所有者且未回答时显示） -->
      <div class="question-actions" v-if="canEdit">
        <el-button type="primary" @click="handleEditQuestion">编辑问题</el-button>
        <el-button type="danger" @click="handleDeleteQuestion">删除问题</el-button>
      </div>
    </el-card>

    <!-- 回答区域 -->
    <el-card class="answers-card" shadow="never">
      <template #header>
        <div class="answers-header">
          <span class="answers-title">回答 ({{ question.answers?.length || 0 }})</span>
        </div>
      </template>

      <div v-if="!question.answers || question.answers.length === 0" class="no-answers">
        <el-empty description="暂无回答" :image-size="100" />
      </div>

      <div v-else class="answers-list">
        <div
          v-for="answer in question.answers"
          :key="answer.answerId"
          class="answer-item"
        >
          <!-- 回答者信息 -->
          <div class="answer-author">
            <el-avatar :size="36" :src="answer.teacherAvatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <div class="answer-author-info">
              <div class="answer-author-name">
                {{ answer.teacherName || '未知教师' }}
                <el-tag v-if="answer.jobTitle" type="info" size="small" style="margin-left: 8px">
                  {{ answer.jobTitle }}
                </el-tag>
              </div>
              <div class="answer-time">{{ formatDateTime(answer.createTime) }}</div>
            </div>
          </div>

          <!-- 回答内容 -->
          <div class="answer-content">
            <div class="answer-text">{{ answer.answerContent }}</div>
            <!-- 回答图片 -->
            <div class="answer-images" v-if="getAnswerImages(answer).length > 0">
              <el-image
                v-for="(img, index) in getAnswerImages(answer)"
                :key="index"
                :src="img"
                :preview-src-list="getAnswerImages(answer)"
                :initial-index="index"
                fit="cover"
                class="content-image"
                :preview-teleported="true"
              />
            </div>
          </div>

          <!-- 回答操作 -->
          <div class="answer-actions">
            <el-button
              :type="answer.isLiked ? 'danger' : 'default'"
              size="small"
              @click="handleLikeAnswer(answer)"
              :loading="answer.liking"
            >
              {{ answer.isLiked ? '已点赞' : '点赞' }}
              <span v-if="answer.likeCount > 0" style="margin-left: 4px">
                ({{ answer.likeCount }})
              </span>
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, UserFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getQuestionDetail,
  deleteQuestion,
  likeAnswer,
  unlikeAnswer
} from '@/api'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const currentUserId = ref(null)

// 问题数据
const question = ref({})

// 从localStorage获取当前用户ID
const userInfo = localStorage.getItem('userInfo')
if (userInfo) {
  try {
    const user = JSON.parse(userInfo)
    currentUserId.value = user.userId || user.userId
  } catch (e) {
    console.error('解析用户信息失败:', e)
  }
}

// 标签列表
const tagList = computed(() => {
  if (!question.value.tags) return []
  return question.value.tags.split(',').filter(tag => tag.trim())
})

// 问题图片列表
const questionImages = computed(() => {
  if (!question.value.imageUrls) return []
  return question.value.imageUrls.split(',').filter(url => url.trim())
})

// 是否可以编辑问题（学生只能编辑自己未回答的问题）
const canEdit = computed(() => {
  if (!question.value.studentId || !currentUserId.value) return false
  return (
    question.value.studentId === currentUserId.value &&
    question.value.isAnswered === 0
  )
})

// 获取回答图片列表
const getAnswerImages = (answer) => {
  if (!answer.imageUrls) return []
  return answer.imageUrls.split(',').filter(url => url.trim())
}

// 加载问题详情
const loadQuestionDetail = async () => {
  loading.value = true
  try {
    const questionId = route.params.id
    const response = await getQuestionDetail(questionId)
    if (response && response.code === 200 && response.data) {
      question.value = response.data
    } else {
      ElMessage.error(response?.message || '获取问题详情失败')
      router.back()
    }
  } catch (error) {
    console.error('加载问题详情失败:', error)
    ElMessage.error('加载问题详情失败')
    router.back()
  } finally {
    loading.value = false
  }
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

// 编辑问题
const handleEditQuestion = () => {
  // TODO: 跳转到编辑页面或显示编辑对话框
  ElMessage.info('编辑功能待实现')
}

// 删除问题
const handleDeleteQuestion = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个问题吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const questionId = route.params.id
    await deleteQuestion(questionId)
    ElMessage.success('删除成功')
    router.push('/student/questions')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除问题失败:', error)
      ElMessage.error(error?.response?.data?.message || '删除失败')
    }
  }
}

// 点赞/取消点赞回答
const handleLikeAnswer = async (answer) => {
  if (answer.liking) return

  answer.liking = true
  try {
    if (answer.isLiked) {
      // 取消点赞
      await unlikeAnswer(answer.answerId)
      answer.isLiked = false
      answer.likeCount = (answer.likeCount || 1) - 1
      ElMessage.success('已取消点赞')
    } else {
      // 点赞
      await likeAnswer(answer.answerId)
      answer.isLiked = true
      answer.likeCount = (answer.likeCount || 0) + 1
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(error?.response?.data?.message || '操作失败')
  } finally {
    answer.liking = false
  }
}

// 跳转到课程资源列表
const goToCourseResources = () => {
  if (question.value.courseId) {
    router.push({
      path: '/student/resources',
      query: { courseId: question.value.courseId }
    })
  }
}

onMounted(() => {
  loadQuestionDetail()
})
</script>

<style scoped>
.question-detail-page {
  max-width: 1200px;
  margin: 0 auto;
}

.question-card,
.answers-card {
  border-radius: 16px;
  margin-bottom: 24px;
}

.question-header {
  margin-bottom: 20px;
}

.question-title {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.question-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-item {
  font-size: 14px;
}

.question-author {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f7fa;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.author-meta {
  font-size: 13px;
  color: #909399;
}

.question-content {
  margin-bottom: 20px;
}

.content-text {
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
  margin-bottom: 16px;
}

.question-images,
.answer-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.content-image {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  cursor: pointer;
}

.question-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.info-label {
  color: #909399;
  margin-right: 8px;
}

.info-value {
  color: #303133;
}

.question-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f5f7fa;
}

.answers-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.answers-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.no-answers {
  padding: 40px 0;
}

.answers-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.answer-item {
  padding-bottom: 24px;
  border-bottom: 1px solid #f5f7fa;
}

.answer-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.answer-author {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.answer-author-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.answer-author-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}

.answer-time {
  font-size: 13px;
  color: #909399;
}

.answer-content {
  margin-left: 48px;
  margin-bottom: 12px;
}

.answer-text {
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}

.answer-actions {
  margin-left: 48px;
}

@media screen and (max-width: 768px) {
  .question-title {
    font-size: 20px;
  }

  .question-info {
    grid-template-columns: 1fr;
  }

  .answer-content,
  .answer-actions {
    margin-left: 0;
  }

  .content-image {
    width: 100%;
    height: auto;
    max-height: 300px;
  }
}
</style>