<template>
  <div class="resource-list-page">
    <!-- 搜索栏区域 -->
    <el-card class="search-card" shadow="never">
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入资源名称或简介"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.fileType"
          placeholder="文件类型"
          class="file-type-select"
          clearable
        >
          <el-option label="全部类型" value="" />
          <el-option label="PDF" value="PDF" />
          <el-option label="Word" value="WORD" />
          <el-option label="Excel" value="EXCEL" />
          <el-option label="PPT" value="PPT" />
          <el-option label="图片" value="IMAGE" />
          <el-option label="视频" value="VIDEO" />
          <el-option label="压缩包" value="ZIP" />
        </el-select>
        <el-select
          v-model="searchForm.orderBy"
          placeholder="排序方式"
          class="sort-select"
        >
          <el-option label="最新上传" value="createTime" />
          <el-option label="下载量" value="downloadCount" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </el-card>

    <!-- 资源列表 -->
    <div class="resources-container">
      <div v-loading="loading" class="resources-grid">
        <ResourceCard
          v-for="resource in resources"
          :key="resource.resourceId"
          :resource="resource"
          @collect-changed="handleCollectChanged"
        />
        <el-empty
          v-if="!loading && resources.length === 0"
          description="暂无资源"
          :image-size="120"
          class="empty-state"
        />
      </div>
    </div>

    <!-- 分页组件 -->
    <div v-if="resources.length > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[12, 24, 48]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getResourceList } from '@/api'
import ResourceCard from '@/components/student/ResourceCard.vue'

const route = useRoute()
const router = useRouter()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  fileType: '',
  orderBy: 'createTime'
})

// 加载状态
const loading = ref(false)

// 资源列表
const resources = ref([])

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 12,
  total: 0
})

// 从URL获取课程ID（如果从课程列表跳转过来）
const courseIdFromUrl = route.query.courseId ? Number(route.query.courseId) : null

// 加载资源列表
const loadResources = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      orderBy: searchForm.orderBy,
      orderDir: 'desc' // 默认降序
    }

    // 添加搜索条件
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    if (searchForm.fileType) {
      params.fileType = searchForm.fileType
    }
    // 如果有课程ID，添加课程筛选
    if (courseIdFromUrl) {
      params.courseId = courseIdFromUrl
    }

    const response = await getResourceList(params)
    if (response && response.code === 200 && response.data) {
      resources.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response?.message || '获取资源列表失败')
      resources.value = []
    }
  } catch (error) {
    console.error('加载资源列表失败:', error)
    ElMessage.error('加载资源列表失败')
    // 失败时使用mock数据
    resources.value = [
      {
        resourceId: 1,
        resourceTitle: 'Java基础教程PPT',
        description: 'Java编程语言基础教程课件，包含变量、数据类型、控制结构等内容',
        courseName: 'Java编程',
        uploaderName: '张老师',
        downloadCount: 125,
        fileType: 'PPT',
        fileSize: 5242880,
        createTime: new Date().toISOString(),
        isCollected: false
      },
      {
        resourceId: 2,
        resourceTitle: '数据结构复习资料',
        description: '数据结构期末考试复习重点整理',
        courseName: '数据结构',
        uploaderName: '李同学',
        downloadCount: 89,
        fileType: 'PDF',
        fileSize: 2097152,
        createTime: new Date(Date.now() - 86400000).toISOString(),
        isCollected: true
      }
    ]
    pagination.total = resources.value.length
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadResources()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.fileType = ''
  searchForm.orderBy = 'createTime'
  pagination.currentPage = 1
  // 如果是从课程跳转来的，保留课程筛选
  if (courseIdFromUrl) {
    router.replace({ query: { courseId: courseIdFromUrl } })
  } else {
    router.replace({ query: {} })
  }
  loadResources()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  loadResources()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadResources()
}

// 收藏状态改变
const handleCollectChanged = (resource) => {
  // 可以在这里更新本地状态或刷新列表
  console.log('收藏状态已更新:', resource)
}

// 监听排序方式变化
watch(
  () => searchForm.orderBy,
  () => {
    handleSearch()
  }
)

// 监听文件类型变化
watch(
  () => searchForm.fileType,
  () => {
    handleSearch()
  }
)

// 初始化
onMounted(() => {
  loadResources()
})
</script>

<style scoped>
.resource-list-page {
  max-width: 1400px;
  margin: 0 auto;
}

.search-card {
  margin-bottom: 24px;
  border-radius: 16px;
}

.search-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  max-width: 400px;
}

.file-type-select,
.sort-select {
  width: 150px;
}

.resources-container {
  margin-bottom: 24px;
}

.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  min-height: 400px;
}

.empty-state {
  grid-column: 1 / -1;
  padding: 60px 0;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media screen and (min-width: 1200px) {
  .resources-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 1199px) and (min-width: 768px) {
  .resources-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 767px) {
  .resources-grid {
    grid-template-columns: 1fr;
  }

  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    max-width: 100%;
  }

  .file-type-select,
  .sort-select {
    width: 100%;
  }

  .search-bar .el-button {
    width: 100%;
  }
}
</style>