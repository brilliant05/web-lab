<template>
  <div class="top-bar">
    <!-- 左侧 Logo + 系统名 -->
    <div class="top-bar-left">
      <img src="@/assets/logo.png" alt="上海理工大学" class="logo" />
      <span class="system-name">大学生线上学习资源共享与问答网站</span>
    </div>

    <!-- 中间搜索框 -->
    <div class="top-bar-center">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索课程、资源、问答..."
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 右侧 通知 + 用户 -->
    <div class="top-bar-right">
      <NotifyDropdown ref="notifyDropdownRef" />
      <UserDropdown />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import NotifyDropdown from './NotifyDropdown.vue'
import UserDropdown from './UserDropdown.vue'

const router = useRouter()
const searchKeyword = ref('')
const notifyDropdownRef = ref(null)

// 搜索处理
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    // TODO: 实现全局搜索功能
    // 暂时跳转到资源列表页面
    router.push({
      path: '/student/resources',
      query: { keyword: searchKeyword.value }
    })
  }
}
</script>

<style scoped>
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.top-bar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.logo {
  height: 40px;
  object-fit: contain;
}

.system-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
}

.top-bar-center {
  flex: 1;
  max-width: 500px;
  margin: 0 32px;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.top-bar-right {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
}

@media screen and (max-width: 1200px) {
  .system-name {
    display: none;
  }
}

@media screen and (max-width: 768px) {
  .top-bar {
    padding: 0 16px;
  }

  .top-bar-center {
    margin: 0 16px;
    max-width: 300px;
  }
}
</style>
