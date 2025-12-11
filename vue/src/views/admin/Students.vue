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
      <el-table-column prop="email" label="邮箱" min-width="200">
        <template #default="{ row }">
          <span v-if="row.email">{{ row.email }}</span>
          <span v-else style="color: #909399">暂无</span>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130">
        <template #default="{ row }">
          <span v-if="row.phone">{{ row.phone }}</span>
          <span v-else style="color: #909399">暂无</span>
        </template>
      </el-table-column>
      <el-table-column prop="college" label="院系" width="150" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
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
                <el-dropdown-item 
                  :icon="row.status === 1 ? Lock : User" 
                  @click="handleStatusChange(row, row.status === 1 ? 0 : 1)"
                >
                  {{ row.status === 1 ? '禁用账户' : '启用账户' }}
                </el-dropdown-item>
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

    <!-- 编辑学生弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="formData.username" 
            placeholder="请输入用户名" 
            :prefix-icon="User"
            disabled
          />
        </el-form-item>
        
        <el-form-item label="姓名" prop="realName">
          <el-input 
            v-model="formData.realName" 
            placeholder="请输入真实姓名" 
            :prefix-icon="Postcard"
          />
        </el-form-item>

        <el-form-item 
          label="密码" 
          prop="password"
        >
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="请输入密码（编辑时留空表示不修改）" 
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item label="学号" prop="studentId">
          <el-input 
            v-model="formData.studentId" 
            placeholder="请输入学号" 
            :prefix-icon="Iphone"
            disabled
          />
        </el-form-item>

        <el-form-item label="院系" prop="college">
          <el-select 
            v-model="formData.college" 
            placeholder="请选择院系" 
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

    <!-- 查看详情弹窗 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="学生详情"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ viewData.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ viewData.realName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ viewData.studentId }}</el-descriptions-item>
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
  </el-card>
</template>

<script setup>
import { deleteStudent, getStudentList, updateStudent, updateStudentStatus } from '@/api'
import { COLLEGE_LIST } from '@/utils/constants'
import {
  ArrowDown, Delete, Edit,
  Iphone,
  Lock,
  Message,
  Postcard,
  Refresh,
  School,
  Search,
  User,
  View
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

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

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const submitLoading = ref(false)

// 查看详情相关
const viewDialogVisible = ref(false)
const viewData = ref({})

const formData = reactive({
  userId: '',
  username: '',
  password: '',
  realName: '',
  studentId: '',
  college: '',
  email: '',
  phone: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: false, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  college: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
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
    // 并行执行删除操作
    const promises = selectedItems.value.map(item => deleteStudent(item.id || item.userId))
    Promise.all(promises).then(() => {
      ElMessage.success('批量删除成功')
      loadTableData()
    }).catch(error => {
      console.error('批量删除失败', error)
    })
  }).catch(() => {
    // 取消操作
  })
}

// 查看详情
const handleView = (row) => {
  viewData.value = row
  viewDialogVisible.value = true
}

// 分配课程
const handleAssignCourse = (row) => {
  ElMessage.info(`为学生 ${row.realName} 分配课程`)
  // TODO: 打开分配课程对话框
}

// 编辑学生
const handleEdit = (row) => {
  dialogTitle.value = '编辑学生'
  // 复制数据到表单
  Object.keys(formData).forEach(key => {
    if (key in row) {
      formData[key] = row[key]
    }
  })
  // 编辑时密码非必填，且不回显
  formData.password = ''
  // 确保 userId 存在
  formData.userId = row.userId || row.id
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      submitLoading.value = true
      const data = { ...formData }
      
      // 如果是编辑且密码为空，则移除密码字段（不修改密码）
      if (!data.password) {
        delete data.password
      }
      
      // 更新操作
      updateStudent(data.userId, data)
        .then(() => {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          loadTableData()
        })
        .catch(err => {
          console.error(err)
        })
        .finally(() => {
          submitLoading.value = false
        })
    }
  })
}

// 删除单个
const handleDeleteSingle = (row) => {
  ElMessageBox.confirm(`确定要删除学生"${row.realName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const id = row.id || row.userId
    deleteStudent(id).then(() => {
      ElMessage.success('删除成功')
      loadTableData()
    }).catch(error => {
      console.error('删除失败', error)
    })
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

const handleStatusChange = (row, val) => {
  const id = row.id || row.userId
  const actionText = val === 1 ? '启用' : '禁用'
  
  ElMessageBox.confirm(`确定要${actionText}该学生账户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateStudentStatus(id, { status: val }).then(() => {
      ElMessage.success(`${actionText}成功`)
      row.status = val
    }).catch(error => {
      console.error('状态更新失败', error)
    })
  }).catch(() => {
    // 取消操作
  })
}

const tableRowClassName = ({ row }) => {
  if (row.status === 0) {
    return 'disabled-row'
  }
  return ''
}

// 加载表格数据
const loadTableData = () => {
  loading.value = true
  const params = {
    pageNum: pagination.currentPage,
    pageSize: pagination.pageSize,
    keyword: searchKeyword.value || undefined
  }

  getStudentList(params).then(({ data }) => {
    const { records, total } = data
    tableData.value = records
    pagination.total = Number(total)
  }).catch(error => {
    console.error('获取学生列表失败', error)
    ElMessage.error('获取数据失败')
  }).finally(() => {
    loading.value = false
  })
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

:deep(.el-table .disabled-row) {
  background: var(--el-color-info-light-9);
  color: var(--el-text-color-secondary);
}
</style>

