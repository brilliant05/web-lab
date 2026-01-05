<template>
  <el-card class="content-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <el-button link @click="goBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <span class="title">问题详情</span>
        </div>
        <div class="header-right">
          <el-button type="primary" size="small" @click="handleEditQuestion">编辑问题</el-button>
          <el-button type="danger" size="small" @click="handleDeleteQuestion">删除问题</el-button>
        </div>
      </div>
    </template>

    <div class="question-detail-container" v-loading="loading">
      <!-- 问题信息 -->
      <div class="question-info">
        <h2 class="question-title">{{ question.questionTitle || '加载中...' }}</h2>
        <div class="question-meta">
          <span><el-icon><User /></el-icon> {{ question.studentName || '未知' }}</span>
          <span><el-icon><Clock /></el-icon> {{ formatDateTime(question.createTime) }}</span>
          <span><el-icon><CollectionTag /></el-icon> {{ question.courseName || '未分类' }}</span>
          <el-tag :type="question.isAnswered === 1 ? 'success' : 'warning'" size="small">
            {{ question.isAnswered === 1 ? '已回答' : '待回答' }}
          </el-tag>
          <span style="margin-left: auto">浏览: {{ question.viewCount || 0 }}</span>
          <span>回答: {{ question.answerCount || 0 }}</span>
        </div>
        <div class="question-content">
          <div class="content-text">{{ question.questionContent }}</div>
          <!-- 问题图片 -->
          <div class="question-images" v-if="questionImages.length > 0">
            <el-image
              v-for="(img, index) in questionImages"
              :key="index"
              :src="img"
              :preview-src-list="questionImages"
              :initial-index="index"
              fit="cover"
              class="content-image"
              :preview-teleported="true"
            />
          </div>
        </div>
      </div>

      <el-divider />

      <!-- 回答区域 -->
      <div class="answer-section">
        <div class="section-title">回答列表 ({{ question.answers?.length || 0 }})</div>

        <div v-if="!question.answers || question.answers.length === 0" class="no-answers">
          <el-empty description="暂无回答" :image-size="100" />
        </div>

        <div v-else class="answer-list">
          <div
            v-for="answer in question.answers"
            :key="answer.answerId"
            class="answer-item"
          >
            <!-- 回答者信息 -->
            <div class="answer-header">
              <div class="teacher-info">
                <el-avatar :size="32" :src="answer.teacherAvatar">
                  <el-icon><UserFilled /></el-icon>
                </el-avatar>
                <span class="teacher-name">{{ answer.teacherName || '未知教师' }}</span>
                <el-tag v-if="answer.jobTitle" type="info" size="small" style="margin-left: 8px">
                  {{ answer.jobTitle }}
                </el-tag>
              </div>
              <span class="answer-time">{{ formatDateTime(answer.createTime) }}</span>
              <div class="answer-actions">
                <el-button type="primary" link :icon="Edit" @click="handleEditAnswer(answer)">编辑</el-button>
                <el-button type="danger" link :icon="Delete" @click="handleDeleteAnswer(answer)">删除</el-button>
              </div>
            </div>

            <!-- 回答内容 -->
            <div class="answer-content">
              <div class="answer-text" v-html="answer.answerContent"></div>
              <!-- 回答图片 -->
              <div class="answer-images" v-if="getAnswerImages(answer).length > 0">
                <el-image
                  v-for="(img, index) in getAnswerImages(answer)"
                  :key="index"
                  :src="img"
                  :preview-src-list="getAnswerImages(answer)"
                  :initial-index="index"
                  fit="cover"
                  class="content-image"
                  :preview-teleported="true"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑问题对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑问题"
      width="50%"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="questionTitle">
          <el-input v-model="editForm.questionTitle" placeholder="请输入问题标题" />
        </el-form-item>
        <el-form-item label="内容" prop="questionContent">
          <el-input
            v-model="editForm.questionContent"
            type="textarea"
            :rows="6"
            placeholder="请输入问题详细内容"
          />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="editForm.tags" placeholder="多个标签请用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="submitting">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑回答对话框 -->
    <el-dialog
      v-model="editAnswerDialogVisible"
      title="编辑回答"
      width="50%"
      :close-on-click-modal="false"
      @opened="handleEditorOpened"
      @closed="handleEditorClosed"
    >
      <el-form
        ref="editAnswerFormRef"
        :model="editAnswerForm"
        :rules="editAnswerRules"
        label-width="80px"
      >
        <el-form-item label="内容" prop="answerContent">
          <div style="border: 1px solid #ccc; width: 100%">
            <div ref="toolbarContainer" style="border-bottom: 1px solid #ccc"></div>
            <div ref="editorContainer" style="height: 300px; overflow-y: hidden;"></div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editAnswerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditAnswer" :loading="submittingAnswer">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted, shallowRef, nextTick, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, UserFilled, ArrowLeft, Clock, CollectionTag, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createEditor, createToolbar } from '@wangeditor/editor'
import '@wangeditor/editor/dist/css/style.css'
import {
  getQuestionDetail,
  deleteQuestion,
  updateQuestion,
  updateAnswer,
  deleteAnswer
} from '@/api'

const route = useRoute()
const router = useRouter()

const goBack = () => {
  router.back()
}

const loading = ref(false)
const question = ref({})

// 编辑问题相关
const editDialogVisible = ref(false)
const submitting = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  questionTitle: '',
  questionContent: '',
  tags: ''
})

const editRules = {
  questionTitle: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  questionContent: [
    { required: true, message: '请输入问题内容', trigger: 'blur' }
  ]
}

