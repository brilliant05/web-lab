<template>
  <div class="auth-page">
    <div class="main">
      <div
        id="a-container"
        :class="['container', 'a-container', { 'is-txl': isSignUpMode }]"
      >
        <form class="form" @submit.prevent="handleRegister">
          <h2 class="form_title title">创建你的账户</h2>
          <span class="form__span">使用用户名创建</span>
          <input
            v-model="registerForm.username"
            class="form__input"
            type="text"
            placeholder="用户名"
            required
          />
          <input
            v-model="registerForm.studentId"
            class="form__input"
            type="text"
            placeholder="学号"
            required
          />
          <input
            v-model="registerForm.password"
            class="form__input"
            type="password"
            placeholder="密码（至少6位）"
            required
            minlength="6"
          />
          <input
            v-model="registerForm.confirmPassword"
            class="form__input"
            type="password"
            placeholder="确认密码"
            required
          />
          <button 
            class="form__button button submit" 
            type="submit"
            :disabled="isRegisterLoading"
          >
            {{ isRegisterLoading ? '注册中...' : '现在开始！' }}
          </button>
        </form>
      </div>

      <div
        id="b-container"
        :class="[
          'container',
          'b-container',
          { 'is-txl': isSignUpMode, 'is-z200': isSignUpMode }
        ]"
      >
        <form class="form" @submit.prevent="handleLogin">
          <h2 class="form_title title">欢迎登录</h2>
          <span class="form__span">使用用户名开始吧！</span>
          <input
            v-model="loginForm.username"
            class="form__input"
            type="text"
            placeholder="用户名"
            required
          />
          <input
            v-model="loginForm.password"
            class="form__input"
            type="password"
            placeholder="密码"
            required
          />
          <a class="form__link" href="#">忘记密码？</a>
          <button 
            class="form__button button submit" 
            type="submit"
            :disabled="isLoading"
          >
            {{ isLoading ? '登录中...' : '现在开始！' }}
          </button>
        </form>
      </div>

      <div
        id="switch-cnt"
        :class="['switch', { 'is-txr': isSignUpMode, 'is-gx': isAnimating }]"
      >
        <div
          :class="['switch__circle', { 'is-txr': isSignUpMode }]"
        ></div>
        <div
          :class="[
            'switch__circle',
            'switch__circle--t',
            { 'is-txr': isSignUpMode }
          ]"
        ></div>
        <div
          id="switch-c1"
          :class="[
            'switch__container',
            !isSignUpMode ? 'is-visible' : 'is-hidden'
          ]"
        >
          <h2 class="switch__title title">欢迎回来！</h2>
          <p class="switch__description description">
            使用您的个人信息登录，欢迎与我们联系！
          </p>
          <button class="switch__button button switch-btn" @click="toggleMode">
            登录
          </button>
        </div>
        <div
          id="switch-c2"
          :class="[
            'switch__container',
            isSignUpMode ? 'is-visible' : 'is-hidden'
          ]"
        >
          <h2 class="switch__title title">同学(老师)你好！</h2>
          <p class="switch__description description">
            输入您的个人信息，与我们一起开始旅程
          </p>
          <button class="switch__button button switch-btn" @click="toggleMode">
            注册
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api/index'
import backgroundImage from '../assets/back.jpeg'

const router = useRouter()

const isSignUpMode = ref(false)
const isAnimating = ref(false)

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

