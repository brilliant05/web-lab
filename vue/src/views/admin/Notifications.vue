<template>
  <el-card class="content-card" shadow="never">
    <div class="operation-bar">
      <div class="operation-left">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入通知标题"
          :prefix-icon="Search"
          style="width: 300px"
          clearable
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
      <div class="operation-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">发布通知</el-button>
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
      <el-table-column prop="notificationId" label="ID" width="80" />
      <el-table-column prop="title" label="通知标题" min-width="250" />
      <el-table-column prop="notificationType" label="通知类型" width="120">
        <template #default="{ row }">
          <el-tag :type="getTypeColor(row.notificationType)">{{ row.notificationType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isRead" label="已读状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isRead ? 'success' : 'warning'">
            {{ row.isRead ? '已读' : '未读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
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

    <!-- 发布通知弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="发布系统通知"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form label-width="80px" :model="dialogForm">
        <el-form-item label="通知标题" required>
          <el-input
            v-model="dialogForm.title"
            placeholder="请输入通知标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="通知内容" required>
          <el-input
            v-model="dialogForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入通知内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="发送范围">
          <span>当前版本：向全体用户发送系统通知</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="dialogLoading" @click="handleDialogConfirm">
            确 定
          </el-button>
        </span>
      </template>
    </el-dialog>

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
import { ArrowDown, Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { createSystemNotification, getNotificationList } from '@/api'

const searchKeyword = ref('')
const loading = ref(false)
const selectedItems = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

// 发布通知弹窗相关状态
const dialogVisible = ref(false)
const dialogLoading = ref(false)
const dialogForm = reactive({
  title: '',
  content: ''
})

const resetDialogForm = () => {
  dialogForm.title = ''
  dialogForm.content = ''
}

const getTypeColor = (type) => {
  const colors = {
    SYSTEM: 'info',
    ANSWER_REPLY: 'success',
    RESOURCE_AUDIT: 'warning',
    COURSE: 'primary'
  }
  return colors[type] || 'info'
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadTableData()
}

const handleRefresh = () => {
  searchKeyword.value = ''
  pagination.currentPage = 1
  loadTableData()
}

// 打开发布通知弹窗
const handleAdd = () => {
  resetDialogForm()
  dialogVisible.value = true
}

// 确认发布通知（当前为全体用户广播）
const handleDialogConfirm = async () => {
  if (!dialogForm.title || !dialogForm.title.trim()) {
    ElMessage.warning('请填写通知标题')
    return
  }
  if (!dialogForm.content || !dialogForm.content.trim()) {
    ElMessage.warning('请填写通知内容')
    return
  }

  dialogLoading.value = true
  try {
    // userId 传 null 表示全体用户，由后端按约定处理
    const res = await createSystemNotification({
      userId: null,
      title: dialogForm.title.trim(),
      content: dialogForm.content.trim()
    })
    if (res && res.code === 200) {
      ElMessage.success(res.message || '通知发布成功')
      dialogVisible.value = false
      // 重新加载列表，展示新通知
      pagination.currentPage = 1
      await loadTableData()
    } else {
      ElMessage.error(res?.message || '通知发布失败')
    }
  } catch (error) {
    console.error('发布通知失败:', error)
    ElMessage.error('发布通知失败，请稍后重试')
  } finally {
    dialogLoading.value = false
  }
}

const handleBatchDelete = () => {
  // 后端暂无删除通知接口，这里仅提示
  ElMessage.info('当前不支持批量删除通知')
}

const handleView = (row) => {
  ElMessage.info(`查看通知详情：${row.title}`)
}

const handleEdit = (row) => {
  ElMessage.info(`编辑通知：${row.title}`)
}

const handleDeleteSingle = () => {
  // 后端暂无删除通知接口，这里仅提示
  ElMessage.info('当前不支持删除通知')
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

// 日期格式化
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  try {
    if (typeof dateTime === 'string') {
      if (dateTime.includes(' ') && dateTime.length > 10) return dateTime
      const d = new Date(dateTime)
      if (isNaN(d.getTime())) return dateTime
      return d
        .toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        })
        .replace(/\\//g, '-')
    }
    const d = new Date(dateTime)
    if (isNaN(d.getTime())) return String(dateTime)
    return d
      .toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
      .replace(/\\//g, '-')
  } catch (e) {
    console.error('日期格式化错误:', e, dateTime)
    return String(dateTime)
  }
}

const loadTableData = () => {
  loading.value = true

  const params = {
    pageNum: pagination.currentPage,
    pageSize: pagination.pageSize,
    notificationType: undefined,
    isRead: undefined
  }

  return getNotificationList(params)
    .then((res) => {
      if (res && res.code === 200 && res.data) {
        const page = res.data
        tableData.value = page.records || []
        pagination.total = page.total || 0
      } else {
        tableData.value = []
        pagination.total = 0
      }
    })
    .catch((error) => {
      console.error('加载通知列表失败:', error)
      ElMessage.error('加载通知列表失败')
      tableData.value = []
      pagination.total = 0
    })
    .finally(() => {
      loading.value = false
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

