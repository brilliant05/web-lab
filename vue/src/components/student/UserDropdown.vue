<template>
  <el-dropdown @command="handleCommand" trigger="click">
    <div class="user-trigger">
      <el-avatar :size="36" :src="userInfo.avatar" />
      <span class="username">{{ userInfo.name }}</span>
      <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-dropdown-item>
        <el-dropdown-item divided command="logout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'

const router = useRouter()

// 用户信息
const userInfo = ref({
  name: '学生',
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
})

// 从localStorage读取用户信息
onMounted(() => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      const user = JSON.parse(userInfoStr)
      userInfo.value.name = user.realName || user.username || '学生'
      if (user.avatar) {
        userInfo.value.avatar = user.avatar
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
})

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/student/profile')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await authApi.logout()
  } catch (error) {
    console.error('退出登录失败:', error)
  }
  
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.success('已退出登录')
  
  setTimeout(() => {
    window.location.href = '/'
  }, 300)
}
</script>

<style scoped>
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background-color 0.2s;
}

.user-trigger:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.username {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}

.dropdown-icon {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
