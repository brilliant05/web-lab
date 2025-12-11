import http from '@/utils/request';

/**
 * 认证相关接口 - AuthController
 */
/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} 返回包含token和用户信息的数据
 */
export const login = (data) => {
    return http.post('/auth/login', data);
};

/**
 * 学生注册
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码（至少6位）
 * @param {string} [data.realName] - 真实姓名（可选）
 * @param {string} [data.studentId] - 学号（可选）
 * @param {string} [data.college] - 学院（可选）
 * @returns {Promise}
 */
export const register = (data) => {
    return http.post('/auth/register', data);
};

/**
 * 退出登录（可选，并没有什么功能）
 * @returns {Promise}
 */
export const logout = () => {
    return http.post('/auth/logout');
};

/**
 * 修改密码（需要登录）
 * @param {Object} data - 密码信息
 * @param {string} data.oldPassword - 原密码
 * @param {string} data.newPassword - 新密码
 * @param {string} [data.confirmPassword] - 确认新密码
 * @returns {Promise}
 */
export const updatePassword = (data) => {
    return http.put('/auth/password', data);
};

/**
 * 忘记密码 - 重置密码（无需登录）
 * @param {Object} data - 重置密码信息
 * @param {string} data.username - 用户名
 * @param {string} data.newPassword - 新密码（至少6位）
 * @param {string} data.confirmPassword - 确认新密码
 * @returns {Promise}
 */
export const resetPassword = (data) => {
    return http.post('/auth/reset-password', data);
};

/**
 * ========================================
 * 用户信息相关接口 - UserController
 * ========================================
 */

/**
 * 获取当前登录用户信息
 * @returns {Promise} 返回当前用户详细信息
 */
export const getCurrentUserInfo = () => {
    return http.get('/users/current');
};

/**
 * 更新用户资料
 * @param {Object} data - 用户资料
 * @param {string} [data.realName] - 真实姓名
 * @param {string} [data.avatarUrl] - 头像URL
 * @param {string} [data.email] - 邮箱
 * @param {string} [data.phone] - 手机号
 * @param {string} [data.profile] - 个人简介
 * @param {string} [data.jobTitle] - 职称（教师）
 * @param {string} [data.studentId] - 学号（学生）
 * @param {string} [data.college] - 学院
 * @returns {Promise}
 */
export const updateUserProfile = (data) => {
    return http.put('/users/profile', data);
};

/**
 * ========================================
 * 课程管理接口 - CourseController
 * ========================================
 */

/**
 * 添加课程（管理员）
 * @param {Object} data - 课程信息
 * @param {string} data.courseName - 课程名称
 * @param {string} data.courseCode - 课程编号
 * @param {string} data.college - 开课学院
 * @param {string} [data.description] - 课程描述
 * @param {number} [data.credit] - 学分
 * @returns {Promise}
 */
export const addCourse = (data) => {
    return http.post('/courses', data);
};

/**
 * 分页查询课程列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} [params.courseName] - 课程名称（模糊查询）
 * @param {string} [params.courseCode] - 课程编号（模糊查询）
 * @param {string} [params.college] - 学院
 * @returns {Promise} 返回分页数据
 */
export const getCourseList = (params) => {
    return http.get('/courses', { params });
};

/**
 * 根据ID查询课程详情
 * @param {number} courseId - 课程ID
 * @returns {Promise} 返回课程详细信息
 */
export const getCourseById = (courseId) => {
    return http.get(`/courses/${courseId}`);
};

/**
 * 更新课程信息（管理员）
 * @param {number} courseId - 课程ID
 * @param {Object} data - 课程信息
 * @returns {Promise}
 */
export const updateCourse = (courseId, data) => {
    return http.put(`/courses/${courseId}`, data);
};

/**
 * 删除课程（管理员）
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export const deleteCourse = (courseId) => {
    return http.delete(`/courses/${courseId}`);
};

/**
 * 为课程分配教师（管理员）
 * @param {number} courseId - 课程ID
 * @param {Object} data - 参数
 * @param {number} data.teacherId - 教师ID
 * @returns {Promise}
 */
