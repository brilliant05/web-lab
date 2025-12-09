import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Admin from '../views/Admin.vue'
import StudentHome from '../views/StudentHome.vue'
import TeacherHome from '../views/TeacherHome.vue'
import AdminCourses from '../views/AdminCourses.vue'
import AdminTeachers from '../views/AdminTeachers.vue'
import AdminStudents from '../views/AdminStudents.vue'

const routes = [
  {
    path: '/',
    name: 'login',
    component: Login
  },
  {
    path: '/admin',
    name: 'admin',
    component: Admin,
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/admin/courses',
    name: 'admin-courses',
    component: AdminCourses,
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/admin/teachers',
    name: 'admin-teachers',
    component: AdminTeachers,
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/admin/students',
    name: 'admin-students',
    component: AdminStudents,
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/student/home',
    name: 'student-home',
    component: StudentHome,
    meta: { requiresAuth: true, role: 'STUDENT' }
  },
  {
    path: '/teacher/home',
    name: 'teacher-home',
    component: TeacherHome,
    meta: { requiresAuth: true, role: 'TEACHER' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router

