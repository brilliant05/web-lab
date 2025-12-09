<template>
  <div class="admin-page">
    <div class="dashboard-card">
      <header class="dashboard-top">
        <div class="logo-chip">教师管理</div>
        <div class="title-chip">教师列表</div>
        <div class="status-chip">共 {{ items.length }} 人</div>
      </header>

      <section class="workspace">
        <div class="workspace-header">
          <div>
            <p class="workspace-label">教师管理</p>
            <h2 class="workspace-title">教师列表</h2>
          </div>
          <div class="actions">
            <input
              v-model="keyword"
              class="search"
              placeholder="按姓名/用户名搜索"
              @keyup.enter="load"
            />
            <button class="primary-btn" @click="load" :disabled="loading">
              {{ loading ? '加载中...' : '刷新' }}
            </button>
          </div>
        </div>

        <div class="workspace-content">
          <div v-if="error" class="error">{{ error }}</div>
          <div class="table">
            <div class="table-head">
              <span>姓名</span>
              <span>用户名</span>
              <span>职称</span>
              <span>邮箱</span>
            </div>
            <div v-if="!items.length && !loading" class="empty">暂无数据</div>
            <div v-for="t in items" :key="t.userId || t.username" class="table-row">
              <span>{{ t.realName || '-' }}</span>
              <span>{{ t.username || '-' }}</span>
              <span>{{ t.jobTitle || '-' }}</span>
              <span>{{ t.email || '-' }}</span>
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

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await adminApi.getTeachers({ keyword: keyword.value || undefined })
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

onMounted(load)
</script>

<style scoped>
@import './admin-shared.css';
</style>

