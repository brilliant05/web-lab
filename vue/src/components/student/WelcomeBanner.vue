<template>
  <div class="welcome-banner">
    <div class="banner-content">
      <div class="banner-title">欢迎回来，{{ userName }}！</div>
      <div class="banner-subtitle">开始今天的学习之旅吧</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const userName = ref('学生')

onMounted(() => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      const user = JSON.parse(userInfoStr)
      userName.value = user.realName || user.username || '学生'
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
})
</script>

<style scoped>
.welcome-banner {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d4f7a 100%);
  border-radius: 16px;
  padding: 32px 40px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(30, 58, 95, 0.25);
}

.banner-content {
  color: #fff;
}

.banner-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
}

.banner-subtitle {
  font-size: 16px;
  opacity: 0.9;
}

@media screen and (max-width: 768px) {
  .welcome-banner {
    padding: 24px;
  }

  .banner-title {
    font-size: 24px;
  }

  .banner-subtitle {
    font-size: 14px;
  }
}
</style>
