<template>
  <div class="quick-actions">
    <div
      v-for="action in actions"
      :key="action.path"
      class="action-card"
      @click="handleClick(action.path)"
    >
      <div class="action-icon" :style="{ background: action.gradient }">
        <el-icon :size="32"><component :is="action.icon" /></el-icon>
      </div>
      <div class="action-label">{{ action.label }}</div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import {
  Reading,
  FolderOpened,
  ChatDotRound,
  User
} from '@element-plus/icons-vue'

const router = useRouter()

// 快捷入口配置
const actions = [
  {
    label: '课程',
    path: '/student/courses',
    icon: Reading,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    label: '资源',
    path: '/student/resources',
    icon: FolderOpened,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    label: '问答',
    path: '/student/questions',
    icon: ChatDotRound,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    label: '个人中心',
    path: '/student/profile',
    icon: User,
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
]

const handleClick = (path) => {
  router.push(path)
}
</script>

<style scoped>
.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.action-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.action-icon {
  width: 72px;
  height: 72px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin: 0 auto 16px;
}

.action-label {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

@media screen and (max-width: 768px) {
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .action-card {
    padding: 24px 16px;
  }

  .action-icon {
    width: 60px;
    height: 60px;
    margin-bottom: 12px;
  }

  .action-label {
    font-size: 14px;
  }
}
</style>
