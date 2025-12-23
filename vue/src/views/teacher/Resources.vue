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
        @current-change="handlePageChange"
      />
    </div>

    <!-- 资源上传/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="formType === 'add' ? '发布资源' : '编辑资源'"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" label-width="80px" ref="formRef" :rules="rules">
        <el-form-item label="所属课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%" :disabled="formType === 'edit'">
            <el-option 
              v-for="course in courses" 
              :key="course.courseId" 
              :label="course.courseName" 
              :value="course.courseId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资源标题" prop="resourceTitle">
          <el-input v-model="form.resourceTitle" placeholder="请输入资源标题" />
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入资源描述" />
        </el-form-item>
        <el-form-item label="可见性" prop="visibility">
          <el-radio-group v-model="form.visibility">
            <el-radio label="PUBLIC">公开 (所有学生可见)</el-radio>
            <el-radio label="COURSE_ONLY">私有 (仅本课程学生可见)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件" prop="file" v-if="formType === 'add'">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            :on-exceed="handleExceed"
            ref="uploadRef"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { deleteResource, getMyCourses, getMyUploads, updateResource, uploadResource } from '@/api'
import { Search, Upload, UploadFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

const searchKeyword = ref('')
const courseFilter = ref('')
const loading = ref(false)
const courses = ref([])
const tableData = ref([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formType = ref('add')
const formRef = ref(null)
const uploadRef = ref(null)

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  resourceId: null,
  courseId: null,
  resourceTitle: '',
  description: '',
  visibility: 'COURSE_ONLY',
  file: null
})

const rules = {
  courseId: [{ required: true, message: '请选择所属课程', trigger: 'change' }],
  resourceTitle: [{ required: true, message: '请输入资源标题', trigger: 'blur' }],
  visibility: [{ required: true, message: '请选择可见性', trigger: 'change' }],
  file: [{ required: true, message: '请上传文件', trigger: 'change' }]
}

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

const handleUpload = () => {
  formType.value = 'add'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  formType.value = 'edit'
  form.resourceId = row.resourceId
  form.courseId = row.courseId
  form.resourceTitle = row.resourceTitle
  form.description = row.description
  form.visibility = row.visibility
  dialogVisible.value = true
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

const handleFileChange = (file) => {
  form.file = file.raw
  // 自动填充标题
  if (!form.resourceTitle) {
    form.resourceTitle = file.name
  }
}

const handleExceed = (files) => {
  uploadRef.value.clearFiles()
  const file = files[0]
  uploadRef.value.handleStart(file)
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.resourceId = null
  form.courseId = null
  form.resourceTitle = ''
  form.description = ''
  form.visibility = 'COURSE_ONLY'
  form.file = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  // 编辑模式下不需要校验文件
  if (formType.value === 'edit') {
    // 移除文件校验规则
    const { file, ...editRules } = rules
    // 这里简单处理，实际应该动态设置 rules
  }

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (formType.value === 'add') {
          const formData = new FormData()
          formData.append('courseId', form.courseId)
          formData.append('resourceTitle', form.resourceTitle)
          formData.append('description', form.description || '')
          formData.append('visibility', form.visibility)
          formData.append('file', form.file)
          
          const res = await uploadResource(formData)
          if (res.code === 200) {
            ElMessage.success('发布成功')
            dialogVisible.value = false
            fetchResources()
          } else {
            ElMessage.error(res.message || '发布失败')
          }
        } else {
          const data = {
            resourceTitle: form.resourceTitle,
            description: form.description,
            visibility: form.visibility
          }
          const res = await updateResource(form.resourceId, data)
          if (res.code === 200) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            fetchResources()
          } else {
            ElMessage.error(res.message || '更新失败')
          }
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
      } finally {
        submitLoading.value = false
      }
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