// 编辑回答相关
const editAnswerDialogVisible = ref(false)
const submittingAnswer = ref(false)
const editAnswerFormRef = ref(null)
const currentAnswerId = ref(null)
const editAnswerForm = ref({
  answerContent: ''
})

const editAnswerRules = {
  answerContent: [
    { required: true, message: '请输入回答内容', trigger: 'blur' }
  ]
}

// 编辑器相关
const editorRef = shallowRef()
const toolbarContainer = shallowRef()
const editorContainer = shallowRef()
const editorConfig = { 
  placeholder: '请输入回答内容...',
  MENU_CONF: {
    uploadImage: {
      server: 'http://localhost:8080/api/v1/files/upload', // 假设的上传接口，根据实际情况调整
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
    editAnswerForm.value.answerContent = editor.getHtml()
  }
}

const initEditor = () => {
  if (editorRef.value) return 
  
  nextTick(() => {
    if (!editorContainer.value) return
    
    const editor = createEditor({
      selector: editorContainer.value,
      html: editAnswerForm.value.answerContent,
      config: editorConfig,
      mode: 'default'
    })
    editorRef.value = editor

    const toolbar = createToolbar({
      editor,
      selector: toolbarContainer.value,
      config: {},
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

const handleEditorOpened = () => {
  initEditor()
}

const handleEditorClosed = () => {
  destroyEditor()
}

// 标签列表
const tagList = computed(() => {
  if (!question.value.tags) return []
  return question.value.tags.split(',').filter(tag => tag.trim())
})

// 问题图片列表
const questionImages = computed(() => {
  if (!question.value.imageUrls) return []
  return question.value.imageUrls.split(',').filter(url => url.trim())
})

// 获取回答图片列表
const getAnswerImages = (answer) => {
  if (!answer.imageUrls) return []
  return answer.imageUrls.split(',').filter(url => url.trim())
}

// 加载问题详情
const loadQuestionDetail = async () => {
  loading.value = true
  try {
    const questionId = route.params.id
    const response = await getQuestionDetail(questionId)
    if (response && response.code === 200 && response.data) {
      question.value = response.data
    } else {
      ElMessage.error(response?.message || '获取问题详情失败')
      router.back()
    }
  } catch (error) {
    console.error('加载问题详情失败:', error)
    ElMessage.error('加载问题详情失败')
    router.back()
  } finally {
    loading.value = false
  }
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
  const seconds = date.getSeconds().toString().padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 编辑问题
const handleEditQuestion = () => {
  editForm.value = {
    questionTitle: question.value.questionTitle,
    questionContent: question.value.questionContent,
    tags: question.value.tags
  }
  editDialogVisible.value = true
}

// 提交编辑问题
const submitEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const questionId = route.params.id
        await updateQuestion(questionId, editForm.value)
        ElMessage.success('修改成功')
        editDialogVisible.value = false
        loadQuestionDetail()
      } catch (error) {
        console.error('修改问题失败:', error)
        ElMessage.error(error?.response?.data?.message || '修改失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 删除问题
const handleDeleteQuestion = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个问题吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const questionId = route.params.id
    await deleteQuestion(questionId)
    ElMessage.success('删除成功')
    router.push('/admin/questions')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除问题失败:', error)
      ElMessage.error(error?.response?.data?.message || '删除失败')
    }
  }
}

// 编辑回答
const handleEditAnswer = (answer) => {
  currentAnswerId.value = answer.answerId
  editAnswerForm.value = {
    answerContent: answer.answerContent
  }
  editAnswerDialogVisible.value = true
}

// 提交编辑回答
const submitEditAnswer = async () => {
  if (!editAnswerFormRef.value) return
  
  await editAnswerFormRef.value.validate(async (valid) => {
    if (valid) {
      submittingAnswer.value = true
      try {
        await updateAnswer(currentAnswerId.value, editAnswerForm.value)
        ElMessage.success('修改成功')
        editAnswerDialogVisible.value = false
        loadQuestionDetail()
      } catch (error) {
        console.error('修改回答失败:', error)
        ElMessage.error(error?.response?.data?.message || '修改失败')
      } finally {
        submittingAnswer.value = false
      }
    }
  })
}

// 删除回答
const handleDeleteAnswer = async (answer) => {
  try {
    await ElMessageBox.confirm('确定要删除这个回答吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteAnswer(answer.answerId)
    ElMessage.success('删除成功')
    loadQuestionDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除回答失败:', error)
      ElMessage.error(error?.response?.data?.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadQuestionDetail()
})

onBeforeUnmount(() => {
  destroyEditor()
})
</script>

<style scoped>
.content-card {
  min-height: calc(100vh - 120px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.question-detail-container {
  max-width: 1000px;
  margin: 0 auto;
}

.question-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}

.question-meta {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
  align-items: center;
}

.question-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.question-content {
  font-size: 16px;
  line-height: 1.6;
  color: #606266;
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 4px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  border-left: 4px solid #409eff;
  padding-left: 10px;
}

.answer-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.answer-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.teacher-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.teacher-name {
  font-weight: bold;
  color: #303133;
}

.answer-time {
  color: #909399;
  font-size: 14px;
}

.answer-content {
  line-height: 1.6;
  color: #606266;
}

.answer-images {
  margin-top: 15px;
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

.editor-wrapper {
  border: 1px solid #ccc;
  z-index: 100;
}

.toolbar-container {
  border-bottom: 1px solid #ccc;
}

.content-image {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  cursor: pointer;
}
</style>