const icons = {
  facebook:
    'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjxzdmcgaGVpZ2h0PSI1MHB4IiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCA1MCA1MCIgd2lkdGg9IjUwcHgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6c2tldGNoPSJodHRwOi8vd3d3LmJvaGVtaWFuY29kaW5nLmNvbS9za2V0Y2gvbnMiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj48dGl0bGUvPjxkZWZzLz48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGlkPSJQYWdlLTEiIHN0cm9rZT0ibm9uZSIgc3Ryb2tlLXdpZHRoPSIxIj48ZyBmaWxsPSIjMDAwMDAwIiBpZD0iRmFjZWJvb2siPjxwYXRoIGQ9Ik0yNSw1MCBDMzguODA3MTE5NCw1MCA1MCwzOC44MDcxMTk0IDUwLDI1IEM1MCwxMS4xOTI4ODA2IDM4LjgwNzExOTQsMCAyNSwwIEMxMS4xOTI4ODA2LDAgMCwxMS4xOTI4ODA2IDAsMjUgQzAsMzguODA3MTE5NCAxMS4xOTI4ODA2LDUwIDI1LDUwIFogTTI1LDQ3IEMzNy4xNTAyNjUxLDQ3IDQ3LDM3LjE1MDI2NTEgNDcsMjUgQzQ3LDEyLjg0OTczNDkgMzcuMTUwMjY1MSwzIDI1LDMgQzEyLjg0OTczNDksMyAzLDEyLjg0OTczNDkgMywyNSBDMywzNy4xNTAyNjUxIDEyLjg0OTczNDksNDcgMjUsNDcgWiBNMjYuODE0NTE5NywzNiBMMjYuODE0NTE5NywyNC45OTg3MTIgTDMwLjA2ODc0NDksMjQuOTk4NzEyIEwzMC41LDIxLjIwNzYwNzIgTDI2LjgxNDUxOTcsMjEuMjA3NjA3MiBMMjYuODIwMDQ4NiwxOS4zMTAxMjI3IEMyNi44MjAwNDg2LDE4LjMyMTM0NDIgMjYuOTIwNzIwOSwxNy43OTE1MzQxIDI4LjQ0MjU1MzgsMTcuNzkxNTM0MSBMMzAuNDc2OTYyOSwxNy43OTE1MzQxIEwzMC40NzY5NjI5LDE0IEwyNy4yMjIyNzY5LDE0IEMyMy4zMTI4NzU3LDE0IDIxLjkzNjg2NzgsMTUuODM5MDkzNyAyMS45MzY4Njc4LDE4LjkzMTg3MDkgTDIxLjkzNjg2NzgsMjEuMjA4MDM2NiBMMTkuNSwyMS4yMDgwMzY2IEwxOS41LDI0Ljk5OTE0MTMgTDIxLjkzNjg2NzgsMjQuOTk5MTQxMyBMMjEuOTM2ODY3OCwzNiBMMjYuODE0NTE5NywzNiBaIE0yNi44MTQ1MTk3LDM2IiBpZD0iT3ZhbC0xIi8+PC9nPjwvZz48L3N2Zz4=',
  linkedin:
    'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjxzdmcgaGVpZ2h0PSI1MHB4IiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCA1MCA1MCIgd2lkdGg9IjUwcHgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6c2tldGNoPSJodHRwOi8vd3d3LmJvaGVtaWFuY29kaW5nLmNvbS9za2V0Y2gvbnMiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj48dGl0bGUvPjxkZWZzLz48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGlkPSJQYWdlLTEiIHN0cm9rZT0ibm9uZSIgc3Ryb2tlLXdpZHRoPSIxIj48ZyBmaWxsPSIjMDAwMDAwIiBpZD0iTGlua2VkSW4iPjxwYXRoIGQ9Ik0yNSw1MCBDMzguODA3MTE5NCw1MCA1MCwzOC44MDcxMTk0IDUwLDI1IEM1MCwxMS4xOTI4ODA2IDM4LjgwNzExOTQsMCAyNSwwIEMxMS4xOTI4ODA2LDAgMCwxMS4xOTI4ODA2IDAsMjUgQzAsMzguODA3MTE5NCAxMS4xOTI4ODA2LDUwIDI1LDUwIFogTTI1LDQ3IEMzNy4xNTAyNjUxLDQ3IDQ3LDM3LjE1MDI2NTEgNDcsMjUgQzQ3LDEyLjg0OTczNDkgMzcuMTUwMjY1MSwzIDI1LDMgQzEyLjg0OTczNDksMy AzLDEyLjg0OTczNDkgMywyNSBDMywzNy4xNTAyNjUxIDEyLjg0OTczNDksNDcgMjUsNDcgWiBNMTQsMjAuMTE4MDQ3OSBMMTQsMzQuNjU4MTgzNCBMMTguNzEwMDg1MSwzNC42NTgxODM0IEwxOC43MTAwODUxLDIwLjExODA0NzkgTDE0LDIwLjExODA0NzkgWiBNMTYuNjY0Njk2MiwxMyBDMTUuMDUzNDA1OCwxMyAxNCwxNC4wODU4NjExIDE0LDE1LjUxMTUxMjIgQzE0LDE2LjkwNzYzMzEgMTUuMDIyMjcxMSwxOC4wMjQ3NjE0IDE2LjYwMzU1NTYsMTguMDI0NzYxNCBMMTYuNjMzNjU1NiwxOC4wMjQ3NjE0IEMxOC4yNzU5ODY3LDE4LjAyNDc2MTQgMTkuMjk4ODIyMiwxNi45MDc2MzMxIDE5LjI5ODgyMjIsMTUuNTExNTEyMiBDMTkuMjY4MjUxOSwxNC4wODU4NjExIDE4LjI3NTk4NjcsMTMgMTYuNjY0Njk2MiwxMyBaIE0zMC41NzY5MjEzLDIwLjExODA0NzkgQzI4LjA3NjE3NiwyMC4xMTgwNDc5IDI2Ljk1NjU1MDEsMjEuNTI5MzE5OS AyNi4zMzE0MTA4LDIyLjUxOTM1MjcgTDI2LjMzMTQxMDgsMjAuNDU5ODY0NCBMMjEuNjIwNzYxNCwyMC40NTk4NjQ0IEMyMS42ODI4NDI3LDIxLjgyNDIzNTYgMjEuNjIwNzYxNCwzNSAyMS42MjA3NjE0LDM1IEwyNi4zMzE0MTA4LDM1IEwyNi4zMzE0MTA4LDI2Ljg3OTU4ODcgQzI2LjMzMTQxMDgsMjYuNDQ1MDMyIDI2LjM2MTk4MTIsMjYuMDExNTM2OCAyNi40ODY1MTk5LDI1LjcwMDQwODQgQzI2LjgyNjkzMiwyNC44MzIyNi AyNy42MDIwMDY5LDIzLjkzMzQyMzMgMjguOTAzMjY3NCwyMy45MzM0MjMzIEMzMC42MDgzMzgxLDIzLjkzMzQyMzMgMzEuMjg5OTE0OSwyNS4yNjY3MjAyIDMxLjI4OTkxNDksMjcuMjIwNjMzMyBMMzEuMjg5OTE0OSwzNC45OTk2MTQgTDM1Ljk5OTgxMTksMzQuOTk5NjE0IEwzNiwyNi42NjI3NDQ2IEMzNiwyMi4xOTY2NDM5IDMzLjY3NjM3NDMsMjAuMTE4MDQ3OS AzMC41NzY5MjEzLDIwLjExODA0NzkgWiBNMzAuNTc2OTIxMywyMC4xMTgwNDc5IiBpZD0iT3ZhbC0xIi8+PC9nPjwvZz48L3N2Zz4=',
  twitter:
    'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjxzdmcgaGVpZ2h0PSI1MHB4IiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCA1MCA1MCIgd2lkdGg9IjUwcHgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6c2tldGNoPSJodHRwOi8vd3d3LmJvaGVtaWFuY29kaW5nLmNvbS9za2V0Y2gvbnMiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj48dGl0bGUvPjxkZWZzLz48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGlkPSJQYWdlLTEiIHN0cm9rZT0ibm9uZSIgc3Ryb2tlLXdpZHRoPSIxIj48ZyBmaWxsPSIjMDAwMDAwIiBpZD0iVHdpdHRlciI+PHBhdGggZD0iTTI1LDUwIEMzOC44MDcxMTk0LDUwIDUwLDM4LjgwNzExOTQgNTAsMjUgQzUwLDExLjE5Mjg4MDYgMzguODA3MTE5NCwwIDI1LDAgQzExLjE5Mjg4MDYsMCAwLDExLjE5Mjg4MDYgMCwyNSBDMCwzOC44MDcxMTk0IDExLjE5Mjg4MDYsNTAgMjUsNTAgWiBNMjUsNDcgQzM3LjE1MDI2NTEsNDcgNDcsMzcuMTUwMjY1MSA0NywyNSBDNDcsMTIuODQ5NzM0OSAzNy4xNTAyNjUxLDMgMjUsMy BDMTIuODQ5NzM0OSwzIDMsMTIuODQ5NzM0OSAzLDI1IEMzLDM3LjE1MDI2NTEgMTIuODQ5NzM0OSw0NyAyNSw0NyBaIE0yNC42ODIyNTU0LDIwLjU1NDI5NzUgTDI0LjcyOTk0NCwyMS4zNzYxMDEx IEwyMy45MzUxMzMzLDIxLjI3NTQ3MjEgQzIxLjA0MjAyMjUsMjAuODg5NzI3NSAxOC41MTQ1MjQ2LDE5LjU4MTU1MDQgMTYuMzY4NTM1OCwxNy4zODQ0ODM3IEwxNS4zMTkzODU3LDE2LjI5NDMzNjEgTDE1LjA0OTE1MDEsMTcuMDk5MzY4MS BDMTQuNDc2ODg2NCwxOC44OTM5MTg4IDE0Ljg0MjQ5OTMsMjAuNzg5MDk4NSAxNi4wMzQ3MTUzLDIyLjA2MzczMjYgQzE2LjY3MDU2MzgsMjIuNzY4MTM1Ny AxNi41Mjc0OTc5LDIyLjg2ODc2NDcgMTUuNDMwNjU5MiwyMi40NDk0Nzc yIEMxNS4wNDkxNTAxLDIyLjMxNTMwNTEgMTQuNzE1MzI5NiwyMi4yMTQ2NzYxIDE0LjY4MzUzNzEsMjIuMjY0 OTkwNyBDMTQuNTcyMjYzNywyMi4zODIzOTEyIDE0Ljk1Mzc3MjgsMjMuOTA4NTk3OCAxNS4yNTU4MDA4LDI0LjUxMjM3MTkgQzE1LjY2OTEwMjQsMjUuMzUwOTQ3IDE2LjUxMTYwMTcsMjYuMTcyNzUwNSAxNy40MzM1ODIsMjYuNjU5MTI0MS BMMTguMjEyNDk2NSwyNy4wNDQ4Njg2IEwxNy4yOTA1MTYxLDI3LjA2MTY0MDEgQzE2LjQwMDMyODIsMjcuMDYxNjQwMS AxNi4zNjg1MzU4LDI3LjA3ODQxMTYgMTYuNDYzOTEzMSwyNy40MzA2 MTMxIEMxNi43ODE4Mzc0LDI4LjUyMDc2MDggMTguMDM3NjM4MiwyOS42Nzc5OTQ0IDE5LjQzNjUwNSwzMC4xODExMzk0 IEwyMC40MjIwNzAxLDMwLjUzMzM0MS BMMTkuNTYzNjc0NiwzMS4wNzAwMjkgQzE4LjI5MTk3NzYsMzEuODQxNTE4MS AxNi43OTc3MzM1LDMyLjI3NzU3NzIgMTUuMzAzNDg5NSwzMi4zMTExMjAyIEMxNC41ODgxNTk5LDMyLjMyNzg5MTYgMTQsMzIuMzk0OTc3Ni AxNCwzMi40NDUyOTIyIEMxNCwz Mi42MTMwMDcxIDE1LjkzOTMzOCwzMy41NTIyMTEzIDE3LjA2Nzk2OTIsMzMuOTIxMTg0My BD MjAuNDUzODYyNiwzNS4wMTEzMzE5IDI0LjQ3NTYwNDYsMzQuNTQxNzI5OCAyNy40OTU4ODUxLDMyLjY4MDA5MzIgQzI5LjY0MTg3MzksMzEuMzU1MTQ0NS AzMS43ODc4NjI4LDI4LjcyMjAxODggMzIuNzg5MzI0MiwyNi4xNzI3NTA1IEMzMy4zMjk3OTU0LDI0LjgxNDI1ODggMzMuODcwMjY2NywyMi4zMzIwNzY3 IDMzLjg3MDI2NjcsMjEuMTQxMy BDMzMuODcwMjY2NywyMC4zNjk4MTEgMzMuOTE3OTU1MywyMC4yNjkxODIgMzQuODA4MTQzMiwxOS4zNDY3NDk0IEMzNS4zMzI3MTgzLDE4LjgxMDA2MTMgMzUuODI1NTAwOSwxOC4yMjMwNTg4IDM1LjkyMDg3ODIsMTguMDU1MzQzNy BD MzYuMDc5ODQwMywxNy43MzY2ODUyIDM2LjA2Mzk0NDIsMTcuNzM2Njg1Mi AzNS4yNTMyMzczLDE4LjAyMTgwMDcgQzMzLjkwMjA1OTEsMTguNTI0OTQ1OCAzMy43MTEzMDQ1LDE4LjQ1Nzg1OTggMzQuMzc4OTQ1NSwxNy43MDMxNDIy IEMzNC44NzE3MjgxLDE3LjE2NjQ1NDEgMzUuNDU5ODg4LDE2LjE5MzcwNzEgMzUuNDU5ODg4LDE1LjkwODU5MTUgQzM1LjQ1OTg4OCwxNS44NTgyNzc g MzUuMjIxNDQ0OCwxNS45NDIxMzQ2IDM0Ljk1MTIwOTIsMTYuMDkzMDc4IEMzNC42NjUwNzczLDE2LjI2MDc5MzEgMzQuMDI5MjI4OCwxNi41MTIzNjU2 IDMzLjU1MjM0MjQsMTYuNjYzMzA5MS BMMzIuNjkzOTQ2OSwxNi45NDg0MjQ2IEwzMS45MTUwMzI0LDE2LjM5NDk2NS BD MzEuNDg1ODM0NiwxNi4wOTMwNzggMzAuODgxNzc4NiwxNS43NTc2NDggMzAuNTYzODU0MywxNS42NTcwMTkgQzI5Ljc1MzE0NzQsMTUuNDIyMjE4IDI4LjUxMzI0MjgsMTUuNDU1NzYxIDI3Ljc4MjAxNjksMTUuNzI0MTA1IEMyNS43OTQ5OTAzLDE2LjQ3ODgyMjYgMjQuNTM5MTg5NCwxOC40MjQzMTY4 IDI0LjY4MjI1NTQsMjAuNTU0Mjk3NS BD MjQuNjgyMjU1NCwyMC41NTQyOTc1IDI0LjUzOTE4OTQsMTguNDI0MzE2OCAyNC42ODIyNTU0LDIwLjU1NDI5NzUgWiBNMjQuNjgyMjU1NCwyMC41NTQyOTc1IiBpZD0iT3ZhbC0xIi8+PC9nPjwvZz48L3N2Zz4='
}

