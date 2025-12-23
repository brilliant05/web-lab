<template>
  <div class="teacher-home">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>我的课程</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">3</span>
            <span class="label">门</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>待回答问题</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number warning">12</span>
            <span class="label">个</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>已发布资源</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number success">25</span>
            <span class="label">份</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>学生总数</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number primary">156</span>
            <span class="label">人</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>近期活跃度</span>
            </div>
          </template>
          <div style="height: 300px; display: flex; justify-content: center; align-items: center; color: #909399;">
            图表区域（可集成 ECharts）
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="notice-card">
          <template #header>
            <div class="card-header">
              <span>最新通知</span>
              <el-button link type="primary">更多</el-button>
            </div>
          </template>
          <div class="notice-list">
            <div class="notice-item" v-for="i in 5" :key="i">
              <span class="notice-title">系统维护通知 {{ i }}</span>
              <span class="notice-time">2023-12-{{ 20 + i }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
</script>

<style scoped>
.stat-card .card-content {
  display: flex;
  align-items: baseline;
  justify-content: center;
  padding: 10px 0;
}

.stat-card .number {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  margin-right: 5px;
}

.stat-card .number.warning { color: #e6a23c; }
.stat-card .number.success { color: #67c23a; }
.stat-card .number.primary { color: #409eff; }

.stat-card .label {
  font-size: 14px;
  color: #909399;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #606266;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-time {
  color: #909399;
  font-size: 12px;
}
</style>
