<template>
  <div class="profile-page">
    <el-card class="profile-card" shadow="never">
      <el-tabs v-model="activeTab" class="profile-tabs" @tab-change="handleTabChange">
        <!-- 个人信息 -->
        <el-tab-pane label="个人信息" name="info">
          <div class="tab-content">
            <el-card shadow="never" class="info-card">
              <template #header>
                <div class="card-header">
                  <span class="card-title">基本信息</span>
                  <el-button
                    v-if="!isEditing"
                    type="primary"
                    :icon="Edit"
                    @click="handleEditInfo"
                  >
                    编辑
                  </el-button>
                  <div v-else>
                    <el-button @click="handleCancelEdit">取消</el-button>
                    <el-button type="primary" @click="handleSaveInfo" :loading="saving">
                      保存
                    </el-button>
                  </div>
                </div>
              </template>

              <el-form
                v-if="isEditing"
                ref="infoFormRef"
                :model="userForm"
                :rules="infoRules"
                label-width="100px"
              >
                <el-form-item label="头像">
                  <div class="avatar-upload">
                    <el-upload
                      :show-file-list="false"
                      :before-upload="beforeAvatarUpload"
                      :http-request="handleAvatarUpload"
                      accept="image/*"
                    >
                      <el-avatar :size="100" :src="userForm.avatarUrl">
                        <el-icon><UserFilled /></el-icon>
                      </el-avatar>
                      <div class="avatar-hint">点击上传</div>
                    </el-upload>
                  </div>
                </el-form-item>

                <el-form-item label="用户名">
                  <el-input v-model="userInfo.username" disabled />
                </el-form-item>

                <el-form-item label="真实姓名">
                  <el-input v-model="userInfo.realName" disabled />
                </el-form-item>

                <el-form-item label="学号" v-if="userInfo.role === 'STUDENT'">
                  <el-input v-model="userInfo.studentId" disabled />
                </el-form-item>

                <el-form-item label="学院" v-if="userInfo.role === 'STUDENT'">
                  <el-input v-model="userInfo.college" disabled />
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="userForm.email" />
                </el-form-item>

                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="userForm.phone" />
                </el-form-item>

                <el-form-item label="个人简介">
                  <el-input
                    v-model="userForm.profile"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入个人简介"
                  />
                </el-form-item>
              </el-form>

              <el-descriptions v-else :column="2" border>
                <el-descriptions-item label="头像">
                  <el-avatar :size="80" :src="userInfo.avatarUrl">
                    <el-icon><UserFilled /></el-icon>
                  </el-avatar>
                </el-descriptions-item>
                <el-descriptions-item label="用户名">
                  {{ userInfo.username }}
                </el-descriptions-item>
                <el-descriptions-item label="真实姓名">
                  {{ userInfo.realName }}
                </el-descriptions-item>
                <el-descriptions-item label="学号" v-if="userInfo.role === 'STUDENT'">
                  {{ userInfo.studentId || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="学院" v-if="userInfo.role === 'STUDENT'">
                  {{ userInfo.college || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="邮箱">
                  {{ userInfo.email || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="手机号">
                  {{ userInfo.phone || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="个人简介" :span="2">
                  {{ userInfo.profile || '暂无简介' }}
                </el-descriptions-item>
                <el-descriptions-item label="注册时间">
                  {{ formatDateTime(userInfo.createTime) }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>

            <!-- 修改密码 -->
            <el-card shadow="never" class="password-card">
              <template #header>
                <span class="card-title">修改密码</span>
              </template>

              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="100px"
                style="max-width: 500px"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    show-password
                    placeholder="请输入原密码"
                  />
                </el-form-item>

                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                    placeholder="请输入新密码（至少6位）"
                  />
                </el-form-item>

                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                    placeholder="请再次输入新密码"
                  />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
                    修改密码
                  </el-button>
                  <el-button @click="handleResetPasswordForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 我的资源 -->
        <el-tab-pane label="我的资源" name="resources">
          <div class="tab-content">
            <div class="resource-header">
              <el-input
                v-model="resourceSearch"
                placeholder="搜索资源标题"
                style="width: 300px"
                clearable
                @keyup.enter="loadMyResources"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" :icon="Plus" @click="goToUpload">
                上传资源
              </el-button>
            </div>

            <div v-loading="resourcesLoading" class="resources-list">
              <div v-if="myResources.length === 0" class="empty-state">
                <el-empty description="暂无资源" />
              </div>
              <div v-else class="resources-list-container">
                <MyResourceCard
                  v-for="resource in myResources"
                  :key="resource.resourceId"
                  :resource="resource"
                  @edit="handleEditResource"
                  @delete="handleDeleteResource"
                />
              </div>
            </div>

            <div v-if="myResources.length > 0" class="pagination-container">
              <el-pagination
                v-model:current-page="resourcePagination.currentPage"
                v-model:page-size="resourcePagination.pageSize"
                :page-sizes="[12, 24, 48]"
                :total="resourcePagination.total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleResourceSizeChange"
                @current-change="handleResourcePageChange"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 我的提问 -->
        <el-tab-pane label="我的提问" name="questions">
          <div class="tab-content">
            <div class="question-header">
              <el-radio-group v-model="questionFilter" @change="loadMyQuestions">
                <el-radio-button label="">全部</el-radio-button>
                <el-radio-button :label="0">待回答</el-radio-button>
                <el-radio-button :label="1">已回答</el-radio-button>
              </el-radio-group>
            </div>

            <div v-loading="questionsLoading" class="questions-list">
              <div v-if="myQuestions.length === 0" class="empty-state">
                <el-empty description="暂无提问" />
              </div>
              <div v-else class="questions-grid">
                <QuestionCard
                  v-for="question in myQuestions"
                  :key="question.questionId"
                  :question="question"
                />
              </div>
            </div>

            <div v-if="myQuestions.length > 0" class="pagination-container">
              <el-pagination
                v-model:current-page="questionPagination.currentPage"
                v-model:page-size="questionPagination.pageSize"
                :page-sizes="[12, 24, 48]"
                :total="questionPagination.total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleQuestionSizeChange"
                @current-change="handleQuestionPageChange"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 我的消息 -->
        <el-tab-pane label="我的消息" name="notifications">
          <div class="tab-content">
            <div class="notification-header">
              <el-button
                type="primary"
                :icon="Check"
                @click="handleMarkAllRead"
                :loading="markingAllRead"
              >
                全部标记为已读
              </el-button>
            </div>

            <div v-loading="notificationsLoading" class="notifications-list">
              <div v-if="notifications.length === 0" class="empty-state">
                <el-empty description="暂无消息" />
              </div>
              <el-timeline v-else>
                <el-timeline-item
                  v-for="notification in notifications"
                  :key="notification.notificationId"
                  :timestamp="formatDateTime(notification.createTime)"
                  placement="top"
                  :type="notification.isRead === 0 ? 'primary' : 'info'"
                >
                  <el-card shadow="hover" :class="{ 'unread': notification.isRead === 0 }">
                    <div class="notification-item">
                      <div class="notification-content">
                        <h4 class="notification-title">{{ notification.title }}</h4>
                        <p class="notification-text">{{ notification.content }}</p>
                      </div>
                      <div class="notification-actions">
                        <el-button
                          v-if="notification.isRead === 0"
                          link
                          type="primary"
                          @click="handleMarkRead(notification.notificationId)"
                        >
                          标记已读
                        </el-button>
                      </div>
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </div>

            <div v-if="notifications.length > 0" class="pagination-container">
              <el-pagination
                v-model:current-page="notificationPagination.currentPage"
                v-model:page-size="notificationPagination.pageSize"
                :page-sizes="[10, 20, 50]"
                :total="notificationPagination.total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleNotificationSizeChange"
                @current-change="handleNotificationPageChange"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 编辑资源对话框 -->
    <el-dialog
      v-model="resourceEditDialog"
      title="编辑资源"
      width="600px"
      @close="editingResource = null"
    >
      <el-form
        ref="resourceEditFormRef"
        :model="resourceEditForm"
        :rules="{
          resourceTitle: [{ required: true, message: '请输入资源标题', trigger: 'blur' }]
        }"
        label-width="100px"
      >
        <el-form-item label="资源标题" prop="resourceTitle">
          <el-input
            v-model="resourceEditForm.resourceTitle"
            placeholder="请输入资源标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="资源描述">
          <el-input
            v-model="resourceEditForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入资源描述"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="资源标签">
          <el-input
            v-model="resourceEditForm.tags"
            placeholder="请输入标签，多个标签用逗号分隔"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="resourceEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveResourceEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {
    deleteResource,
    getCurrentUser,
    getMyQuestions,
    getMyUploads,
    getNotificationList,
    markAllNotificationRead,
    markNotificationRead,
    updatePassword,
    updateResource,
    updateUserProfile,
    uploadAvatar
} from '@/api'
import MyResourceCard from '@/components/student/MyResourceCard.vue'
import QuestionCard from '@/components/student/QuestionCard.vue'
import { fixImageUrl } from '@/utils/image'
import {
    Check,
    Edit,
    Plus,
    Search,
    UserFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Tab切换
const activeTab = ref('info')

// 用户信息
const userInfo = ref({})
const isEditing = ref(false)
const saving = ref(false)
const changingPassword = ref(false)

// 表单引用
const infoFormRef = ref(null)
const passwordFormRef = ref(null)

// 编辑表单数据
const userForm = reactive({
  email: '',
  phone: '',
  avatarUrl: '',
  profile: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const infoRules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 我的资源
const myResources = ref([])
const resourcesLoading = ref(false)
const resourceSearch = ref('')
const resourcePagination = reactive({
  currentPage: 1,
  pageSize: 12,
  total: 0
})

// 我的提问
const myQuestions = ref([])
const questionsLoading = ref(false)
const questionFilter = ref('')
const questionPagination = reactive({
  currentPage: 1,
  pageSize: 15,
  total: 0
})

// 我的消息
const notifications = ref([])
const notificationsLoading = ref(false)
const markingAllRead = ref(false)
const notificationPagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await getCurrentUser()
    console.log('loadUserInfo 获取到的用户信息:', response)
    if (response && response.code === 200 && response.data) {
      userInfo.value = response.data
      // 初始化表单数据
      userForm.email = userInfo.value.email || ''
      userForm.phone = userInfo.value.phone || ''
      userForm.avatarUrl = fixImageUrl(userInfo.value.avatarUrl || '')
      userForm.profile = userInfo.value.profile || ''
      
      // 修复头像URL（如果有的话）
      if (userInfo.value.avatarUrl) {
        userInfo.value.avatarUrl = fixImageUrl(userInfo.value.avatarUrl)
      }
      
      // 同步更新 localStorage，确保其他组件也能获取最新数据
      try {
        const userInfoStr = localStorage.getItem('userInfo')
        if (userInfoStr) {
          const user = JSON.parse(userInfoStr)
          // 合并更新，保留token等信息
          Object.assign(user, {
            realName: userInfo.value.realName,
            username: userInfo.value.username,
            email: userInfo.value.email,
            phone: userInfo.value.phone,
            avatarUrl: userInfo.value.avatarUrl,
            profile: userInfo.value.profile,
            role: userInfo.value.role
          })
          localStorage.setItem('userInfo', JSON.stringify(user))
          console.log('loadUserInfo 已更新 localStorage:', user)
          // 触发自定义事件，通知其他组件更新
          window.dispatchEvent(new CustomEvent('userInfoUpdated'))
        }
      } catch (err) {
        console.error('更新 localStorage 失败:', err)
      }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  }
}

// 开始编辑
const handleEditInfo = () => {
  isEditing.value = true
  // 重新加载最新数据
  loadUserInfo()
}

// 取消编辑
const handleCancelEdit = () => {
  isEditing.value = false
  // 恢复原始数据
  userForm.email = userInfo.value.email || ''
  userForm.phone = userInfo.value.phone || ''
  userForm.avatarUrl = userInfo.value.avatarUrl || ''
  userForm.profile = userInfo.value.profile || ''
}

// 保存个人信息
const handleSaveInfo = async () => {
  if (!infoFormRef.value) return

  await infoFormRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      const updateData = {
        email: userForm.email,
        phone: userForm.phone,
        avatarUrl: userForm.avatarUrl,
        profile: userForm.profile
      }

      const response = await updateUserProfile(updateData)
      if (response && response.code === 200) {
        ElMessage.success('保存成功')
        isEditing.value = false
        // 更新用户信息
        await loadUserInfo()
        // 更新localStorage
        const userInfoStr = localStorage.getItem('userInfo')
        if (userInfoStr) {
          const user = JSON.parse(userInfoStr)
          Object.assign(user, updateData)
          localStorage.setItem('userInfo', JSON.stringify(user))
        }
      } else {
        ElMessage.error(response?.message || '保存失败')
      }
    } catch (error) {
      console.error('保存用户信息失败:', error)
      ElMessage.error(error?.response?.data?.message || '保存失败')
    } finally {
      saving.value = false
    }
  })
}

// 头像上传前校验
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt1M = file.size / 1024 / 1024 < 1

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt1M) {
    ElMessage.error('头像大小不能超过 1MB!')
    return false
  }
  return true
}

// 上传头像
const handleAvatarUpload = async (options) => {
  const { file, onSuccess, onError } = options
  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await uploadAvatar(formData)
    console.log('头像上传响应:', response)
    if (response && response.code === 200 && response.data) {
      const avatarUrl = response.data.url
      const fixedAvatarUrl = fixImageUrl(avatarUrl) // 修复HTTPS证书问题
      console.log('头像URL (原始):', avatarUrl)
      console.log('头像URL (修复后):', fixedAvatarUrl)
      
      // 先更新前端显示，让用户立即看到（使用修复后的URL）
      userForm.avatarUrl = fixedAvatarUrl
      userInfo.value.avatarUrl = fixedAvatarUrl
      
      // 立即保存头像URL到数据库（保存原始URL，后端配置已改为HTTP，新上传的会直接是HTTP）
      try {
        const updateResponse = await updateUserProfile({ avatarUrl })
        console.log('保存头像URL响应:', updateResponse)
        if (updateResponse && updateResponse.code === 200) {
          // 重新从后端获取最新的用户信息（包括头像URL）
          await loadUserInfo()
          console.log('用户信息已重新加载，头像URL:', userInfo.value.avatarUrl)
        }
      } catch (updateError) {
        console.error('保存头像URL到数据库失败:', updateError)
        ElMessage.warning('头像上传成功，但保存到数据库失败，请手动保存')
      }
      
      if (onSuccess) onSuccess(response.data)
      ElMessage.success('头像上传成功')
    } else {
      const msg = response?.message || '头像上传失败'
      ElMessage.error(msg)
      if (onError) onError(new Error(msg))
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    const msg = error?.response?.data?.message || '头像上传失败'
    ElMessage.error(msg)
    if (onError) onError(error)
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    changingPassword.value = true
    try {
      const response = await updatePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword,
        confirmPassword: passwordForm.confirmPassword
      })

      if (response && response.code === 200) {
        ElMessage.success('密码修改成功，请重新登录')
        handleResetPasswordForm()
        // 可选：提示重新登录
        setTimeout(() => {
          router.push('/')
        }, 2000)
      } else {
        ElMessage.error(response?.message || '密码修改失败')
      }
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error(error?.response?.data?.message || '密码修改失败')
    } finally {
      changingPassword.value = false
    }
  })
}

// 重置密码表单
const handleResetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

// 加载我的资源
const loadMyResources = async () => {
  resourcesLoading.value = true
  try {
    const params = {
      pageNum: resourcePagination.currentPage,
      pageSize: resourcePagination.pageSize
    }

    const response = await getMyUploads(params)
    if (response && response.code === 200 && response.data) {
      let resources = response.data.records || []
      
      // 前端搜索（如果后端不支持）
      if (resourceSearch.value) {
        resources = resources.filter(r =>
          r.resourceTitle.includes(resourceSearch.value)
        )
      }

      myResources.value = resources
      resourcePagination.total = response.data.total || 0
    } else {
      ElMessage.error(response?.message || '加载资源列表失败')
      myResources.value = []
    }
  } catch (error) {
    console.error('加载我的资源失败:', error)
    ElMessage.error('加载资源列表失败')
    myResources.value = []
  } finally {
    resourcesLoading.value = false
  }
}

// 资源分页
const handleResourceSizeChange = (val) => {
  resourcePagination.pageSize = val
  resourcePagination.currentPage = 1
  loadMyResources()
}

const handleResourcePageChange = (val) => {
  resourcePagination.currentPage = val
  loadMyResources()
}

// 编辑资源
const editingResource = ref(null)
const resourceEditDialog = ref(false)
const resourceEditForm = reactive({
  resourceTitle: '',
  description: '',
  tags: ''
})
const resourceEditFormRef = ref(null)

const handleEditResource = (resource) => {
  editingResource.value = resource
  resourceEditForm.resourceTitle = resource.resourceTitle || ''
  resourceEditForm.description = resource.description || ''
  resourceEditForm.tags = resource.tags || ''
  resourceEditDialog.value = true
}

const handleSaveResourceEdit = async () => {
  if (!resourceEditFormRef.value) return

  await resourceEditFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const response = await updateResource(editingResource.value.resourceId, {
        resourceTitle: resourceEditForm.resourceTitle,
        description: resourceEditForm.description,
        tags: resourceEditForm.tags
      })

      if (response && response.code === 200) {
        ElMessage.success('资源更新成功')
        resourceEditDialog.value = false
        await loadMyResources()
      } else {
        ElMessage.error(response?.message || '更新失败')
      }
    } catch (error) {
      console.error('更新资源失败:', error)
      ElMessage.error(error?.response?.data?.message || '更新失败')
    }
  })
}

