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
  </el-card>
</template>

<script setup>
import { getQuestionList } from '@/api'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchKeyword = ref('')
const statusFilter = ref(null) // null for all, 0 for unanswered, 1 for answered
const loading = ref(false)
const tableData = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
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

const handleReply = (row) => {
  router.push(`/teacher/questions/${row.questionId}`)
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


</style>
