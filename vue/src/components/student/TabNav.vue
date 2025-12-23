<template>
  <div class="tab-nav">
    <div
      v-for="item in navItems"
      :key="item.path"
      class="tab-item"
      :class="{ active: isActive(item.path) }"
      @click="handleNavClick(item.path)"
    >
      <span class="tab-text">{{ item.label }}</span>
      <div v-if="isActive(item.path)" class="tab-indicator"></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 导航项配置
const navItems = [
  { path: '/student/home', label: '首页' },
  { path: '/student/courses', label: '课程' },
  { path: '/student/resources', label: '资源' },
  { path: '/student/questions', label: '问答' },
  { path: '/student/profile', label: '个人中心' }
]

// 判断当前路由是否激活
const isActive = (path) => {
  if (path === '/student/home') {
    return route.path === '/student/home'
  }
  return route.path.startsWith(path)
}

// 处理导航点击
const handleNavClick = (path) => {
  router.push(path)
}
</script>

<style scoped>
.tab-nav {
  display: flex;
  align-items: center;
  height: 56px;
  padding: 0 24px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  gap: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.tab-item {
  position: relative;
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  cursor: pointer;
  transition: color 0.2s;
}

.tab-text {
  font-size: 15px;
  color: #606266;
  font-weight: 500;
  transition: color 0.2s;
}


.tab-item.active .tab-text {
  color: #1e3a5f;
  font-weight: 600;
}

.tab-item:hover .tab-text {
  color: #1e3a5f;
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 32px;
  height: 3px;
  background-color: #1e3a5f;
  border-radius: 2px 2px 0 0;
}

@media screen and (max-width: 768px) {
  .tab-nav {
    padding: 0 12px;
    overflow-x: auto;
    gap: 4px;
  }

  .tab-item {
    padding: 0 12px;
    flex-shrink: 0;
  }

  .tab-text {
    font-size: 14px;
  }
}
</style>
