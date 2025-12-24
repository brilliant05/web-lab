<template>
  <el-card class="content-card">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <el-button link @click="goBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <span class="title">资源回收站</span>
        </div>
        <el-button type="primary" @click="fetchRecycleBin">刷新</el-button>
      </div>
    </template>

    <el-table v-loading="loading" :data="tableData" style="width: 100%" height="100%">
      <el-table-column prop="resourceTitle" label="资源名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="courseName" label="所属课程" width="150" show-overflow-tooltip />
      <el-table-column prop="fileType" label="类型" width="80" />
      <el-table-column prop="updateTime" label="删除时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="success" size="small" @click="handleRestore(row)">恢复</el-button>
          <el-button link type="danger" size="small" @click="handlePermanentDelete(row)">彻底删除</el-button>
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
import { deleteResourcePermanently, getRecycleBinList, restoreResource } from '@/api'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const goBack = () => {
  router.back()
}

const fetchRecycleBin = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize
    }
    const res = await getRecycleBinList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取回收站列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  pagination.currentPage = val
  fetchRecycleBin()
}

const handleRestore = async (row) => {
  try {
    const res = await restoreResource(row.resourceId)
    if (res.code === 200) {
      ElMessage.success('恢复成功')
      fetchRecycleBin()
    } else {
      ElMessage.error(res.message || '恢复失败')
    }
  } catch (error) {
    ElMessage.error('恢复失败')
  }
}

const handlePermanentDelete = (row) => {
  ElMessageBox.confirm(`确定要彻底删除资源 "${row.resourceTitle}" 吗？此操作不可恢复！`, '警告', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      const res = await deleteResourcePermanently(row.resourceId)
      if (res.code === 200) {
        ElMessage.success('彻底删除成功')
        fetchRecycleBin()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  fetchRecycleBin()
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

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 10px 0;
  flex-shrink: 0;
}
</style>