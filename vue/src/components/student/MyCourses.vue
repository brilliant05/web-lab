<template>
  <div class="my-courses">
    <div class="courses-header">
      <h2 class="courses-title">我的课程</h2>
      <el-button link type="primary" @click="goToAllCourses">查看全部</el-button>
    </div>

    <div class="courses-grid" v-loading="loading">
      <el-card
        v-for="course in courses"
        :key="course.courseId"
        class="course-card"
        shadow="hover"
        @click="goToCourseDetail(course.courseId)"
      >
        <div class="course-cover">
          <div class="course-cover-placeholder">
            <el-icon :size="48"><Reading /></el-icon>
          </div>
        </div>
        <div class="course-info">
          <div class="course-name">{{ course.courseName }}</div>
          <div class="course-teacher">{{ course.teacherName || '暂无教师' }}</div>
        </div>
      </el-card>

      <el-empty
        v-if="!loading && courses.length === 0"
        description="暂无课程"
        :image-size="120"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Reading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCourseList } from '@/api'

const router = useRouter()

const loading = ref(false)
const courses = ref([])

// 加载我的课程（暂时显示前6门课程，后续可根据学生选课接口调整）
const loadCourses = async () => {
  loading.value = true
  try {
    const response = await getCourseList({ pageNum: 1, pageSize: 6 })
    if (response && response.code === 200 && response.data) {
      courses.value = response.data.records || []
    } else {
      ElMessage.error(response?.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
    // 失败时使用mock数据
    courses.value = [
      {
        courseId: 1,
        courseName: 'Java编程基础',
        teacherName: '张老师'
      },
      {
        courseId: 2,
        courseName: '数据结构',
        teacherName: '李老师'
      },
      {
        courseId: 3,
        courseName: '计算机网络',
        teacherName: '王老师'
      }
    ]
  } finally {
    loading.value = false
  }
}

// 跳转到所有课程
const goToAllCourses = () => {
  router.push('/student/courses')
}

// 跳转到课程详情
const goToCourseDetail = (courseId) => {
  // TODO: 实现课程详情页跳转
  // 暂时跳转到资源列表，筛选该课程
  router.push({
    path: '/student/resources',
    query: { courseId }
  })
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.my-courses {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.courses-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.courses-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.course-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.2s;
}

.course-card:hover {
  transform: translateY(-4px);
}

.course-cover {
  width: 100%;
  height: 160px;
  overflow: hidden;
}

.course-cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.course-info {
  padding: 16px;
}

.course-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-teacher {
  font-size: 14px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

  .my-courses {
    padding: 16px;
  }
}
</style>
