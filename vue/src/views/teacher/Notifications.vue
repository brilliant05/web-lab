<template>
  <div class="notifications-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的通知</span>
          <el-button type="primary" link @click="markAllRead">全部已读</el-button>
        </div>
      </template>
      
      <div v-loading="loading" class="notification-list">
        <el-empty v-if="list.length === 0" description="暂无通知" />
        
        <div 
          v-for="item in list" 
          :key="item.notificationId" 
          class="notification-item"
          :class="{ 'unread': !item.isRead }"
          @click="handleItemClick(item)"
        >
          <div class="item-icon">
            <el-icon v-if="item.notificationType === 1" class="icon-question"><ChatDotRound /></el-icon>
            <el-icon v-else class="icon-system"><Bell /></el-icon>
          </div>
          <div class="item-content">
            <div class="item-title">
              {{ item.title }}
              <span v-if="!item.isRead" class="unread-dot"></span>
            </div>
            <div class="item-desc">{{ item.content }}</div>
            <div class="item-time">{{ item.createTime }}</div>
          </div>
        </div>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { getNotificationList, markAllNotificationRead, markNotificationRead } from '@/api'
import { Bell, ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getNotificationList({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      list.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取通知失败', error)
  } finally {
    loading.value = false
  }
}

const markAllRead = async () => {
  try {
    await markAllNotificationRead()
    ElMessage.success('已全部标记为已读')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleItemClick = async (item) => {
  // 标记为已读
  if (!item.isRead) {
    try {
      await markNotificationRead(item.notificationId)
      item.isRead = 1
    } catch (e) {
      console.error(e)
    }
  }
  
  // 跳转逻辑
  if (item.notificationType === 1) { // 问题通知
    router.push('/teacher/questions')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.notifications-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-list {
  min-height: 400px;
}

.notification-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: all 0.3s;
  border-left: 4px solid transparent;
}

.notification-item:hover {
  background-color: var(--el-fill-color-light);
}

.notification-item.unread {
  background-color: var(--el-color-primary-light-9);
  border-left-color: var(--el-color-primary);
}

.notification-item.unread .item-title {
  font-weight: bold;
  color: var(--el-text-color-primary);
}

.item-icon {
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-icon .el-icon {
  font-size: 24px;
}

.icon-question {
  color: var(--el-color-warning);
}

.icon-system {
  color: var(--el-color-primary);
}

.item-content {
  flex: 1;
}

.item-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: var(--el-color-danger);
  margin-left: 8px;
}

.item-desc {
  color: var(--el-text-color-regular);
  font-size: 14px;
  margin-bottom: 5px;
}

.item-time {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
