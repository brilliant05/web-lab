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
              