# API 接口说明（后端现状梳理）
> 基础路径：`/api/v1`（前端 axios baseURL），以下列出的路径均应加上前缀 `/api/v1`

## 认证与用户
- `POST /auth/login` — 登录，Body: `{username, password}`，返回 `{token, userInfo}`
- `POST /auth/register` — 学生注册，Body: `User`（至少 `username, password`）
- `POST /auth/logout` — 退出
- `PUT /auth/password` — 修改密码，Body: `{oldPassword, newPassword, confirmPassword?}`
- `PUT /auth/reset-password` — 重置密码，Body 同上
- `GET /users/current` — 获取当前登录用户信息
- `PUT /users/profile` — 更新当前用户信息

## 课程（CourseController `/courses`）
- `POST /courses` — 新建课程（ADMIN），Body: `{courseName, courseCode, college, ...}`
- `GET /courses/page` — 分页查询课程（ADMIN/STUDENT），Query: `pageNum,pageSize,keyword...`
- `GET /courses/{courseId}` — 课程详情
- `PUT /courses/{courseId}` — 更新课程（ADMIN）
- `DELETE /courses/{courseId}` — 删除课程（ADMIN）
- `POST /courses/{courseId}/teachers` — 分配教师（ADMIN），Body: `{teacherId}`
- `DELETE /courses/{courseId}/teachers/{teacherId}` — 移除教师（ADMIN）
- `GET /courses/my` — 教师“我的课程”（TEACHER），Query: `pageNum,pageSize`
- `POST /courses/join` — 学生邀请码加入课程（STUDENT），Body: `{inviteCode}`

## 管理员-学生（AdminStudentController `/admin/students`）
- `GET /admin/students/page` — 分页学生列表（ADMIN）
- `GET /admin/students/{studentId}` — 学生详情（ADMIN）
- `PUT /admin/students/{studentId}/status` — 启用/禁用（ADMIN），Body: `User{status}`
- `DELETE /admin/students/{studentId}` — 删除（逻辑删除，ADMIN）
- `PUT /admin/students/{studentId}` — 更新信息（ADMIN）
- `POST /admin/students/{studentId}/courses` — 分配课程（ADMIN），Body: `{courseId, teacherId}`
- `DELETE /admin/students/{studentId}/courses/{courseId}` — 移除课程（ADMIN）

## 管理员-教师（AdminTeacherController `/admin/teachers`）
- `POST /admin/teachers` — 新增教师（ADMIN），Body: `User`
- `PUT /admin/teachers/{teacherId}` — 更新教师（ADMIN）
- `PUT /admin/teachers/{teacherId}/status` — 启用/禁用（ADMIN）
- `DELETE /admin/teachers/{teacherId}` — 删除（逻辑删除，ADMIN）
- `GET /admin/teachers/page` — 分页教师列表（ADMIN）

## 资源（ResourceController `/resources`）
- `GET /resources` — 资源分页，Query: `pageNum,pageSize,keyword,...`
- `GET /resources/{resourceId}` — 资源详情
- `POST /resources` — 上传资源（所有登录），`multipart/form-data`，需参数：`file, resourceTitle, courseId` 等
- `PUT /resources/{resourceId}` — 更新资源
- `DELETE /resources/{resourceId}` — 删除资源
- `GET /resources/{resourceId}/download` — 获取下载 URL
- `POST /resources/{resourceId}/collect` — 收藏
- `DELETE /resources/{resourceId}/collect` — 取消收藏
- `GET /resources/collections` — 我的收藏
- `GET /resources/my-uploads` — 我上传的资源
- `PUT /resources/{resourceId}/top` — 置顶（ADMIN/TEACHER），Body: `{isTop}`

## 问答（QuestionController `/questions`）
- `GET /questions` — 问题列表，Query: `pageNum,pageSize,courseId,teacherId,keyword,isAnswered...`
- `GET /questions/{questionId}` — 问题详情
- `POST /questions` — 提问（STUDENT），`multipart/form-data`：`questionTitle, questionContent, courseId, teacherId, tags?, images[]`
- `PUT /questions/{questionId}` — 更新问题（STUDENT/ADMIN）
- `DELETE /questions/{questionId}` — 删除问题（STUDENT/ADMIN）
- `GET /questions/my-questions` — 我的提问（STUDENT）
- `GET /questions/pending` — 待回答（TEACHER）
- `POST /questions/{questionId}/answers` — 回答（TEACHER），`multipart/form-data`：`answerContent, images[]`
- `PUT /answers/{answerId}` — 更新回答（TEACHER/ADMIN），Body: `{answerContent}`
- `DELETE /answers/{answerId}` — 删除回答（TEACHER/ADMIN）
- `POST /answers/{answerId}/like` — 点赞回答
- `DELETE /answers/{answerId}/like` — 取消点赞

## 通知（NotificationController `/notifications`）
- `GET /notifications` — 通知列表
- `GET /notifications/unread-count` — 未读数量
- `PUT /notifications/{notificationId}/read` — 标记已读
- `PUT /notifications/read-all` — 全部已读
- `POST /notifications/system` — 创建系统通知（ADMIN），Body: `{userId?, title, content}`

## 文件上传（FileUploadController `/upload`）
- `POST /upload` — 通用上传（文件类型/大小校验，10MB）
- `POST /upload/image` — 图片上传（5MB，JPG/PNG/GIF/WEBP）

---
说明：
- 所有需要鉴权的接口依赖 JWT，后端在拦截器中从请求属性 `userId/role` 获取当前用户。
- 角色常量：`ADMIN`、`TEACHER`、`STUDENT`。
- 响应统一格式：`{ code, message, data, timestamp }`，`code=200` 表示成功。

