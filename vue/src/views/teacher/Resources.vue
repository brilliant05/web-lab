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
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="courseFilter" placeholder="所属课程" style="width: 180px" clearable @change="handleSearch">
          <el-option 
            v-for="course in courses" 
            :key="course.courseId" 
            :label="course.courseName" 
            :value="course.courseId" 
          />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      </div>
      <div class="operation-right">
        <el-button type="warning" :icon="Delete" @click="handleOpenRecycleBin">回收站</el-button>
        <el-button type="primary" :icon="Upload" @click="handleUpload">发布资源</el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      style="width: 100%; margin-top: 20px; flex: 1"
      height="100%"
    >
      <el-table-column prop="resourceTitle" label="资源名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="courseName" label="所属课程" width="180" show-overflow-tooltip />
      <el-table-column prop="fileType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ row.fileType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fileSize" label="大小" width="100">
        <template #default="{ row }">
          {{ formatFileSize(row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column prop="visibility" label="可见性" width="120">
        <template #default="{ row }">
          <el-tag :type="row.visibility === 'PUBLIC' ? 'success' : 'warning'" size="small">
            {{ row.visibility === 'PUBLIC' ? '公开' : '仅课程学生' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="downloadCount" label="下载量" width="100" align="center" />
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handlePreview(row)">预览</el-button>
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
        @current-change="handlePageChange"
      />
    </div>
  </el-card>
</template>

<script setup>
import { deleteResource, getMyCourses, getMyUploads } from '@/api'
import { Delete, Search, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchKeyword = ref('')
const courseFilter = ref('')
const loading = ref(false)
const courses = ref([])
const tableData = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 获取课程列表
const fetchCourses = async () => {
  try {
    const res = await getMyCourses()
    if (res.code === 200) {
      courses.value = res.data.records || res.data
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

// 获取资源列表
const fetchResources = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      resourceTitle: searchKeyword.value,
      courseId: courseFilter.value
    }
    const res = await getMyUploads(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取资源列表失败:', error)
    ElMessage.error('获取资源列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  fetchResources()
}

const handlePageChange = (val) => {
  pagination.currentPage = val
  fetchResources()
}

const handleOpenRecycleBin = () => {
  router.push('/teacher/resources/recycle-bin')
}

const handleUpload = () => {
  router.push('/teacher/resources/publish')
}

const handlePreview = (row) => {
  router.push(`/teacher/resources/${row.resourceId}`)
}

const handleEdit = (row) => {
  router.push({
    path: '/teacher/resources/publish',
    query: {
      id: row.resourceId,
      courseId: row.courseId,
      title: row.resourceTitle,
      description: row.description,
      visibility: row.visibility
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除资源 "${row.resourceTitle}" 吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      const res = await deleteResource(row.resourceId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchResources()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

onMounted(() => {
  fetchCourses()
  fetchResources()
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
