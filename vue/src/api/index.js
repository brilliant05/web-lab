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

// 统计数据接口
export const getStatisticsOverview = () =>
  http.get('/admin/statistics/overview')

// 课程管理接口
export const getCourseList = (params = {}) =>
  http.get('/courses', { params })

export const getCourseById = (courseId) =>
  http.get(`/courses/${courseId}`)

export const addCourse = (data) =>
  http.post('/courses', data)

export const updateCourse = (courseId, data) =>
  http.put(`/courses/${courseId}`, data)

export const deleteCourse = (courseId) =>
  http.delete(`/courses/${courseId}`)

// 资源管理接口
export const getResourceList = (params = {}) =>
  http.get('/resources', { params })

export const getResourceDetail = (resourceId) =>
  http.get(`/resources/${resourceId}`)

export const deleteResource = (resourceId) =>
  http.delete(`/resources/${resourceId}`)

export const downloadResource = (resourceId) =>
  http.get(`/resources/${resourceId}/download`)

// 问答管理接口
export const getQuestionList = (params = {}) =>
  http.get('/questions', { params })

export const deleteQuestion = (questionId) =>
  http.delete(`/questions/${questionId}`)

// 通知管理接口
export const getNotificationList = (params = {}) =>
  http.get('/notifications', { params })

export const deleteNotification = (notificationId) =>
  http.delete(`/notifications/${notificationId}`)

// 便于按需扩展其它模块
export default {
  authApi
}