const toggleMode = () => {
  isSignUpMode.value = !isSignUpMode.value
  isAnimating.value = true
  setTimeout(() => {
    isAnimating.value = false
  }, 1500)
}

const handleRegister = async () => {
  // 表单验证
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
    // 构建注册数据
    const registerData = {
      username: registerForm.username.trim(),
      password: registerForm.password,
      studentId: registerForm.studentId.trim()
    }

    console.log('注册数据:', registerData)

    const response = await authApi.register(registerData)

    console.log('注册响应:', response)

    if (response && response.code === 200) {
      ElMessage.success(response.message || '注册成功')
      
      // 注册成功后，清空表单并切换到登录模式
      setTimeout(() => {
        // 清空表单
        registerForm.username = ''
        registerForm.studentId = ''
        registerForm.password = ''
        registerForm.confirmPassword = ''
        
        // 切换到登录模式
        isSignUpMode.value = false
        
        // 自动填充用户名到登录表单
        loginForm.username = registerData.username
      }, 1000)
    } else {
      const errorMsg = response?.message || '注册失败'
      ElMessage.error(errorMsg)
      console.error('注册失败:', response)
    }
  } catch (error) {
    console.error('注册失败:', error)
    // 错误信息已在 request.js 的拦截器中处理
    // 如果是用户名已存在的错误，这里可以额外处理
    if (error?.response?.data?.message) {
      const errorMsg = error.response.data.message
      if (errorMsg.includes('用户名') || errorMsg.includes('已存在')) {
        ElMessage.error('用户名已存在，请更换用户名')
      }
    }
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

    console.log('登录响应:', response)

    if (response && response.code === 200) {
      // 获取数据
      const data = response.data
      if (!data) {
        ElMessage.error('登录失败：响应数据为空')
        console.error('响应数据为空:', response)
        return
      }

      // 保存 token
      const token = data.token
      if (token) {
        localStorage.setItem('token', token)
        console.log('Token 已保存')
      } else {
        console.warn('Token 不存在')
      }
      
      // 保存用户信息
      const userInfo = data.userInfo || data
      if (userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
        console.log('用户信息已保存:', userInfo)
      } else {
        console.warn('用户信息不存在')
      }

      ElMessage.success(response.message || '登录成功')

      // 根据角色跳转（兼容多种格式）
      const role = (userInfo?.role || data?.role || '').toUpperCase()
      console.log('用户角色:', role, '原始值:', userInfo?.role || data?.role)
      
      // 延迟跳转，确保消息提示显示
      setTimeout(() => {
        let targetPath = '/admin' // 默认路径
        
        if (role === 'ADMIN') {
          targetPath = '/admin'
        } else if (role === 'TEACHER') {
          targetPath = '/teacher/home'
        } else if (role === 'STUDENT') {
          targetPath = '/student/home'
        } else {
          console.warn('未知角色，使用默认路径:', role)
        }
        
        console.log('准备跳转到:', targetPath)
        
        // 使用 router.push，失败则使用 window.location
        router.push(targetPath).then(() => {
          console.log('路由跳转成功:', targetPath)
        }).catch(err => {
          console.error('路由跳转失败，使用 window.location:', err)
          window.location.href = targetPath
        })
      }, 500)
    } else {
      const errorMsg = response?.message || '登录失败：响应数据格式错误'
      ElMessage.error(errorMsg)
      console.error('登录失败:', response)
    }
  } catch (error) {
    console.error('登录失败:', error)
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
  font-family: 'Montserrat', sans-serif;
  background-color: #ecf0f3;
  color: #a0a5a8;
}

