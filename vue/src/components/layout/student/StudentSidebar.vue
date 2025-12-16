<template>
  <el-aside :width="collapsed ? '64px' : '240px'" class="student-aside">
    <div class="aside-header" v-if="!collapsed">
      <el-avatar :size="60" :src="userInfo.avatar" />
      <div class="user-name">{{ userInfo.name }}</div>
      <div class="user-role">学生</div>
    </div>
    <div class="aside-header-collapsed" v-else>
      <el-avatar :size="40" :src="userInfo.avatar" />
    </div>

    <!-- 侧边栏菜单 -->
    <el-menu
      :default-active="activeMenu"
      class="aside-menu"
      :collapse="collapsed"
      background-color="#1e3a5f"
      text-color="#ffffff"
      active-text-color="#ffd04b"
      router
    >
      <el-menu-item index="/student/home">
        <el-icon><HomeFilled /></el-icon>
        <template #title>首页</template>
      </el-menu-item>

      <el-menu-item index="/student/courses">
        <el-icon><Reading /></el-icon>
        <template #title>课程</template>
      </el-menu-item>

      <el-menu-item index="/student/resources">
        <el-icon><FolderOpened /></el-icon>
        <template #title>资源</template>
      </el-menu-item>

      <el-menu-item index="/student/upload">
        <el-icon><Upload /></el-icon>
        <template #title>上传</template>
      </el-menu-item>

      <el-menu-item index="/student/questions">
        <el-icon><ChatDotRound /></el-icon>
        <template #title>问答广场</template>
      </el-menu-item>

      <el-menu-item index="/student/ask">
        <el-icon><EditPen /></el-icon>
        <template #title>提问</template>
      </el-menu-item>

      <el-menu-item index="/student/profile">
        <el-icon><User /></el-icon>
        <template #title>个人中心</template>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import {
  HomeFilled,
  Reading,
  FolderOpened,
  Upload,
  ChatDotRound,
  EditPen,
  User
} from '@element-plus/icons-vue'
import { computed, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const collapsed = ref(false)

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

// 当前激活的菜单（根据当前路由）
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.student-aside {
  background-color: #1e3a5f;
  overflow-y: auto;
  margin: 15px 0 15px 15px;
  height: calc(100% - 30px);
  border-radius: 15px;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  transition: width 0.3s;
}

.aside-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.aside-header-collapsed {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px;
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

.aside-menu:not(.el-menu--collapse) {
  width: 100%;
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
.student-aside::-webkit-scrollbar {
  width: 6px;
}

.student-aside::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.student-aside::-webkit-scrollbar-thumb:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .student-aside {
    width: 180px !important;
  }

  .aside-header {
    padding: 20px 10px;
  }

  .aside-header .el-avatar {
    width: 50px !important;
    height: 50px !important;
  }

  .user-name {
    font-size: 14px;
  }
}
</style>
