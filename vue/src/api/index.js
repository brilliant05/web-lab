import http from '../utils/request'

// 认证相关接口
export const authApi = {
  login: (data) => http.post('/auth/login', data),
  register: (data) => http.post('/auth/register', data),
  logout: () => http.post('/auth/logout'),
  getCurrentUser: () => http.get('/auth/me')
}

// 教师管理接口（与后端 AdminTeacherController 对应）
export const getTeacherList = (params = {}) =>
  http.get('/admin/teachers', { params })

export const addTeacher = (data) =>
  http.post('/admin/teachers', data)

export const updateTeacher = (teacherId, data) =>
  http.put(`/admin/teachers/${teacherId}`, data)

export const updateTeacherStatus = (teacherId, data) =>
  http.put(`/admin/teachers/${teacherId}/status`, data)

export const deleteTeacher = (teacherId) =>
  http.delete(`/admin/teachers/${teacherId}`)

// 学生管理接口（与后端 AdminStudentController 对应）
export const getStudentList = (params = {}) =>
  http.get('/admin/students', { params })

export const addStudent = (data) =>
  http.post('/admin/students', data)

export const updateStudent = (studentId, data) =>
  http.put(`/admin/students/${studentId}`, data)

export const updateStudentStatus = (studentId, data) =>
  http.put(`/admin/students/${studentId}/status`, data)

export const deleteStudent = (studentId) =>
  http.delete(`/admin/students/${studentId}`)

// 便于按需扩展其它模块
export default {
  authApi
}

