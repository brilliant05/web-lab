<template>
  <el-header class="admin-header">
    <div class="header-left">
      <img src="@/assets/logo.png" alt="上海理工大学" class="school-logo" />
    </div>
    <div class="header-center">
      <span class="system-title">大学生线上资源与问答管理系统</span>
    </div>
    <div class="header-right">
      <el-dropdown @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="60" :src="userInfo.avatarUrl || defaultAvatar" />
          <span class="username">{{ userInfo.username }}</span>
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

    <!-- 系统设置对话框 -->
    <el-dialog
      v-model="settingsVisible"
      title="系统设置"
      width="500px"
      @close="resetForm"
    >
      <el-tabs tab-position="left" style="height: 250px">
        <el-tab-pane label="安全设置">
          <div class="setting-content">
            <h4 class="setting-title">修改密码</h4>
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="80px"
              size="small"
            >
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdatePassword" :loading="loading">确认修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        <el-tab-pane label="通用设置">
          <div class="setting-content">
            <div class="setting-item">
              <span class="setting-label">深色模式</span>
              <el-switch
                v-model="isDark"
                inline-prompt
                :active-icon="Moon"
                :inactive-icon="Sunny"
                @change="toggleTheme"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </el-header>
</template>

<script setup>
import { authApi, updatePassword } from '@/api'
import notificationWS from '@/utils/notificationWebSocket'
import { ArrowDown, Moon, Sunny } from '@element-plus/icons-vue'
import { ElMessage, ElNotification } from 'element-plus'
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 用户信息
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo')) || {})
const settingsVisible = ref(false)
const passwordFormRef = ref(null)
const loading = ref(false)

// 主题设置
const isDark = ref(localStorage.getItem('theme') === 'dark')

const toggleTheme = (val) => {
  isDark.value = val
  if (val) {
    document.documentElement.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
}

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
}

const updateUserInfo = () => {
  userInfo.value = JSON.parse(localStorage.getItem('userInfo')) || {}
}

onMounted(() => {
  window.addEventListener('userInfoUpdated', updateUserInfo)
  // 初始化主题
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  }

  // 连接WebSocket
  if (userInfo.value.userId) {
    notificationWS.connect(userInfo.value.userId)
    notificationWS.addListener(handleNotification)
  }
})

onUnmounted(() => {
  window.removeEventListener('userInfoUpdated', updateUserInfo)
  notificationWS.removeListener(handleNotification)
  notificationWS.disconnect()
})

const handleNotification = (data) => {
  if (data.type === 'notification') {
    ElNotification({
      title: data.title,
      message: data.content,
      type: 'info',
      duration: 5000,
      onClick: () => {
        // 如果是问题通知，跳转到问题管理页面
        if (userInfo.value.role === 'TEACHER') {
           router.push('/teacher/questions')
        } else if (userInfo.value.role === 'STUDENT') {
           router.push('/student/questions')
        }
      }
    })
  }
}

// 顶部用户下拉菜单操作
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      // 根据角色跳转到不同的个人中心
      const role = userInfo.value.role ? userInfo.value.role.toLowerCase() : 'student'
      router.push(`/${role}/profile`)
      break
    case 'settings':
      settingsVisible.value = true
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        
        if (res.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          settingsVisible.value = false
          handleLogout()
        } else {
          ElMessage.error(res.message || '修改失败')
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = () => {
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    // 调用后端退出登录API
    await authApi.logout()
  } catch (error) {
    console.error('退出登录失败:', error)
    // 即使API调用失败，也要清除本地存储并退出
  }
  
  // 清除登录信息
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  
  // 显示成功消息（短暂显示）
  ElMessage.success('已退出登录')
  
  // 使用setTimeout确保消息显示后再跳转
  setTimeout(() => {
    // 使用window.location.href确保完全跳转并重置状态
    window.location.href = '/'
  }, 300)
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
  position: relative;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-center {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.system-title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
  white-space: nowrap;
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

@media screen and (max-width: 768px) {
  .header-center {
    display: none;
  }
}

@media screen and (max-width: 576px) {
  .admin-header {
    padding: 0 10px;
  }

  .school-name {
    font-size: 16px !important;
  }
}

.setting-content {
  padding: 0 20px;
}

.setting-title {
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
  font-weight: 500;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.setting-label {
  font-size: 14px;
  color: var(--el-text-color-regular);
}
</style>

