<template>
  <el-container class="student-container" direction="vertical">
    <!-- 顶部导航栏 -->
    <StudentHeader />

    <el-container class="main-container">
      <!-- 左侧侧边栏 -->
      <StudentSidebar />

      <!-- 主内容区 -->
      <el-main class="student-main">
        <!-- 面包屑导航 -->
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/student/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 路由视图 - 显示子路由组件 -->
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import StudentHeader from '@/components/layout/student/StudentHeader.vue'
import StudentSidebar from '@/components/layout/student/StudentSidebar.vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 计算面包屑
const breadcrumbs = computed(() => {
  return route.matched.filter(
    item => item.meta && item.meta.title && item.path !== '/student/home'
  )
})
</script>

<style scoped>
.student-container {
  height: 100vh;
  background-color: #f5f7fa;
}

/* 主容器 */
.main-container {
  flex: 1;
  overflow: hidden;
}

/* 主内容区 */
.student-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

/* 面包屑容器 */
.breadcrumb-container {
  margin-bottom: 20px;
  padding: 0 5px;
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