export const assignTeacherToCourse = (courseId, data) => {
    return http.post(`/courses/${courseId}/teachers`, data);
};

/**
 * 移除课程教师（管理员）
 * @param {number} courseId - 课程ID
 * @param {number} teacherId - 教师ID
 * @returns {Promise}
 */
export const removeTeacherFromCourse = (courseId, teacherId) => {
    return http.delete(`/courses/${courseId}/teachers/${teacherId}`);
};

/**
 * ========================================
 * 管理员-教师管理接口 - AdminTeacherController
 * ========================================
 */

/**
 * 添加教师（管理员）
 * @param {Object} data - 教师信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} [data.realName] - 真实姓名
 * @param {string} [data.jobTitle] - 职称
 * @param {string} [data.college] - 学院
 * @param {string} [data.email] - 邮箱
 * @param {string} [data.phone] - 手机号
 * @returns {Promise}
 */
export const addTeacher = (data) => {
    return http.post('/admin/teachers', data);
};

/**
 * 更新教师信息（管理员）
 * @param {number} teacherId - 教师ID
 * @param {Object} data - 教师信息
 * @returns {Promise}
 */
export const updateTeacher = (teacherId, data) => {
    return http.put(`/admin/teachers/${teacherId}`, data);
};

/**
 * 启用/禁用教师账号（管理员）
 * @param {number} teacherId - 教师ID
 * @param {Object} data - 状态信息
 * @param {number} data.status - 状态（0-禁用，1-启用）
 * @returns {Promise}
 */
export const updateTeacherStatus = (teacherId, data) => {
    return http.put(`/admin/teachers/${teacherId}/status`, data);
};

/**
 * 删除教师（管理员，逻辑删除）
 * @param {number} teacherId - 教师ID
 * @returns {Promise}
 */
export const deleteTeacher = (teacherId) => {
    return http.delete(`/admin/teachers/${teacherId}`);
};

/**
 * 分页查询教师列表（管理员）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} [params.keyword] - 关键词（姓名/用户名）
 * @param {string} [params.college] - 学院
 * @param {number} [params.status] - 状态
 * @returns {Promise} 返回分页数据
 */
export const getTeacherList = (params) => {
    return http.get('/admin/teachers', { params });
};

/**
 * ========================================
 * 管理员-学生管理接口 - AdminStudentController
 * ========================================
 */

/**
 * 分页查询学生列表（管理员）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} [params.keyword] - 关键词（姓名/用户名/学号）
 * @param {string} [params.college] - 学院
 * @param {number} [params.status] - 状态
 * @returns {Promise} 返回分页数据
 */
export const getStudentList = (params) => {
    return http.get('/admin/students', { params });
};

/**
 * 添加学生（管理员）
 * @param {Object} data - 学生信息
 * @returns {Promise}
 */
export const addStudent = (data) => {
    return http.post('/admin/students', data);
};

/**
 * 获取学生详情（管理员）
 * @param {number} studentId - 学生ID
 * @returns {Promise} 返回学生详细信息
 */
export const getStudentDetail = (studentId) => {
    return http.get(`/admin/students/${studentId}`);
};

/**
 * 更新学生信息（管理员）
 * @param {number} studentId - 学生ID
 * @param {Object} data - 学生信息
 * @returns {Promise}
 */
export const updateStudent = (studentId, data) => {
    return http.put(`/admin/students/${studentId}`, data);
};

/**
 * 启用/禁用学生账号（管理员）
 * @param {number} studentId - 学生ID
 * @param {Object} data - 状态信息
 * @param {number} data.status - 状态（0-禁用，1-启用）
 * @returns {Promise}
 */
export const updateStudentStatus = (studentId, data) => {
    return http.put(`/admin/students/${studentId}/status`, data);
};

/**
 * 删除学生（管理员，逻辑删除）
 * @param {number} studentId - 学生ID
 * @returns {Promise}
 */
export const deleteStudent = (studentId) => {
    return http.delete(`/admin/students/${studentId}`);
};

