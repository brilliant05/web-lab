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
          <div class="invite-code" v-if="courseInfo.inviteCode">
            <span>邀请码：</span>
            <el-tag type="warning" effect="plain" class="code-tag">
              {{ courseInfo.inviteCode }}
            </el-tag>
            <el-button link type="primary" size="small" @click="copyInviteCode">复制</el-button>
          </div>
        </div>
      </div>
    </el-card>

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

    <!-- 资源上传/编辑对话框 -->
    <el-dialog
      v-model="resourceDialogVisible"
      :title="resourceFormType === 'add' ? '上传资源' : '编辑资源'"
      width="500px"
    >
      <el-form :model="resourceForm" label-width="80px" ref="resourceFormRef" :rules="resourceRules">
        <el-form-item label="标题" prop="resourceTitle">
          <el-input v-model="resourceForm.resourceTitle" placeholder="请输入资源标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="resourceForm.description" type="textarea" placeholder="请输入资源描述" />
        </el-form-item>
        <el-form-item label="可见性" prop="visibility">
          <el-radio-group v-model="resourceForm.visibility">
            <el-radio label="PUBLIC">公开 (所有学生可见)</el-radio>
            <el-radio label="COURSE_ONLY">私有 (仅本课程学生可见)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件" prop="file" v-if="resourceFormType === 'add'">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            :on-exceed="handleExceed"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resourceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitResource" :loading="resourceSubmitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 问题详情与回答对话框 -->
    <el-dialog
      v-model="questionDialogVisible"
      title="问题详情"
      width="700px"
      destroy-on-close
      @opened="initEditor"
      @closed="destroyEditor"
    >
      <div class="question-detail" v-if="currentQuestion">
        <div class="detail-header">
          <h3>{{ currentQuestion.questionTitle }}</h3>
          <div class="detail-meta">
            <span>提问者: {{ currentQuestion.studentName }}</span>
            <span>时间: {{ currentQuestion.createTime }}</span>
          </div>
        </div>
        <div class="detail-content">
          {{ currentQuestion.questionContent }}
        </div>
        <div class="detail-images" v-if="currentQuestion.imageUrls">
           <!-- 图片展示逻辑 -->
        </div>

        <el-divider content-position="left">教师回答</el-divider>

        <div v-if="currentQuestion.answers && currentQuestion.answers.length > 0" class="answer-list">
          <div v-for="answer in currentQuestion.answers" :key="answer.answerId" class="answer-item">
            <div class="answer-meta">
              <span class="teacher-name">{{ answer.teacherName }}</span>
              <span class="answer-time">{{ answer.createTime }}</span>
              <div class="answer-actions">
                <el-button type="primary" link :icon="Edit" @click="handleEditAnswer(answer)">编辑</el-button>
                <el-button type="danger" link :icon="Delete" @click="handleDeleteAnswer(answer.answerId)">删除</el-button>
              </div>
            </div>
            <div class="answer-content" v-html="answer.answerContent"></div>
            <div v-if="answer.imageUrls" class="answer-images">
              <el-image 
                v-for="(url, index) in answer.imageUrls.split(',')" 
                :key="index" 
                :src="url" 
                :preview-src-list="answer.imageUrls.split(',')"
                class="answer-image"
              />
            </div>
          </div>
        </div>
        <div v-else class="no-answer">暂无回答</div>

        <div class="answer-form" style="margin-top: 20px;">
          <h4>{{ editingAnswerId ? '修改回答' : '撰写回答' }}</h4>
          <div style="border: 1px solid #ccc">
            <div ref="toolbarContainer" style="border-bottom: 1px solid #ccc"></div>
            <div ref="editorContainer" style="height: 300px; overflow-y: hidden;"></div>
          </div>
          <div style="margin-top: 10px; text-align: right;">
            <el-button v-if="editingAnswerId" @click="handleCancelEdit">取消修改</el-button>
            <el-button type="primary" @click="submitAnswer" :loading="answerSubmitLoading">
              {{ editingAnswerId ? '保存修改' : '提交回答' }}
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {
  answerQuestion,
  deleteAnswer,
  deleteResource,
  getCourseById,
  getCourseStudents,
  getQuestionDetail,
  getQuestionList,
  getResourceList,
  updateAnswer,
  updateResource,
  uploadResource
} from '@/api'
import CourseCover from '@/components/CourseCover.vue'
import { Clock, Delete, Edit, Refresh, Search, Upload, UploadFilled, User, View } from '@element-plus/icons-vue'
import { createEditor, createToolbar } from '@wangeditor/editor'
import '@wangeditor/editor/dist/css/style.css'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id

