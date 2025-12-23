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
        />
        <el-select v-model="statusFilter" placeholder="回答状态" style="width: 120px" clearable>
          <el-option label="未回答" value="unanswered" />
          <el-option label="已回答" value="answered" />
        </el-select>
        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      style="width: 100%; margin-top: 20px; flex: 1"
      height="100%"
    >
      <el-table-column prop="title" label="问题标题" min-width="250" show-overflow-tooltip />
      <el-table-column prop="courseName" label="所属课程" width="180" />
      <el-table-column prop="studentName" label="提问学生" width="120" />
      <el-table-column prop="createTime" label="提问时间" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'answered' ? 'success' : 'warning'">
            {{ row.status === 'answered' ? '已回答' : '待回答' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleReply(row)">
            {{ row.status === 'answered' ? '查看' : '回答' }}
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
      />
    </div>
  </el-card>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'

const searchKeyword = ref('')
const statusFilter = ref('')
const loading = ref(false)

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 15
})

const tableData = ref([
  { id: 1, title: 'Servlet 中 doGet 和 doPost 的区别是什么？', courseName: 'Java Web 开发基础', studentName: '张三', createTime: '2023-12-20 10:30:00', status: 'unanswered' },
  { id: 2, title: 'Vue 3 中 setup 语法糖如何使用 props？', courseName: 'Vue.js 前端框架实战', studentName: '李四', createTime: '2023-12-19 15:20:00', status: 'answered' },
  { id: 3, title: '数据库事务的四大特性（ACID）具体指什么？', courseName: '数据库系统原理', studentName: '王五', createTime: '2023-12-18 09:00:00', status: 'unanswered' },
])

const handleSearch = () => {
  loading.value = true
  setTimeout(() => loading.value = false, 500)
}

const handleReply = (row) => {
  ElMessage.success(`打开回答窗口：${row.title}`)
}
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
