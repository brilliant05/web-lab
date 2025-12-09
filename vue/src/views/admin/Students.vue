<template>
  <el-card class="content-card" shadow="never">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <div class="operation-left">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入学生姓名或学号"
          :prefix-icon="Search"
          style="width: 300px"
          clearable
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
      <div class="operation-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增学生</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete" :disabled="selectedItems.length === 0">
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 数据列表 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      style="width: 100%; margin-top: 20px"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="userId" label="学号" width="100" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="email" label="邮箱" min-width="200" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="major" label="专业" width="150" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button link type="primary" size="small" @click="handleAssignCourse(row)">
            分配课程
          </el-button>
          <el-dropdown trigger="click" style="margin-left: 12px">
            <el-button link type="primary" size="small">
              更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="View" @click="handleView(row)">查看详情</el-dropdown-item>
                <el-dropdown-item :icon="Delete" style="color: var(--el-color-danger)" divided @click="handleDeleteSingle(row)">删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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
import { ArrowDown, Delete, Edit, Plus, Refresh, Search, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

// 搜索关键词
const searchKeyword = ref('')

// 加载状态
const loading = ref(false)

// 选中的项
const selectedItems = ref([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([])

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadTableData()
}

// 刷新
const handleRefresh = () => {
  searchKeyword.value = ''
  pagination.currentPage = 1
  loadTableData()
}

// 新增
const handleAdd = () => {
  ElMessage.info('打开新增学生对话框')
  // TODO: 打开新增对话框
}

// 批量删除
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
    // TODO: 调用批量删除接口
    ElMessage.success('删除成功')
    loadTableData()
  }).catch(() => {
    // 取消操作
  })
}

// 查看详情
const handleView = (row) => {
  ElMessage.info(`查看学生详情：${row.realName}`)
  // TODO: 打开详情对话框
}

// 分配课程
const handleAssignCourse = (row) => {
  ElMessage.info(`为学生 ${row.realName} 分配课程`)
  // TODO: 打开分配课程对话框
}

// 编辑
const handleEdit = (row) => {
  ElMessage.info(`编辑学生：${row.realName}`)
  // TODO: 打开编辑对话框
}

// 删除单个
const handleDeleteSingle = (row) => {
  ElMessageBox.confirm(`确定要删除学生"${row.realName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // TODO: 调用删除接口
    ElMessage.success('删除成功')
    loadTableData()
  }).catch(() => {
    // 取消操作
  })
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

// 每页数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  loadTableData()
}

// 当前页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadTableData()
}

// 加载表格数据
const loadTableData = () => {
  loading.value = true

  // 模拟接口请求
  return new Promise((resolve) => {
    setTimeout(() => {
      // 模拟数据
      const mockData = []
      const majors = ['计算机科学与技术', '软件工程', '网络工程', '数据科学', '人工智能']

      for (let i = 1; i <= pagination.pageSize; i++) {
        const index = (pagination.currentPage - 1) * pagination.pageSize + i
        mockData.push({
          userId: 2020000 + index,
          username: `student${index}`,
          realName: `学生${index}`,
          email: `student${index}@example.com`,
          phone: `138${String(index).padStart(8, '0')}`,
          major: majors[index % majors.length],
          createTime: new Date().toLocaleString('zh-CN')
        })
      }

      tableData.value = mockData
      pagination.total = 100 // 模拟总数
      loading.value = false
      resolve()
    }, 500)
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadTableData()
})
</script>

<style scoped>
.content-card {
  min-height: calc(100vh - 120px);
}

/* 操作栏 */
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

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 20px 0;
}

/* 响应式设计 */
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

