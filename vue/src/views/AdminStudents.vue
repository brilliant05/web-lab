<template>
  <div class="admin-page">
    <div class="dashboard-card">
      <header class="dashboard-top">
        <div class="logo-chip">学生管理</div>
        <div class="title-chip">学生列表</div>
        <div class="status-chip">共 {{ items.length }} 人</div>
      </header>

      <section class="workspace">
        <div class="workspace-header">
          <div>
            <p class="workspace-label">学生管理</p>
            <h2 class="workspace-title">学生列表</h2>
          </div>
          <div class="actions">
            <input
              v-model="keyword"
              class="search"
              placeholder="按姓名/学号搜索"
              @keyup.enter="load"
            />
            <button class="primary-btn" @click="load" :disabled="loading">
              {{ loading ? '加载中...' : '刷新' }}
            </button>
            <button class="primary-btn" @click="openCreate" :disabled="loading">
              新建学生
            </button>
          </div>
        </div>

        <div class="workspace-content">
          <div v-if="error" class="error">{{ error }}</div>
          <div class="table">
            <div class="table-head">
              <span>姓名</span>
              <span>学号</span>
              <span>用户名</span>
              <span>学院</span>
            </div>
            <div v-if="!items.length && !loading" class="empty">暂无数据</div>
            <div v-for="s in items" :key="s.userId || s.username" class="table-row">
              <span>{{ s.realName || '-' }}</span>
              <span>{{ s.studentId || '-' }}</span>
              <span>{{ s.username || '-' }}</span>
              <span>{{ s.college || '-' }}</span>
            </div>
          </div>
        </div>
        <div v-if="showCreate" class="modal-mask">
          <div class="modal">
            <h3>新建学生</h3>
            <div class="modal-body">
              <input v-model="form.username" class="input" placeholder="用户名" />
              <input v-model="form.realName" class="input" placeholder="姓名（可选）" />
              <input v-model="form.studentId" class="input" placeholder="学号（可选）" />
              <input v-model="form.college" class="input" placeholder="学院（可选）" />
              <input
                v-model="form.password"
                class="input"
                type="password"
                placeholder="密码（至少6位）"
              />
            </div>
            <div class="modal-footer">
              <button class="secondary-btn" @click="showCreate = false">取消</button>
              <button class="primary-btn" @click="createStudent">确定</button>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'

const items = ref([])
const loading = ref(false)
const error = ref('')
const keyword = ref('')
const showCreate = ref(false)

const form = reactive({
  username: '',
  realName: '',
  studentId: '',
  college: '',
  password: ''
})

const load = async () => {
  loading.value = true
  // 模拟数据加载
  setTimeout(() => {
    items.value = [
      { userId: 1, username: 'student1', realName: '张三', studentId: '2023001', college: '计算机学院' },
      { userId: 2, username: 'student2', realName: '李四', studentId: '2023002', college: '软件学院' }
    ]
    loading.value = false
  }, 500)
}

const openCreate = () => {
  showCreate.value = true
  Object.assign(form, {
    username: '',
    realName: '',
    studentId: '',
    college: '',
    password: ''
  })
}

const createStudent = () => {
  // 模拟创建
  showCreate.value = false
  load()
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.admin-page { padding: 20px; height: 100%; box-sizing: border-box; }
.dashboard-card { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1); height: 100%; display: flex; flex-direction: column; }
.dashboard-top { padding: 20px; border-bottom: 1px solid #ebeef5; display: flex; gap: 20px; align-items: center; }
.workspace { padding: 20px; flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.workspace-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.workspace-content { flex: 1; overflow: auto; }
.table { width: 100%; }
.table-head { display: grid; grid-template-columns: repeat(4, 1fr); padding: 10px; background: #f5f7fa; font-weight: bold; }
.table-row { display: grid; grid-template-columns: repeat(4, 1fr); padding: 10px; border-bottom: 1px solid #ebeef5; }
.modal-mask { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal { background: #fff; padding: 20px; border-radius: 8px; width: 400px; }
.modal-body { display: flex; flex-direction: column; gap: 10px; margin: 20px 0; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; }
.input { padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; }
.actions { display: flex; gap: 10px; }
.primary-btn { background: #409eff; color: #fff; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
.secondary-btn { background: #fff; border: 1px solid #dcdfe6; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
.logo-chip, .title-chip, .status-chip { padding: 4px 8px; background: #f0f2f5; border-radius: 4px; font-size: 12px; }
</style>
              