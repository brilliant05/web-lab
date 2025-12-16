<template>
  <el-header class="student-header">
    <div class="header-left">
      <img src="@/assets/logo.png" alt="上海理工大学" class="school-logo" />
      <span class="system-name">大学生线上学习资源共享与问答网站</span>
    </div>
    <div class="header-right">
      <!-- 消息通知 -->
      <el-dropdown @command="handleNotificationCommand" trigger="click">
        <div class="notification-bell">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <el-icon :size="20" style="cursor: pointer">
              <BellFilled />
            </el-icon>
          </el-badge>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <div class="notification-list">
              <div v-if="notifications.length === 0" class="empty-notification">暂无通知</div>
              <div
                v-for="notification in notifications.slice(0, 5)"
                :key="notification.id"
                class="notification-item"
                :class="{ unread: !notification.isRead }"
              >
                <div class="notification-title">{{ notification.title }}</div>
                <div class="notification-time">{{ notification.time }}</div>
              </div>
            </div>
            <el-dropdown-item divided>
              <el-button link type="primary" @click="goToMessages">查看全部</el-button>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <!-- 用户信息下拉 -->
      <el-dropdown @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="35" :src="userInfo.avatar" />
          <span class="username">{{ userInfo.name }}</span>
          <el-icon class="el-icon--right">
            <ArrowDown />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { ArrowDown, BellFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api'

const router = useRouter()

// 用户信息
const userInfo = ref({
  name: '学生',
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
})

// 未读消息数量（mock数据，后续对接API）
const unreadCount = ref(5)

// 通知列表（mock数据，后续对接API）
const notifications = ref([
  { id: 1, title: '您的问题已收到回答', time: '2分钟前', isRead: false },
  { id: 2, title: '您上传的资源已通过审核', time: '1小时前', isRead: false },
  { id: 3, title: '系统通知：新功能上线', time: '3小时前', isRead: true }
])

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

// 处理通知下拉菜单命令
const handleNotificationCommand = (command) => {
  // 处理通知相关操作
}

// 跳转到消息中心
const goToMessages = () => {
  router.push('/student/profile?tab=messages')
}

// 用户下拉菜单操作
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
.student-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #1e3a5f;
  color: white;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left .school-logo {
  height: 40px;
  object-fit: contain;
}

.system-name {
  font-size: 16px;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-bell {
  cursor: pointer;
  padding: 5px;
}

.notification-badge {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.user-info .username {
  font-size: 14px;
  color: white;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
  padding: 5px 0;
}

.notification-item {
  padding: 10px 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.notification-item:hover {
  background-color: #f5f5f5;
}

.notification-item.unread {
  background-color: #e6f7ff;
}

.notification-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

.empty-notification {
  padding: 20px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

@media screen and (max-width: 768px) {
  .system-name {
    display: none;
  }
}
</style>
