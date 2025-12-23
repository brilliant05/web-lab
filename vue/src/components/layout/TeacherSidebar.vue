<template>
  <el-aside width="240px" class="teacher-aside">
    <div class="aside-header">
      <el-avatar :size="80" :src="userInfo.avatar" />
      <div class="user-name">{{ userInfo.name }}</div>
      <div class="user-role">{{ userInfo.role }}</div>
    </div>

    <!-- 侧边栏菜单 -->
    <el-menu
      :default-active="activeMenu"
      class="aside-menu"
      background-color="#2c3e50"
      text-color="#ffffff"
      active-text-color="#42b983"
      router
    >
      <el-menu-item index="/teacher/home">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>

      <el-menu-item index="/teacher/courses">
        <el-icon><Reading /></el-icon>
        <span>我的课程</span>
      </el-menu-item>

      <el-menu-item index="/teacher/resources">
        <el-icon><FolderOpened /></el-icon>
        <span>资源管理</span>
      </el-menu-item>

      <el-menu-item index="/teacher/questions">
        <el-icon><ChatDotRound /></el-icon>
        <span>问答管理</span>
      </el-menu-item>

      <el-menu-item index="/teacher/profile">
        <el-icon><User /></el-icon>
        <span>个人中心</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import {
    ChatDotRound,
    FolderOpened,
    HomeFilled,
    Reading,
    User
} from '@element-plus/icons-vue'
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 用户信息
const userInfo = ref({
  name: '教师用户',
  role: '教师',
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
})

// 当前激活的菜单（根据当前路由）
const activeMenu = computed(() => route.path)
</script>

<style scoped>
/* 左侧侧边栏 */
.teacher-aside {
  background-color: #2c3e50;
  overflow-y: auto;
  margin: 15px 0 15px 15px;
  height: calc(100% - 30px);
  border-radius: 15px;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
}

.aside-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.user-name {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  margin-top: 15px;
}

.user-role {
  color: #42b983;
  font-size: 14px;
  margin-top: 5px;
}

.aside-menu {
  border-right: none;
  margin-top: 10px;
}

:deep(.el-menu-item) {
  margin: 5px 15px;
  border-radius: 8px;
  height: 50px;
  line-height: 50px;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(66, 185, 131, 0.1) !important;
  font-weight: bold;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.05) !important;
}
</style>
