<template>
  <div class="teacher-courses">
    <div class="operation-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索课程名称"
        prefix-icon="Search"
        style="width: 300px"
        clearable
      />
      <el-button type="primary" icon="Plus" @click="handleAddCourse">申请新课程</el-button>
    </div>

    <el-row :gutter="20" class="course-list">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in filteredCourses" :key="course.id">
        <el-card class="course-card" shadow="hover" :body-style="{ padding: '0px' }">
          <div class="course-cover" :style="{ backgroundColor: getRandomColor(course.id) }">
            <div class="course-title-overlay">
              <h3>{{ course.name }}</h3>
            </div>
          </div>
          <div class="course-info">
            <div class="course-meta">
              <el-tag size="small" :type="course.status === 'active' ? 'success' : 'info'">
                {{ course.status === 'active' ? '进行中' : '已结课' }}
              </el-tag>
              <span class="student-count">
                <el-icon><User /></el-icon> {{ course.studentCount }} 人
              </span>
            </div>
            <p class="course-desc">{{ course.description }}</p>
            <div class="course-actions">
              <el-button type="primary" plain size="small" @click="handleManage(course)">管理课程</el-button>
              <el-button type="info" plain size="small" @click="handleStudents(course)">学生名单</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="filteredCourses.length === 0" description="暂无课程数据" />
  </div>
</template>

<script setup>
import { User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, ref } from 'vue'

const searchKeyword = ref('')

const courses = ref([
  { id: 1, name: 'Java Web 开发基础', description: '本课程主要讲解Java Web开发的核心技术，包括Servlet、JSP、MVC模式等。', studentCount: 45, status: 'active' },
  { id: 2, name: 'Vue.js 前端框架实战', description: '深入浅出讲解Vue 3的核心概念与实战技巧，构建现代化前端应用。', studentCount: 62, status: 'active' },
  { id: 3, name: '数据库系统原理', description: '介绍关系型数据库的基本原理、SQL语言以及数据库设计范式。', studentCount: 38, status: 'finished' },
  { id: 4, name: '软件工程导论', description: '软件开发生命周期、敏捷开发方法论及项目管理基础。', studentCount: 50, status: 'active' },
  { id: 5, name: '计算机网络', description: '计算机网络体系结构、TCP/IP协议栈详解。', studentCount: 42, status: 'active' },
])

const filteredCourses = computed(() => {
  if (!searchKeyword.value) return courses.value
  return courses.value.filter(c => c.name.toLowerCase().includes(searchKeyword.value.toLowerCase()))
})

const getRandomColor = (id) => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#303133']
  return colors[id % colors.length]
}

const handleAddCourse = () => {
  ElMessage.info('申请新课程功能开发中...')
}

const handleManage = (course) => {
  ElMessage.success(`管理课程：${course.name}`)
}

const handleStudents = (course) => {
  ElMessage.info(`查看 ${course.name} 的学生名单`)
}
</script>

<style scoped>
.teacher-courses {
  padding: 10px;
}

.operation-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-list {
  margin-bottom: 20px;
}

.course-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
  border-radius: 12px;
  overflow: hidden;
  border: none;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.course-cover {
  height: 120px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.course-title-overlay {
  padding: 0 20px;
  text-align: center;
}

.course-title-overlay h3 {
  margin: 0;
  font-size: 18px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.course-info {
  padding: 15px;
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
}

.course-actions .el-button {
  flex: 1;
}
</style>
