<template>
  <div class="student-home">
    <!-- 欢迎横幅 -->
    <WelcomeBanner />

    <!-- 快捷入口 -->
    <QuickActions />

    <!-- 我的课程 -->
    <MyCourses />
  </div>
</template>

<script setup>
import WelcomeBanner from '@/components/student/WelcomeBanner.vue'
import QuickActions from '@/components/student/QuickActions.vue'
import MyCourses from '@/components/student/MyCourses.vue'
</script>

<style scoped>
.student-home {
  max-width: 1400px;
  margin: 0 auto;
}
</style>