// 课程信息
const courseInfo = ref({})
const courseLoading = ref(false)

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
const resourceDialogVisible = ref(false)
const resourceFormType = ref('add')
const resourceSubmitLoading = ref(false)
const resourceForm = reactive({
  resourceId: null,
  resourceTitle: '',
  description: '',
  visibility: 'PUBLIC',
  file: null
})
const resourceFormRef = ref(null)
const resourceRules = {
  resourceTitle: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  visibility: [{ required: true, message: '请选择可见性', trigger: 'change' }],
  file: [{ required: true, message: '请上传文件', trigger: 'change' }]
}

// 问答管理
const questionsLoading = ref(false)
const questionList = ref([])
const questionFilter = ref('all')
const questionPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const questionDialogVisible = ref(false)
const currentQuestion = ref(null)
const answerSubmitLoading = ref(false)
const editingAnswerId = ref(null)

// Editor related
const editorRef = shallowRef()
const toolbarContainer = shallowRef()
const editorContainer = shallowRef()
const valueHtml = ref('')
const toolbarConfig = {}
const editorConfig = { 
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      server: 'http://localhost:8080/api/v1/files/upload',
      fieldName: 'file',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      customInsert(res, insertFn) {
        if (res.code === 200) {
          insertFn(res.data.url, '', '')
        } else {
          ElMessage.error(res.msg || '上传图片失败')
        }
      }
    }
  },
  onChange(editor) {
    valueHtml.value = editor.getHtml()
  }
}

const initEditor = () => {
  if (editorRef.value) return 
  
  nextTick(() => {
    if (!editorContainer.value) return
    
    const editor = createEditor({
      selector: editorContainer.value,
      html: valueHtml.value,
      config: editorConfig,
      mode: 'default'
    })
    editorRef.value = editor

    const toolbar = createToolbar({
      editor,
      selector: toolbarContainer.value,
      config: toolbarConfig,
      mode: 'default'
    })
  })
}

const destroyEditor = () => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
  editorRef.value = null
}

