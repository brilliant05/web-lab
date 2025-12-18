<template>
  <el-card class="course-card" shadow="hover" @click="handleClick">
    <!-- 封面图片 -->
    <div class="course-cover">
      <img
        v-if="course.coverImage"
        :src="course.coverImage"
        :alt="course.courseName"
        class="cover-image"
      />
      <div v-else class="cover-placeholder">
        <el-icon :size="48"><Reading /></el-icon>
      </div>
    </div>

    <!-- 课程信息 -->
    <div class="course-info">
      <div class="course-name" :title="course.courseName">{{ course.courseName }}</div>
      <div class="course-code" v-if="course.courseCode">{{ course.courseCode }}</div>
      <div class="course-meta">
        <span class="course-college">
          <el-icon><OfficeBuilding /></el-icon>
          {{ course.college || '未设置' }}
        </span>
        <span class="course-teacher">
          <el-icon><UserFilled /></el-icon>
          {{ course.teacherName || '暂无教师' }}
        </span>
      </div>
      <div class="course-action">
        <el-button type="primary" size="small" @click.stop="handleEnterResources">
          进入资源
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { Reading, OfficeBuilding, UserFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  course: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const router = useRouter()

// 点击卡片
const handleClick = () => {
  handleEnterResources()
}

// 进入资源列表
const handleEnterResources = () => {
  router.push({
    path: '/student/resources',
    query: { courseId: props.course.courseId }
  })
}
</script>

<style scoped>
.course-card {
  cursor: pointer;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.course-cover {
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.9);
}

.course-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.course-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  min-height: 50px;
}

.course-code {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
}

.course-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
  flex: 1;
}

.course-college,
.course-teacher {
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 6px;
}

.course-college .el-icon,
.course-teacher .el-icon {
  font-size: 16px;
  color: #909399;
}

.course-action {
  margin-top: auto;
}

.course-action .el-button {
  width: 100%;
}
</style>
