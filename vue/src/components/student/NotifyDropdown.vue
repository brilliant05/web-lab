<template>
  <el-dropdown trigger="click" @command="handleCommand" @visible-change="handleVisibleChange">
    <div class="notification-trigger">
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
        <el-icon :size="20" style="cursor: pointer; color: #fff">
          <Bell />
        </el-icon>
      </el-badge>
    </div>
    <template #dropdown>
      <el-dropdown-menu class="notification-dropdown">
        <div class="notification-header">
          <span class="notification-title">通知</span>
          <el-button
            v-if="unreadCount > 0"
            link
            type="primary"
            size="small"
            @click.stop="handleMarkAllRead"
          >
            全部已读
          </el-button>
        </div>
        <div class="notification-list">
          <div v-if="notifications.length === 0" class="empty-notification">
            <el-empty description="暂无通知" :image-size="60" />
          </div>
          <div
            v-for="notification in notifications.slice(0, 5)"
            :key="notification.notificationId"
            class="notification-item"
            :class="{ unread: notification.isRead === 0 || notification.isRead === false }"
            @click="handleItemClick(notification)"
          >
            <div class="notification-item-content">
              <div class="notification-item-title">{{ notification.content || notification.title }}</div>
              <div class="notification-item-time">{{ formatTime(notification.createTime) }}</div>
            </div>
            <div v-if="notification.isRead === 0 || notification.isRead === false" class="unread-dot"></div>
          </div>
        </div>
        <el-dropdown-item divided>
          <el-button link type="primary" @click="goToMessages">查看全部</el-button>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import {
    getNotificationList,
    getUnreadCount,
    markAllNotificationRead,
    markNotificationRead
} from '@/api'
import { Bell } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 未读数量
const unreadCount = ref(0)

// 通知列表
const notifications = ref([])

// 定时器
let timer = null

// 加载未读数量
const loadUnreadCount = async () => {
  try {
    const response = await getUnreadCount()
    if (response && response.code === 200 && response.data) {
      unreadCount.value = response.data.unreadCount || 0
    }
  } catch (error) {
    console.error('获取未读通知数量失败:', error)
  }
}

// 加载通知列表
const loadNotifications = async () => {
  try {
    const response = await getNotificationList({ pageNum: 1, pageSize: 5 })
    if (response && response.code === 200 && response.data) {
      notifications.value = response.data.records || []
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
  }
}

// 标记单条已读
const handleItemClick = async (notification) => {
  if (notification.isRead === 0 || notification.isRead === false) {
    try {
      await markNotificationRead(notification.notificationId)
      notification.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
      ElMessage.success('已标记为已读')
    } catch (error) {
      console.error('标记通知已读失败:', error)
    }
  }
}

// 全部已读
const handleMarkAllRead = async () => {
  try {
    await markAllNotificationRead()
    notifications.value.forEach(item => {
      item.isRead = 1
    })
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    console.error('标记全部已读失败:', error)
  }
}

// 下拉菜单命令处理
const handleCommand = (command) => {
  // 可以在需要时处理命令
}

// 跳转到消息中心
const goToMessages = () => {
  router.push('/student/profile?tab=messages')
}

// 下拉框显示/隐藏时触发
const handleVisibleChange = (visible) => {
  if (visible) {
    loadNotifications()
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadUnreadCount()
  // 定时刷新未读数量（每30秒）
  timer = setInterval(() => {
    loadUnreadCount()
  }, 30000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

// 暴露方法供父组件调用
defineExpose({
  loadUnreadCount,
  loadNotifications
})
</script>

<style scoped>
.notification-trigger {
  cursor: pointer;
  padding: 5px;
  display: flex;
  align-items: center;
  color: #fff;
  transition: opacity 0.2s;
}

.notification-trigger:hover {
  opacity: 0.8;
}

.notification-badge {
  cursor: pointer;
}

.notification-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
  border-color: #fff;
}

.notification-dropdown {
  min-width: 320px;
  max-width: 400px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

.notification-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.empty-notification {
  padding: 40px 20px;
}

.notification-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f5f7fa;
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #ecf5ff;
}

.notification-item-content {
  flex: 1;
  min-width: 0;
  position: relative;
  padding-right: 8px;
  padding-bottom: 4px;
}

.notification-item-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  line-height: 1.4;
  word-wrap: break-word;
  max-height: 2.8em;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-item-time {
  font-size: 12px;
  color: #909399;
  position: absolute;
  bottom: 0;
  right: 0;
  white-space: nowrap;
}

.unread-dot {
  width: 8px;
  height: 8px;
  background-color: #409eff;
  border-radius: 50%;
  margin-left: 8px;
  flex-shrink: 0;
}
</style>
