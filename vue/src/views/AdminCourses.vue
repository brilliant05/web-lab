<template>
  <div class="admin-page">
    <div class="dashboard-card">
      <header class="dashboard-top">
        <div class="logo-chip">课程管理</div>
        <div class="title-chip">课程列表</div>
        <div class="status-chip">共 {{ items.length }} 门</div>
      </header>

      <section class="workspace">
        <div class="workspace-header">
          <div>
            <p class="workspace-label">课程管理</p>
            <h2 class="workspace-title">课程列表</h2>
          </div>
          <div class="actions">
            <input
              v-model="keyword"
              class="search"
              placeholder="按名称搜索"
              @keyup.enter="load"
            />
            <button class="primary-btn" @click="load" :disabled="loading">
              {{ loading ? '加载中...' : '刷新' }}
            </button>
            <button class="primary-btn" @click="openCreate" :disabled="loading">
              新建课程
            </button>
          </div>
        </div>

        <div class="workspace-content">
          <div v-if="error" class="error">{{ error }}</div>
          <div class="table">
            <div class="table-head">
              <span>课程名称</span>
              <span>学院</span>
              <span>教师数</span>
              <span>学生数</span>
            </div>
            <div v-if="!items.length && !loading" class="empty">暂无数据</div>
            <div v-for="course in items" :key="course.courseId" class="table-row">
              <span>{{ course.courseName || '-' }}</span>
              <span>{{ course.college || '-' }}</span>
              <span>{{ course.teacherCount ?? '-' }}</span>
              <span>{{ course.studentCount ?? '-' }}</span>
            </div>
          </div>
        </div>
        <div v-if="showCreate" class="modal-mask">
          <div class="modal">
            <h3>新建课程</h3>
            <div class="modal-body">
              <input v-model="form.courseName" class="input" placeholder="课程名称" />
              <input v-model="form.courseCode" class="input" placeholder="课程编号(可选)" />
              <input v-model="form.college" class="input" placeholder="学院(可选)" />
              <textarea v-model="form.description" class="textarea" placeholder="课程描述(可选)"></textarea>
            </div>
            <div class="modal-actions">
              <button class="ghost-btn" @click="closeCreate">取消</button>
              <button class="primary-btn" :disabled="saving" @click="submitCreate">
                {{ saving ? '保存中...' : '保存' }}
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { adminApi } from '../api/admin'

const items = reactive([])
const loading = ref(false)
const error = ref('')
const keyword = ref('')
const showCreate = ref(false)
const saving = ref(false)
const form = reactive({
  courseName: '',
  courseCode: '',
  college: '',
  description: ''
})

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await adminApi.getCourses({
      keyword: keyword.value || undefined,
      pageNum: 1,
      pageSize: 50
    })
    if (res?.code === 200) {
      const records = res.data?.records || res.data || []
      items.splice(0, items.length, ...(records || []))
    } else {
      error.value = res?.message || '加载失败'
    }
  } catch (e) {
    error.value = e?.message || '请求失败'
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  showCreate.value = true
}

const closeCreate = () => {
  showCreate.value = false
  form.courseName = ''
  form.courseCode = ''
  form.college = ''
  form.description = ''
}

const submitCreate = async () => {
  if (!form.courseName.trim()) return (error.value = '请输入课程名称')
  if (!form.courseCode.trim()) return (error.value = '请输入课程编号')
  if (!form.college.trim()) return (error.value = '请输入开课学院')
  saving.value = true
  error.value = ''
  try {
    const res = await adminApi.createCourse({
      courseName: form.courseName.trim(),
      courseCode: form.courseCode.trim(),
      college: form.college.trim(),
      description: form.description || undefined
    })
    if (res?.code === 200) {
      closeCreate()
      await load()
    } else {
      error.value = res?.message || '创建失败'
      console.error('创建课程失败:', res)
    }
  } catch (e) {
    error.value = e?.message || '创建失败'
    console.error('创建课程异常:', e)
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
@import './admin-shared.css';

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.25);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal {
  width: min(520px, 90vw);
  background: #ecf0f3;
  padding: 20px;
  border-radius: 20px;
  box-shadow: 10px 10px 24px #d1d9e6, -10px -10px 24px #f9f9f9;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input,
.textarea {
  width: 100%;
  border: none;
  border-radius: 12px;
  padding: 10px 12px;
}
</style>