<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon student">
              <el-icon :size="40"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.students }}</div>
              <div class="stat-label">学生总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon teacher">
              <el-icon :size="40"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.teachers }}</div>
              <div class="stat-label">教师总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon course">
              <el-icon :size="40"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.courses }}</div>
              <div class="stat-label">课程总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon resource">
              <el-icon :size="40"><FolderOpened /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.resources }}</div>
              <div class="stat-label">资源总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-actions" shadow="never" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" :lg="4" v-for="action in quickActions" :key="action.name">
          <div class="action-item" @click="handleAction(action)">
            <el-icon :size="30">
              <component :is="action.icon" />
            </el-icon>
            <div class="action-name">{{ action.name }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最新动态 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="12">
        <el-card class="activity-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>最新通知</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="item in notifications"
              :key="item.id"
              :timestamp="item.time"
              placement="top"
            >
              {{ item.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="activity-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="运行环境">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="最后更新">{{ new Date().toLocaleString('zh-CN') }}</el-descriptions-item>
            <el-descriptions-item label="系统状态">
              <el-tag type="success">运行正常</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, markRaw, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  UserFilled,
  Reading,
  FolderOpened,
  Plus,
  Edit,
  Delete,
  Search,
  DataAnalysis,
  BellFilled
} from '@element-plus/icons-vue'
import { getStatisticsOverview } from '@/api'

const router = useRouter()

// 统计数据
const statistics = reactive({
  students: 0,
  teachers: 0,
  courses: 0,
  resources: 0
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await getStatisticsOverview()
    if (response && response.code === 200 && response.data) {
      const data = response.data
      statistics.students = data.totalStudents || 0
      statistics.teachers = data.totalTeachers || 0
      statistics.courses = data.totalCourses || 0
      statistics.resources = data.totalResources || 0
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

// 快捷操作 - 使用markRaw标记图标组件，避免响应式包装
const quickActions = ref([
  { name: '新增学生', icon: markRaw(User), route: '/admin/students' },
  { name: '新增教师', icon: markRaw(UserFilled), route: '/admin/teachers' },
  { name: '新增课程', icon: markRaw(Reading), route: '/admin/courses' },
  { name: '上传资源', icon: markRaw(FolderOpened), route: '/admin/resources' },
  { name: '发布通知', icon: markRaw(BellFilled), route: '/admin/notifications' },
  { name: '数据统计', icon: markRaw(DataAnalysis), route: '/admin/statistics' }
])

// 最新通知
const notifications = ref([
  { id: 1, content: '系统管理员发布了新的通知', time: '2025-12-09 10:00' },
  { id: 2, content: '新增课程《数据结构》', time: '2025-12-09 09:30' },
  { id: 3, content: '教师张三上传了新的学习资源', time: '2025-12-09 09:00' },
  { id: 4, content: '学生李四提交了新问题', time: '2025-12-09 08:30' }
])

const handleAction = (action) => {
  ElMessage.success(`跳转到：${action.name}`)
  router.push(action.route)
}
</script>

<style scoped>
.dashboard {
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

.stat-icon.student {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.teacher {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.course {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.resource {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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

/* 快捷操作 */
.quick-actions .action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #f5f7fa;
}

.quick-actions .action-item:hover {
  background-color: #e4e7ed;
  transform: translateY(-3px);
}

.quick-actions .action-name {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}

/* 活动卡片 */
.activity-card {
  min-height: 300px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
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

