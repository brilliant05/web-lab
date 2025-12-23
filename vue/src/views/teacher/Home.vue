<template>
  <div class="teacher-home">
    <!-- 未回答问题提醒 -->
    <div v-if="stats.pendingQuestions > 0" class="alert-container" @click="goToQuestions">
      <el-alert
        :title="`您所在课程有 ${stats.pendingQuestions} 条未回答问题`"
        type="warning"
        effect="dark"
        show-icon
        :closable="false"
      >
        <template #default>
          <span class="alert-link">点击进入答疑界面</span>
        </template>
      </el-alert>
    </div>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>我的课程</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.courseCount }}</span>
            <span class="label">门</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>待回答问题</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number warning">{{ stats.pendingQuestions }}</span>
            <span class="label">个</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>已发布资源</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number success">{{ stats.resourceCount }}</span>
            <span class="label">份</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>学生总数</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number primary">{{ stats.studentCount }}</span>
            <span class="label">人</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>课程学生分布</span>
            </div>
          </template>
          <div ref="chartRef" style="height: 300px; width: 100%;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="notice-card">
          <template #header>
            <div class="card-header">
              <span>最新通知</span>
              <el-button link type="primary" @click="goToNotifications">更多</el-button>
            </div>
          </template>
          <div class="notice-list" v-loading="noticeLoading">
            <el-empty v-if="notifications.length === 0" description="暂无通知" :image-size="60" />
            <div 
              class="notice-item" 
              v-for="notice in notifications" 
              :key="notice.notificationId"
              :class="{ 'unread': !notice.isRead }"
              @click="handleNoticeClick(notice)"
            >
              <span class="notice-title" :title="notice.title">
                {{ notice.title }}
                <span v-if="!notice.isRead" class="unread-dot"></span>
              </span>
              <span class="notice-time">{{ notice.createTime?.substring(0, 10) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { getMyCourses, getMyUploads, getNotificationList, getPendingQuestions, markNotificationRead } from '@/api'
import notificationWS from '@/utils/notificationWebSocket'
import * as echarts from 'echarts'
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const noticeLoading = ref(false)
const notifications = ref([])
const chartRef = ref(null)
let chartInstance = null
const isDark = ref(document.documentElement.classList.contains('dark'))
let observer = null

const stats = reactive({
  courseCount: 0,
  pendingQuestions: 0,
  resourceCount: 0,
  studentCount: 0
})

const goToQuestions = () => {
  router.push('/teacher/questions')
}

const goToNotifications = () => {
  router.push('/teacher/notifications')
}

const handleNoticeClick = async (notice) => {
  // 标记为已读
  if (!notice.isRead) {
    try {
      await markNotificationRead(notice.notificationId)
      notice.isRead = 1
    } catch (e) {
      console.error(e)
    }
  }
  
  // 跳转
  if (notice.notificationType === 1) {
    router.push('/teacher/questions')
  } else {
    router.push('/teacher/notifications')
  }
}

const initChart = (courses) => {
  if (!chartRef.value) return
  
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  
  const courseNames = courses.map(c => c.courseName)
  const studentCounts = courses.map(c => c.studentCount || 0)

  const textColor = isDark.value ? '#E5EAF3' : '#606266'
  const axisColor = isDark.value ? '#A3A6AD' : '#909399'
  const splitLineColor = isDark.value ? '#4C4D4F' : '#E0E6F1'

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: courseNames,
        axisTick: {
          alignWithLabel: true
        },
        axisLabel: {
          interval: 0,
          rotate: 30,
          overflow: 'truncate',
          width: 80,
          color: axisColor
        },
        axisLine: {
          lineStyle: {
            color: axisColor
          }
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '学生人数',
        nameTextStyle: {
          color: textColor
        },
        axisLabel: {
          color: axisColor
        },
        splitLine: {
          lineStyle: {
            color: splitLineColor
          }
        }
      }
    ],
    series: [
      {
        name: '学生人数',
        type: 'bar',
        barWidth: '40%',
        data: studentCounts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2378f7' },
              { offset: 0.7, color: '#2378f7' },
              { offset: 1, color: '#83bff6' }
            ])
          }
        }
      }
    ]
  }

  chartInstance.setOption(option)
  
  window.addEventListener('resize', handleResize)
}