/**
 * 为学生分配课程（管理员）
 * @param {number} studentId - 学生ID
 * @param {Object} data - 分配信息
 * @param {number} data.courseId - 课程ID
 * @returns {Promise}
 */
export const assignCourseToStudent = (studentId, data) => {
    return http.post(`/admin/students/${studentId}/courses`, data);
};

/**
 * 移除学生课程（管理员）
 * @param {number} studentId - 学生ID
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export const removeCourseFromStudent = (studentId, courseId) => {
    return http.delete(`/admin/students/${studentId}/courses/${courseId}`);
};

/**
 * ========================================
 * 学习资源管理接口 - ResourceController
 * ========================================
 */

/**
 * 分页查询资源列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {number} [params.courseId] - 课程ID
 * @param {string} [params.keyword] - 关键词（标题）
 * @param {string} [params.fileType] - 文件类型（PDF/VIDEO/DOCUMENT/IMAGE/OTHER）
 * @param {string} [params.orderBy] - 排序字段（createTime/downloadCount/viewCount）
 * @param {string} [params.orderDir] - 排序方向（asc/desc）
 * @returns {Promise} 返回分页数据
 */
export const getResourceList = (params) => {
    return http.get('/resources', { params });
};

/**
 * 获取资源详情
 * @param {number} resourceId - 资源ID
 * @returns {Promise} 返回资源详细信息
 */
export const getResourceDetail = (resourceId) => {
    return http.get(`/resources/${resourceId}`);
};

/**
 * 上传资源（multipart/form-data）
 * @param {FormData} formData - 表单数据
 * formData 应包含以下字段：
 * - file: 文件（必填）
 * - resourceTitle: 资源标题（必填）
 * - description: 资源描述（可选）
 * - courseId: 课程ID（必填）
 * - visibility: 可见性 PUBLIC/COURSE_ONLY（可选，默认PUBLIC）
 * - tags: 标签，逗号分隔（可选）
 * @returns {Promise}
 */
