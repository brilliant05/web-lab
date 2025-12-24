<template>
  <el-card class="content-card" shadow="never">
    <div class="operation-bar">
      <div class="operation-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索问题关键词"
          prefix-icon="Search"
          style="width: 300px"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="statusFilter" placeholder="回答状态" style="width: 120px" clearable @change="handleSearch">
          <el-option label="未回答" :value="0" />
          <el-option label="已回答" :value="1" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      style="width: 100%; margin-top: 20px; flex: 1"
      height="100%"
    >
      <el-table-column prop="questionTitle" label="问题标题" min-width="250" show-overflow-tooltip />
      <el-table-column prop="courseName" label="所属课程" width="180" show-overflow-tooltip />
      <el-table-column prop="studentName" label="提问学生" width="120" />
      <el-table-column prop="createTime" label="提问时间" width="180" />
      <el-table-column prop="isAnswered" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isAnswered === 1 ? 'success' : 'warning'">
            {{ row.isAnswered === 1 ? '已回答' : '待回答' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleReply(row)">
            {{ row.isAnswered === 1 ? '查看' : '回答' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 回答/查看对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentQuestion?.isAnswered === 1 ? '查看详情' : '回答问题'"
      width="600px"
      @close="resetDialog"
      @opened="initEditor"
      @closed="destroyEditor"
    >
      <div v-if="currentQuestion" class="question-detail">
        <div class="detail-item">
          <span class="label">问题：</span>
          <span class="content title">{{ currentQuestion.questionTitle }}</span>
        </div>
        <div class="detail-item">
          <span class="label">描述：</span>
          <div class="content description">{{ currentQuestion.questionContent }}</div>
        </div>
        <div class="detail-info">
          <span><el-icon><User /></el-icon> {{ currentQuestion.studentName }}</span>
          <span><el-icon><Clock /></el-icon> {{ currentQuestion.createTime }}</span>
          <span><el-icon><CollectionTag /></el-icon> {{ currentQuestion.courseName }}</span>
        </div>
        
        <el-divider />
        
        <div class="answer-section">
          <div class="label" style="margin-bottom: 10px;">回答列表：</div>
          
          <!-- 回答列表 -->
          <div v-if="currentQuestion.answers && currentQuestion.answers.length > 0" class="answer-list">
            <div v-for="answer in currentQuestion.answers" :key="answer.answerId" class="answer-item">
              <div class="answer-header">
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
          <el-empty v-else description="暂无回答" :image-size="60" />

          <el-divider />

          <!-- 回答输入框 -->
          <div class="answer-input-area">
            <div class="label">{{ editingAnswerId ? '修改回答：' : '添加回答：' }}</div>
            <div style="border: 1px solid #ccc">
              <div ref="toolbarContainer" style="border-bottom: 1px solid #ccc"></div>
              <div ref="editorContainer" style="height: 300px; overflow-y: hidden;"></div>
            </div>
            <div class="action-buttons" style="margin-top: 10px; text-align: right;">
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
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { answerQuestion, deleteAnswer, getQuestionDetail, getQuestionList, updateAnswer } from '@/api'
import { Clock, CollectionTag, Delete, Edit, Search, User } from '@element-plus/icons-vue'
import { createEditor, createToolbar } from '@wangeditor/editor'
import '@wangeditor/editor/dist/css/style.css'
import { ElMessage, ElMessageBox } from 'element-plus'
import { nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'

const searchKeyword = ref('')
const statusFilter = ref(null) // null for all, 0 for unanswered, 1 for answered
const loading = ref(false)
const tableData = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// Dialog related
const dialogVisible = ref(false)
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
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      server: 'http://localhost:8080/api/v1/files/upload',
      fieldName: 'file',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      customInsert(res, insertFn) {
        // res 即服务端的返回结果
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

const fetchQuestions = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      isAnswered: statusFilter.value
    }
    const res = await getQuestionList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取问题列表失败:', error)
    ElMessage.error('获取问题列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  fetchQuestions()
}

const handlePageChange = (val) => {
  pagination.currentPage = val
  fetchQuestions()
}

const handleReply = async (row) => {
  try {
    const res = await getQuestionDetail(row.questionId)
    if (res.code === 200) {
      currentQuestion.value = res.data
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取问题详情失败:', error)
    ElMessage.error('获取问题详情失败')
  }
}

const resetDialog = () => {
  currentQuestion.value = null
  valueHtml.value = ''
  editingAnswerId.value = null
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
      // 如果没有回答了，刷新列表状态
      if (!res.data.answers || res.data.answers.length === 0) {
        fetchQuestions()
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmitAnswer = async () => {
  // 确保 valueHtml 是最新的
  if (editorRef.value) {
    valueHtml.value = editorRef.value.getHtml()
  }
  
  if (editorRef.value && editorRef.value.isEmpty()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  
  submitLoading.value = true
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
      // 富文本模式下，图片已嵌入HTML，不再需要单独的images参数
      
      const res = await answerQuestion(currentQuestion.value.questionId, formData)
      if (res.code === 200) {
        ElMessage.success('回答成功')
        valueHtml.value = ''
        if (editorRef.value) {
          editorRef.value.setHtml('')
        }
        
        // 刷新详情
        const detailRes = await getQuestionDetail(currentQuestion.value.questionId)
        if (detailRes.code === 200) {
          currentQuestion.value = detailRes.data
        }
        fetchQuestions() // 刷新列表状态
      } else {
        ElMessage.error(res.message || '回答失败')
      }
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchQuestions()
})
</script>

<style scoped>
.content-card {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.operation-bar {
  display: flex;
  align-items: center;
}

.operation-left {
  display: flex;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 10px 0;
  flex-shrink: 0;
}

.question-detail {
  padding: 0 10px;
}

.detail-item {
  margin-bottom: 15px;
}

.label {
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
  display: block;
}

.content {
  color: #606266;
  line-height: 1.6;
}

.content.title {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.content.description {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

.detail-info {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 13px;
  margin-top: 10px;
}

.detail-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.existing-answer {
  background-color: var(--el-color-success-light-9);
  padding: 15px;
  border-radius: 4px;
  color: var(--el-text-color-regular);
  line-height: 1.6;
  border: 1px solid var(--el-color-success-light-8);
}

.answer-list {
  margin-bottom: 20px;
}

.answer-item {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
}

.teacher-name {
  font-weight: bold;
  color: #409eff;
}

.answer-time {
  color: #909399;
  margin-left: 10px;
  flex: 1;
}

.answer-content {
  color: #303133;
  line-height: 1.6;
  white-space: pre-wrap;
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