onBeforeUnmount(() => {
  destroyEditor()
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
  resourceFormType.value = 'add'
  resourceForm.resourceId = null
  resourceForm.resourceTitle = ''
  resourceForm.description = ''
  resourceForm.visibility = 'PUBLIC'
  resourceForm.file = null
  resourceDialogVisible.value = true
}

const handleEditResource = (row) => {
  resourceFormType.value = 'edit'
  resourceForm.resourceId = row.resourceId
  resourceForm.resourceTitle = row.resourceTitle
  resourceForm.description = row.description
  resourceForm.visibility = row.visibility
  resourceDialogVisible.value = true
}

const handleFileChange = (file) => {
  resourceForm.file = file.raw
}

const handleExceed = (files) => {
  ElMessage.warning('只能上传一个文件，请先删除旧文件')
}

const submitResource = async () => {
  if (!resourceFormRef.value) return
  
  // 编辑模式下不需要校验文件
  if (resourceFormType.value === 'edit') {
    // 简单校验
    if (!resourceForm.resourceTitle) {
      ElMessage.warning('请输入标题')
      return
    }
  } else {
    await resourceFormRef.value.validate()
  }

  resourceSubmitLoading.value = true
  try {
    if (resourceFormType.value === 'add') {
      const formData = new FormData()
      formData.append('file', resourceForm.file)
      formData.append('resourceTitle', resourceForm.resourceTitle)
      formData.append('description', resourceForm.description || '')
      formData.append('courseId', courseId)
      formData.append('visibility', resourceForm.visibility)
      
      await uploadResource(formData)
      ElMessage.success('上传成功')
    } else {
      const data = {
        resourceTitle: resourceForm.resourceTitle,
        description: resourceForm.description,
        visibility: resourceForm.visibility
      }
      await updateResource(resourceForm.resourceId, data)
      ElMessage.success('更新成功')
    }
    resourceDialogVisible.value = false
    loadResources()
  } catch (error) {
    console.error('提交资源失败', error)
    ElMessage.error('操作失败')
  } finally {
    resourceSubmitLoading.value = false
  }
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

const handleViewQuestion = async (question) => {
  try {
    const res = await getQuestionDetail(question.questionId)
    if (res.code === 200) {
      currentQuestion.value = res.data
      valueHtml.value = ''
      if (editorRef.value) {
        editorRef.value.setHtml('')
      }
      editingAnswerId.value = null
      questionDialogVisible.value = true
      // 如果已经在问答Tab且编辑器已初始化，这里不需要额外操作
      // 如果是弹窗模式（这里似乎是弹窗），则需要在弹窗打开后初始化
      // 但 CourseDetail 里的 questionDialogVisible 似乎是用于查看详情的弹窗？
      // 检查代码发现 CourseDetail 里并没有使用 el-dialog 来包裹 answer-form，
      // 而是直接显示在页面下方（当 currentQuestion 存在时？）
      // 不，CourseDetail 里并没有 el-dialog 包裹 answer-form。
      // 让我们检查一下模板结构。
      // 模板里似乎没有 el-dialog 包裹 answer-form。
      // 实际上，CourseDetail.vue 的结构是：
      // Tab -> Questions List -> (Click View) -> Dialog?
      // 让我们再读一下 CourseDetail.vue 的模板部分。
    }
  } catch (error) {
    console.error('获取问题详情失败', error)
  }
}

const handleEditAnswer = (answer) => {
  editingAnswerId.value = answer.answerId
  valueHtml.value = answer.answerContent
  if (editorRef.value) {
    editorRef.value.setHtml(answer.answerContent)
  }
}

const handleCancelEdit = () => {
  editingAnswerId.value = null
  valueHtml.value = ''
  if (editorRef.value) {
    editorRef.value.setHtml('')
  }
}

const handleDeleteAnswer = async (answerId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条回答吗？', '提示', {
      type: 'warning'
    })
    
    await deleteAnswer(answerId)
    ElMessage.success('删除成功')
    
    // 刷新详情
    const res = await getQuestionDetail(currentQuestion.value.questionId)
    if (res.code === 200) {
      currentQuestion.value = res.data
    }
    loadQuestions() // 刷新列表状态
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const submitAnswer = async () => {
  // 确保 valueHtml 是最新的
  if (editorRef.value) {
    valueHtml.value = editorRef.value.getHtml()
  }

  if (editorRef.value && editorRef.value.isEmpty()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  
  answerSubmitLoading.value = true
  try {
    if (editingAnswerId.value) {
      // 更新回答
      const data = {
        answerContent: valueHtml.value
      }
      const res = await updateAnswer(editingAnswerId.value, data)
      if (res.code === 200) {
        ElMessage.success('更新成功')
        editingAnswerId.value = null
        valueHtml.value = ''
        if (editorRef.value) {
          editorRef.value.setHtml('')
        }
        
        // 刷新详情
        const detailRes = await getQuestionDetail(currentQuestion.value.questionId)
        if (detailRes.code === 200) {
          currentQuestion.value = detailRes.data
        }
      } else {
        ElMessage.error(res.message || '更新失败')
      }
    } else {
      // 新增回答
      const formData = new FormData()
      formData.append('answerContent', valueHtml.value)
      
      await answerQuestion(currentQuestion.value.questionId, formData)
      ElMessage.success('回答成功')
      
      // 刷新详情
      const res = await getQuestionDetail(currentQuestion.value.questionId)
      if (res.code === 200) {
        currentQuestion.value = res.data
      }
      valueHtml.value = ''
      if (editorRef.value) {
        editorRef.value.setHtml('')
      }
      loadQuestions() // 刷新列表状态
    }
  } catch (error) {
    console.error('操作失败', error)
    ElMessage.error('操作失败')
  } finally {
    answerSubmitLoading.value = false
  }
}
const handlePreview = (row) => {
  if (row.filePath) {
    window.open(row.filePath, '_blank')
  } else {
    ElMessage.warning('文件地址不存在')
  }
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
