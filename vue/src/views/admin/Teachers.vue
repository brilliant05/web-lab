<template>
  <el-card class="content-card" shadow="never">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <div class="operation-left">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入教师姓名或工号"
          :prefix-icon="Search"
          style="width: 300px"
          clearable
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
      <div class="operation-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增教师</el-button>
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
      :row-class-name="tableRowClassName"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="80" align="center" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="jobTitle" label="职称" width="120" />
      <el-table-column prop="college" label="院系" width="150" />
      <el-table-column prop="email" label="邮箱" min-width="200">
        <template #default="{ row }">
          <span v-if="row.email">{{ row.email }}</span>
          <span v-else style="color: #909399">暂未绑定</span>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130">
        <template #default="{ row }">
          <span v-if="row.phone">{{ row.phone }}</span>
          <span v-else style="color: #909399">暂未绑定</span>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="(val) => handleStatusChange(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
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
                <el-dropdown-item :icon="List" @click="handleViewCourses(row)">查看课程</el-dropdown-item>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
      align-center
      class="teacher-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
        class="teacher-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="formData.username" 
            placeholder="请输入用户名" 
            :disabled="formType === 'edit'"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="姓名" prop="realName">
          <el-input 
            v-model="formData.realName" 
            placeholder="请输入真实姓名" 
            :prefix-icon="User"
          />
        </el-form-item>

        <!-- <el-form-item label="密码" prop="password" v-if="formType === 'add'">
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="请输入密码" 
            show-password 
            :prefix-icon="Lock"
          />
        </el-form-item> -->

        <el-form-item label="职称" prop="jobTitle">
          <el-select v-model="formData.jobTitle" placeholder="请选择职称" style="width: 100%">
            <template #prefix><el-icon><Medal /></el-icon></template>
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
          </el-select>
        </el-form-item>

        <el-form-item label="院系" prop="college">
          <el-select 
            v-model="formData.college" 
            placeholder="请选择所属院系" 
            style="width: 100%" 
            filterable
          >
            <template #prefix><el-icon><School /></el-icon></template>
            <el-option 
              v-for="item in collegeList" 
              :key="item" 
              :label="item" 
              :value="item" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model="formData.email" 
            placeholder="请输入邮箱" 
            :prefix-icon="Message"
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="formData.phone" 
            placeholder="请输入手机号" 
            :prefix-icon="Iphone"
          />
        </el-form-item>

        <el-form-item label="分配课程">
          <el-select
            v-model="formData.courseIds"
            placeholder="请选择课程（可选，可多选）"
            multiple
            filterable
            style="width: 100%"
            :loading="coursesLoading"
          >
            <el-option
              v-for="course in courseList"
              :key="course.courseId"
              :label="`${course.courseName} (${course.courseCode})`"
              :value="course.courseId"
            />
          </el-select>
          <div style="font-size: 12px; color: #909399; margin-top: 4px">
            可选择多个课程，留空表示暂不分配课程
          </div>
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
      title="教师详情"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ viewData.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ viewData.realName }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ viewData.jobTitle }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ viewData.college }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ viewData.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ viewData.phone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'danger'">
            {{ viewData.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    <!-- 查看课程对话框 -->
    <el-dialog
      v-model="viewCoursesDialogVisible"
      title="已分配课程"
      width="700px"
    >
      <el-table :data="assignedCourses" style="width: 100%" border>
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="college" label="开课学院" width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '开放' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </el-card>
</template>

<script setup>
import {
    ArrowDown, Delete, Edit,
    Iphone,
    List,
    Medal,
    Message,
    Plus, Refresh,
    School,
    Search,
    User,
    View
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { nextTick, onMounted, reactive, ref } from 'vue'

import { addTeacher, deleteTeacher, getTeacherCourses, getTeacherList, updateTeacher, updateTeacherStatus, getCourseList } from '@/api'
import { COLLEGE_LIST } from '@/utils/constants'

const collegeList = COLLEGE_LIST

const searchKeyword = ref('')
const loading = ref(false)
const selectedItems = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formType = ref('add') // 'add' | 'edit'
const formRef = ref(null)
const submitLoading = ref(false)

// 查看详情相关
const viewDialogVisible = ref(false)
const viewData = ref({})

// 查看课程相关
const viewCoursesDialogVisible = ref(false)
const assignedCourses = ref([])

const formData = reactive({
  userId: '',
  username: '',
  password: '',
  realName: '',
  jobTitle: '',
  college: '',
  email: '',
  phone: '',
  courseIds: [] // 课程ID列表
})

// 课程列表
const courseList = ref([])
const coursesLoading = ref(false)

// 加载课程列表
const loadCourseList = async () => {
  coursesLoading.value = true
  try {
    const response = await getCourseList({ pageNum: 1, pageSize: 1000, status: 1 }) // 获取所有开放的课程
    if (response && response.code === 200 && response.data) {
      courseList.value = response.data.records || []
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  } finally {
    coursesLoading.value = false
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  jobTitle: [
    { required: true, message: '请输入职称', trigger: 'blur' }
  ],
  college: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

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
  pagination.currentPage = 1
  loadTableData()
  ElMessage.success('刷新成功')
}

const handleAdd = () => {
  formType.value = 'add'
  dialogTitle.value = '新增教师'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.resetFields()
    Object.keys(formData).forEach(key => {
      if (key === 'courseIds') {
        formData[key] = []
      } else {
        formData[key] = ''
      }
    })
  })
  loadCourseList() // 加载课程列表
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
  }).then(async () => {
    try {
      // 并行执行删除操作
      // 优先使用 id，如果不存在则使用 userId
      const promises = selectedItems.value.map(item => deleteTeacher(item.id || item.userId))
      await Promise.all(promises)
      ElMessage.success('批量删除成功')
      loadTableData()
    } catch (error) {
      console.error('批量删除失败', error)
      // request.js 已经处理了错误提示，这里不需要重复提示，除非是 Promise.all 的其他错误
    }
  }).catch(() => {})
}

const handleView = (row) => {
  viewData.value = row
  viewDialogVisible.value = true
}

const handleViewCourses = async (row) => {
  viewCoursesDialogVisible.value = true
  assignedCourses.value = [] // 清空旧数据
  try {
    // 优先使用 id
    const id = row.id || row.userId
    const res = await getTeacherCourses(id)
    if (res.code === 200) {
      assignedCourses.value = res.data
    }
  } catch (error) {
    console.error('获取教师课程失败', error)
    ElMessage.error('获取课程列表失败')
  }
}

const handleEdit = (row) => {
  formType.value = 'edit'
  dialogTitle.value = '编辑教师'
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.resetFields()
    // 填充表单数据
    Object.keys(formData).forEach(key => {
      if (row[key] !== undefined) {
        formData[key] = row[key]
      }
    })
    // 确保userId被设置，优先使用 id
    formData.userId = row.id || row.userId
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (formType.value === 'add') {
          // 构建请求数据，包含courseIds
          const payload = { ...formData }
          // 如果没有选择课程，不传courseIds字段
          if (!payload.courseIds || payload.courseIds.length === 0) {
            delete payload.courseIds
          }
          await addTeacher(payload)
          ElMessage.success('新增成功')
        } else {
          // 更新时使用 id 或 userId
          await updateTeacher(formData.userId, formData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        loadTableData()
      } catch (error) {
        console.error('提交失败', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDeleteSingle = (row) => {
  ElMessageBox.confirm(`确定要删除教师"${row.realName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 优先使用 id，如果不存在则使用 userId
      const id = row.id || row.userId
      console.log('Deleting teacher with ID:', id)
      await deleteTeacher(id)
      ElMessage.success('删除成功')
      loadTableData()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

const handleStatusChange = async (row, val) => {
  try {
    // 优先使用 id
    const id = row.id || row.userId
    await updateTeacherStatus(id, { status: val })
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = val === 1 ? 0 : 1 // 恢复原状态
    console.error('状态更新失败', error)
  }
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

const tableRowClassName = ({ row }) => {
  if (row.status === 0) {
    return 'disabled-row'
  }
  return ''
}

const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      realName: searchKeyword.value || undefined
    }
    const res = await getTeacherList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = Number(res.data.total)
    }
  } catch (error) {
    console.error('获取教师列表失败', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
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

:deep(.teacher-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.teacher-dialog .el-dialog__header) {
  margin-right: 0;
  padding: 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

:deep(.teacher-dialog .el-dialog__footer) {
  padding: 20px;
  border-top: 1px solid var(--el-border-color-lighter);
}

:deep(.el-table .disabled-row) {
  background: var(--el-color-info-light-9);
  color: var(--el-text-color-secondary);
}
</style>

