<template>
  <div class="question-list-page">
    <!-- 搜索栏区域 -->
    <el-card class="search-card" shadow="never">
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入问题标题或内容"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.courseId"
          placeholder="选择课程"
          class="course-select"
          clearable
          filterable
        >
          <el-option label="全部课程" value="" />
          <el-option
            v-for="course in courses"
            :key="course.courseId"
            :label="course.courseName"
            :value="course.courseId"
          />
        </el-select>
        <el-select
          v-model="searchForm.isAnswered"
          placeholder="回答状态"
          class="status-select"
          clearable
        >
          <el-option label="全部" value="" />
          <el-option label="已回答" :value="1" />
          <el-option label="待回答" :value="0" />
        </el-select>
        <el-select
          v-model="searchForm.orderBy"
          placeholder="排序方式"
          class="sort-select"
        >
          <el-option label="最新发布" value="createTime" />
          <el-option label="最多回答" value="answerCount" />
          <el-option label="最多浏览" value="viewCount" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button type="success" :icon="Edit" @click="goToAsk">我要提问</el-button>
      </div>
    </el-card>

    <!-- 问题列表 -->
    <div class="questions-container">
      <div v-loading="loading" class="questions-grid">
        <QuestionCard
          v-for="question in questions"
          :key="question.questionId"
          :question="question"
        />
        <el-empty
          v-if="!loading && questions.length === 0"
          description="暂无问题"
          :image-size="120"
          class="empty-state"
        />
      </div>
    </div>

    <!-- 分页组件 -->
    <div v-if="questions.length > 0" class="pagination-container">
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
import { useRouter } from 'vue-router'
import { Search, Refresh, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getQuestionList, getCourseList } from '@/api'
import QuestionCard from '@/components/student/QuestionCard.vue'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  courseId: '',
  isAnswered: '',
  orderBy: 'createTime'
})

// 课程列表（用于筛选）
const courses = ref([])

// 加载状态
const loading = ref(false)

// 问题列表
const questions = ref([])

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 15,
  total: 0
})

// 加载课程列表
const loadCourses = async () => {
  try {
    const response = await getCourseList({ pageNum: 1, pageSize: 100, status: 1 })
    if (response && response.code === 200 && response.data) {
      courses.value = response.data.records || []
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }
}

// 加载问题列表
const loadQuestions = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize
    }

    // 添加搜索条件
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    if (searchForm.courseId) {
      params.courseId = searchForm.courseId
    }
    if (searchForm.isAnswered !== '') {
      params.isAnswered = searchForm.isAnswered
    }
    // 排序（后端可能不支持orderBy，先保留）
    // if (searchForm.orderBy) {
    //   params.orderBy = searchForm.orderBy
    // }

    const response = await getQuestionList(params)
    if (response && response.code === 200 && response.data) {
      questions.value = response.data.records || []
      pagination.total = response.data.total || 0
      
      // 如果有排序需求，前端排序（临时方案）
      if (searchForm.orderBy === 'answerCount') {
        questions.value.sort((a, b) => (b.answerCount || 0) - (a.answerCount || 0))
      } else if (searchForm.orderBy === 'viewCount') {
        questions.value.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
      }
    } else {
      ElMessage.error(response?.message || '获取问题列表失败')
      questions.value = []
    }
  } catch (error) {
    console.error('加载问题列表失败:', error)
    ElMessage.error('加载问题列表失败')
    // 失败时使用mock数据
    questions.value = [
      {
        questionId: 1,
        questionTitle: 'Java中如何理解多态？',
        questionContent: '在学习Java面向对象编程时，对多态的概念不太理解，希望能有老师详细解释一下。',
        courseName: 'Java编程',
        studentName: '张同学',
        teacherName: '李老师',
        viewCount: 125,
        answerCount: 3,
        isAnswered: 1,
        tags: 'Java,多态,面向对象',
        createTime: new Date().toISOString()
      },
      {
        questionId: 2,
        questionTitle: '数据结构中二叉树的遍历方法',
        questionContent: '请问二叉树的前序、中序、后序遍历有什么区别？',
        courseName: '数据结构',
        studentName: '王同学',
        teacherName: '赵老师',
        viewCount: 89,
        answerCount: 0,
        isAnswered: 0,
        tags: '数据结构,二叉树',
        createTime: new Date(Date.now() - 86400000).toISOString()
      }
    ]
    pagination.total = questions.value.length
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadQuestions()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.courseId = ''
  searchForm.isAnswered = ''
  searchForm.orderBy = 'createTime'
  pagination.currentPage = 1
  loadQuestions()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  loadQuestions()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadQuestions()
}

// 跳转到提问页面
const goToAsk = () => {
  router.push('/student/ask')
}

// 监听排序方式变化
watch(
  () => searchForm.orderBy,
  () => {
    handleSearch()
  }
)

// 监听回答状态变化
watch(
  () => searchForm.isAnswered,
  () => {
    handleSearch()
  }
)

// 初始化
onMounted(() => {
  loadCourses()
  loadQuestions()
})
</script>

<style scoped>
.question-list-page {
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

.course-select,
.status-select,
.sort-select {
  width: 150px;
}

.questions-container {
  margin-bottom: 24px;
}

.questions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
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
  .questions-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 1199px) and (min-width: 768px) {
  .questions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 767px) {
  .questions-grid {
    grid-template-columns: 1fr;
  }

  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    max-width: 100%;
  }

  .course-select,
  .status-select,
  .sort-select {
    width: 100%;
  }

  .search-bar .el-button {
    width: 100%;
  }
}
</style>