<template>
  <el-container class="teacher-container" direction="vertical">
    <!-- 顶部导航栏 -->
    <Header />

    <el-container class="main-container">
      <!-- 左侧侧边栏 -->
      <TeacherSidebar />

      <!-- 主内容区 -->
      <el-main class="teacher-main">
        <!-- 面包屑导航 -->
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/teacher/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item 
              v-for="(item, index) in breadcrumbs" 
              :key="index"
              :to="index < breadcrumbs.length - 1 ? { path: item.path } : null"
            >
              {{ item.title }}
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
import Header from '@/components/layout/Header.vue';
import TeacherSidebar from '@/components/layout/TeacherSidebar.vue';
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

// 计算面包屑
const breadcrumbs = computed(() => {
  const items = route.matched
    .filter(item => item.meta && item.meta.title && item.path !== '/teacher/home')
    .map(item => ({
      title: item.meta.title,
      path: item.path
    }));

  // 特殊处理：课程详情页添加"我的课程"面包屑
  if (route.name === 'TeacherCourseDetail') {
    items.splice(items.length - 1, 0, {
      title: '我的课程',
      path: '/teacher/courses'
    });
  }

  return items;
});
</script>

<style scoped>
.teacher-container {
  height: 100vh;
  background-color: var(--el-bg-color-page);
}

.main-container {
  height: calc(100vh - 60px);
  overflow: hidden;
}

.teacher-main {
  padding: 20px;
  overflow-y: auto;
}

.breadcrumb-container {
  margin-bottom: 20px;
  background: var(--el-bg-color);
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: var(--el-box-shadow-light);
}

/* 路由切换动画 */
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
