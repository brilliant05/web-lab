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
          <div class="label" style="margin-bottom: 10px;">回答：</div>
          <div v-if="currentQuestion.isAnswered === 1">
            <div class="existing-answer">{{ currentAnswer }}</div>
            <div class="answer-time" v-if="answerTime">回答时间：{{ answerTime }}</div>
          </div>
          <div v-else>
            <el-input
              v-model="answerContent"
              type="textarea"
              :rows="6"
              placeholder="请输入您的回答..."
            />
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button 
            v-if="currentQuestion?.isAnswered === 0" 
            type="primary" 
            @click="handleSubmitAnswer"
            :loading="submitLoading"
          >
            提交回答
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { answerQuestion, getQuestionDetail, getQuestionList } from '@/api'
import { Clock, CollectionTag, Search, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

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
const answerContent = ref('')
const currentAnswer = ref('')
const answerTime = ref('')
const submitLoading = ref(false)

const fetchQuestions = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      questionTitle: searchKeyword.value,
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
      // 如果已回答，填充回答内容
      if (res.data.isAnswered === 1 && res.data.answerList && res.data.answerList.length > 0) {
        // 假设取最新的回答
        const latestAnswer = res.data.answerList[0]
        currentAnswer.value = latestAnswer.answerContent
        answerTime.value = latestAnswer.createTime
      }
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取问题详情失败:', error)
    ElMessage.error('获取问题详情失败')
  }
}

const resetDialog = () => {
  currentQuestion.value = null
  answerContent.value = ''
  currentAnswer.value = ''
  answerTime.value = ''
}

const handleSubmitAnswer = async () => {
  if (!answerContent.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  
  submitLoading.value = true
  try {
    const data = {
      content: answerContent.value
    }
    const res = await answerQuestion(currentQuestion.value.questionId, data)
    if (res.code === 200) {
      ElMessage.success('回答成功')
      dialogVisible.value = false
      fetchQuestions()
    } else {
      ElMessage.error(res.message || '回答失败')
    }
  } catch (error) {
    console.error('回答失败:', error)
    ElMessage.error('回答失败')
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

.answer-time {
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  text-align: right;
}
</style>
