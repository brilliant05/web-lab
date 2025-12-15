<template>
  <div class="login-page">
    <div class="login-hero">
      <div class="hero-overlay">
        <div class="hero-title">学习资源与问答后台</div>
        <div class="hero-sub">来自USST别样的风格体验</div>
        <div class="hero-meta">
          <el-tag round type="success">UV {{ uv.today }}</el-tag>
          <el-tag round type="warning">PV {{ pv.today }}</el-tag>
        </div>
      </div>
    </div>

    <div class="login-panel">
      <div class="panel-header">
        <div>
          <div class="panel-title">欢迎登录</div>
          <div class="panel-sub">使用账号密码进入后台</div>
        </div>
        <el-segmented v-model="mode" :options="['登录', '注册']" size="large" />
      </div>

      <div v-if="mode === '登录'" class="panel-body">
        <el-form :model="loginForm" label-position="top" @submit.prevent @keydown.enter="handleLogin">
          <el-form-item label="用户名">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
            v-model="loginForm.password"
            type="password"
              show-password
              placeholder="请输入密码"
            />
          </el-form-item>
          <div class="panel-actions">
            <el-button type="primary" size="large" class="w-full" :loading="isLoading" @click="handleLogin">
              {{ isLoading ? '登录中...' : '进入后台' }}
            </el-button>
          </div>
        </el-form>
      </div>

      <div v-else class="panel-body">
        <el-form :model="registerForm" label-position="top" @submit.prevent>
          <el-form-item label="用户名">
            <el-input v-model="registerForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="学号">
            <el-input v-model="registerForm.studentId" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="registerForm.password"
              type="password"
              show-password
              placeholder="请输入密码（至少6位）"
            />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              show-password
              placeholder="请再次输入密码"
            />
          </el-form-item>
          <div class="panel-actions">
            <el-button
              type="primary"
              size="large"
              class="w-full"
              :loading="isRegisterLoading"
              @click="handleRegister"
            >
              {{ isRegisterLoading ? '注册中...' : '创建账号' }}
            </el-button>
        </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api/index'

const router = useRouter()
const mode = ref('登录')

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  studentId: ''
})

const isRegisterLoading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const isLoading = ref(false)
const uv = reactive({ today: 128 })
const pv = reactive({ today: 326 })

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }

  if (!registerForm.studentId || !registerForm.studentId.trim()) {
    ElMessage.warning('请输入学号')
    return
  }

  if (!registerForm.password || !registerForm.password.trim()) {
    ElMessage.warning('请输入密码')
    return
  }

  if (registerForm.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  isRegisterLoading.value = true
  try {
    const registerData = {
      username: registerForm.username.trim(),
      password: registerForm.password,
      studentId: registerForm.studentId.trim()
    }

    const response = await authApi.register(registerData)

    if (response && response.code === 200) {
      ElMessage.success(response.message || '注册成功')
      setTimeout(() => {
        registerForm.username = ''
        registerForm.studentId = ''
        registerForm.password = ''
        registerForm.confirmPassword = ''
        mode.value = '登录'
        loginForm.username = registerData.username
      }, 500)
    } else {
      const errorMsg = response?.message || '注册失败'
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    const msg = error?.response?.data?.message || error?.message || '注册失败'
    ElMessage.error(msg)
  } finally {
    isRegisterLoading.value = false
  }
}

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  isLoading.value = true
  try {
    const response = await authApi.login({
      username: loginForm.username,
      password: loginForm.password
    })

    if (response && response.code === 200) {
      const data = response.data
      if (!data) {
        ElMessage.error('登录失败：响应数据为空')
        return
      }

      const token = data.token
      if (token) {
        localStorage.setItem('token', token)
      }

      const userInfo = data.userInfo || data
      if (userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
      }

      ElMessage.success(response.message || '登录成功')

      const role = (userInfo?.role || data?.role || '').toUpperCase()
      setTimeout(() => {
        let targetPath = '/admin'
        if (role === 'TEACHER') targetPath = '/teacher/home'
        else if (role === 'STUDENT') targetPath = '/student/home'

        router
          .push(targetPath)
          .catch(() => (window.location.href = targetPath))
      }, 300)
    } else {
      const errorMsg = response?.message || '登录失败：响应数据格式错误'
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    const msg =
      error?.response?.data?.message ||
      error?.message ||
      '用户名或密码错误'
    ElMessage.error(msg)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
:global(body) {
  margin: 0;
  font-family: 'Montserrat', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: #f5f7fb;
}

.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  background: linear-gradient(135deg, #ecf2ff 0%, #f7f9fc 40%, #fefefe 100%);
}

.login-hero {
  position: relative;
  background: url('../assets/back.jpeg') center bottom / cover no-repeat;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.82), rgba(99, 102, 241, 0.78));
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 48px;
  color: #fff;
  gap: 12px;
}

.hero-title {
  font-size: 32px;
  font-weight: 700;
}

.hero-sub {
  font-size: 16px;
  opacity: 0.9;
}

.hero-meta {
  display: flex;
  gap: 8px;
}

.login-panel {
  max-width: 520px;
  margin: auto;
  background: #fff;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  border-radius: 18px;
  padding: 32px 32px 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

.panel-sub {
  color: #6b7280;
  margin-top: 4px;
}

.panel-body {
  margin-top: 4px;
}

.panel-actions {
  margin-top: 12px;
}

.w-full {
  width: 100%;
}

@media (max-width: 960px) {
  .login-page {
    grid-template-columns: 1fr;
  }
  .login-hero {
    min-height: 220px;
  }
}
</style>