export const uploadResource = (formData) => {
    return http.post('/resources', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
};

/**
 * 更新资源信息
 * @param {number} resourceId - 资源ID
 * @param {Object} data - 资源信息
 * @param {string} [data.resourceTitle] - 资源标题
 * @param {string} [data.description] - 资源描述
 * @param {string} [data.tags] - 标签
 * @param {string} [data.visibility] - 可见性
 * @param {number} [data.isTop] - 是否置顶（0/1）
 * @returns {Promise}
 */
export const updateResource = (resourceId, data) => {
    return http.put(`/resources/${resourceId}`, data);
};

/**
 * 删除资源
 * @param {number} resourceId - 资源ID
 * @returns {Promise}
 */
export const deleteResource = (resourceId) => {
    return http.delete(`/resources/${resourceId}`);
};

/**
 * 下载资源（获取下载URL）
 * @param {number} resourceId - 资源ID
 * @returns {Promise} 返回包含url的对象
 */
export const downloadResource = (resourceId) => {
    return http.get(`/resources/${resourceId}/download`);
};

/**
 * 收藏资源
 * @param {number} resourceId - 资源ID
 * @returns {Promise}
 */
export const collectResource = (resourceId) => {
    return http.post(`/resources/${resourceId}/collect`);
};

/**
 * 取消收藏资源
 * @param {number} resourceId - 资源ID
 * @returns {Promise}
 */
export const uncollectResource = (resourceId) => {
    return http.delete(`/resources/${resourceId}/collect`);
};

/**
 * 获取我的收藏列表
 * @param {Object} params - 查询参数
 * @param {number} [params.pageNum=1] - 页码
 * @param {number} [params.pageSize=10] - 每页数量
 * @returns {Promise} 返回分页数据
 */
export const getMyCollections = (params) => {
    return http.get('/resources/collections', { params });
};

/**
 * 获取我上传的资源列表
 * @param {Object} params - 查询参数
 * @param {number} [params.pageNum=1] - 页码
 * @param {number} [params.pageSize=10] - 每页数量
 * @returns {Promise} 返回分页数据
 */
export const getMyUploads = (params) => {
    return http.get('/resources/my-uploads', { params });
};

/**
 * 置顶资源（教师/管理员）
 * @param {number} resourceId - 资源ID
 * @param {Object} data - 置顶信息
 * @param {number} data.isTop - 是否置顶（0/1）
 * @returns {Promise}
 */
export const topResource = (resourceId, data) => {
    return http.put(`/resources/${resourceId}/top`, data);
};

/**
 * ========================================
 * 问答管理接口 - QuestionController
 * ========================================
 */

/**
 * 分页查询问题列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {number} [params.courseId] - 课程ID
 * @param {string} [params.keyword] - 关键词（标题/内容）
 * @param {number} [params.isAnswered] - 是否已回答（0/1）
 * @param {string} [params.orderBy] - 排序字段
 * @param {string} [params.orderDir] - 排序方向
 * @returns {Promise} 返回分页数据
 */
export const getQuestionList = (params) => {
    return http.get('/questions', { params });
};

/**
 * 获取问题详情
 * @param {number} questionId - 问题ID
 * @returns {Promise} 返回问题详情（包含回答信息）
 */
export const getQuestionDetail = (questionId) => {
    return http.get(`/questions/${questionId}`);
};

/**
 * 提交问题（学生，multipart/form-data）
 * @param {FormData} formData - 表单数据
 * formData 应包含以下字段：
 * - questionTitle: 问题标题（必填）
 * - questionContent: 问题内容（必填）
 * - courseId: 课程ID（必填）
 * - teacherId: 教师ID（必填）
 * - tags: 标签，逗号分隔（可选）
 * - images: 图片文件数组（可选）
 * @returns {Promise}
 */
export const submitQuestion = (formData) => {
    return http.post('/questions', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
};

/**
 * 更新问题（学生/管理员）
 * @param {number} questionId - 问题ID
 * @param {Object} data - 问题信息
 * @param {string} [data.questionTitle] - 问题标题
 * @param {string} [data.questionContent] - 问题内容
 * @param {string} [data.tags] - 标签
 * @returns {Promise}
 */
export const updateQuestion = (questionId, data) => {
    return http.put(`/questions/${questionId}`, data);
};

/**
 * 删除问题（学生/管理员）
 * @param {number} questionId - 问题ID
 * @returns {Promise}
 */
export const deleteQuestion = (questionId) => {
    return http.delete(`/questions/${questionId}`);
};

/**
 * 获取我的提问列表（学生）
 * @param {Object} params - 查询参数
 * @param {number} [params.pageNum=1] - 页码
 * @param {number} [params.pageSize=10] - 每页数量
 * @param {number} [params.isAnswered] - 是否已回答（0/1）
 * @returns {Promise} 返回分页数据
 */
export const getMyQuestions = (params) => {
    return http.get('/questions/my-questions', { params });
};

/**
 * 获取待回答问题列表（教师）
 * @param {Object} params - 查询参数
 * @param {number} [params.pageNum=1] - 页码
 * @param {number} [params.pageSize=10] - 每页数量
 * @param {number} [params.courseId] - 课程ID
 * @returns {Promise} 返回分页数据
 */
export const getPendingQuestions = (params) => {
    return http.get('/questions/pending', { params });
};

/**
 * 回答问题（教师，multipart/form-data）
 * @param {number} questionId - 问题ID
 * @param {FormData} formData - 表单数据
 * formData 应包含以下字段：
 * - answerContent: 回答内容（必填）
 * - images: 图片文件数组（可选）
 * @returns {Promise}
 */
export const answerQuestion = (questionId, formData) => {
    return http.post(`/questions/${questionId}/answers`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
};

/**
 * 更新回答（教师/管理员）
 * @param {number} answerId - 回答ID
 * @param {Object} data - 回答信息
 * @param {string} data.answerContent - 回答内容
 * @returns {Promise}
 */
export const updateAnswer = (answerId, data) => {
    return http.put(`/questions/answers/${answerId}`, data);
};

/**
 * 删除回答（教师/管理员）
 * @param {number} answerId - 回答ID
 * @returns {Promise}
 */
export const deleteAnswer = (answerId) => {
    return http.delete(`/questions/answers/${answerId}`);
};

/**
 * 获取我的回答列表（教师）
 * @param {Object} params - 查询参数
 * @param {number} [params.pageNum=1] - 页码
 * @param {number} [params.pageSize=10] - 每页数量
 * @returns {Promise} 返回分页数据
 */
export const getMyAnswers = (params) => {
    return http.get('/questions/answers/my-answers', { params });
};

/**
 * ========================================
 * 通知管理接口 - NotificationController
 * ========================================
 */

/**
 * 分页查询通知列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {number} [params.isRead] - 是否已读（0-未读，1-已读）
 * @returns {Promise} 返回分页数据
 */
export const getNotificationList = (params) => {
    return http.get('/notifications', { params });
};

/**
 * 获取未读通知数量
 * @returns {Promise} 返回包含unreadCount的对象
 */
export const getUnreadCount = () => {
    return http.get('/notifications/unread-count');
};

/**
 * 标记通知为已读
 * @param {number} notificationId - 通知ID
 * @returns {Promise}
 */
export const markNotificationAsRead = (notificationId) => {
    return http.put(`/notifications/${notificationId}/read`);
};

/**
 * 标记所有通知为已读
 * @returns {Promise}
 */
export const markAllNotificationsAsRead = () => {
    return http.put('/notifications/read-all');
};

/**
 * 创建系统通知（管理员）
 * @param {Object} data - 通知信息
 * @param {number} [data.userId] - 接收通知的用户ID，null表示全体用户
 * @param {string} data.title - 通知标题
 * @param {string} data.content - 通知内容
 * @returns {Promise}
 */
export const createSystemNotification = (data) => {
    return http.post('/notifications/system', data);
};

/**
 * ========================================
 * 文件上传接口 - FileUploadController
 * ========================================
 */

/**
 * 上传单个文件（通用上传接口）
 * @param {File} file - 文件对象
 * @returns {Promise} 返回包含url、fileName、fileSize的对象
 */
export const uploadFile = (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return http.post('/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
};

/**
 * 上传图片（专用接口，带图片格式校验）
 * @param {File} file - 图片文件对象
 * @returns {Promise} 返回包含url、fileName、fileSize的对象
 */
export const uploadImage = (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return http.post('/upload/image', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
};

/**
 * ========================================
 * 默认导出所有API（可选）
 * ========================================
 */
export default {
    // 认证相关
    login,
    register,
    logout,
    updatePassword,
    resetPassword,

    // 用户信息
    getCurrentUserInfo,
    updateUserProfile,

    // 课程管理
    addCourse,
    getCourseList,
    getCourseById,
    updateCourse,
    deleteCourse,
    assignTeacherToCourse,
    removeTeacherFromCourse,

    // 管理员-教师管理
    addTeacher,
    updateTeacher,
    updateTeacherStatus,
    deleteTeacher,
    getTeacherList,

    // 管理员-学生管理
    getStudentList,
    addStudent,
    getStudentDetail,
    updateStudent,
    updateStudentStatus,
    deleteStudent,
    assignCourseToStudent,
    removeCourseFromStudent,

    // 学习资源管理
    getResourceList,
    getResourceDetail,
    uploadResource,
    updateResource,
    deleteResource,
    downloadResource,
    collectResource,
    uncollectResource,
    getMyCollections,
    getMyUploads,
    topResource,

    // 问答管理
    getQuestionList,
    getQuestionDetail,
    submitQuestion,
    updateQuestion,
    deleteQuestion,
    getMyQuestions,
    getPendingQuestions,
    answerQuestion,
    updateAnswer,
    deleteAnswer,
    getMyAnswers,

    // 通知管理
    getNotificationList,
    getUnreadCount,
    markNotificationAsRead,
    markAllNotificationsAsRead,
    createSystemNotification,

    // 文件上传
    uploadFile,
    uploadImage
};