.auth-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('../assets/back.jpeg');
  background-position: bottom center;
  background-repeat: no-repeat;
  background-size: cover;
  position: relative;
}

.main {
  position: relative;
  width: 1000px;
  min-width: 1000px;
  min-height: 600px;
  height: 600px;
  padding: 25px;
  background-color: #ecf0f3;
  box-shadow: 10px 10px 10px #d1d9e6, -10px -10px 10px #f9f9f9;
  border-radius: 12px;
  overflow: hidden;
}

@media (max-width: 1200px) {
  .main {
    transform: scale(0.7);
  }
}

@media (max-width: 1000px) {
  .main {
    transform: scale(0.6);
  }
}

@media (max-width: 800px) {
  .main {
    transform: scale(0.5);
  }
}

@media (max-width: 600px) {
  .main {
    transform: scale(0.4);
  }
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  width: 600px;
  height: 100%;
  padding: 25px;
  background-color: #ecf0f3;
  transition: 1.25s;
}

.form {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  width: 100%;
  height: 100%;
}

.form__icon {
  object-fit: contain;
  width: 30px;
  margin: 0 5px;
  opacity: 0.5;
  transition: 0.15s;
}

.form__icon:hover {
  opacity: 1;
  cursor: pointer;
}

.form__input {
  width: 350px;
  height: 40px;
  margin: 4px 0;
  padding-left: 25px;
  font-size: 13px;
  letter-spacing: 0.15px;
  border: none;
  outline: none;
  font-family: 'Montserrat', sans-serif;
  background-color: #ecf0f3;
  border-radius: 8px;
  transition: 0.25s ease;
  box-shadow: inset 2px 2px 4px #d1d9e6, inset -2px -2px 4px #f9f9f9;
}

