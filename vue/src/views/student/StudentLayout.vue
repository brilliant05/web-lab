<template>
  <div class="student-layout">
    <!-- 顶部栏 -->
    <TopBar />

    <!-- 顶部导航Tab -->
    <TabNav />

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade-transform" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import TopBar from '@/components/student/TopBar.vue'
import TabNav from '@/components/student/TabNav.vue'
</script>

<style scoped>
.student-layout {
  min-height: 100vh;
  background-color: #f0f2f5;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media screen and (max-width: 768px) {
  .main-content {
    padding: 16px;
  }
}
</style>