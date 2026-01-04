import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '../views/AdminLayout.vue'
import Login from '../views/Login.vue'
import Dashboard from '../views/admin/Dashboard.vue'
import StudentLayout from '../views/student/StudentLayout.vue'

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
    meta: { requiresAuth: true, role: 'ADMIN' },
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
        path: 'questions/:id',
        name: 'AdminQuestionDetail',
        component: () => import('../views/admin/QuestionDetail.vue'),
        meta: { title: '问题详情' }
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
  },
  {
    path: '/student',
    component: StudentLayout,
    redirect: '/student/home',
    meta: { requiresAuth: true, role: 'STUDENT' },
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('../views/student/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'courses',
        name: 'StudentCourses',
        component: () => import('../views/student/CourseList.vue'),
        meta: { title: '课程列表' }
      },
      {
        path: 'resources',
        name: 'StudentResources',
        component: () => import('../views/student/ResourceList.vue'),
        meta: { title: '资源浏览' }
      },
      {
        path: 'resources/:id',
        name: 'StudentResourceDetail',
        component: () => import('../views/student/ResourceDetail.vue'),
        meta: { title: '资源详情' }
      },
      {
        path: 'upload',
        name: 'StudentUpload',
        component: () => import('../views/student/UploadResource.vue'),
        meta: { title: '资源上传' }
      },
      {
        path: 'questions',
        name: 'StudentQuestions',
        component: () => import('../views/student/QuestionList.vue'),
        meta: { title: '问答广场' }
      },
      {
        path: 'questions/:id',
        name: 'StudentQuestionDetail',
        component: () => import('../views/student/QuestionDetail.vue'),
        meta: { title: '问题详情' }
      },
      {
        path: 'ask',
        name: 'StudentAsk',
        component: () => import('../views/student/AskQuestion.vue'),
        meta: { title: '提问' }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('../views/student/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/teacher',
    component: () => import('../views/teacher/TeacherLayout.vue'),
    redirect: '/teacher/home',
    meta: { requiresAuth: true, role: 'TEACHER' },
    children: [
      {
        path: 'home',
        name: 'TeacherHome',
        component: () => import('../views/teacher/Home.vue'),
        meta: { title: '教师首页' }
      },
      {
        path: 'courses',
        name: 'TeacherCourses',
        component: () => import('../views/teacher/Courses.vue'),
        meta: { title: '我的课程' }
      },
      {
        path: 'courses/:id',
        name: 'TeacherCourseDetail',
        component: () => import('../views/teacher/CourseDetail.vue'),
        meta: { title: '课程详情' }
      },
      {
        path: 'resources',
        name: 'TeacherResources',
        component: () => import('../views/teacher/Resources.vue'),
        meta: { title: '资源管理' }
      },
      {
        path: 'resources/recycle-bin',
        name: 'TeacherResourceRecycleBin',
        component: () => import('../views/teacher/ResourceRecycleBin.vue'),
        meta: { title: '资源回收站' }
      },
      {
        path: 'resources/publish',
        name: 'TeacherResourcePublish',
        component: () => import('../views/teacher/ResourcePublish.vue'),
        meta: { title: '发布资源' }
      },
      {
        path: 'resources/:id',
        name: 'TeacherResourceDetail',
        component: () => import('../views/teacher/ResourceDetail.vue'),
        meta: { title: '资源详情' }
      },
      {
        path: 'questions',
        name: 'TeacherQuestions',
        component: () => import('../views/teacher/Questions.vue'),
        meta: { title: '答疑解惑' }
      },
      {
        path: 'questions/:id',
        name: 'TeacherQuestionDetail',
        component: () => import('../views/teacher/QuestionDetail.vue'),
        meta: { title: '问题详情' }
      },
      {
        path: 'notifications',
        name: 'TeacherNotifications',
        component: () => import('../views/teacher/Notifications.vue'),
        meta: { title: '消息通知' }
      },
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: () => import('../views/teacher/Profile.vue'),
        meta: { title: '个人中心' }
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
  let userRole = ''

  // 获取用户角色
  if (token) {
    try {
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        const userInfo = JSON.parse(userInfoStr)
        userRole = (userInfo.role || '').toUpperCase()
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }

  // 如果需要登录
  if (to.meta.requiresAuth) {
    if (!token) {
      // 未登录，跳转到登录页
      next({ name: 'login' })
      return
    }

    // 检查角色权限
    if (to.meta.role && to.meta.role !== userRole) {
      // 角色不符，跳转到对应角色的首页
      switch (userRole) {
        case 'STUDENT':
          next('/student/home')
          break
        case 'TEACHER':
          next('/teacher/home')
          break
        case 'ADMIN':
          next('/admin/dashboard')
          break
        default:
          next({ name: 'login' })
      }
      return
    }
  }

  // 已登录用户访问登录页，重定向到对应角色首页
  if (to.path === '/' && token && userRole) {
    switch (userRole) {
      case 'STUDENT':
        next('/student/home')
        break
      case 'TEACHER':
        next('/teacher/home')
        break
      case 'ADMIN':
        next('/admin/dashboard')
        break
      default:
        next()
    }
    return
  }

  next()
})

export default router

