import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import AdminLayout from '../views/AdminLayout.vue'
import Dashboard from '../views/admin/Dashboard.vue'

const routes = [
  {
    path: '/',
    name: 'login',
    component: Login
  },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '工作台' }
      },
      {
        path: 'students',
        name: 'Students',
        component: () => import('../views/admin/Students.vue'),
        meta: { title: '学生管理' }
      },
      {
        path: 'teachers',
        name: 'Teachers',
        component: () => import('../views/admin/Teachers.vue'),
        meta: { title: '教师管理' }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('../views/admin/Courses.vue'),
        meta: { title: '课程管理' }
      },
      {
        path: 'resources',
        name: 'Resources',
        component: () => import('../views/admin/Resources.vue'),
        meta: { title: '资源管理' }
      },
      {
        path: 'questions',
        name: 'Questions',
        component: () => import('../views/admin/Questions.vue'),
        meta: { title: '问答管理' }
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('../views/admin/Notifications.vue'),
        meta: { title: '通知管理' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('../views/admin/Statistics.vue'),
        meta: { title: '数据统计' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 如果访问管理后台页面，需要验证登录状态
  if (to.path.startsWith('/admin')) {
    if (!token) {
      next({ name: 'login' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router

