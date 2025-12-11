<template>
  <el-card class="content-card" shadow="never">
    <div class="operation-bar">
      <div class="operation-left">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入资源名称"
          :prefix-icon="Search"
          style="width: 300px"
          clearable
        />
        <el-select v-model="fileType" placeholder="文件类型" style="width: 150px" clearable>
          <el-option label="PDF" value="PDF" />
          <el-option label="Word" value="WORD" />
          <el-option label="PPT" value="PPT" />
          <el-option label="视频" value="VIDEO" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
      <div class="operation-right">
        <el-button type="primary" :icon="Upload" @click="handleUpload">上传资源</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete" :disabled="selectedItems.length === 0">
          批量删除
        </el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      style="width: 100%; margin-top: 20px; flex: 1"
      height="100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="resourceId" label="ID" width="80" />
      <el-table-column prop="resourceTitle" label="资源名称" min-width="200" />
      <el-table-column prop="fileType" label="文件类型" width="100" />
      <el-table-column prop="courseName" label="所属课程" width="150" />
      <el-table-column prop="uploadUsername" label="上传者" width="120" />
      <el-table-column prop="downloadCount" label="下载次数" width="100" />
      <el-table-column prop="createTime" label="上传时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleDownload(row)">下载</el-button>
          <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-dropdown trigger="click" style="margin-left: 12px">
            <el-button link type="primary" size="small">
              更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="Delete" style="color: var(--el-color-danger)" @click="handleDeleteSingle(row)">删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </el-card>
</template>

<script setup>
import { ArrowDown, Delete, Edit, Refresh, Search, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

const searchKeyword = ref('')
const fileType = ref('')
const loading = ref(false)
const selectedItems = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const handleSearch = () => {
  pagination.currentPage = 1
  loadTableData()
}

const handleRefresh = () => {
  searchKeyword.value = ''
  fileType.value = ''
  pagination.currentPage = 1
  loadTableData()
}

const handleUpload = () => {
  ElMessage.info('打开上传资源对话框')
}

const handleBatchDelete = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请至少选择一项')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.value.length} 项吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadTableData()
  }).catch(() => {})
}

const handleDownload = (row) => {
  ElMessage.success(`下载资源：${row.resourceTitle}`)
}

const handleEdit = (row) => {
  ElMessage.info(`编辑资源：${row.resourceTitle}`)
}

const handleDeleteSingle = (row) => {
  ElMessageBox.confirm(`确定要删除资源"${row.resourceTitle}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadTableData()
  }).catch(() => {})
}

const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  loadTableData()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadTableData()
}

const loadTableData = () => {
  loading.value = true

  return new Promise((resolve) => {
    setTimeout(() => {
      const mockData = []
      const types = ['PDF', 'WORD', 'PPT', 'VIDEO']
      const courses = ['数据结构', 'Java编程', '计算机网络', '操作系统']

      for (let i = 1; i <= pagination.pageSize; i++) {
        const index = (pagination.currentPage - 1) * pagination.pageSize + i
        mockData.push({
          resourceId: index,
          resourceTitle: `学习资源${index}`,
          fileType: types[index % types.length],
          courseName: courses[index % courses.length],
          uploadUsername: `教师${index % 10}`,
          downloadCount: Math.floor(Math.random() * 100),
          createTime: new Date().toLocaleString('zh-CN')
        })
      }

      tableData.value = mockData
      pagination.total = 150
      loading.value = false
      resolve()
    }, 500)
  })
}

onMounted(() => {
  loadTableData()
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
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.operation-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.operation-right {
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

@media screen and (max-width: 768px) {
  .operation-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .operation-left,
  .operation-right {
    width: 100%;
  }

  .operation-left .el-input {
    width: 100% !important;
  }
}

@media screen and (max-width: 576px) {
  .pagination-container {
    overflow-x: auto;
  }
}
</style>

