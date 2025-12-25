<template>
  <div class="course-list-page">
    <!-- 搜索栏区域 -->
    <el-card class="search-card" shadow="never">
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入课程名称"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.college"
          placeholder="选择学院"
          class="college-select"
          clearable
          filterable
        >
          <el-option label="全部学院" value="" />
          <el-option
            v-for="college in collegeList"
            :key="college"
            :label="college"
            :value="college"
          />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button type="success" :icon="Plus" @click="handleJoinByCode">邀请码加入</el-button>
      </div>
    </el-card>

    <!-- 邀请码加入对话框 -->
    <el-dialog
      v-model="joinDialogVisible"
      title="通过邀请码加入课程"
      width="400px"
    >
      <el-form :model="joinForm" label-width="80px">
        <el-form-item label="邀请码">
          <el-input v-model="joinForm.code" placeholder="请输入教师提供的邀请码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="joinDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitJoinCode" :loading="joining">加入</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 课程卡片网格 -->
    <div class="courses-container">
      <div v-loading="loading" class="courses-grid">
        <CourseCard
          v-for="course in courses"
          :key="course.courseId"
          :course="course"
          :is-joined="enrolledCourseIds.has(course.courseId)"
          @join-success="handleJoinSuccess"
        />
        <el-empty
          v-if="!loading && courses.length === 0"
          description="暂无课程"
          :image-size="120"
          class="empty-state"
        />
      </div>
    </div>

    <!-- 分页组件 -->
    <div v-if="courses.length > 0" class="pagination-container">
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
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCourseList, getStudentCourses, joinCourse } from '@/api'
import { COLLEGE_LIST } from '@/utils/constants'
import CourseCard from '@/components/student/CourseCard.vue'

const route = useRoute()

// 邀请码加入
const joinDialogVisible = ref(false)
const joining = ref(false)
const joinForm = reactive({
  code: ''
})

const handleJoinByCode = () => {
  joinForm.code = ''
  joinDialogVisible.value = true
}

const submitJoinCode = async () => {
  if (!joinForm.code || !joinForm.code.trim()) {
    ElMessage.warning('请输入邀请码')
    return
  }
  
  joining.value = true
  try {
    await joinCourse(joinForm.code.trim())
    ElMessage.success('加入课程成功')
    joinDialogVisible.value = false
    // 刷新列表
    loadStudentCourses()
    loadCourses()
  } catch (error) {
    console.error('加入课程失败:', error)
  } finally {
    joining.value = false
  }
}

// 搜索表单
const searchForm = reactive({
  keyword: '',
  college: ''
})

// 学院列表（使用项目常量，同时从接口数据中动态补充）
const collegeList = ref([...COLLEGE_LIST])

// 加载状态
const loading = ref(false)

// 课程列表
const courses = ref([])

// 学生已加入的课程列表
const enrolledCourses = ref([])

// 已加入课程ID集合（用于快速判断）
const enrolledCourseIds = computed(() => {
  return new Set(enrolledCourses.value.map(c => c.courseId))
})

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 12,
  total: 0
})

// 加载学生已加入的课程
const loadEnrolledCourses = async () => {
  try {
    const response = await getStudentCourses()
    if (response && response.code === 200) {
      enrolledCourses.value = response.data || []
    }
  } catch (error) {
    console.error('加载已加入课程失败:', error)
  }
}

// 加入课程成功回调
const handleJoinSuccess = () => {
  loadEnrolledCourses()
}

// 加载课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      status: 1 // 只显示开放的课程
    }

    // 添加搜索条件
    if (searchForm.keyword) {
      params.courseName = searchForm.keyword
    }
    if (searchForm.college) {
      params.college = searchForm.college
    }

    const response = await getCourseList(params)
    if (response && response.code === 200 && response.data) {
      courses.value = response.data.records || []
      pagination.total = response.data.total || 0

      // 提取学院列表（从返回的数据中获取唯一学院）
      const colleges = new Set()
      courses.value.forEach(course => {
        if (course.college) {
          colleges.add(course.college)
        }
      })
      // 如果现有列表中没有的学院，添加到列表中
      colleges.forEach(college => {
        if (!collegeList.value.includes(college)) {
          collegeList.value.push(college)
        }
      })
    } else {
      ElMessage.error(response?.message || '获取课程列表失败')
      courses.value = []
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
    // 失败时使用mock数据
    courses.value = [
      {
        courseId: 1,
        courseName: 'Java编程基础',
        courseCode: 'CS001',
        college: '计算机学院',
        teacherName: '张老师',
        coverImage: null,
        status: 1
      },
      {
        courseId: 2,
        courseName: '数据结构',
        courseCode: 'CS002',
        college: '计算机学院',
        teacherName: '李老师',
        coverImage: null,
        status: 1
      },
      {
        courseId: 3,
        courseName: '计算机网络',
        courseCode: 'CS003',
        college: '计算机学院',
        teacherName: '王老师',
        coverImage: null,
        status: 1
      }
    ]
    pagination.total = courses.value.length
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadCourses()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.college = ''
  pagination.currentPage = 1
  loadCourses()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  loadCourses()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadCourses()
}

// 监听学院筛选变化
watch(
  () => searchForm.college,
  () => {
    handleSearch()
  }
)

// 初始化
onMounted(() => {
  loadCourses()
  loadEnrolledCourses()
})
</script>

<style scoped>
.course-list-page {
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

.college-select {
  width: 180px;
}

.courses-container {
  margin-bottom: 24px;
}

.courses-grid {
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
  .courses-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 1199px) and (min-width: 768px) {
  .courses-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 767px) {
  .courses-grid {
    grid-template-columns: 1fr;
  }

  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    max-width: 100%;
  }

  .college-select {
    width: 100%;
  }

  .search-bar .el-button {
    width: 100%;
  }
}
</style>