.form__input:focus {
  box-shadow: inset 4px 4px 4px #d1d9e6, inset -4px -4px 4px #f9f9f9;
}

.form__span {
  margin-top: 30px;
  margin-bottom: 12px;
}

.form__link {
  color: #181818;
  font-size: 15px;
  margin-top: 25px;
  border-bottom: 1px solid #a0a5a8;
  line-height: 2;
  text-decoration: none;
}

.title {
  font-size: 34px;
  font-weight: 700;
  line-height: 3;
  color: #181818;
}

.description {
  font-size: 14px;
  letter-spacing: 0.25px;
  text-align: center;
  line-height: 1.6;
}

.button {
  width: 180px;
  height: 50px;
  border-radius: 25px;
  margin-top: 50px;
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 1.15px;
  background-color: #4b70e2;
  color: #f9f9f9;
  border: none;
  outline: none;
  box-shadow: 8px 8px 16px #d1d9e6, -8px -8px 16px #f9f9f9;
  cursor: pointer;
}

.button:hover {
  box-shadow: 6px 6px 10px #d1d9e6, -6px -6px 10px #f9f9f9;
  transform: scale(0.985);
}

.button:active,
.button:focus {
  box-shadow: 2px 2px 6px #d1d9e6, -2px -2px 6px #f9f9f9;
  transform: scale(0.97);
}

