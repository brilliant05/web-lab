<template>
  <div class="course-cover-wrapper" :style="{ backgroundColor: getRandomColor(id) }">
    <img v-if="src" :src="src" class="cover-image" alt="课程封面" />
    <div v-else class="course-title-overlay">
      <h3>{{ title }}</h3>
    </div>
  </div>
</template>

<script setup>
defineProps({
  src: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: ''
  },
  id: {
    type: [Number, String],
    default: 0
  }
})

const getRandomColor = (id) => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#303133']
  if (!id) return colors[0]
  return colors[id % colors.length]
}
</script>

<style scoped>
.course-cover-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-title-overlay {
  padding: 0 10px;
  text-align: center;
  width: 100%;
}

.course-title-overlay h3 {
  margin: 0;
  font-size: 16px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  word-break: break-word;
  color: #fff;
}
</style>
