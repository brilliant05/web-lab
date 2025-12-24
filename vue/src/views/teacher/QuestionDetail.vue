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
      </div>
    </template>

    <div v-if="currentQuestion" class="question-detail-container">
      <div class="question-info">
        <h2 class="question-title">{{ currentQuestion.questionTitle }}</h2>
        <div class="question-meta">
          <span><el-icon><User /></el-icon> {{ currentQuestion.studentName }}</span>
          <span><el-icon><Clock /></el-icon> {{ currentQuestion.createTime }}</span>
          <span><el-icon><CollectionTag /></el-icon> {{ currentQuestion.courseName }}</span>
          <el-tag :type="currentQuestion.isAnswered === 1 ? 'success' : 'warning'" size="small">
            {{ currentQuestion.isAnswered === 1 ? '已回答' : '待回答' }}
          </el-tag>
        </div>
        <div class="question-content">{{ currentQuestion.questionContent }}</div>
      </div>
      
      <el-divider />
      
      <div class="answer-section">
        <div class="section-title">回答列表</div>
        
        <!-- 回答列表 -->
        <div v-if="currentQuestion.answers && currentQuestion.answers.length > 0" class="answer-list">
          <div v-for="answer in currentQuestion.answers" :key="answer.answerId" class="answer-item">
            <div class="answer-header">
              <div class="teacher-info">
                <el-avatar :size="32" icon="UserFilled" />
                <span class="teacher-name">{{ answer.teacherName }}</span>
              </div>
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
        <el-empty v-else description="暂无回答" :image-size="60" />

        <el-divider />

        <!-- 回答输入框 -->
        <div class="answer-input-area">
          <div class="section-title">{{ editingAnswerId ? '修改回答' : '添加回答' }}</div>
          <div class="editor-wrapper">
            <div ref="toolbarContainer" class="toolbar-container"></div>
            <div ref="editorContainer" class="editor-container"></div>
          </div>
          <div class="action-buttons">
            <el-button v-if="editingAnswerId" @click="handleCancelEdit">取消修改</el-button>
            <el-button 
              type="primary" 
              @click="handleSubmitAnswer"
              :loading="submitLoading"
            >
              {{ editingAnswerId ? '保存修改' : '提交回答' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
    <el-skeleton v-else :rows="10" animated />
  </el-card>
</template>

<script setup>
import { answerQuestion, deleteAnswer, getQuestionDetail, updateAnswer } from '@/api'
import { ArrowLeft, Clock, CollectionTag, Delete, Edit, User } from '@element-plus/icons-vue'
import { createEditor, createToolbar } from '@wangeditor/editor'
import '@wangeditor/editor/dist/css/style.css'
import { ElMessage, ElMessageBox } from 'element-plus'
import { nextTick, onBeforeUnmount, onMounted, ref, shallowRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const currentQuestion = ref(null)
const submitLoading = ref(false)
const editingAnswerId = ref(null)

// Editor related
const editorRef = shallowRef()
const toolbarContainer = shallowRef()
const editorContainer = shallowRef()
const valueHtml = ref('')
const toolbarConfig = {}
const editorConfig = { 
  placeholder: '请输入回答内容...',
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

const goBack = () => {
  router.back()
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

const fetchQuestionDetail = async () => {
  const id = route.params.id
  if (!id) return
  
  try {
    const res = await getQuestionDetail(id)
    if (res.code === 200) {
      currentQuestion.value = res.data
      // 初始化编辑器
      initEditor()
    }
  } catch (error) {
    console.error('获取问题详情失败:', error)
    ElMessage.error('获取问题详情失败')
  }
}

const handleEditAnswer = (answer) => {
  editingAnswerId.value = answer.answerId
  valueHtml.value = answer.answerContent
  if (editorRef.value) {
    editorRef.value.setHtml(answer.answerContent)
    // 滚动到编辑器位置
    editorContainer.value.scrollIntoView({ behavior: 'smooth' })
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
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    const res = await deleteAnswer(answerId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchQuestionDetail()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmitAnswer = async () => {
  if (!valueHtml.value || valueHtml.value === '<p><br></p>') {
    ElMessage.warning('请输入回答内容')
    return
  }

  submitLoading.value = true
  try {
    if (editingAnswerId.value) {
      // 更新回答
      const res = await updateAnswer(editingAnswerId.value, {
        answerContent: valueHtml.value
      })
      if (res.code === 200) {
        ElMessage.success('修改成功')
        handleCancelEdit()
        fetchQuestionDetail()
      } else {
        ElMessage.error(res.message || '修改失败')
      }
    } else {
      // 新增回答
      const formData = new FormData()
      formData.append('answerContent', valueHtml.value)
      
      const res = await answerQuestion(currentQuestion.value.questionId, formData)
      if (res.code === 200) {
        ElMessage.success('回答成功')
        handleCancelEdit()
        fetchQuestionDetail()
      } else {
        ElMessage.error(res.message || '回答失败')
      }
    }
  } catch (error) {
    console.error('提交回答失败:', error)
    ElMessage.error('提交回答失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchQuestionDetail()
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

.editor-container {
  height: 400px;
  overflow-y: hidden;
}

.action-buttons {
  margin-top: 20px;
  text-align: right;
}
</style>