<template>
  <div class="teacher-page">
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
            <button class="primary-btn" v-if="activeModule.actionText">
              {{ activeModule.actionText }}
            </button>
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
              <button 
                class="ghost-btn" 
                v-for="action in activeModule.actions" 
                :key="action"
              >
                {{ action }}
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

const modules = [
  {
    id: 'home',
    label: '首页',
    actionText: '查看课程',
    description: '管理您所教授的课程，查看课程统计和学生学习情况。',
    stats: [
      { label: '我的课程', value: '5' },
      { label: '学生总数', value: '186' },
      { label: '活跃学生', value: '142' }
    ],
    actions: ['课程列表', '课程统计', '学生管理']
  },
  {
    id: 'resources',
    label: '资源管理',
    actionText: '发布资源',
    description: '上传和管理高质量学习资源，为学生提供优质学习材料。',
    stats: [
      { label: '已发布', value: '48' },
      { label: '待审核', value: '2' },
      { label: '下载量', value: '3.2k' }
    ],
    actions: ['我的资源', '发布资源', '资源统计']
  },
  {
    id: 'questions',
    label: '问答管理',
    actionText: '回答问题',
    description: '查看学生提问，及时回答学生问题，帮助学生解决学习困难。',
    stats: [
      { label: '待回答', value: '12' },
      { label: '已回答', value: '156' },
      { label: '今日回答', value: '5' }
    ],
    actions: ['待回答问题', '已回答问题', '问题统计']
  },
  {
    id: 'profile',
    label: '个人中心',
    actionText: '编辑资料',
    description: '管理个人信息、查看教学数据和消息通知。',
    stats: [
      { label: '未读消息', value: '8' },
      { label: '教学时长', value: '320h' },
      { label: '学生评价', value: '4.8' }
    ],
    actions: ['我的资源', '我的回答', '消息中心']
  }
]

const currentUser = reactive({
  name: '教师',
  role: 'TEACHER'
})

// 从 localStorage 读取用户信息
const userInfo = localStorage.getItem('userInfo')
if (userInfo) {
  try {
    const user = JSON.parse(userInfo)
    currentUser.name = user.realName || user.username || '教师'
    currentUser.role = user.role || 'TEACHER'
  } catch (e) {
    console.error('解析用户信息失败:', e)
  }
}

const activeModule = ref(modules[0])

const setActive = (module) => {
  activeModule.value = module
}
</script>

<style scoped>
:global(body) {
  margin: 0;
  font-family: 'Montserrat', sans-serif;
  background-color: #ecf0f3;
  color: #181818;
}

.teacher-page {
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
  border-radius: 0;
  padding: 60px clamp(32px, 5vw, 80px);
  background-color: #ecf0f3;
  display: flex;
  flex-direction: column;
  gap: 30px;
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

