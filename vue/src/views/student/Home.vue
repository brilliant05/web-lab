<template>
  <div class="student-home">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <el-icon :size="30"><Reading /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">我的课程</div>
            <div class="stat-value">{{ statistics.myCourses }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <el-icon :size="30"><Star /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">我的收藏</div>
            <div class="stat-value">{{ statistics.myCollections }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <el-icon :size="30"><Upload /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">我的上传</div>
            <div class="stat-value">{{ statistics.myUploads }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
            <el-icon :size="30"><Bell /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">未读消息</div>
            <div class="stat-value">{{ statistics.unreadMessages }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 最新资源和问答 -->
    <div class="content-grid">
      <!-- 最新资源 -->
      <el-card class="content-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">最新资源</span>
            <el-button link type="primary" @click="goToResources">查看更多</el-button>
          </div>
        </template>
        <div class="resource-list">
          <div
            v-for="resource in latestResources"
            :key="resource.id"
            class="resource-item"
            @click="goToResourceDetail(resource.id)"
          >
            <div class="resource-icon">
              <el-icon :size="24"><Document /></el-icon>
            </div>
            <div class="resource-info">
              <div class="resource-title">{{ resource.title }}</div>
              <div class="resource-meta">
                <span>{{ resource.courseName }}</span>
                <span class="separator">·</span>
                <span>{{ resource.uploader }}</span>
                <span class="separator">·</span>
                <span>{{ resource.time }}</span>
              </div>
            </div>
            <div class="resource-action">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <div v-if="latestResources.length === 0" class="empty-state">
            <el-empty description="暂无资源" :image-size="80" />
          </div>
        </div>
      </el-card>

      <!-- 最新问答 -->
      <el-card class="content-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">最新问答</span>
            <el-button link type="primary" @click="goToQuestions">查看更多</el-button>
          </div>
        </template>
        <div class="question-list">
          <div
            v-for="question in latestQuestions"
            :key="question.id"
            class="question-item"
            @click="goToQuestionDetail(question.id)"
          >
            <div class="question-header">
              <div class="question-title">{{ question.title }}</div>
              <el-tag v-if="question.isAnswered" type="success" size="small">已解答</el-tag>
              <el-tag v-else type="warning" size="small">待回答</el-tag>
            </div>
            <div class="question-meta">
              <span>{{ question.courseName }}</span>
              <span class="separator">·</span>
              <span>{{ question.asker }}</span>
              <span class="separator">·</span>
              <span>{{ question.time }}</span>
              <span class="separator">·</span>
              <span>{{ question.answerCount }}个回答</span>
            </div>
            <div class="question-action">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <div v-if="latestQuestions.length === 0" class="empty-state">
            <el-empty description="暂无问答" :image-size="80" />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Reading, Star, Upload, Bell, Document, ArrowRight } from '@element-plus/icons-vue'
// TODO: 导入API接口
// import { getMyStatistics, getLatestResources, getLatestQuestions } from '@/api'

const router = useRouter()

// 统计数据（mock数据，后续对接API）
const statistics = ref({
  myCourses: 8,
  myCollections: 28,
  myUploads: 12,
  unreadMessages: 5
})

// 最新资源列表（mock数据，后续对接API）
const latestResources = ref([
  {
    id: 1,
    title: 'Java基础教程PPT',
    courseName: 'Java编程',
    uploader: '张老师',
    time: '2小时前'
  },
  {
    id: 2,
    title: '数据结构复习资料',
    courseName: '数据结构',
    uploader: '李同学',
    time: '5小时前'
  },
  {
    id: 3,
    title: '计算机网络实验报告',
    courseName: '计算机网络',
    uploader: '王同学',
    time: '1天前'
  },
  {
    id: 4,
    title: '操作系统课程笔记',
    courseName: '操作系统',
    uploader: '赵老师',
    time: '2天前'
  },
  {
    id: 5,
    title: '数据库设计文档',
    courseName: '数据库原理',
    uploader: '钱同学',
    time: '3天前'
  }
])

// 最新问答列表（mock数据，后续对接API）
const latestQuestions = ref([
  {
    id: 1,
    title: '如何理解Java中的多线程？',
    courseName: 'Java编程',
    asker: '学生A',
    time: '1小时前',
    answerCount: 3,
    isAnswered: true
  },
  {
    id: 2,
    title: '数据结构中红黑树的应用场景？',
    courseName: '数据结构',
    asker: '学生B',
    time: '3小时前',
    answerCount: 1,
    isAnswered: true
  },
  {
    id: 3,
    title: 'TCP和UDP的区别是什么？',
    courseName: '计算机网络',
    asker: '学生C',
    time: '5小时前',
    answerCount: 0,
    isAnswered: false
  },
  {
    id: 4,
    title: '操作系统中的死锁如何避免？',
    courseName: '操作系统',
    asker: '学生D',
    time: '1天前',
    answerCount: 2,
    isAnswered: true
  },
  {
    id: 5,
    title: '数据库索引的作用和原理？',
    courseName: '数据库原理',
    asker: '学生E',
    time: '2天前',
    answerCount: 4,
    isAnswered: true
  }
])

// 加载统计数据
const loadStatistics = async () => {
  // TODO: 调用API获取统计数据
  // try {
  //   const response = await getMyStatistics()
  //   if (response && response.code === 200) {
  //     statistics.value = response.data
  //   }
  // } catch (error) {
  //   console.error('加载统计数据失败:', error)
  // }
}

// 加载最新资源
const loadLatestResources = async () => {
  // TODO: 调用API获取最新资源
  // try {
  //   const response = await getLatestResources({ limit: 5 })
  //   if (response && response.code === 200) {
  //     latestResources.value = response.data
  //   }
  // } catch (error) {
  //   console.error('加载最新资源失败:', error)
  // }
}

// 加载最新问答
const loadLatestQuestions = async () => {
  // TODO: 调用API获取最新问答
  // try {
  //   const response = await getLatestQuestions({ limit: 5 })
  //   if (response && response.code === 200) {
  //     latestQuestions.value = response.data
  //   }
  // } catch (error) {
  //   console.error('加载最新问答失败:', error)
  // }
}

// 跳转到资源列表
const goToResources = () => {
  router.push('/student/resources')
}

// 跳转到资源详情
const goToResourceDetail = (id) => {
  router.push(`/student/resources/${id}`)
}

// 跳转到问答列表
const goToQuestions = () => {
  router.push('/student/questions')
}

// 跳转到问答详情
const goToQuestionDetail = (id) => {
  router.push(`/student/questions/${id}`)
}

onMounted(() => {
  loadStatistics()
  loadLatestResources()
  loadLatestQuestions()
})
</script>

<style scoped>
.student-home {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.content-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 资源列表 */
.resource-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resource-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.resource-item:hover {
  background-color: #f5f7fa;
}

.resource-icon {
  width: 48px;
  height: 48px;
  background-color: #f0f2f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
  flex-shrink: 0;
}

.resource-info {
  flex: 1;
  min-width: 0;
}

.resource-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-meta {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
}

.separator {
  color: #dcdfe6;
}

.resource-action {
  color: #c0c4cc;
  transition: color 0.3s;
}

.resource-item:hover .resource-action {
  color: #409eff;
}

/* 问答列表 */
.question-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-item {
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
  position: relative;
}

.question-item:hover {
  background-color: #f5f7fa;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 8px;
}

.question-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.question-meta {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
}

.question-action {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #c0c4cc;
  transition: color 0.3s;
}

.question-item:hover .question-action {
  color: #409eff;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

/* 响应式设计 */
@media screen and (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media screen and (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>
