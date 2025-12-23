<template>
  <el-dropdown @command="handleCommand" trigger="click">
    <div class="user-trigger">
      <el-avatar :size="36" :src="userInfo.avatarUrl">
        <el-icon><User /></el-icon>
      </el-avatar>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'
import { fixImageUrl } from '@/utils/image'

const router = useRouter()

// 用户信息
const userInfo = ref({
  name: '学生',
  avatarUrl: ''
})

// 从localStorage读取用户信息
const loadUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      const user = JSON.parse(userInfoStr)
      userInfo.value.name = user.realName || user.username || '学生'
      if (user.avatarUrl) {
        userInfo.value.avatarUrl = fixImageUrl(user.avatarUrl)
        console.log('UserDropdown 加载头像URL:', userInfo.value.avatarUrl)
      } else {
        console.log('UserDropdown 未找到头像URL')
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  } else {
    console.log('UserDropdown localStorage 中没有用户信息')
  }
}

const handleUserInfoUpdate = () => {
  console.log('UserDropdown 收到 userInfoUpdated 事件')
  loadUserInfo()
}

onMounted(() => {
  loadUserInfo()
  // 监听自定义事件，实时更新头像
  window.addEventListener('userInfoUpdated', handleUserInfoUpdate)
})

onUnmounted(() => {
  window.removeEventListener('userInfoUpdated', handleUserInfoUpdate)
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
