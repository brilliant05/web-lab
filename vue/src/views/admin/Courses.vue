<template>
  <el-card class="content-card" shadow="never">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <div class="operation-left">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入搜索关键词"
          :prefix-icon="Search"
          style="width: 300px"
          clearable
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
      <div class="operation-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增课程</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete" :disabled="selectedItems.length === 0">
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 数据列表 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      style="width: 100%; margin-top: 20px; flex: 1"
      height="100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="courseId" label="课程ID" width="100" />
      <el-table-column prop="courseCode" label="课程编号" width="120" />
      <el-table-column prop="courseName" label="课程名称" min-width="180" />
      <el-table-column prop="description" label="课程描述" min-width="250" show-overflow-tooltip />
      <el-table-column prop="college" label="开课学院" width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '开放' : '关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">
            编辑
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

    <!-- 新增/编辑课程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="courseName">
          <el-input
            v-model="formData.courseName"
            placeholder="请输入课程名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="课程编号" prop="courseCode">
          <el-input
            v-model="formData.courseCode"
            placeholder="请输入课程编号"
            maxlength="50"
          />
        </el-form-item>

        <el-form-item label="开课学院" prop="college">
          <el-select
            v-model="formData.college"
            placeholder="请选择开课学院"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="college in collegeList"
              :key="college"
              :label="college"
              :value="college"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="课程描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="课程封面">
          <el-upload
            :show-file-list="false"
            :before-upload="beforeCoverUpload"
            :http-request="handleCoverUpload"
            accept="image/*"
            class="cover-uploader"
          >
            <img v-if="formData.coverImage" :src="formData.coverImage" class="cover-image" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
            <div v-if="!formData.coverImage" class="cover-uploader-text">
              <div>点击上传封面</div>
              <div class="cover-uploader-hint">支持 JPG、PNG 格式，大小不超过 2MB</div>
            </div>
          </el-upload>
          <div v-if="formData.coverImage" class="cover-actions">
            <el-button link type="danger" size="small" @click="handleRemoveCover">删除封面</el-button>
          </div>
        </el-form-item>

        <el-form-item label="课程状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">开放</el-radio>
            <el-radio :label="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="课程详情"
      width="600px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="课程ID">{{ viewData.courseId }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ viewData.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程编号">{{ viewData.courseCode }}</el-descriptions-item>
        <el-descriptions-item label="开课学院">{{ viewData.college }}</el-descriptions-item>
        <el-descriptions-item label="课程描述">
          {{ viewData.description || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="课程封面">
          <img v-if="viewData.coverImage" :src="viewData.coverImage" class="view-cover-image" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'info'">
            {{ viewData.status === 1 ? '开放' : '关闭' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(viewData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDateTime(viewData.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ArrowDown, Delete, Edit, Plus, Refresh, Search, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { addCourse, deleteCourse, getCourseList, updateCourse, uploadCourseCover } from '@/api'
import { COLLEGE_LIST } from '@/utils/constants'

const collegeList = COLLEGE_LIST

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

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增课程')
const formType = ref('add') // 'add' | 'edit'
const formRef = ref(null)
const submitLoading = ref(false)
const uploadCoverLoading = ref(false)

// 查看详情相关
const viewDialogVisible = ref(false)
const viewData = ref({})

// 表单数据
const formData = reactive({
  courseId: null,
  courseName: '',
  courseCode: '',
  college: '',
  description: '',
  coverImage: '',
  status: 1
})

// 表单验证规则
const rules = {
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { max: 100, message: '课程名称不能超过100个字符', trigger: 'blur' }
  ],
  courseCode: [
    { required: true, message: '请输入课程编号', trigger: 'blur' },
    { max: 50, message: '课程编号不能超过50个字符', trigger: 'blur' }
  ],
  college: [
    { required: true, message: '请选择开课学院', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择课程状态', trigger: 'change' }
  ]
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  
  try {
    // 如果是字符串，检查格式
    if (typeof dateTime === 'string') {
      // 如果已经是格式化好的字符串（包含空格，格式：yyyy-MM-dd HH:mm:ss），直接返回
      if (dateTime.includes(' ') && dateTime.length > 10) {
        return dateTime
      }
      // ISO格式或其他格式，转换为本地格式
      const date = new Date(dateTime)
      if (isNaN(date.getTime())) {
        return dateTime // 如果转换失败，返回原字符串
      }
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      }).replace(/\//g, '-')
    }
    
    // 如果是Date对象或其他格式
    const date = new Date(dateTime)
    if (isNaN(date.getTime())) {
      return String(dateTime)
    }
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-')
  } catch (e) {
    console.error('日期格式化错误:', e, dateTime)
    return String(dateTime)
  }
}

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
  formType.value = 'add'
  dialogTitle.value = '新增课程'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  formType.value = 'edit'
  dialogTitle.value = '编辑课程'
  
  // 填充表单数据
  formData.courseId = row.courseId
  formData.courseName = row.courseName || ''
  formData.courseCode = row.courseCode || ''
  formData.college = row.college || ''
  formData.description = row.description || ''
  formData.coverImage = row.coverImage || ''
  formData.status = row.status !== undefined ? row.status : 1
  
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  viewData.value = { ...row }
  viewDialogVisible.value = true
}

// 删除单个
const handleDeleteSingle = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除课程"${row.courseName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteCourse(row.courseId)
    ElMessage.success('删除成功')
    loadTableData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error?.response?.data?.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请至少选择一项')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.value.length} 项吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 逐个删除
    const deletePromises = selectedItems.value.map(item => deleteCourse(item.courseId))
    await Promise.all(deletePromises)

    ElMessage.success('删除成功')
    loadTableData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
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

// 重置表单
const resetForm = () => {
  formData.courseId = null
  formData.courseName = ''
  formData.courseCode = ''
  formData.college = ''
  formData.description = ''
  formData.coverImage = ''
  formData.status = 1
  
  // 清除表单验证
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 封面上传前校验
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('封面大小不能超过 2MB!')
    return false
  }
  return true
}

// 上传封面
const handleCoverUpload = async (options) => {
  const file = options.file
  const uploadFormData = new FormData()
  uploadFormData.append('file', file)

  uploadCoverLoading.value = true
  try {
    const response = await uploadCourseCover(uploadFormData)
    if (response && response.code === 200 && response.data) {
      formData.coverImage = response.data.url
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error(response?.message || '封面上传失败')
    }
  } catch (error) {
    console.error('上传封面失败:', error)
    ElMessage.error(error?.response?.data?.message || '封面上传失败')
  } finally {
    uploadCoverLoading.value = false
  }
}

// 删除封面
const handleRemoveCover = () => {
  formData.coverImage = ''
  ElMessage.success('封面已删除')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const courseData = {
        courseName: formData.courseName.trim(),
        courseCode: formData.courseCode.trim(),
        college: formData.college,
        description: formData.description ? formData.description.trim() : null,
        coverImage: formData.coverImage || null,
        status: formData.status
      }

      if (formType.value === 'add') {
        // 新增课程
        const response = await addCourse(courseData)
        if (response && response.code === 200) {
          ElMessage.success('课程添加成功')
          dialogVisible.value = false
          loadTableData()
        } else {
          ElMessage.error(response?.message || '课程添加失败')
        }
      } else {
        // 编辑课程
        const response = await updateCourse(formData.courseId, courseData)
        if (response && response.code === 200) {
          ElMessage.success('课程更新成功')
          dialogVisible.value = false
          loadTableData()
        } else {
          ElMessage.error(response?.message || '课程更新失败')
        }
      }
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error(error?.response?.data?.message || '操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 加载表格数据
const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize
    }

    // 如果有搜索关键词，添加到查询参数中
    if (searchKeyword.value && searchKeyword.value.trim()) {
      params.courseName = searchKeyword.value.trim()
    }

    const response = await getCourseList(params)

    if (response && response.code === 200 && response.data) {
      const pageResult = response.data
      tableData.value = pageResult.records || pageResult.list || []
      pagination.total = pageResult.total || 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
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
  padding: 10px 0;
  flex-shrink: 0;
}

/* 封面上传样式 */
.cover-uploader {
  width: 100%;
}

.cover-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 100%;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--el-fill-color-light);
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.cover-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
}

.cover-uploader-icon {
  font-size: 28px;
  color: var(--el-text-color-placeholder);
}

.cover-uploader-text {
  text-align: center;
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.cover-uploader-hint {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
  margin-top: 8px;
}

.cover-actions {
  margin-top: 8px;
}

.view-cover-image {
  max-width: 300px;
  max-height: 200px;
  object-fit: contain;
  border-radius: 4px;
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

