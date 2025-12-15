<template>
  <el-header class="admin-header">
    <div class="header-left">
      <img src="@/assets/logo.png" alt="上海理工大学" class="school-logo" />
    </div>
    <div class="header-right">
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
            <el-dropdown-item command="settings">系统设置</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 用户信息
const userInfo = ref({
  name: '管理员',
  role: '系统管理员',
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
})

// 顶部用户下拉菜单操作
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('打开个人中心')
      break
    case 'settings':
      ElMessage.info('打开系统设置')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('退出成功')
    // 清除登录信息
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    // 跳转到登录页
    router.push('/')
  }).catch(() => {
    // 取消操作
  })
}
</script>

<style scoped>
/* 顶部导航栏 */
.admin-header {
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
}

.header-left .school-logo {
  height: 40px;
  object-fit: contain;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.header-right .username {
  font-size: 14px;
  color: white;
}

@media screen and (max-width: 576px) {
  .admin-header {
    padding: 0 10px;
  }

  .school-name {
    font-size: 16px !important;
  }
}
</style>

