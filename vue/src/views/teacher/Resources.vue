<template>
  <el-card class="content-card" shadow="never">
    <div class="operation-bar">
      <div class="operation-left">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入资源名称"
          prefix-icon="Search"
          style="width: 300px"
          clearable
        />
        <el-select v-model="courseFilter" placeholder="所属课程" style="width: 180px" clearable>
          <el-option label="Java Web 开发基础" value="1" />
          <el-option label="Vue.js 前端框架实战" value="2" />
        </el-select>
        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
      </div>
      <div class="operation-right">
        <el-button type="primary" icon="Upload" @click="handleUpload">发布资源</el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      style="width: 100%; margin-top: 20px; flex: 1"
      height="100%"
    >
      <el-table-column prop="title" label="资源名称" min-width="200" />
      <el-table-column prop="courseName" label="所属课程" width="180" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="downloadCount" label="下载量" width="100" align="center" />
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { reactive, ref } from 'vue'

const searchKeyword = ref('')
const courseFilter = ref('')
const loading = ref(false)

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 25
})

const tableData = ref([
  { id: 1, title: 'Java Web 课程大纲.pdf', courseName: 'Java Web 开发基础', type: 'PDF', downloadCount: 120, createTime: '2023-12-01 10:00:00' },
  { id: 2, title: 'Servlet 生命周期详解.pptx', courseName: 'Java Web 开发基础', type: 'PPT', downloadCount: 85, createTime: '2023-12-05 14:30:00' },
  { id: 3, title: 'Vue 3 响应式原理.docx', courseName: 'Vue.js 前端框架实战', type: 'WORD', downloadCount: 60, createTime: '2023-12-10 09:15:00' },
])

const handleSearch = () => {
  loading.value = true
  setTimeout(() => loading.value = false, 500)
}

const handleUpload = () => {
  ElMessage.success('打开上传资源对话框')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑资源：${row.title}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除资源 "${row.title}" 吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  })
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
  justify-content: space-between;
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
