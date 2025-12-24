<template>
  <el-card class="content-card">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <el-button link @click="goBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <span class="title">{{ formType === 'add' ? '发布资源' : '编辑资源' }}</span>
        </div>
      </div>
    </template>

    <div class="form-container">
      <el-form :model="form" label-width="100px" ref="formRef" :rules="rules" style="max-width: 800px">
        <el-form-item label="所属课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%" :disabled="formType === 'edit'">
            <el-option 
              v-for="course in courses" 
              :key="course.courseId" 
              :label="course.courseName" 
              :value="course.courseId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资源标题" prop="resourceTitle">
          <el-input v-model="form.resourceTitle" placeholder="请输入资源标题" />
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入资源描述" />
        </el-form-item>
        <el-form-item label="可见性" prop="visibility">
          <el-radio-group v-model="form.visibility">
            <el-radio label="PUBLIC">公开 (所有学生可见)</el-radio>
            <el-radio label="COURSE_ONLY">私有 (仅本课程学生可见)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件" prop="file" v-if="formType === 'add'">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            :on-exceed="handleExceed"
            ref="uploadRef"
            style="width: 100%"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            {{ formType === 'add' ? '立即发布' : '保存修改' }}
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script setup>
import { getMyCourses, updateResource, uploadResource } from '@/api'
import { ArrowLeft, UploadFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const formType = ref('add')
const submitLoading = ref(false)
const formRef = ref(null)
const uploadRef = ref(null)
const courses = ref([])

const form = reactive({
  resourceId: null,
  courseId: null,
  resourceTitle: '',
  description: '',
  visibility: 'COURSE_ONLY',
  file: null
})

const rules = {
  courseId: [{ required: true, message: '请选择所属课程', trigger: 'change' }],
  resourceTitle: [{ required: true, message: '请输入资源标题', trigger: 'blur' }],
  visibility: [{ required: true, message: '请选择可见性', trigger: 'change' }],
  file: [{ required: true, message: '请上传文件', trigger: 'change' }]
}

const goBack = () => {
  router.back()
}

const fetchCourses = async () => {
  try {
    const res = await getMyCourses()
    if (res.code === 200) {
      courses.value = res.data.records || res.data
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

const handleFileChange = (file) => {
  form.file = file.raw
  // 自动填充标题
  if (!form.resourceTitle) {
    form.resourceTitle = file.name
  }
}

const handleExceed = (files) => {
  uploadRef.value.clearFiles()
  const file = files[0]
  uploadRef.value.handleStart(file)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  // 编辑模式下不需要校验文件
  if (formType.value === 'edit') {
    // 移除文件校验规则
    // 注意：这里不能直接修改 rules 对象，因为它是响应式的源
    // 但 Element Plus 的 validate 方法会使用当前的 rules
    // 我们可以临时忽略 file 字段的校验，或者在 edit 模式下不设置 file 的 rule
  }

  await formRef.value.validate(async (valid, fields) => {
    // 如果是编辑模式，忽略 file 字段的错误
    if (formType.value === 'edit' && fields && fields.file) {
      delete fields.file
      if (Object.keys(fields).length === 0) valid = true
    }

    if (valid) {
      submitLoading.value = true
      try {
        if (formType.value === 'add') {
          const formData = new FormData()
          formData.append('courseId', form.courseId)
          formData.append('resourceTitle', form.resourceTitle)
          formData.append('description', form.description || '')
          formData.append('visibility', form.visibility)
          formData.append('file', form.file)
          
          const res = await uploadResource(formData)
          if (res.code === 200) {
            ElMessage.success('发布成功')
            goBack()
          } else {
            ElMessage.error(res.message || '发布失败')
          }
        } else {
          const data = {
            resourceTitle: form.resourceTitle,
            description: form.description,
            visibility: form.visibility
          }
          const res = await updateResource(form.resourceId, data)
          if (res.code === 200) {
            ElMessage.success('更新成功')
            goBack()
          } else {
            ElMessage.error(res.message || '更新失败')
          }
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchCourses()
  
  // 检查路由参数，如果有 id 则是编辑模式
  const query = route.query
  if (query.id) {
    formType.value = 'edit'
    form.resourceId = query.id
    form.courseId = Number(query.courseId)
    form.resourceTitle = query.title
    form.description = query.description
    form.visibility = query.visibility
  }
})
</script>

<style scoped>
.content-card {
  min-height: calc(100vh - 120px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.form-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}
</style>