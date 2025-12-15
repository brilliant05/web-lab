<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="aside">
      <div class="brand">
        <div class="brand-tag">USST · ADMIN</div>
        <div class="brand-title">学习资源与问答后台</div>
      </div>
      <el-menu
        :default-active="activeNav"
        class="menu"
        background-color="#1f2937"
        text-color="#d1d5db"
        active-text-color="#fff"
        @select="handleNavClick"
      >
        <el-menu-item v-for="item in navItems" :key="item.id" :index="item.id">
          <el-icon><component :is="markRaw(item.icon)" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <div class="page-title">欢迎，{{ currentUser.name || '管理员' }}</div>
          <div class="page-sub">请选择左侧菜单进入对应管理模块</div>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="header-user">
              <el-avatar
                :size="36"
                :src="currentUser.avatar"
                :icon="!currentUser.avatar ? UserFilled : undefined"
              />
              <span class="user-role">{{ currentUser.role }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <el-row :gutter="12" class="mb-12">
          <el-col :xs="24" :md="12">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-label">访客数 (UV)</div>
              <div class="stat-number">{{ uv.today }}</div>
              <div class="stat-footer">累计 {{ uv.total }}</div>
            </el-card>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-label">浏览量 (PV)</div>
              <div class="stat-number">{{ pv.today }}</div>
              <div class="stat-footer">累计 {{ pv.total }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-card shadow="never" class="module-card">
          <template #header>
            <div class="header-bar">
              <span>{{ activeModule.label }}</span>
              <el-button type="primary" size="small" @click="goModule(activeModule.id)">
                进入 {{ activeModule.label }}
              </el-button>
            </div>
          </template>
          <p class="module-desc">{{ activeModule.description }}</p>
          <el-row :gutter="12">
            <el-col
              v-for="meta in activeModule.stats"
              :key="meta.label"
              :xs="24"
              :sm="12"
              :md="8"
            >
              <el-card shadow="hover" class="stat-card">
                <div class="stat-label">{{ meta.label }}</div>
                <div class="stat-value">{{ meta.value }}</div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, markRaw, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'
import {
  UserFilled,
  House,
  Collection,
  Reading,
  User,
  Files,
  ChatSquare
} from '@element-plus/icons-vue'

const router = useRouter()

const modules = [
  {
    id: 'dashboard',
    label: '仪表盘',
    short: '概览',
    description: '查看全站运行状态、重点指标与最新的活跃情况。',
    stats: [
      { label: '新增用户', value: '42' },
      { label: '活跃课程', value: '18' },
      { label: '待处理问答', value: '6' }
    ]
  },
  {
    id: 'course',
    label: '课程管理',
    short: '课程',
    description: '维护课程大纲、教学资料以及授课教师安排。',
    stats: [
      { label: '课程总数', value: '126' },
      { label: '审核中', value: '8' },
      { label: '近期更新', value: '23' }
    ]
  },
  {
    id: 'student',
    label: '学生管理',
    short: '学生',
    description: '快速检索学生档案，查看学习记录与选课情况。',
    stats: [
      { label: '活跃学生', value: '3,482' },
      { label: '待跟进', value: '17' },
      { label: '提醒', value: '5' }
    ]
  },
  {
    id: 'teacher',
    label: '教师管理',
    short: '教师',
    description: '管理教师身份信息、授课任务与教学评价。',
    stats: [
      { label: '在职教师', value: '215' },
      { label: '培训中', value: '11' },
      { label: '异常', value: '0' }
    ]
  },
  {
    id: 'resource',
    label: '学习资料管理',
    short: '资料',
    description: '集中维护学习资料库，确保分类、标签与权限准确。',
    stats: [
      { label: '资料总量', value: '1,204' },
      { label: '待审核', value: '14' },
      { label: '下载量', value: '8.4k' }
    ]
  },
  {
    id: 'qa',
    label: '问答内容管理',
    short: '问题',
    description: '审核问题与回答，保障内容质量与互动体验。',
    stats: [
      { label: '今日提问', value: '57' },
      { label: '待回复', value: '9' },
      { label: '精选', value: '32' }
    ]
  }
]

const currentUser = reactive({
  name: '管理员',
  role: 'ADMIN',
  avatar: ''
})

// 从 localStorage 读取用户信息
const userInfo = localStorage.getItem('userInfo')
if (userInfo) {
  try {
    const user = JSON.parse(userInfo)
    currentUser.name = user.realName || user.username || '管理员'
    currentUser.role = user.role || 'ADMIN'
  } catch (e) {
    console.error('解析用户信息失败:', e)
  }
}

const activeNav = ref('home')
const activeModuleId = ref(modules[0].id)
const activeModule = computed(
  () => modules.find((m) => m.id === activeModuleId.value) || modules[0]
)

const navItems = computed(() => [
  { id: 'home', label: '首页', icon: House },
  { id: 'course', label: '课程管理', icon: Collection },
  { id: 'teacher', label: '教师管理', icon: Reading },
  { id: 'student', label: '学生管理', icon: User },
  { id: 'resource', label: '资料管理', icon: Files },
  { id: 'qa', label: '问答管理', icon: ChatSquare }
])

const uv = reactive({ today: 1280, total: '52,314', rate: 12 })
const pv = reactive({ today: 3420, total: '183,902', rate: -3 })

const homeStats = computed(() => [
  { label: '在线用户', value: '1', trend: '已连接' },
  { label: '访客数 (UV)', value: uv.today.toString(), trend: `累计 ${uv.total}` },
  { label: '浏览量 (PV)', value: pv.today.toString(), trend: `累计 ${pv.total}` },
  { label: '模块数', value: modules.length.toString(), trend: '核心功能入口' }
])

const pendingTasks = ref([
  { id: 1, title: '审核新课程提交', deadline: '今日 18:00', status: 'pending' },
  { id: 2, title: '处理问答举报', deadline: '明日 10:00', status: 'progress' },
  { id: 3, title: '教师账号权限复核', deadline: '周五 17:00', status: 'pending' }
])

const schedules = [
  { id: 1, weekday: '周三', topic: '课程审核会', time: '10:00 - 12:00' },
  { id: 2, weekday: '周四', topic: '资源分级讨论', time: '14:00 - 16:00' },
  { id: 3, weekday: '周五', topic: '运营周报', time: '09:00 - 09:30' }
]

const userInitial = computed(() =>
  currentUser.name ? currentUser.name.slice(0, 1) : '管'
)

const handleNavClick = (id) => {
  activeNav.value = id
  activeModuleId.value = id === 'home' ? modules[0].id : id
  if (id !== 'home') goModule(id)
}

const goModule = (id) => {
  if (id === 'course') {
    router.push('/courses')
  } else if (id === 'teacher') {
    router.push('/admin/teachers')
  } else if (id === 'student') {
    router.push('/admin/students')
  } else if (id === 'resource') {
    router.push('/admin/resources')
  } else if (id === 'qa') {
    router.push('/admin/questions')
  } else if (id === 'home') {
    router.push('/admin')
  } else {
    router.push('/admin')
  }
}

const handleLogout = async () => {
  try {
    await authApi.logout()
  } catch (e) {
    console.error('退出登录失败:', e)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
    router.push('/')
  }
}

</script>

<style scoped>
:global(body) {
  background: #f5f7fb;
}

.dashboard-container {
  padding: 16px;
}

.welcome-card {
  margin-bottom: 12px;
}

.welcome-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.welcome-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.greeting {
  font-size: 18px;
  font-weight: 700;
  color: #1f2d3d;
}

.subtitle {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.actions {
  display: flex;
  gap: 8px;
}

.module-card {
  margin-top: 8px;
}

.chart-card,
.timeline-card {
  height: 100%;
}

.links-card {
  margin: 12px 0;
}

.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.module-desc {
  color: var(--el-text-color-regular);
  margin-bottom: 12px;
}

.stat-card {
  border-radius: 12px;
}

.header-user {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.stat-label {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.stat-value {
  margin-top: 6px;
  font-size: 24px;
  font-weight: 700;
  color: var(--el-color-primary);
}

.stat-extra {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.module-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.chart-placeholder {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
  background: var(--el-fill-color-light);
  border-radius: 8px;
  border: 1px dashed var(--el-border-color);
}

.footer {
  text-align: center;
  color: var(--el-text-color-secondary);
  padding: 12px 0;
  font-size: 13px;
}

@media (max-width: 768px) {
  .welcome-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>