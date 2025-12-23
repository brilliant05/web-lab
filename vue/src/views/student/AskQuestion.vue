<template>
  <div class="ask-question-page">
    <el-card class="ask-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h2 class="card-title">我要提问</h2>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="ask-form"
      >
        <el-form-item label="所属课程" prop="courseId">
          <el-select
            v-model="formData.courseId"
            placeholder="请选择课程"
            filterable
            style="width: 100%"
            :loading="coursesLoading"
            @change="handleCourseChange"
          >
            <el-option
              v-for="course in courses"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            >
              <span>{{ course.courseName }}</span>
              <span style="color: #909399; font-size: 12px; margin-left: 8px">
                {{ course.college }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="被提问教师" prop="teacherId">
          <el-select
            v-model="formData.teacherId"
            placeholder="请选择教师（暂时需手动输入教师ID）"
            filterable
            allow-create
            default-first-option
            style="width: 100%"
            :loading="teachersLoading"
          >
            <el-option
              v-for="teacher in teachers"
              :key="teacher.userId"
              :label="`${teacher.realName} (${teacher.jobTitle || ''})`"
              :value="teacher.userId"
            />
          </el-select>
          <div class="form-tip">
            提示：暂时需要手动输入教师ID，或从下拉列表选择（如果有数据）
          </div>
        </el-form-item>

        <el-form-item label="问题标题" prop="questionTitle">
          <el-input
            v-model="formData.questionTitle"
            placeholder="请输入问题标题（最多100个字符）"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="问题内容" prop="questionContent">
          <el-input
            v-model="formData.questionContent"
            type="textarea"
            :rows="6"
            placeholder="请详细描述您的问题（最多1000个字符）"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="上传图片" prop="images">
          <el-upload
            v-model:file-list="fileList"
            list-type="picture-card"
            :auto-upload="false"
            :limit="3"
            :on-exceed="handleExceed"
            :before-upload="beforeUpload"
            :on-remove="handleRemoveFile"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">支持 JPG、PNG 格式，最多上传 3 张，每张不超过 5MB</div>
        </el-form-item>

        <el-form-item label="问题标签" prop="tags">
          <el-input
            v-model="formData.tags"
            placeholder="请输入标签，多个标签用逗号分隔（可选）"
          />
          <div class="form-tip">例如：Java,多态,面向对象</div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="submitting"
            @click="handleSubmit"
          >
            {{ submitting ? '提交中...' : '提交问题' }}
          </el-button>
          <el-button size="large" @click="handleReset">重置</el-button>
          <el-button size="large" @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { submitQuestion, getCourseList } from '@/api'

const router = useRouter()

const formRef = ref(null)
const fileList = ref([])
const courses = ref([])
const teachers = ref([])
const coursesLoading = ref(false)
const teachersLoading = ref(false)
const submitting = ref(false)

// 表单数据
const formData = reactive({
  courseId: null,
  teacherId: null,
  questionTitle: '',
  questionContent: '',
  tags: ''
})

// 表单验证规则
const formRules = {
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'change' }
  ],
  teacherId: [
    { required: true, message: '请选择或输入被提问教师ID', trigger: 'change' }
  ],
  questionTitle: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  questionContent: [
    { required: true, message: '请输入问题内容', trigger: 'blur' },
    { min: 1, max: 1000, message: '内容长度在 1 到 1000 个字符', trigger: 'blur' }
  ]
}

// 加载课程列表
const loadCourses = async () => {
  coursesLoading.value = true
  try {
    const response = await getCourseList({ pageNum: 1, pageSize: 100, status: 1 })
    if (response && response.code === 200 && response.data) {
      courses.value = response.data.records || []
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  } finally {
    coursesLoading.value = false
  }
}

// 加载教师列表（暂时为空，因为后端可能没有学生可访问的接口）
const loadTeachers = async () => {
  // TODO: 如果后端有学生可访问的教师列表接口，在这里调用
  // 暂时留空，使用allow-create让用户手动输入
  teachers.value = []
}

// 课程改变时的处理（如果后续需要根据课程筛选教师，可以在这里实现）
const handleCourseChange = (courseId) => {
  // TODO: 如果后端有根据课程获取教师列表的接口，在这里调用
  console.log('课程已选择:', courseId)
}

// 文件超出限制
const handleExceed = () => {
  ElMessage.warning('最多只能上传 3 张图片')
}

// 上传前校验
const beforeUpload = (file) => {
  // 文件大小限制 5MB
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }

  // 文件类型检查
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }

  return true
}

// 删除文件
const handleRemoveFile = () => {
  // 文件列表会自动更新
}

// 提交问题
const handleSubmit = async () => {
  // 表单验证
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (!valid) return false
  })

  // 验证教师ID
  if (!formData.teacherId || (typeof formData.teacherId === 'string' && isNaN(Number(formData.teacherId)))) {
    ElMessage.warning('请输入有效的教师ID（数字）')
    return
  }

  // 将教师ID转换为数字
  const teacherId = typeof formData.teacherId === 'string' 
    ? Number(formData.teacherId) 
    : formData.teacherId

  if (!teacherId || isNaN(teacherId)) {
    ElMessage.warning('请输入有效的教师ID')
    return
  }

  submitting.value = true
  try {
    // 构建 FormData
    const formDataToSubmit = new FormData()
    formDataToSubmit.append('questionTitle', formData.questionTitle.trim())
    formDataToSubmit.append('questionContent', formData.questionContent.trim())
    formDataToSubmit.append('courseId', formData.courseId)
    formDataToSubmit.append('teacherId', teacherId)

    if (formData.tags && formData.tags.trim()) {
      formDataToSubmit.append('tags', formData.tags.trim())
    }

    // 添加图片文件
    fileList.value.forEach((file) => {
      const fileObj = file.raw || file
      if (fileObj) {
        formDataToSubmit.append('images', fileObj)
      }
    })

    const response = await submitQuestion(formDataToSubmit)
    if (response && response.code === 200) {
      ElMessage.success('问题提交成功')
      // 跳转到问题列表
      setTimeout(() => {
        router.push('/student/questions')
      }, 1500)
    } else {
      ElMessage.error(response?.message || '提交失败')
    }
  } catch (error) {
    console.error('提交问题失败:', error)
    ElMessage.error(error?.response?.data?.message || '提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  fileList.value = []
  formData.courseId = null
  formData.teacherId = null
  formData.questionTitle = ''
  formData.questionContent = ''
  formData.tags = ''
}

// 取消
const handleCancel = () => {
  router.back()
}

onMounted(() => {
  loadCourses()
  loadTeachers()
})
</script>

<style scoped>
.ask-question-page {
  max-width: 1000px;
  margin: 0 auto;
}

.ask-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.ask-form {
  margin-top: 20px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}

@media screen and (max-width: 768px) {
  .ask-card {
    padding: 16px;
  }
}
</style>