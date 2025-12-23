<template>
  <div class="teacher-courses">
    <div class="operation-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索课程名称"
        prefix-icon="Search"
        style="width: 300px"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <div 
      class="course-list-container"
      v-infinite-scroll="loadMore"
      :infinite-scroll-disabled="disabled"
      :infinite-scroll-distance="10"
    >
      <el-row :gutter="20" class="course-list">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in courses" :key="course.courseId">
          <el-card class="course-card" shadow="hover" :body-style="{ padding: '0px' }">
            <div class="course-cover">
              <CourseCover 
                :src="course.coverImage" 
                :title="course.courseName" 
                :id="course.courseId" 
              />
            </div>
            <div class="course-info">
              <div class="course-meta">
                <el-tag size="small" type="success">
                  {{ course.college }}
                </el-tag>
                <span class="student-count">
                  <el-icon><User /></el-icon> {{ course.studentCount || 0 }} 人
                </span>
              </div>
              <p class="course-desc" :title="course.description">{{ course.description || '暂无简介' }}</p>
              <div class="course-actions">
                <el-button type="primary" plain  style="width: 100%" @click="handleManage(course)">进入课程</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <div v-if="loading" class="loading-text">加载中...</div>
      <div v-if="noMore && courses.length > 0" class="no-more-text">没有更多了</div>
    </div>

    <el-empty v-if="!loading && courses.length === 0" description="暂无课程数据" />
  </div>
</template>

<script setup>
import { getMyCourses } from '@/api'
import CourseCover from '@/components/CourseCover.vue'
import { User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchKeyword = ref('')
const loading = ref(false)
const courses = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const noMore = computed(() => courses.value.length >= total.value)
const disabled = computed(() => loading.value || noMore.value)

const loadMore = () => {
  currentPage.value++
  fetchCourses(true)
}

const handleSearch = () => {
  currentPage.value = 1
  fetchCourses(false)
}

const fetchCourses = async (append = false) => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      courseName: searchKeyword.value
    }
    const res = await getMyCourses(params)
    if (res.code === 200) {
      if (append) {
        courses.value.push(...res.data.records)
      } else {
        courses.value = res.data.records
      }
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleManage = (course) => {
  router.push({ name: 'TeacherCourseDetail', params: { id: course.courseId } })
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.teacher-courses {
  padding: 10px;
}

.operation-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.course-list {
  margin-bottom: 20px;
  min-height: 400px;
}

.course-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
  border-radius: 12px;
  overflow: hidden;
  border: none;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.course-cover {
  height: 140px;
  overflow: hidden;
}

.course-info {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.student-count {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 15px;
}

.course-actions {
  display: flex;
  gap: 10px;
  margin-top: auto;
}

.course-actions .el-button {
  flex: 1;
}

.loading-text,
.no-more-text {
  text-align: center;
  padding: 20px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}
</style>
