<template>
  <div class="statistics">
    <el-row :gutter="20">
      <!-- 数据概览卡片 -->
      <el-col :xs="24" :sm="12" :lg="6" v-for="stat in stats" :key="stat.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon :size="40">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表区域 - 可集成 ECharts 或其他图表库" />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>课程分类统计</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表区域 - 可集成 ECharts 或其他图表库" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 排行榜 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="12">
        <el-card class="rank-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>热门课程排行</span>
            </div>
          </template>
          <el-table :data="hotCourses" stripe>
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="name" label="课程名称" />
            <el-table-column prop="students" label="学生数" width="100" />
            <el-table-column prop="score" label="评分" width="100">
              <template #default="{ row }">
                <el-rate v-model="row.score" disabled show-score text-color="#ff9900" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="rank-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>热门资源排行</span>
            </div>
          </template>
          <el-table :data="hotResources" stripe>
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="name" label="资源名称" />
            <el-table-column prop="downloads" label="下载次数" width="120" />
            <el-table-column prop="type" label="类型" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, UserFilled, Reading, FolderOpened } from '@element-plus/icons-vue'
import { getStatisticsOverview } from '@/api'

// 统计数据
const stats = ref([
  {
    label: '学生总数',
    value: 0,
    icon: User,
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    label: '教师总数',
    value: 0,
    icon: UserFilled,
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    label: '课程总数',
    value: 0,
    icon: Reading,
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    label: '资源总数',
    value: 0,
    icon: FolderOpened,
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await getStatisticsOverview()
    if (response && response.code === 200 && response.data) {
      const data = response.data
      // 更新统计数据
      const studentStat = stats.value.find(s => s.label === '学生总数')
      if (studentStat) studentStat.value = data.totalStudents || 0
      
      const teacherStat = stats.value.find(s => s.label === '教师总数')
      if (teacherStat) teacherStat.value = data.totalTeachers || 0
      
      const courseStat = stats.value.find(s => s.label === '课程总数')
      if (courseStat) courseStat.value = data.totalCourses || 0
      
      const resourceStat = stats.value.find(s => s.label === '资源总数')
      if (resourceStat) resourceStat.value = data.totalResources || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 如果接口调用失败，保持默认值0
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadStatistics()
})

// 热门课程
const hotCourses = ref([
  { name: '数据结构与算法', students: 235, score: 4.8 },
  { name: 'Java程序设计', students: 198, score: 4.6 },
  { name: '计算机网络', students: 176, score: 4.5 },
  { name: '操作系统', students: 156, score: 4.7 },
  { name: '数据库原理', students: 145, score: 4.4 }
])

// 热门资源
const hotResources = ref([
  { name: 'Java基础教程.pdf', downloads: 456, type: 'PDF' },
  { name: '数据结构课件.ppt', downloads: 389, type: 'PPT' },
  { name: '网络协议详解.pdf', downloads: 312, type: 'PDF' },
  { name: '算法实战视频', downloads: 278, type: 'VIDEO' },
  { name: 'MySQL实战教程.pdf', downloads: 245, type: 'PDF' }
])
</script>

<style scoped>
.statistics {
  padding: 0;
}

/* 统计卡片 */
.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 图表卡片 */
.chart-card {
  min-height: 400px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

/* 排行榜卡片 */
.rank-card {
  min-height: 400px;
}

@media screen and (max-width: 768px) {
  .stat-content {
    gap: 10px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
  }

  .stat-icon :deep(.el-icon) {
    font-size: 24px !important;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>