.a-container {
  z-index: 100;
  left: calc(100% - 600px);
}

.b-container {
  left: calc(100% - 600px);
  z-index: 0;
}

.switch {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: 400px;
  padding: 50px;
  z-index: 200;
  transition: 1.25s;
  background-color: #ecf0f3;
  overflow: hidden;
  box-shadow: 4px 4px 10px #d1d9e6, -4px -4px 10px #f9f9f9;
}

.switch__circle {
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background-color: #ecf0f3;
  box-shadow: inset 8px 8px 12px #d1d9e6, inset -8px -8px 12px #f9f9f9;
  bottom: -60%;
  left: -60%;
  transition: 1.25s;
}

.switch__circle--t {
  top: -30%;
  left: 60%;
  width: 300px;
  height: 300px;
}

.switch__container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: absolute;
  width: 400px;
  padding: 50px 55px;
  transition: 1.25s;
}

.switch__container.is-hidden {
  visibility: hidden;
  opacity: 0;
}

.switch__container.is-visible {
  visibility: visible;
  opacity: 1;
}

.switch__button {
  cursor: pointer;
}

.is-txr {
  left: calc(100% - 400px);
  transform-origin: left;
}

.is-txl {
  left: 0;
  transform-origin: right;
}

.is-z200 {
  z-index: 200;
}

.is-gx {
  animation: is-gx 1.25s;
}

@keyframes is-gx {
  0%,
  10%,
  100% {
    width: 400px;
  }
  30%,
  50% {
    width: 500px;
  }
}
</style>

