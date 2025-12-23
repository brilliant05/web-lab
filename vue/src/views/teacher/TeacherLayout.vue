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
import Header from '@/components/layout/Header.vue';
import TeacherSidebar from '@/components/layout/TeacherSidebar.vue';
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

// 计算面包屑
const breadcrumbs = computed(() => {
  return route.matched.filter(item => item.meta && item.meta.title && item.path !== '/teacher/home');
});
</script>

<style scoped>
.teacher-container {
  height: 100vh;
  background-color: #f0f2f5;
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
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
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
