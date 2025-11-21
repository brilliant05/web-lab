-- 数据库结构脚本（基于 DataDesign.md）
-- 统一字符集、引擎设置
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS answer_attachment;
DROP TABLE IF EXISTS answer;
DROP TABLE IF EXISTS question_attachment;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS resource_log;
DROP TABLE IF EXISTS learning_resource;
DROP TABLE IF EXISTS user_course;
DROP TABLE IF EXISTS teacher_course;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '登录账号',
    password VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL COMMENT '角色类型',
    title VARCHAR(50) COMMENT '职称(教师)',
    intro TEXT COMMENT '教师简介',
    status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE' COMMENT '账号状态',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

CREATE TABLE course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(50) UNIQUE COMMENT '课程编码',
    college VARCHAR(100) COMMENT '开课学院',
    description TEXT COMMENT '课程简介',
    cover_image VARCHAR(255) COMMENT '封面图',
    status ENUM('PUBLISHED', 'DRAFT', 'ARCHIVED') DEFAULT 'PUBLISHED' COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course_name (course_name),
    INDEX idx_college (college)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息';

CREATE TABLE teacher_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    teacher_id BIGINT NOT NULL COMMENT '教师用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    main_teacher TINYINT DEFAULT 0 COMMENT '是否主讲',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_teacher_course (teacher_id, course_id),
    INDEX idx_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师授课关联';

CREATE TABLE user_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID（学生/教师）',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    user_role ENUM('TEACHER', 'STUDENT') NOT NULL COMMENT '角色类型',
    class_name VARCHAR(100) COMMENT '班级或教学班',
    enroll_type ENUM('MANDATORY', 'ELECTIVE', 'PRACTICE') DEFAULT 'MANDATORY' COMMENT '修读类型',
    status ENUM('ACTIVE', 'INACTIVE', 'QUIT') DEFAULT 'ACTIVE' COMMENT '选课状态',
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    quit_time DATETIME COMMENT '退出时间',
    UNIQUE KEY uk_user_course (user_id, course_id, user_role),
    INDEX idx_course_role (course_id, user_role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户课程关联';

CREATE TABLE learning_resource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    course_id BIGINT NOT NULL COMMENT '所属课程ID',
    uploader_id BIGINT NOT NULL COMMENT '上传者ID',
    uploader_role ENUM('TEACHER', 'STUDENT') NOT NULL COMMENT '上传者角色',
    title VARCHAR(200) NOT NULL COMMENT '资源标题',
    description TEXT COMMENT '资源简介',
    file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_type VARCHAR(50) COMMENT '文件类型',
    file_url VARCHAR(500) NOT NULL COMMENT '存储路径/URL',
    file_size BIGINT COMMENT '文件大小(字节)',
    visibility_scope ENUM('ALL', 'COURSE', 'CLASS') DEFAULT 'COURSE' COMMENT '可见范围',
    class_name VARCHAR(100) COMMENT '限定班级',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    status ENUM('PUBLISHED', 'HIDDEN', 'DELETED') DEFAULT 'PUBLISHED' COMMENT '状态',
    audit_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'APPROVED' COMMENT '审核状态',
    audit_remark VARCHAR(255) COMMENT '审核说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course_id (course_id),
    INDEX idx_uploader (uploader_id, uploader_role),
    INDEX idx_visibility (visibility_scope, class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资源';

CREATE TABLE resource_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    operator_id BIGINT NOT NULL COMMENT '操作人(管理员)',
    action ENUM('UPDATE', 'DELETE') NOT NULL COMMENT '操作类型',
    reason VARCHAR(255) COMMENT '操作原因',
    snapshot JSON COMMENT '变更前关键字段快照',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_resource (resource_id),
    INDEX idx_operator (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源操作日志';

CREATE TABLE question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    course_id BIGINT NOT NULL COMMENT '所属课程',
    student_id BIGINT NOT NULL COMMENT '提问学生ID',
    title VARCHAR(200) NOT NULL COMMENT '问题标题',
    content TEXT NOT NULL COMMENT '问题内容',
    tag VARCHAR(100) COMMENT '标签/学科',
    has_attachment TINYINT DEFAULT 0 COMMENT '是否有附件',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    answer_count INT DEFAULT 0 COMMENT '回答次数',
    status ENUM('PUBLISHED', 'CLOSED', 'DELETED') DEFAULT 'PUBLISHED' COMMENT '问题状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course (course_id),
    INDEX idx_student (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题';

CREATE TABLE question_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    question_id BIGINT NOT NULL COMMENT '问题ID',
    file_url VARCHAR(500) NOT NULL COMMENT '附件路径',
    file_type VARCHAR(50) COMMENT '附件类型',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    INDEX idx_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题附件';

CREATE TABLE answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    question_id BIGINT NOT NULL COMMENT '问题ID',
    teacher_id BIGINT NOT NULL COMMENT '回答教师ID',
    content TEXT NOT NULL COMMENT '回答内容',
    has_attachment TINYINT DEFAULT 0 COMMENT '是否有附件',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status ENUM('PUBLISHED', 'EDITED', 'DELETED') DEFAULT 'PUBLISHED' COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '回答时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_question (question_id),
    INDEX idx_teacher (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答';

CREATE TABLE answer_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    answer_id BIGINT NOT NULL COMMENT '回答ID',
    file_url VARCHAR(500) NOT NULL COMMENT '附件路径',
    file_type VARCHAR(50) COMMENT '附件类型',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    INDEX idx_answer (answer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答附件';

CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    type ENUM('ANSWER', 'SYSTEM') NOT NULL COMMENT '通知类型',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    related_id BIGINT COMMENT '关联ID',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_time DATETIME COMMENT '已读时间',
    INDEX idx_user (user_id),
    INDEX idx_type (type, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息';

CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    module ENUM('COURSE', 'TEACHER', 'RESOURCE', 'QUESTION', 'ANSWER') NOT NULL COMMENT '模块',
    action VARCHAR(50) NOT NULL COMMENT '动作',
    target_id BIGINT NOT NULL COMMENT '实体ID',
    detail JSON COMMENT '变更详情',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_operator (operator_id),
    INDEX idx_module (module, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台操作日志';

SET FOREIGN_KEY_CHECKS = 1;

