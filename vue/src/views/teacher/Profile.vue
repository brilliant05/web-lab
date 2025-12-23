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
          <el-form-item label="工号">
            <el-input v-model="infoForm.username" disabled />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="infoForm.name" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="infoForm.email" />
          </el-form-item>
          <el-form-item label="职称">
            <el-input v-model="infoForm.title" />
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
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'

const activeTab = ref('info')

const infoForm = reactive({
  username: 'T2023001',
  name: '王老师',
  email: 'wang@example.com',
  title: '副教授',
  bio: '主要研究方向为Web开发技术、分布式系统。'
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const saveInfo = () => {
  ElMessage.success('个人信息保存成功')
}

const changePassword = () => {
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  ElMessage.success('密码修改成功，请重新登录')
}
</script>

<style scoped>
.content-card {
  min-height: calc(100vh - 120px);
}
</style>
