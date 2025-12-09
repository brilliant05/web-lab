<template>
  <el-aside width="240px" class="admin-aside">
    <div class="aside-header">
      <el-avatar :size="80" :src="userInfo.avatar" />
      <div class="user-name">{{ userInfo.name }}</div>
      <div class="user-role">{{ userInfo.role }}</div>
    </div>

    <!-- 侧边栏菜单 -->
    <el-menu
      :default-active="activeMenu"
      class="aside-menu"
      background-color="#1e3a5f"
      text-color="#ffffff"
      active-text-color="#ffd04b"
      router
    >
      <el-menu-item index="/admin/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <span>工作台</span>
      </el-menu-item>

      <el-menu-item index="/admin/students">
        <el-icon><User /></el-icon>
        <span>学生管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/teachers">
        <el-icon><UserFilled /></el-icon>
        <span>教师管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/courses">
        <el-icon><Reading /></el-icon>
        <span>课程管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/resources">
        <el-icon><FolderOpened /></el-icon>
        <span>资源管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/questions">
        <el-icon><ChatDotRound /></el-icon>
        <span>问答管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/notifications">
        <el-icon><BellFilled /></el-icon>
        <span>通知管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/statistics">
        <el-icon><DataAnalysis /></el-icon>
        <span>数据统计</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import {
  BellFilled,
  ChatDotRound,
  DataAnalysis,
  FolderOpened,
  HomeFilled,
  Reading,
  User,
  UserFilled
} from '@element-plus/icons-vue'
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 用户信息
const userInfo = ref({
  name: '管理员',
  role: '系统管理员',
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
})

// 当前激活的菜单（根据当前路由）
const activeMenu = computed(() => route.path)
</script>

<style scoped>
/* 左侧侧边栏 */
.admin-aside {
  background-color: #1e3a5f;
  overflow-y: auto;
  margin: 15px 0 15px 15px;
  height: calc(100% - 30px);
  border-radius: 15px;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
}

.aside-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.user-name {
  margin-top: 15px;
  font-size: 16px;
  font-weight: bold;
  color: white;
}

.user-role {
  margin-top: 5px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.aside-menu {
  border: none;
}

.aside-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.aside-menu .el-menu-item:hover {
  background-color: #2c4d6f !important;
}

.aside-menu .el-menu-item.is-active {
  background-color: #2c4d6f !important;
}

/* 滚动条样式 */
.admin-aside::-webkit-scrollbar {
  width: 6px;
}

.admin-aside::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.admin-aside::-webkit-scrollbar-thumb:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .admin-aside {
    width: 180px !important;
  }

  .aside-header {
    padding: 20px 10px;
  }

  .aside-header .el-avatar {
    width: 60px !important;
    height: 60px !important;
  }

  .user-name {
    font-size: 14px;
  }
}
</style>

