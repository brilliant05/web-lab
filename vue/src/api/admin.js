import http from '../utils/request'

export const adminApi = {
  // 课程管理
  getCourses: (params = {}) => http.get('/courses/page', { params }),
  createCourse: (data) => http.post('/courses', data),
  // 教师管理
  getTeachers: (params = {}) => http.get('/admin/teachers', { params }),
  // 学生管理
  getStudents: (params = {}) => http.get('/admin/students/page', { params })}