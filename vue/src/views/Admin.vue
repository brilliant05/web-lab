<template>
  <div class="admin-page">
    <div class="dashboard-card">
      <header class="dashboard-top">
        <div class="logo-chip">
          <span>上海理工大学Logo</span>
        </div>
        <div class="title-chip">大学生线上学习资源共享与问答网站</div>
        <div class="status-chip">
          <span class="status-label">用户状态</span>
          <span class="status-value">{{ currentUser.role }}</span>
        </div>
      </header>

      <div class="dashboard-body">
        <aside class="nav-panel">
          <button
            v-for="module in modules"
            :key="module.id"
            class="nav-chip"
            :class="{ active: module.id === activeModule.id }"
            @click="setActive(module)"
          >
            {{ module.label }}
          </button>
        </aside>

        <section class="workspace">
          <div class="workspace-header">
            <div>
              <p class="workspace-label">当前模块</p>
              <h2 class="workspace-title">{{ activeModule.label }}</h2>
            </div>
            <button class="primary-btn" @click="goModule(activeModule.id)">新建{{ activeModule.short }}</button>
          </div>

          <div class="workspace-content">
            <p class="workspace-description">
              {{ activeModule.description }}
            </p>

            <div class="meta-grid">
              <div class="meta-card" v-for="meta in activeModule.stats" :key="meta.label">
                <p class="meta-label">{{ meta.label }}</p>
                <p class="meta-value">{{ meta.value }}</p>
              </div>
            </div>

            <div class="workspace-actions">
              <button class="ghost-btn">查看记录</button>
              <button class="ghost-btn">导出数据</button>
              <button class="ghost-btn">分配权限</button>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

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
  role: 'ADMIN'
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

const activeModule = ref(modules[0])

const setActive = (module) => {
  activeModule.value = module
}

const goModule = (id) => {
  if (id === 'course') {
    router.push('/admin/courses')
  } else if (id === 'teacher') {
    router.push('/admin/teachers')
  } else if (id === 'student') {
    router.push('/admin/students')
  }
}
</script>

<style scoped>
:global(body) {
  margin: 0;
  font-family: 'Montserrat', sans-serif;
  background-color: #ecf0f3;
  color: #181818;
}

.admin-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: stretch;
  align-items: stretch;
  padding: 0;
  background-color: #ecf0f3;
}

.dashboard-card {
  width: 100%;
  min-height: 100vh;
  background-color: #ecf0f3;
  border-radius: 0;
  box-shadow: 18px 18px 40px #d1d9e6, -12px -12px 30px #f9f9f9;
  padding: 48px clamp(20px, 3vw, 60px);
  display: flex;
  flex-direction: column;
  gap: 36px;
}

.dashboard-top {
  display: grid;
  width: 100%;
  grid-template-columns: minmax(200px, 1fr) minmax(320px, 2fr) minmax(200px, 1fr);
  gap: 10px;
  align-items: center;
}

.logo-chip,
.title-chip,
.status-chip {
  background-color: #ecf0f3;
  border-radius: 999px;
  padding: 16px 24px;
  box-shadow: inset 4px 4px 8px #d1d9e6, inset -4px -4px 8px #f9f9f9;
  text-align: center;
  font-weight: 600;
  color: #4b4f57;
}

.title-chip {
  font-size: 18px;
}

.status-chip {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-value {
  background-color: #4b70e2;
  color: #fff;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 12px;
  letter-spacing: 1px;
}

.dashboard-body {
  display: grid;
  grid-template-columns: minmax(220px, 0.7fr) minmax(720px, 2.5fr);
  gap: clamp(20px, 2vw, 40px);
  height: 100%;
}

.nav-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.nav-chip {
  border: none;
  outline: none;
  border-radius: 35px;
  padding: 18px 24px;
  background-color: #ecf0f3;
  box-shadow: 6px 6px 12px #d1d9e6, -6px -6px 12px #f9f9f9;
  font-size: 16px;
  letter-spacing: 0.5px;
  color: #4b4f57;
  cursor: pointer;
  transition: all 0.25s ease;
}

.nav-chip:hover {
  transform: translateX(6px);
}

.nav-chip.active {
  background: linear-gradient(135deg, #5773ff, #8ab4ff);
  color: #fff;
  box-shadow: 4px 4px 12px rgba(71, 104, 255, 0.3);
}

.workspace {
  background-color: #ecf0f3;
  border-radius: 32px;
  padding: 32px;
  box-shadow: inset 6px 6px 18px #d1d9e6, inset -6px -6px 18px #f9f9f9;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.workspace-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.workspace-label {
  margin: 0;
  color: #a0a5a8;
  letter-spacing: 1px;
  text-transform: uppercase;
  font-size: 12px;
}

.workspace-title {
  margin: 4px 0 0;
  font-size: 28px;
  color: #181818;
}

.primary-btn {
  border: none;
  border-radius: 28px;
  padding: 12px 32px;
  background-color: #4b70e2;
  color: #fff;
  font-weight: 600;
  box-shadow: 10px 10px 20px rgba(75, 112, 226, 0.3),
    -6px -6px 12px rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: transform 0.2s ease;
}

.primary-btn:hover {
  transform: scale(0.98);
}

.workspace-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.workspace-description {
  margin: 0;
  color: #4b4f57;
  line-height: 1.6;
  font-size: 16px;
}

.meta-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 18px;
}

.meta-card {
  padding: 18px;
  border-radius: 24px;
  background-color: #ecf0f3;
  box-shadow: 8px 8px 18px #d1d9e6, -8px -8px 18px #f9f9f9;
}

.meta-label {
  margin: 0;
  color: #a0a5a8;
  font-size: 13px;
  letter-spacing: 0.5px;
}

.meta-value {
  margin: 6px 0 0;
  font-size: 26px;
  font-weight: 700;
  color: #181818;
}

.workspace-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.ghost-btn {
  border: none;
  border-radius: 24px;
  padding: 12px 26px;
  background-color: #ecf0f3;
  color: #4b70e2;
  font-weight: 600;
  box-shadow: 5px 5px 12px #d1d9e6, -5px -5px 12px #f9f9f9;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.ghost-btn:hover {
  transform: translateY(2px);
}

@media (max-width: 1100px) {
  .dashboard-card {
    width: 100%;
  }

  .dashboard-top {
    grid-template-columns: 1fr;
  }

  .dashboard-body {
    grid-template-columns: 1fr;
  }

  .nav-panel {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .nav-chip {
    flex: 1 1 45%;
  }
}
</style>