// 删除资源
const handleDeleteResource = async (resource) => {
  try {
    const response = await deleteResource(resource.resourceId)
    if (response && response.code === 200) {
      ElMessage.success('资源删除成功')
      await loadMyResources()
    } else {
      ElMessage.error(response?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除资源失败:', error)
    ElMessage.error(error?.response?.data?.message || '删除失败')
  }
}

// 跳转到上传页面
const goToUpload = () => {
  router.push('/student/upload')
}

// 加载我的提问
const loadMyQuestions = async () => {
  questionsLoading.value = true
  try {
    const params = {
      pageNum: questionPagination.currentPage,
      pageSize: questionPagination.pageSize
    }

    if (questionFilter.value !== '') {
      params.isAnswered = questionFilter.value
    }

    const response = await getMyQuestions(params)
    if (response && response.code === 200 && response.data) {
      myQuestions.value = response.data.records || []
      questionPagination.total = response.data.total || 0
    } else {
      ElMessage.error(response?.message || '加载提问列表失败')
      myQuestions.value = []
    }
  } catch (error) {
    console.error('加载我的提问失败:', error)
    ElMessage.error('加载提问列表失败')
    myQuestions.value = []
  } finally {
    questionsLoading.value = false
  }
}

// 提问分页
const handleQuestionSizeChange = (val) => {
  questionPagination.pageSize = val
  questionPagination.currentPage = 1
  loadMyQuestions()
}

const handleQuestionPageChange = (val) => {
  questionPagination.currentPage = val
  loadMyQuestions()
}

// 加载通知列表
const loadNotifications = async () => {
  notificationsLoading.value = true
  try {
    const params = {
      pageNum: notificationPagination.currentPage,
      pageSize: notificationPagination.pageSize
    }

    const response = await getNotificationList(params)
    if (response && response.code === 200 && response.data) {
      notifications.value = response.data.records || []
      notificationPagination.total = response.data.total || 0
    } else {
      ElMessage.error(response?.message || '加载通知列表失败')
      notifications.value = []
    }
  } catch (error) {
    console.error('加载通知列表失败:', error)
    ElMessage.error('加载通知列表失败')
    notifications.value = []
  } finally {
    notificationsLoading.value = false
  }
}

// 标记已读
const handleMarkRead = async (notificationId) => {
  try {
    const response = await markNotificationRead(notificationId)
    if (response && response.code === 200) {
      ElMessage.success('已标记为已读')
      // 更新本地状态
      const notification = notifications.value.find(n => n.notificationId === notificationId)
      if (notification) {
        notification.isRead = 1
      }
      // 重新加载
      await loadNotifications()
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('操作失败')
  }
}

// 全部标记为已读
const handleMarkAllRead = async () => {
  markingAllRead.value = true
  try {
    const response = await markAllNotificationRead()
    if (response && response.code === 200) {
      ElMessage.success('全部标记为已读')
      await loadNotifications()
    }
  } catch (error) {
    console.error('标记全部已读失败:', error)
    ElMessage.error('操作失败')
  } finally {
    markingAllRead.value = false
  }
}

// 通知分页
const handleNotificationSizeChange = (val) => {
  notificationPagination.pageSize = val
  notificationPagination.currentPage = 1
  loadNotifications()
}

const handleNotificationPageChange = (val) => {
  notificationPagination.currentPage = val
  loadNotifications()
}

// 格式化日期时间
const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 监听Tab切换
const handleTabChange = (tabName) => {
  if (tabName === 'resources') {
    loadMyResources()
  } else if (tabName === 'questions') {
    loadMyQuestions()
  } else if (tabName === 'notifications') {
    loadNotifications()
  }
}

// 初始化
onMounted(() => {
  loadUserInfo()
  // 默认加载第一个tab的数据
  if (activeTab.value === 'resources') {
    loadMyResources()
  } else if (activeTab.value === 'questions') {
    loadMyQuestions()
  } else if (activeTab.value === 'notifications') {
    loadNotifications()
  }
})
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 16px;
}

.profile-tabs {
  min-height: 600px;
}

.tab-content {
  padding: 20px 0;
}

/* 个人信息卡片 */
.info-card,
.password-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-hint {
  font-size: 12px;
  color: #909399;
  cursor: pointer;
}

/* 我的资源 */
.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.resources-list,
.questions-list,
.notifications-list {
  min-height: 400px;
}

.resources-list-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.questions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 我的提问 */
.question-header {
  margin-bottom: 20px;
}

/* 我的消息 */
.notification-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.notification-content {
  flex: 1;
}

.notification-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.notification-text {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

:deep(.el-timeline-item__wrapper) {
  padding-left: 20px;
}

:deep(.el-card.unread) {
  border-left: 4px solid #409eff;
  background-color: #f0f9ff;
}

@media screen and (max-width: 768px) {
  .resources-grid,
  .questions-grid {
    grid-template-columns: 1fr;
  }

  .resource-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .resource-header .el-button {
    width: 100%;
  }
}
</style>