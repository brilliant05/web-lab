<template>
  <el-card 
    class="course-card" 
    :class="{ 'has-joined': isJoined }"
    shadow="hover" 
    @click="handleCardClick"
  >
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
        <el-button
          v-if="isJoined"
          type="primary"
          size="small"
          @click.stop="handleEnterResources"
        >
          进入资源
        </el-button>
        <el-button
          v-else
          type="primary"
          size="small"
          @click.stop="handleJoinCourse"
        >
          加入课程
        </el-button>
      </div>
    </div>

    <!-- 加入课程对话框 -->
    <el-dialog
      v-model="joinDialogVisible"
      title="加入课程"
      width="400px"
      @close="resetJoinForm"
    >
      <el-form :model="joinForm" label-width="100px">
        <el-form-item label="课程名称">
          <el-input :value="course.courseName" disabled />
        </el-form-item>
        <el-form-item label="邀请码" required>
          <el-input
            v-model="joinForm.inviteCode"
            placeholder="请输入课程邀请码"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="joinDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="joinLoading" @click="handleConfirmJoin">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { Reading, OfficeBuilding, UserFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { joinCourse } from '@/api'

const props = defineProps({
  course: {
    type: Object,
    required: true,
    default: () => ({})
  },
  isJoined: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['join-success'])

const router = useRouter()

// 加入课程对话框
const joinDialogVisible = ref(false)
const joinLoading = ref(false)
const joinForm = ref({
  inviteCode: ''
})

// 点击卡片（仅已加入课程时可点击进入资源）
const handleCardClick = () => {
  if (props.isJoined) {
    handleEnterResources()
  }
  // 未加入课程时点击卡片不做任何操作
}

// 打开加入课程对话框
const handleJoinCourse = () => {
  joinDialogVisible.value = true
}

// 重置加入表单
const resetJoinForm = () => {
  joinForm.value.inviteCode = ''
}

// 确认加入课程
const handleConfirmJoin = async () => {
  if (!joinForm.value.inviteCode || !joinForm.value.inviteCode.trim()) {
    ElMessage.warning('请输入邀请码')
    return
  }

  joinLoading.value = true
  try {
    const response = await joinCourse(joinForm.value.inviteCode.trim())
    if (response && response.code === 200) {
      ElMessage.success('加入课程成功！')
      joinDialogVisible.value = false
      resetJoinForm()
      // 触发父组件刷新
      emit('join-success')
    } else {
      ElMessage.error(response?.message || '加入课程失败')
    }
  } catch (error) {
    console.error('加入课程失败:', error)
    ElMessage.error(error?.response?.data?.message || '加入课程失败，请检查邀请码是否正确')
  } finally {
    joinLoading.value = false
  }
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
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.course-card.has-joined {
  cursor: pointer;
}

.course-card:not(.has-joined) {
  cursor: default;
}

.course-card.has-joined:hover {
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
