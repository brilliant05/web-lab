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

// 文件上传接口
export const uploadCourseCover = (formData) =>
  http.post('/files/course-cover', formData)

// 资源管理接口
export const getResourceList = (params = {}) =>
  http.get('/resources', { params })

export const getResourceDetail = (resourceId) =>
  http.get(`/resources/${resourceId}`)

export const deleteResource = (resourceId) =>
  http.delete(`/resources/${resourceId}`)

export const downloadResource = (resourceId) =>
  http.get(`/resources/${resourceId}/download`)

export const collectResource = (resourceId) =>
  http.post(`/resources/${resourceId}/collect`)

export const uncollectResource = (resourceId) =>
  http.delete(`/resources/${resourceId}/collect`)

export const uploadResource = (formData) =>
  http.post('/resources', formData)

export const updateResource = (resourceId, data) =>
  http.put(`/resources/${resourceId}`, data)

export const getMyUploads = (params = {}) =>
  http.get('/resources/my-uploads', { params })

// 问答管理接口
export const getQuestionList = (params = {}) =>
  http.get('/questions', { params })

export const getQuestionDetail = (questionId) =>
  http.get(`/questions/${questionId}`)

export const submitQuestion = (formData) =>
  http.post('/questions', formData)

export const updateQuestion = (questionId, data) =>
  http.put(`/questions/${questionId}`, data)

export const deleteQuestion = (questionId) =>
  http.delete(`/questions/${questionId}`)

export const getMyQuestions = (params = {}) =>
  http.get('/questions/my-questions', { params })

export const likeAnswer = (answerId) =>
  http.post(`/answers/${answerId}/like`)

export const unlikeAnswer = (answerId) =>
  http.delete(`/answers/${answerId}/like`)

export const updateAnswer = (answerId, data) =>
  http.put(`/answers/${answerId}`, data)

export const deleteAnswer = (answerId) =>
  http.delete(`/answers/${answerId}`)

export const getPendingQuestions = (params = {}) =>
  http.get('/questions/pending', { params })

export const answerQuestion = (questionId, formData) =>
  http.post(`/questions/${questionId}/answers`, formData)

// 通知管理接口
export const getNotificationList = (params = {}) =>
  http.get('/notifications', { params })

export const getUnreadCount = () =>
  http.get('/notifications/unread-count')

export const markNotificationRead = (notificationId) =>
  http.put(`/notifications/${notificationId}/read`)

export const markAllNotificationRead = () =>
  http.put('/notifications/read-all')

export const deleteNotification = (notificationId) =>
  http.delete(`/notifications/${notificationId}`)

// 用户信息接口
export const getCurrentUser = () =>
  http.get('/users/current')

export const updateUserProfile = (data) =>
  http.put('/users/profile', data)

export const updatePassword = (data) =>
  http.put('/auth/password', data)

export const uploadAvatar = (formData) =>
  http.post('/files/avatar', formData)

// 便于按需扩展其它模块
export default {
  authApi
}

export const getMyCourses = (params = {}) =>
  http.get('/courses/my', { params })

export const getCourseStudents = (courseId) =>
  http.get(`/courses/${courseId}/students`)

export const assignTeacher = (courseId, teacherId) =>
  http.post(`/courses/${courseId}/teachers`, { teacherId })

export const removeTeacher = (courseId, teacherId) =>
  http.delete(`/courses/${courseId}/teachers/${teacherId}`)

export const getCourseTeachers = (courseId) =>
  http.get(`/courses/${courseId}/teachers`)

export const getTeacherCourses = (teacherId) =>
  http.get(`/admin/teachers/${teacherId}/courses`)

