# API接口设计文档

## 一、接口设计原则

### 1.1 RESTful规范
- 使用标准HTTP方法: GET(查询)、POST(创建)、PUT(更新)、DELETE(删除)
- URL使用名词复数形式,不使用动词
- 合理使用HTTP状态码
- 统一的响应格式

### 1.2 URL设计规范
```
基础路径: /api/v1
完整示例: http://localhost:8080/api/v1/courses
```

### 1.3 统一响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {},
    "timestamp": 1700000000000
}
```

### 1.4 分页响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "records": [],
        "total": 100,
        "pageNum": 1,
        "pageSize": 10,
        "pages": 10
    },
    "timestamp": 1700000000000
}
```

---

## 二、认证授权接口

### 2.1 用户注册
```
POST /api/v1/auth/register
```
**请求参数:**
```json
{
    "username": "student01",
    "password": "password123",
    "confirmPassword": "password123",
    "realName": "张三",
    "email": "student01@example.com",
    "phone": "13800138000",
    "studentId": "2024001",
    "college": "计算机学院"
}
```
**响应:**
```json
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "userId": 1,
        "username": "student01",
        "role": "STUDENT"
    }
}
```

### 2.2 用户登录
```
POST /api/v1/auth/login
```
**请求参数:**
```json
{
    "username": "admin",
    "password": "admin123"
}
```
**响应:**
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "userInfo": {
            "userId": 1,
            "username": "admin",
            "realName": "管理员",
            "role": "ADMIN",
            "avatarUrl": "https://..."
        }
    }
}
```

### 2.3 退出登录
```
POST /api/v1/auth/logout
```
**Headers:** `Authorization: Bearer {token}`

**响应:**
```json
{
    "code": 200,
    "message": "退出成功"
}
```

### 2.4 刷新Token
```
POST /api/v1/auth/refresh-token
```
**请求参数:**
```json
{
    "refreshToken": "xxx"
}
```

### 2.5 修改密码
```
PUT /api/v1/auth/password
```
**请求参数:**
```json
{
    "oldPassword": "old123",
    "newPassword": "new123",
    "confirmPassword": "new123"
}
```

---

## 三、用户管理接口

### 3.1 获取当前用户信息
```
GET /api/v1/users/current
```
**响应:**
```json
{
    "code": 200,
    "data": {
        "userId": 1,
        "username": "student01",
        "realName": "张三",
        "email": "student01@example.com",
        "role": "STUDENT",
        "avatarUrl": "https://..."
    }
}
```

### 3.2 更新个人信息
```
PUT /api/v1/users/profile
```
**请求参数:**
```json
{
    "realName": "张三",
    "email": "newemail@example.com",
    "phone": "13800138001",
    "profile": "个人简介"
}
```

### 3.3 上传头像
```
POST /api/v1/users/avatar
```
**请求参数:** `multipart/form-data`
- `file`: 图片文件

**响应:**
```json
{
    "code": 200,
    "message": "头像上传成功",
    "data": {
        "avatarUrl": "https://..."
    }
}
```

---

## 四、课程管理接口

### 4.1 获取课程列表(分页)
```
GET /api/v1/courses?pageNum=1&pageSize=10&keyword=Java&college=计算机学院
```
**查询参数:**
- `pageNum`: 页码(默认1)
- `pageSize`: 每页条数(默认10)
- `keyword`: 搜索关键字(可选)
- `college`: 学院(可选)
- `status`: 状态(可选: 0-关闭, 1-开放)

**响应:**
```json
{
    "code": 200,
    "data": {
        "records": [
            {
                "courseId": 1,
                "courseName": "Java程序设计",
                "courseCode": "CS101",
                "college": "计算机学院",
                "coverImage": "https://...",
                "status": 1,
                "teacherCount": 3,
                "studentCount": 120,
                "resourceCount": 45
            }
        ],
        "total": 100,
        "pageNum": 1,
        "pageSize": 10
    }
}
```

### 4.2 获取课程详情
```
GET /api/v1/courses/{courseId}
```
**响应:**
```json
{
    "code": 200,
    "data": {
        "courseId": 1,
        "courseName": "Java程序设计",
        "courseCode": "CS101",
        "description": "课程描述...",
        "college": "计算机学院",
        "coverImage": "https://...",
        "status": 1,
        "teachers": [
            {
                "teacherId": 2,
                "realName": "李老师",
                "jobTitle": "教授",
                "className": "2024级1班",
                "inviteCode": "ABC123"
            }
        ],
        "createTime": "2024-01-01 10:00:00"
    }
}
```

### 4.3 创建课程(管理员)
```
POST /api/v1/courses
```
**请求参数:**
```json
{
    "courseName": "Java程序设计",
    "courseCode": "CS101",
    "description": "课程描述",
    "college": "计算机学院",
    "coverImage": "https://..."
}
```

### 4.4 更新课程(管理员)
```
PUT /api/v1/courses/{courseId}
```
**请求参数:** 同创建课程

### 4.5 删除课程(管理员)
```
DELETE /api/v1/courses/{courseId}
```

### 4.6 为课程分配教师(管理员)
```
POST /api/v1/courses/{courseId}/teachers
```
**请求参数:**
```json
{
    "teacherId": 2,
    "className": "2024级1班"
}
```

### 4.7 移除课程教师(管理员)
```
DELETE /api/v1/courses/{courseId}/teachers/{teacherId}
```

### 4.8 获取我的课程列表
```
GET /api/v1/courses/my-courses?role=STUDENT
```
**查询参数:**
- `role`: STUDENT(学生选课) / TEACHER(教师授课)

**响应(学生):**
```json
{
    "code": 200,
    "data": [
        {
            "courseId": 1,
            "courseName": "Java程序设计",
            "teacherName": "李老师",
            "className": "2024级1班",
            "joinTime": "2024-01-01 10:00:00"
        }
    ]
}
```

### 4.9 学生加入课程(通过邀请码)
```
POST /api/v1/courses/join
```
**请求参数:**
```json
{
    "inviteCode": "ABC123"
}
```

---

## 五、学习资源接口

### 5.1 获取资源列表(分页)
```
GET /api/v1/resources?pageNum=1&pageSize=10&courseId=1&keyword=Java&fileType=PDF
```
**查询参数:**
- `pageNum`: 页码
- `pageSize`: 每页条数
- `courseId`: 课程ID(可选)
- `keyword`: 搜索关键字(可选)
- `fileType`: 文件类型(可选)
- `uploaderId`: 上传者ID(可选)
- `orderBy`: 排序字段(createTime/downloadCount/viewCount)
- `orderDir`: 排序方向(asc/desc)

**响应:**
```json
{
    "code": 200,
    "data": {
        "records": [
            {
                "resourceId": 1,
                "resourceTitle": "Java基础教程",
                "description": "资源描述",
                "courseName": "Java程序设计",
                "uploaderName": "张三",
                "uploaderRole": "TEACHER",
                "fileName": "java_basic.pdf",
                "fileSize": 1024000,
                "fileType": "PDF",
                "downloadCount": 120,
                "viewCount": 500,
                "isTop": 1,
                "createTime": "2024-01-01 10:00:00"
            }
        ],
        "total": 100,
        "pageNum": 1,
        "pageSize": 10
    }
}
```

### 5.2 获取资源详情
```
GET /api/v1/resources/{resourceId}
```
**响应:**
```json
{
    "code": 200,
    "data": {
        "resourceId": 1,
        "resourceTitle": "Java基础教程",
        "description": "资源详细描述",
        "courseId": 1,
        "courseName": "Java程序设计",
        "uploaderId": 2,
        "uploaderName": "张三",
        "uploaderRole": "TEACHER",
        "fileName": "java_basic.pdf",
        "filePath": "/uploads/...",
        "fileSize": 1024000,
        "fileType": "PDF",
        "visibility": "PUBLIC",
        "downloadCount": 120,
        "viewCount": 500,
        "isTop": 1,
        "isCollected": false,
        "tags": "基础,教程",
        "createTime": "2024-01-01 10:00:00"
    }
}
```

### 5.3 上传资源
```
POST /api/v1/resources
```
**请求参数:** `multipart/form-data`
- `file`: 文件
- `resourceTitle`: 资源标题
- `description`: 资源描述
- `courseId`: 所属课程ID
- `visibility`: 可见性(PUBLIC/COURSE_ONLY) - 仅教师可设置
- `tags`: 标签(可选)

**响应:**
```json
{
    "code": 200,
    "message": "上传成功",
    "data": {
        "resourceId": 1,
        "resourceTitle": "Java基础教程",
        "fileName": "java_basic.pdf",
        "fileSize": 1024000
    }
}
```

### 5.4 更新资源信息
```
PUT /api/v1/resources/{resourceId}
```
**请求参数:**
```json
{
    "resourceTitle": "新标题",
    "description": "新描述",
    "tags": "新标签",
    "visibility": "PUBLIC",
    "isTop": 1
}
```

### 5.5 删除资源
```
DELETE /api/v1/resources/{resourceId}
```

### 5.6 下载资源
```
GET /api/v1/resources/{resourceId}/download
```
**响应:** 文件流(二进制)

### 5.7 收藏资源(学生)
```
POST /api/v1/resources/{resourceId}/collect
```

### 5.8 取消收藏(学生)
```
DELETE /api/v1/resources/{resourceId}/collect
```

### 5.9 获取我的收藏列表(学生)
```
GET /api/v1/resources/collections?pageNum=1&pageSize=10
```

### 5.10 获取我上传的资源
```
GET /api/v1/resources/my-uploads?pageNum=1&pageSize=10
```

### 5.11 置顶资源(教师)
```
PUT /api/v1/resources/{resourceId}/top
```
**请求参数:**
```json
{
    "isTop": 1
}
```

---

## 六、问答管理接口

### 6.1 获取问题列表(分页)
```
GET /api/v1/questions?pageNum=1&pageSize=10&courseId=1&teacherId=2&isAnswered=1
```
**查询参数:**
- `pageNum`: 页码
- `pageSize`: 每页条数
- `courseId`: 课程ID(可选)
- `teacherId`: 教师ID(可选)
- `studentId`: 学生ID(可选)
- `keyword`: 搜索关键字(可选)
- `isAnswered`: 是否已回答(0/1,可选)
- `orderBy`: 排序字段(createTime/viewCount/answerCount)

**响应:**
```json
{
    "code": 200,
    "data": {
        "records": [
            {
                "questionId": 1,
                "questionTitle": "Java多线程如何实现?",
                "questionContent": "问题内容...",
                "courseName": "Java程序设计",
                "studentName": "张三",
                "teacherName": "李老师",
                "viewCount": 100,
                "answerCount": 3,
                "isAnswered": 1,
                "tags": "多线程,并发",
                "createTime": "2024-01-01 10:00:00"
            }
        ],
        "total": 50,
        "pageNum": 1,
        "pageSize": 10
    }
}
```

### 6.2 获取问题详情
```
GET /api/v1/questions/{questionId}
```
**响应:**
```json
{
    "code": 200,
    "data": {
        "questionId": 1,
        "questionTitle": "Java多线程如何实现?",
        "questionContent": "问题详细内容...",
        "imageUrls": "https://...,https://...",
        "courseId": 1,
        "courseName": "Java程序设计",
        "studentId": 3,
        "studentName": "张三",
        "teacherId": 2,
        "teacherName": "李老师",
        "viewCount": 100,
        "answerCount": 3,
        "isAnswered": 1,
        "tags": "多线程,并发",
        "answers": [
            {
                "answerId": 1,
                "answerContent": "回答内容...",
                "imageUrls": "https://...",
                "teacherId": 2,
                "teacherName": "李老师",
                "likeCount": 15,
                "isLiked": false,
                "createTime": "2024-01-01 11:00:00"
            }
        ],
        "createTime": "2024-01-01 10:00:00"
    }
}
```

### 6.3 提交问题(学生)
```
POST /api/v1/questions
```
**请求参数:** `multipart/form-data`
- `questionTitle`: 问题标题
- `questionContent`: 问题内容
- `courseId`: 课程ID
- `teacherId`: 向哪位教师提问
- `tags`: 标签(可选)
- `images`: 图片文件数组(可选,最多3张)

**响应:**
```json
{
    "code": 200,
    "message": "提问成功",
    "data": {
        "questionId": 1,
        "questionTitle": "Java多线程如何实现?"
    }
}
```

### 6.4 更新问题(学生,仅未回答的问题)
```
PUT /api/v1/questions/{questionId}
```
**请求参数:**
```json
{
    "questionTitle": "新标题",
    "questionContent": "新内容",
    "tags": "新标签"
}
```

### 6.5 删除问题
```
DELETE /api/v1/questions/{questionId}
```

### 6.6 获取我的提问列表(学生)
```
GET /api/v1/questions/my-questions?pageNum=1&pageSize=10&isAnswered=1
```

### 6.7 获取待回答问题列表(教师)
```
GET /api/v1/questions/pending?pageNum=1&pageSize=10&courseId=1
```
**说明:** 获取向当前教师提问且未回答的问题列表

### 6.8 回答问题(教师)
```
POST /api/v1/questions/{questionId}/answers
```
**请求参数:** `multipart/form-data`
- `answerContent`: 回答内容
- `images`: 图片文件数组(可选,最多5张)

**响应:**
```json
{
    "code": 200,
    "message": "回答成功",
    "data": {
        "answerId": 1,
        "answerContent": "回答内容..."
    }
}
```

### 6.9 更新回答(教师)
```
PUT /api/v1/answers/{answerId}
```
**请求参数:**
```json
{
    "answerContent": "修改后的回答内容"
}
```

### 6.10 删除回答(教师)
```
DELETE /api/v1/answers/{answerId}
```

### 6.11 获取我的回答列表(教师)
```
GET /api/v1/answers/my-answers?pageNum=1&pageSize=10
```

### 6.12 点赞回答
```
POST /api/v1/answers/{answerId}/like
```

### 6.13 取消点赞
```
DELETE /api/v1/answers/{answerId}/like
```

---

## 七、通知管理接口

### 7.1 获取通知列表(分页)
```
GET /api/v1/notifications?pageNum=1&pageSize=10&isRead=0&notificationType=ANSWER_REPLY
```
**查询参数:**
- `isRead`: 是否已读(0/1,可选)
- `notificationType`: 通知类型(可选)

**响应:**
```json
{
    "code": 200,
    "data": {
        "records": [
            {
                "notificationId": 1,
                "notificationType": "ANSWER_REPLY",
                "title": "您的问题有新回答",
                "content": "李老师回答了您的问题...",
                "relatedId": 1,
                "relatedType": "QUESTION",
                "isRead": 0,
                "createTime": "2024-01-01 10:00:00"
            }
        ],
        "total": 20,
        "pageNum": 1,
        "pageSize": 10
    }
}
```

### 7.2 获取未读通知数量
```
GET /api/v1/notifications/unread-count
```
**响应:**
```json
{
    "code": 200,
    "data": {
        "count": 5
    }
}
```

### 7.3 标记单条通知已读
```
PUT /api/v1/notifications/{notificationId}/read
```

### 7.4 标记全部已读
```
PUT /api/v1/notifications/read-all
```

---

## 八、管理员专用接口

### 8.1 教师管理

#### 8.1.1 获取教师列表(分页)
```
GET /api/v1/admin/teachers?pageNum=1&pageSize=10&keyword=李&jobTitle=教授
```

#### 8.1.2 添加教师
```
POST /api/v1/admin/teachers
```
**请求参数:**
```json
{
    "username": "teacher01",
    "password": "123456",
    "realName": "李老师",
    "email": "teacher01@example.com",
    "phone": "13800138000",
    "jobTitle": "教授",
    "profile": "个人简介"
}
```

#### 8.1.3 更新教师信息
```
PUT /api/v1/admin/teachers/{teacherId}
```

#### 8.1.4 删除教师
```
DELETE /api/v1/admin/teachers/{teacherId}
```

#### 8.1.5 启用/禁用教师账号
```
PUT /api/v1/admin/teachers/{teacherId}/status
```
**请求参数:**
```json
{
    "status": 1
}
```

### 8.2 学生管理

#### 8.2.1 获取学生列表(分页)
```
GET /api/v1/admin/students?pageNum=1&pageSize=10&keyword=张&college=计算机学院
```

#### 8.2.2 获取学生详情
```
GET /api/v1/admin/students/{studentId}
```

#### 8.2.3 启用/禁用学生账号
```
PUT /api/v1/admin/students/{studentId}/status
```

#### 8.2.4 删除学生
```
DELETE /api/v1/admin/students/{studentId}
```

### 8.3 资源管理

#### 8.3.1 获取所有资源(分页,含审核)
```
GET /api/v1/admin/resources?pageNum=1&pageSize=10&status=1
```

#### 8.3.2 删除资源(强制)
```
DELETE /api/v1/admin/resources/{resourceId}
```
**请求参数:**
```json
{
    "reason": "违规原因说明"
}
```

### 8.4 问答管理

#### 8.4.1 获取所有问答
```
GET /api/v1/admin/questions?pageNum=1&pageSize=10
```

#### 8.4.2 删除问题
```
DELETE /api/v1/admin/questions/{questionId}
```

#### 8.4.3 删除回答
```
DELETE /api/v1/admin/answers/{answerId}
```

### 8.5 系统统计

#### 8.5.1 获取系统概览数据
```
GET /api/v1/admin/statistics/overview
```
**响应:**
```json
{
    "code": 200,
    "data": {
        "totalUsers": 1000,
        "totalStudents": 850,
        "totalTeachers": 50,
        "totalCourses": 30,
        "totalResources": 500,
        "totalQuestions": 300,
        "totalAnswers": 450
    }
}
```

#### 8.5.2 获取用户增长趋势
```
GET /api/v1/admin/statistics/user-growth?days=30
```

---

## 九、系统配置接口

### 9.1 获取系统配置
```
GET /api/v1/system/config?configKey=max_file_size
```

### 9.2 更新系统配置(管理员)
```
PUT /api/v1/system/config
```
**请求参数:**
```json
{
    "configKey": "max_file_size",
    "configValue": "100"
}
```

---

## 十、文件上传接口

### 10.1 通用文件上传
```
POST /api/v1/upload/file
```
**请求参数:** `multipart/form-data`
- `file`: 文件
- `type`: 文件类型(avatar/resource/question/answer)

**响应:**
```json
{
    "code": 200,
    "message": "上传成功",
    "data": {
        "fileName": "xxx.pdf",
        "fileUrl": "https://...",
        "fileSize": 1024000
    }
}
```

### 10.2 批量上传图片
```
POST /api/v1/upload/images
```
**请求参数:** `multipart/form-data`
- `files`: 图片文件数组

**响应:**
```json
{
    "code": 200,
    "data": {
        "urls": ["https://...", "https://..."]
    }
}
```

---

## 十一、搜索接口

### 11.1 全局搜索
```
GET /api/v1/search?keyword=Java&type=all
```
**查询参数:**
- `keyword`: 搜索关键字
- `type`: 搜索类型(all/course/resource/question)
- `pageNum`: 页码
- `pageSize`: 每页条数

**响应:**
```json
{
    "code": 200,
    "data": {
        "courses": [],
        "resources": [],
        "questions": []
    }
}
```

---

## 十二、HTTP状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 204 | 删除成功(无返回内容) |
| 400 | 请求参数错误 |
| 401 | 未登录或Token失效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 409 | 资源冲突(如用户名已存在) |
| 500 | 服务器内部错误 |

---

## 十三、错误响应格式

```json
{
    "code": 400,
    "message": "参数错误: 用户名不能为空",
    "data": null,
    "timestamp": 1700000000000
}
```

---

## 十四、接口权限说明

| 接口前缀 | 访问权限 |
|---------|---------|
| `/api/v1/auth/**` | 公开访问 |
| `/api/v1/users/**` | 需登录 |
| `/api/v1/courses/**` | 需登录 |
| `/api/v1/resources/**` | 需登录 |
| `/api/v1/questions/**` | 需登录 |
| `/api/v1/admin/**` | 仅管理员 |
| `/api/v1/teachers/**` | 仅教师 |

---

## 十五、接口调用示例

### 15.1 使用Axios调用示例

```javascript
// 登录
const login = async (username, password) => {
    const response = await axios.post('/api/v1/auth/login', {
        username,
        password
    });
    // 保存token
    localStorage.setItem('token', response.data.data.token);
    return response.data;
};

// 获取课程列表
const getCourses = async (pageNum = 1, pageSize = 10) => {
    const response = await axios.get('/api/v1/courses', {
        params: { pageNum, pageSize },
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    });
    return response.data;
};

// 上传资源
const uploadResource = async (file, resourceTitle, courseId) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('resourceTitle', resourceTitle);
    formData.append('courseId', courseId);
    
    const response = await axios.post('/api/v1/resources', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    });
    return response.data;
};
```

---

## 十六、接口优化建议

### 16.1 已实现的优化
1. ✅ 统一的RESTful风格
2. ✅ 合理的资源嵌套(不超过2层)
3. ✅ 分页查询支持
4. ✅ 灵活的搜索过滤
5. ✅ 统一的响应格式
6. ✅ 明确的权限控制

### 16.2 未来可扩展功能
- WebSocket实时通知
- 文件分片上传
- 资源评论功能
- 问题关注功能
- 数据导出功能

---

## 十七、接口总结

### 按模块统计

| 模块 | 接口数量 | 说明 |
|------|---------|------|
| 认证授权 | 5 | 注册、登录、登出、刷新Token、修改密码 |
| 用户管理 | 3 | 获取信息、更新信息、上传头像 |
| 课程管理 | 9 | CRUD、教师分配、学生加入 |
| 资源管理 | 11 | CRUD、上传下载、收藏、置顶 |
| 问答管理 | 13 | 问题CRUD、回答CRUD、点赞 |
| 通知管理 | 4 | 列表、未读数、标记已读 |
| 管理员接口 | 13 | 教师管理、学生管理、内容管理、统计 |
| 其他 | 5 | 文件上传、搜索、系统配置 |
| **总计** | **63** | 完整覆盖所有业务需求 |

---

## 附录: 接口快速索引

### A. 认证相关
- POST `/auth/register` - 注册
- POST `/auth/login` - 登录
- POST `/auth/logout` - 登出

### B. 课程相关
- GET `/courses` - 课程列表
- GET `/courses/{id}` - 课程详情
- POST `/courses/join` - 加入课程

### C. 资源相关
- GET `/resources` - 资源列表
- POST `/resources` - 上传资源
- GET `/resources/{id}/download` - 下载资源

### D. 问答相关
- GET `/questions` - 问题列表
- POST `/questions` - 提问
- POST `/questions/{id}/answers` - 回答

### E. 管理员相关
- GET `/admin/teachers` - 教师管理
- GET `/admin/statistics/overview` - 系统统计