const updateChartTheme = () => {
  if (!chartInstance) return
  
  const textColor = isDark.value ? '#E5EAF3' : '#606266'
  const axisColor = isDark.value ? '#A3A6AD' : '#909399'
  const splitLineColor = isDark.value ? '#4C4D4F' : '#E0E6F1'
  
  chartInstance.setOption({
    xAxis: {
      axisLabel: { color: axisColor },
      axisLine: { lineStyle: { color: axisColor } }
    },
    yAxis: {
      nameTextStyle: { color: textColor },
      axisLabel: { color: axisColor },
      splitLine: { lineStyle: { color: splitLineColor } }
    }
  })
}

const handleResize = () => {
  chartInstance?.resize()
}

const fetchStats = async () => {
  try {
    const [coursesRes, questionsRes, resourcesRes] = await Promise.all([
      getMyCourses({ pageSize: 100 }),
      getPendingQuestions({ pageSize: 1 }),
      getMyUploads({ pageSize: 1 })
    ])

    if (coursesRes.code === 200) {
      stats.courseCount = coursesRes.data.total
      const courses = coursesRes.data.records || []
      stats.studentCount = courses.reduce((sum, course) => sum + (course.studentCount || 0), 0)
      
      // 初始化图表
      if (courses.length > 0) {
        initChart(courses)
      }
    }

    if (questionsRes.code === 200) {
      stats.pendingQuestions = questionsRes.data.total
    }

    if (resourcesRes.code === 200) {
      stats.resourceCount = resourcesRes.data.total
    }

  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchNotifications = async () => {
  noticeLoading.value = true
  try {
    const res = await getNotificationList({ pageNum: 1, pageSize: 5 })
    if (res.code === 200) {
      notifications.value = res.data.records
    }
  } catch (error) {
    console.error('获取通知失败:', error)
  } finally {
    noticeLoading.value = false
  }
}

const handleWSMessage = (data) => {
  if (data.type === 'notification') {
    fetchNotifications()
    // 如果是问题通知，更新待回答问题数量
    if (data.relatedId) {
      stats.pendingQuestions++
    }
  }
}

onMounted(() => {
  fetchStats()
  fetchNotifications()
  
  // 监听WebSocket消息
  notificationWS.addListener(handleWSMessage)
  
  // 监听主题变化
  observer = new MutationObserver((mutations) => {
    mutations.forEach((mutation) => {
      if (mutation.attributeName === 'class') {
        const newIsDark = document.documentElement.classList.contains('dark')
        if (isDark.value !== newIsDark) {
          isDark.value = newIsDark
          updateChartTheme()
        }
      }
    })
  })
  
  observer.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['class']
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
  observer?.disconnect()
  notificationWS.removeListener(handleWSMessage)
})
</script>

<style scoped>
.alert-container {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s;
}

.alert-container:hover {
  transform: translateY(-2px);
}

.alert-link {
  margin-left: 10px;
  text-decoration: underline;
  font-size: 13px;
}

.stat-card .card-content {
  display: flex;
  align-items: baseline;
  justify-content: center;
  padding: 10px 0;
}

.stat-card .number {
  font-size: 36px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  margin-right: 5px;
}

.stat-card .number.warning { color: var(--el-color-warning); }
.stat-card .number.success { color: var(--el-color-success); }
.stat-card .number.primary { color: var(--el-color-primary); }

.stat-card .label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--el-text-color-regular);
  padding: 10px 5px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: background-color 0.2s;
}

.notice-item:hover {
  background-color: var(--el-fill-color-light);
}

.notice-item.unread {
  font-weight: 500;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-title {
  display: flex;
  align-items: center;
}

.unread-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: var(--el-color-danger);
  margin-left: 5px;
}

.notice-time {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
