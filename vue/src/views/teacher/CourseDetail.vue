<template>
  <div class="course-detail-container">
    <!-- 课程头部信息 -->
    <el-card class="course-header" shadow="never" v-loading="courseLoading">
      <div class="header-content">
        <div class="course-cover">
          <CourseCover 
            :src="courseInfo.coverImage" 
            :title="courseInfo.courseName" 
            :id="courseInfo.courseId" 
          />
        </div>
        <div class="course-info">
          <h2 class="course-title">{{ courseInfo.courseName }}</h2>
          <div class="course-meta">
            <el-tag size="small">{{ courseInfo.courseCode }}</el-tag>
            <el-tag size="small" type="info">{{ courseInfo.college }}</el-tag>
            <el-tag size="small" :type="courseInfo.status === 1 ? 'success' : 'info'">
              {{ courseInfo.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
          </div>
          <p class="course-desc">{{ courseInfo.description || '暂无描述' }}</p>
          <div class="invite-code">
            <span>邀请码：</span>
            <el-tag v-if="courseInfo.inviteCode" type="warning" effect="plain" class="code-tag">
              {{ courseInfo.inviteCode }}
            </el-tag>
            <span v-else class="no-code" style="color: #909399; font-size: 14px; margin-right: 8px;">未设置</span>
            <el-button v-if="courseInfo.inviteCode" link type="primary" size="small" @click="copyInviteCode">复制</el-button>
            <el-button link type="primary" size="small" @click="handleEditInviteCode">设置</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 邀请码设置对话框 -->
    <el-dialog
      v-model="inviteCodeDialogVisible"
      title="设置邀请码"
      width="400px"
    >
      <el-form 
        ref="inviteCodeFormRef"
        :model="inviteCodeForm" 
        :rules="inviteCodeRules"
        label-width="80px"
      >
        <el-form-item label="邀请码" prop="code">
          <el-input v-model="inviteCodeForm.code" placeholder="请输入6-20位纯数字邀请码" maxlength="20" show-word-limit />
          <div style="font-size: 12px; color: #909399; margin-top: 4px;">
            学生可通过此邀请码加入课程
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="inviteCodeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveInviteCode" :loading="savingInviteCode">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 主要内容区域 -->
    <el-card class="main-content" shadow="never">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <!-- 学生管理 -->
        <el-tab-pane label="学生管理" name="students">
          <div class="tab-pane-content">
            <div class="operation-bar">
              <el-input
                v-model="studentSearch"
                placeholder="搜索学生姓名/学号"
                style="width: 300px"
                :prefix-icon="Search"
                clearable
                @input="filterStudents"
              />
              <span class="student-count">共 {{ filteredStudents.length }} 名学生</span>
            </div>
            <el-table :data="filteredStudents" style="width: 100%" v-loading="studentsLoading">
              <el-table-column type="index" label="序号" width="80" align="center" />
              <el-table-column prop="realName" label="姓名" width="120" />
              <el-table-column prop="username" label="学号" width="150" />
              <el-table-column prop="college" label="学院" width="180" />
              <el-table-column prop="email" label="邮箱" min-width="200" />
              <el-table-column prop="phone" label="手机号" width="150" />
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 课程资源 -->
        <el-tab-pane label="课程资源" name="resources">
          <div class="tab-pane-content">
            <div class="operation-bar">
              <div class="left">
                <el-input
                  v-model="resourceSearch"
                  placeholder="搜索资源名称"
                  style="width: 300px"
                  :prefix-icon="Search"
                  clearable
                  @keyup.enter="loadResources"
                />
                <el-button type="primary" @click="loadResources">搜索</el-button>
              </div>
              <div class="right">
                <el-button type="primary" :icon="Upload" @click="handleUploadResource">上传资源</el-button>
              </div>
            </div>
            
            <el-table :data="resourceList" style="width: 100%" v-loading="resourcesLoading">
              <el-table-column prop="resourceTitle" label="资源名称" min-width="200" show-overflow-tooltip />
              <el-table-column prop="fileType" label="类型" width="100">
                <template #default="{ row }">
                  <el-tag size="small">{{ row.fileType }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="fileSize" label="大小" width="100">
                <template #default="{ row }">
                  {{ formatFileSize(row.fileSize) }}
                </template>
              </el-table-column>
              <el-table-column prop="visibility" label="可见性" width="120">
                <template #default="{ row }">
                  <el-tag :type="row.visibility === 'PUBLIC' ? 'success' : 'warning'" size="small">
                    {{ row.visibility === 'PUBLIC' ? '公开' : '仅课程学生' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="downloadCount" label="下载" width="80" align="center" />
              <el-table-column prop="createTime" label="上传时间" width="180" />
              <el-table-column label="操作" width="220" fixed="right">
                <template #default="{ row }">
                  <el-button link type="primary" size="small" @click="handlePreview(row)">预览</el-button>
                  <el-button link type="primary" size="small" @click="handleEditResource(row)">编辑</el-button>
                  <el-button link type="danger" size="small" @click="handleDeleteResource(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="pagination-container">
              <el-pagination
                v-model:current-page="resourcePagination.pageNum"
                v-model:page-size="resourcePagination.pageSize"
                :total="resourcePagination.total"
                layout="total, prev, pager, next"
                @current-change="handleResourcePageChange"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 问答讨论 -->
        <el-tab-pane label="问答讨论" name="questions">
          <div class="tab-pane-content">
            <div class="operation-bar">
              <el-radio-group v-model="questionFilter" @change="loadQuestions">
                <el-radio-button label="all">全部问题</el-radio-button>
                <el-radio-button label="unanswered">待回答</el-radio-button>
              </el-radio-group>
              <el-button :icon="Refresh" circle @click="loadQuestions" />
            </div>

            <div class="question-list" v-loading="questionsLoading">
              <el-empty v-if="questionList.length === 0" description="暂无提问" />
              
              <el-card 
                v-for="question in questionList" 
                :key="question.questionId" 
                class="question-card" 
                shadow="hover"
                @click="handleViewQuestion(question)"
              >
                <div class="question-header">
                  <h3 class="question-title">{{ question.questionTitle }}</h3>
                  <el-tag v-if="question.isAnswered === 1" type="success" size="small">已回答</el-tag>
                  <el-tag v-else type="danger" size="small">待回答</el-tag>
                </div>
                <div class="question-meta">
                  <span><el-icon><User /></el-icon> {{ question.studentName }}</span>
                  <span><el-icon><Clock /></el-icon> {{ question.createTime }}</span>
                  <span><el-icon><View /></el-icon> {{ question.viewCount }}</span>
                </div>
                <p class="question-preview">{{ question.questionContent }}</p>
              </el-card>
            </div>

            <div class="pagination-container">
              <el-pagination
                v-model:current-page="questionPagination.pageNum"
                v-model:page-size="questionPagination.pageSize"
                :total="questionPagination.total"
                layout="total, prev, pager, next"
                @current-change="handleQuestionPageChange"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>




  </div>
</template>

<script setup>
import {
    deleteResource,
    getCourseById,
    getCourseStudents,
    getQuestionList,
    getResourceList,
    updateInviteCode
} from '@/api'
import CourseCover from '@/components/CourseCover.vue'
import { Clock, Refresh, Search, Upload, User, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id

// 课程信息
const courseInfo = ref({})
const courseLoading = ref(false)

// 邀请码相关
const inviteCodeDialogVisible = ref(false)
const savingInviteCode = ref(false)
const inviteCodeFormRef = ref(null)
const inviteCodeForm = reactive({
  code: ''
})

const inviteCodeRules = {
  code: [
    { required: true, message: '请输入邀请码', trigger: 'blur' },
    { pattern: /^\d{6,20}$/, message: '邀请码必须为6到20位的纯数字组合', trigger: 'blur' }
  ]
}

const handleEditInviteCode = () => {
  inviteCodeForm.code = courseInfo.value.inviteCode || ''
  inviteCodeDialogVisible.value = true
  // 重置校验状态
  if (inviteCodeFormRef.value) {
    inviteCodeFormRef.value.clearValidate()
  }
}

const saveInviteCode = async () => {
  if (!inviteCodeFormRef.value) return
  
  await inviteCodeFormRef.value.validate(async (valid) => {
    if (valid) {
      savingInviteCode.value = true
      try {
        await updateInviteCode(courseId, inviteCodeForm.code)
        ElMessage.success('邀请码设置成功')
        inviteCodeDialogVisible.value = false
        // Reload course info to update display
        loadCourseInfo()
      } catch (error) {
        console.error('设置邀请码失败', error)
        // 错误信息已由拦截器处理，这里不再重复弹窗
      } finally {
        savingInviteCode.value = false
      }
    }
  })
}

// 标签页
const activeTab = ref('students')

// 学生管理
const studentsLoading = ref(false)
const studentList = ref([])
const studentSearch = ref('')
const filteredStudents = computed(() => {
  if (!studentSearch.value) return studentList.value
  const keyword = studentSearch.value.toLowerCase()
  return studentList.value.filter(s => 
    (s.realName && s.realName.toLowerCase().includes(keyword)) ||
    (s.username && s.username.toLowerCase().includes(keyword))
  )
})

// 资源管理
const resourcesLoading = ref(false)
const resourceList = ref([])
const resourceSearch = ref('')
const resourcePagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 问答管理
const questionsLoading = ref(false)
const questionList = ref([])
const questionFilter = ref('all')
const questionPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 初始化
onMounted(async () => {
  if (!courseId) {
    ElMessage.error('参数错误')
    router.push('/teacher/courses')
    return
  }
  await loadCourseInfo()
  loadStudents() // 默认加载学生
})


const handleTabClick = (tab) => {
  if (tab.paneName === 'students') {
    loadStudents()
  } else if (tab.paneName === 'resources') {
    loadResources()
  } else if (tab.paneName === 'questions') {
    loadQuestions()
  }
}

// 加载课程信息
const loadCourseInfo = async () => {
  courseLoading.value = true
  try {
    const res = await getCourseById(courseId)
    if (res.code === 200) {
      courseInfo.value = res.data
    }
  } catch (error) {
    console.error('加载课程信息失败', error)
  } finally {
    courseLoading.value = false
  }
}

// 复制邀请码
const copyInviteCode = () => {
  if (courseInfo.value.inviteCode) {
    navigator.clipboard.writeText(courseInfo.value.inviteCode)
    ElMessage.success('邀请码已复制')
  }
}

// 加载学生列表
const loadStudents = async () => {
  studentsLoading.value = true
  try {
    const res = await getCourseStudents(courseId)
    if (res.code === 200) {
      studentList.value = res.data
    }
  } catch (error) {
    console.error('加载学生列表失败', error)
  } finally {
    studentsLoading.value = false
  }
}

const filterStudents = () => {
  // computed property handles filtering
}

// 加载资源列表
const loadResources = async () => {
  resourcesLoading.value = true
  try {
    const params = {
      pageNum: resourcePagination.pageNum,
      pageSize: resourcePagination.pageSize,
      courseId: courseId,
      keyword: resourceSearch.value || undefined
    }
    const res = await getResourceList(params)
    if (res.code === 200) {
      resourceList.value = res.data.records
      resourcePagination.total = Number(res.data.total)
    }
  } catch (error) {
    console.error('加载资源列表失败', error)
  } finally {
    resourcesLoading.value = false
  }
}

const handleResourcePageChange = (page) => {
  resourcePagination.pageNum = page
  loadResources()
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const handleUploadResource = () => {
  router.push({
    path: '/teacher/resources/publish',
    query: { courseId: courseId }
  })
}

const handleEditResource = (row) => {
  router.push({
    path: '/teacher/resources/publish',
    query: {
      id: row.resourceId,
      courseId: row.courseId,
      title: row.resourceTitle,
      description: row.description,
      visibility: row.visibility
    }
  })
}

const handleDeleteResource = (row) => {
  ElMessageBox.confirm('确定要删除该资源吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteResource(row.resourceId)
      ElMessage.success('删除成功')
      loadResources()
    } catch (error) {
      console.error('删除失败', error)
    }
  })
}

// 加载问题列表
const loadQuestions = async () => {
  questionsLoading.value = true
  try {
    const params = {
      pageNum: questionPagination.pageNum,
      pageSize: questionPagination.pageSize,
      courseId: courseId,
      isAnswered: questionFilter.value === 'unanswered' ? 0 : undefined
    }
    const res = await getQuestionList(params)
    if (res.code === 200) {
      questionList.value = res.data.records
      questionPagination.total = Number(res.data.total)
    }
  } catch (error) {
    console.error('加载问题列表失败', error)
  } finally {
    questionsLoading.value = false
  }
}

const handleQuestionPageChange = (page) => {
  questionPagination.pageNum = page
  loadQuestions()
}

const handleViewQuestion = (question) => {
  router.push(`/teacher/questions/${question.questionId}`)
}

const handlePreview = (row) => {
  router.push(`/teacher/resources/${row.resourceId}`)
}
</script>

<style scoped>
.course-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.course-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  gap: 20px;
}

.course-cover {
  width: 200px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.course-info {
  flex: 1;
}

.course-title {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #303133;
}

.course-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.course-desc {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 10px;
}

.invite-code {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #606266;
}

.main-content {
  min-height: 500px;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.operation-bar .left {
  display: flex;
  gap: 10px;
}

.student-count {
  color: #909399;
  font-size: 14px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.question-card {
  cursor: pointer;
  transition: all 0.3s;
}

.question-card:hover {
  transform: translateY(-2px);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.question-title {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.question-meta {
  display: flex;
  gap: 15px;
  color: #909399;
  font-size: 13px;
  margin-bottom: 10px;
}

.question-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.question-preview {
  color: #606266;
  font-size: 14px;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.detail-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
}

.detail-header h3 {
  margin: 0 0 10px 0;
}

.detail-meta {
  color: #909399;
  font-size: 13px;
  display: flex;
  gap: 20px;
}

.detail-content {
  font-size: 15px;
  line-height: 1.6;
  color: #303133;
  margin-bottom: 20px;
  white-space: pre-wrap;
}

.answer-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.answer-item {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.answer-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.teacher-name {
  font-weight: bold;
  color: #409eff;
}

.answer-time {
  color: #909399;
}

.answer-content {
  color: #606266;
  line-height: 1.5;
}

.no-answer {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.answer-actions {
  display: flex;
  gap: 10px;
}

.answer-images {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.answer-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  cursor: pointer;
}

.upload-area {
  margin-top: 10px;
}
</style>
