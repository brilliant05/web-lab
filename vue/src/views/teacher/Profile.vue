<template>
  <el-card class="content-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span>个人资料设置</span>
      </div>
    </template>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="info">
        <el-form :model="infoForm" label-width="100px" style="max-width: 500px; margin-top: 20px">
          <el-form-item label="头像">
            <div style="display: flex; align-items: center; gap: 15px;">
              <el-avatar :size="120" :src="infoForm.avatar" :icon="UserFilled" />
              <el-upload
                class="avatar-uploader"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :on-error="handleAvatarError"
                accept="image/*"
              >
                <el-button type="primary">点击上传</el-button>
              </el-upload>
            </div>
          </el-form-item>
          <el-form-item label="工号">
            <el-input v-model="infoForm.username" disabled />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="infoForm.name" disabled />
          </el-form-item>
          <el-form-item label="学院">
            <el-input v-model="infoForm.college" disabled />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="infoForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="infoForm.email" />
          </el-form-item>
          <el-form-item label="职称">
            <el-input v-model="infoForm.title" disabled />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input v-model="infoForm.bio" type="textarea" :rows="4" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveInfo">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="修改密码" name="password">
        <el-form :model="pwdForm" label-width="100px" style="max-width: 500px; margin-top: 20px">
          <el-form-item label="原密码">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwdForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认新密码">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="changePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { getCurrentUser, updatePassword, updateUserProfile } from '@/api'
import { UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'

const activeTab = ref('info')

const uploadUrl = 'http://localhost:8080/api/v1/upload/avatar'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

const infoForm = reactive({
  username: '',
  name: '',
  college: '',
  phone: '',
  email: '',
  title: '',
  profile: '',
  avatar: ''
})

// 获取当前用户信息
const fetchUserInfo = async () => {
  try {
    const res = await getCurrentUser()
    if (res.code === 200) {
      const user = res.data
      infoForm.username = res.data.username
      infoForm.name = res.data.realName
      infoForm.college = res.data.college
      infoForm.phone = res.data.phone
      infoForm.email = res.data.email
      infoForm.title = user.jobTitle
      infoForm.bio = user.profile
      infoForm.avatar = user.avatarUrl
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

const handleAvatarError = (error) => {
  ElMessage.error('头像上传失败，请重试')
  console.error('Avatar upload error:', error)
}

const handleAvatarSuccess = async (response) => {
  console.log('Avatar upload response:', response)
  if (response.code === 200 || response.code === '200') {
    const newAvatarUrl = response.data.url
    infoForm.avatar = newAvatarUrl
    
    // 优先显示成功消息
    ElMessage.success('头像更新成功')
    
    // 上传成功后立即保存到用户信息
    try {
      console.log('Saving avatar to profile...')
      await updateUserProfile({
        avatarUrl: newAvatarUrl
      })
      console.log('Avatar saved to profile.')
      
      // 更新本地存储的用户信息
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr)
          userInfo.avatarUrl = newAvatarUrl
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          // 触发自定义事件，通知其他组件更新用户信息
          window.dispatchEvent(new Event('userInfoUpdated'))
        } catch (e) {
          console.error('Update local storage failed:', e)
        }
      }
    } catch (error) {
      console.error('Save avatar failed:', error)
      // 如果是业务错误，request.js 已经弹出了错误提示，这里就不重复弹出了
      // 除非是网络错误等没有 response 的情况
      if (!error.code) {
        ElMessage.warning('头像已上传，但同步个人信息失败')
      }
    }
  } else {
    console.warn('Upload failed:', response.message)
    ElMessage.error(response.message || '上传失败')
  }
}

const saveInfo = async () => {
  // 校验手机号
  if (infoForm.phone && !/^1[3-9]\d{9}$/.test(infoForm.phone)) {
    ElMessage.error('请输入正确的手机号')
    return
  }

  try {
    await updateUserProfile({
      phone: infoForm.phone,
      email: infoForm.email,
      profile: infoForm.bio
    })

    // 更新本地存储的用户信息
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      userInfo.phone = infoForm.phone
      userInfo.email = infoForm.email
      userInfo.profile = infoForm.bio
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      // 触发自定义事件，通知其他组件更新用户信息
      window.dispatchEvent(new Event('userInfoUpdated'))
    }

    ElMessage.success('个人信息保存成功')
  } catch (error) {
    console.error('保存个人信息失败:', error)
    // 如果不是 API 错误（没有 response），则可能是代码逻辑错误，需要提示
    if (!error.response && !error.code) {
      ElMessage.error('保存失败：' + (error.message || '未知错误'))
    }
  }
}

onMounted(() => {
  fetchUserInfo()
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const changePassword = async () => {
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  
  if (pwdForm.newPassword.length < 6) {
    ElMessage.error('新密码长度不能少于6位')
    return
  }

  if (pwdForm.newPassword === pwdForm.oldPassword) {
    ElMessage.error('新密码不能与原密码相同')
    return
  }

  try {
    await updatePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
      confirmPassword: pwdForm.confirmPassword
    })
    
    ElMessage.success('密码修改成功，请重新登录')
    
    // 清空表单
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
    
    // 可选：强制退出登录
    setTimeout(() => {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/'
    }, 1500)
    
  } catch (error) {
    console.error('修改密码失败:', error)
    // 如果不是 API 错误（没有 response），则可能是代码逻辑错误
    if (!error.response && !error.code) {
      ElMessage.error('修改失败：' + (error.message || '未知错误'))
    }
  }
}
</script>

<style scoped>
.content-card {
  min-height: calc(100vh - 120px);
}
</style>
