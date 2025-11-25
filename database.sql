-- =====================================================
-- 大学生线上学习资源共享与问答系统 - 数据库初始化脚本
-- 数据库版本: MySQL 8.0+
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- 创建日期: 2025-01-23
-- 说明: 使用逻辑外键而非物理外键，提升性能和灵活性
-- =====================================================

-- =====================================================
-- 1. 创建数据库
-- =====================================================
DROP DATABASE IF EXISTS `learning_system`;
CREATE DATABASE `learning_system` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `learning_system`;

-- =====================================================
-- 2. 创建数据表
-- =====================================================

-- -----------------------------------------------------
-- 2.1 用户表 (user)
-- 存储系统所有用户信息(管理员、教师、学生)
-- -----------------------------------------------------
CREATE TABLE `user` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID,主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名,唯一',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `real_name` VARCHAR(50)   COMMENT '真实姓名',
  `email` VARCHAR(100)  COMMENT '邮箱,唯一',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `role` VARCHAR(20) NOT NULL COMMENT '角色: ADMIN-管理员, TEACHER-教师, STUDENT-学生',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态: 0-禁用, 1-正常',
  `student_id` VARCHAR(30) DEFAULT NULL COMMENT '学号(仅学生有)',
  `college` VARCHAR(100) DEFAULT NULL COMMENT '所属学院(学生)',
  `job_title` VARCHAR(50) DEFAULT NULL COMMENT '职称(教师): 助教、讲师、副教授、教授',
  `profile` TEXT DEFAULT NULL COMMENT '个人简介',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------------------
-- 2.2 课程表 (course)
-- 存储课程基本信息
-- -----------------------------------------------------
CREATE TABLE `course` (
  `course_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID,主键',
  `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `course_code` VARCHAR(50) DEFAULT NULL COMMENT '课程编号',
  `description` TEXT DEFAULT NULL COMMENT '课程描述',
  `college` VARCHAR(100) DEFAULT NULL COMMENT '开课学院',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '课程封面图片URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '课程状态: 0-关闭, 1-开放',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`course_id`),
  UNIQUE KEY `uk_course_code` (`course_code`),
  KEY `idx_college` (`college`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- -----------------------------------------------------
-- 2.3 教师授课关系表 (teacher_course)
-- 记录教师与课程的多对多关系，每个教师可为自己的课程生成邀请码
-- 使用逻辑外键，通过应用层保证数据一致性
-- -----------------------------------------------------
CREATE TABLE `teacher_course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID,逻辑外键关联user表',
  `course_id` BIGINT NOT NULL COMMENT '课程ID,逻辑外键关联course表',
  `invite_code` VARCHAR(20) DEFAULT NULL COMMENT '该教师为该课程生成的邀请码',
  `invite_code_expire_time` DATETIME DEFAULT NULL COMMENT '邀请码过期时间',
  `class_name` VARCHAR(100) DEFAULT NULL COMMENT '班级名称(可选),如"2024级1班"',
  `assign_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_course` (`teacher_id`, `course_id`),
  UNIQUE KEY `uk_invite_code` (`invite_code`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师授课关系表';

-- -----------------------------------------------------
-- 2.4 学生选课关系表 (student_course)
-- 记录学生与课程的多对多关系，明确学生跟随哪个教师上课
-- 使用逻辑外键，通过应用层保证数据一致性
-- -----------------------------------------------------
CREATE TABLE `student_course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID,逻辑外键关联user表',
  `course_id` BIGINT NOT NULL COMMENT '课程ID,逻辑外键关联course表',
  `teacher_id` BIGINT NOT NULL COMMENT '授课教师ID,逻辑外键关联user表',
  `join_method` VARCHAR(20) NOT NULL COMMENT '加入方式: ADMIN_ASSIGN-管理员分配, INVITE_CODE-邀请码加入',
  `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_course_teacher` (`student_id`, `course_id`, `teacher_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_join_method` (`join_method`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生选课关系表';

-- -----------------------------------------------------
-- 2.5 学习资源表 (resource)
-- 存储学习资料信息
-- 使用逻辑外键，通过应用层保证数据一致性
-- -----------------------------------------------------
CREATE TABLE `resource` (
  `resource_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资源ID,主键',
  `resource_title` VARCHAR(200) NOT NULL COMMENT '资源标题',
  `description` TEXT DEFAULT NULL COMMENT '资源描述',
  `course_id` BIGINT NOT NULL COMMENT '所属课程ID,逻辑外键关联course表',
  `uploader_id` BIGINT NOT NULL COMMENT '上传者ID,逻辑外键关联user表',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名称',
  `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
  `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
  `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型: PDF, WORD, EXCEL, PPT, IMAGE, VIDEO, ZIP等',
  `visibility` VARCHAR(20) NOT NULL DEFAULT 'PUBLIC' COMMENT '可见性: PUBLIC-全部学生, COURSE_ONLY-仅本课程学生',
  `download_count` INT NOT NULL DEFAULT 0 COMMENT '下载次数',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是(仅教师可设置)',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '资源标签,逗号分隔',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-已下架, 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`resource_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_uploader_id` (`uploader_id`),
  KEY `idx_visibility` (`visibility`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_download_count` (`download_count`),
  FULLTEXT KEY `ft_title_desc` (`resource_title`, `description`) COMMENT '全文索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习资源表';

-- -----------------------------------------------------
-- 2.6 问题表 (question)
-- 存储学生向具体教师提问的信息
-- 使用逻辑外键，通过应用层保证数据一致性
-- -----------------------------------------------------
CREATE TABLE `question` (
  `question_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问题ID,主键',
  `course_id` BIGINT NOT NULL COMMENT '所属课程ID,逻辑外键关联course表',
  `student_id` BIGINT NOT NULL COMMENT '提问学生ID,逻辑外键关联user表',
  `teacher_id` BIGINT NOT NULL COMMENT '被提问的教师ID,逻辑外键关联user表',
  `question_title` VARCHAR(200) NOT NULL COMMENT '问题标题',
  `question_content` TEXT NOT NULL COMMENT '问题内容',
  `image_urls` TEXT DEFAULT NULL COMMENT '图片附件URL,多个用逗号分隔',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '问题标签,逗号分隔',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `answer_count` INT NOT NULL DEFAULT 0 COMMENT '回答数量',
  `is_answered` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已回答: 0-未回答, 1-已回答',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-已关闭, 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`question_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_is_answered` (`is_answered`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_course_teacher` (`course_id`, `teacher_id`),
  FULLTEXT KEY `ft_title_content` (`question_title`, `question_content`) COMMENT '全文索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题表';

-- -----------------------------------------------------
-- 2.7 回答表 (answer)
-- 存储教师回答信息
-- 使用逻辑外键，通过应用层保证数据一致性
-- -----------------------------------------------------
CREATE TABLE `answer` (
  `answer_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回答ID,主键',
  `question_id` BIGINT NOT NULL COMMENT '问题ID,逻辑外键关联question表',
  `teacher_id` BIGINT NOT NULL COMMENT '回答教师ID,逻辑外键关联user表',
  `answer_content` TEXT NOT NULL COMMENT '回答内容',
  `image_urls` TEXT DEFAULT NULL COMMENT '图片附件URL,多个用逗号分隔',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回答时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`answer_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回答表';

-- -----------------------------------------------------
-- 2.8 回答点赞表 (answer_like)
-- 记录用户对回答的点赞
-- 使用逻辑外键，通过应用层保证数据一致性
-- -----------------------------------------------------
CREATE TABLE `answer_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `answer_id` BIGINT NOT NULL COMMENT '回答ID,逻辑外键关联answer表',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID,逻辑外键关联user表',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_answer_user` (`answer_id`, `user_id`),
  KEY `idx_answer_id` (`answer_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回答点赞表';

-- -----------------------------------------------------
-- 2.9 通知表 (notification)
-- 存储系统通知信息
-- 使用逻辑外键，通过应用层保证数据一致性
-- -----------------------------------------------------
CREATE TABLE `notification` (
  `notification_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID,主键',
  `user_id` BIGINT NOT NULL COMMENT '接收通知的用户ID,逻辑外键关联user表',
  `notification_type` VARCHAR(50) NOT NULL COMMENT '通知类型: ANSWER_REPLY-问题被回答, RESOURCE_AUDIT-资源审核, SYSTEM-系统公告',
  `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
  `content` TEXT NOT NULL COMMENT '通知内容',
  `related_id` BIGINT DEFAULT NULL COMMENT '关联ID(如问题ID、资源ID)',
  `related_type` VARCHAR(50) DEFAULT NULL COMMENT '关联类型: QUESTION, RESOURCE, COURSE',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`notification_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- -----------------------------------------------------
-- 2.10 资源收藏表 (resource_collection)
-- 存储学生收藏的资源
-- 使用逻辑外键，通过应用层保证数据一致性
-- -----------------------------------------------------
CREATE TABLE `resource_collection` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID,逻辑外键关联user表',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID,逻辑外键关联resource表',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_resource` (`student_id`, `resource_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源收藏表';

-- -----------------------------------------------------
-- 2.11 系统配置表 (system_config)
-- 存储系统配置参数
-- -----------------------------------------------------
CREATE TABLE `system_config` (
  `config_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT NOT NULL COMMENT '配置值',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '配置描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- -----------------------------------------------------
-- 2.12 操作日志表 (operation_log)
-- 记录用户重要操作,便于审计和问题追踪
-- -----------------------------------------------------
CREATE TABLE `operation_log` (
  `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT NOT NULL COMMENT '操作用户ID',
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `operation_desc` VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
  `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法: GET, POST等',
  `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
  `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =====================================================
-- 3. 初始化系统管理员账号
-- =====================================================
-- 默认管理员账号: admin
-- 默认密码: admin123 (BCrypt加密后的值)
INSERT INTO `user` (
  `username`, 
  `password`, 
  `real_name`, 
  `email`, 
  `role`, 
  `status`
) VALUES (
  'admin',
  '$2a$10$1CAWdko5zIgj3QjGuMeWJeaghW8QoktRPYHQXQ/b5TQGfLmLby6ue',
  '系统管理员',
  'admin@example.com',
  'ADMIN',
  1
);

-- =====================================================
-- 4. 初始化系统配置
-- =====================================================
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('max_file_size', '52428800', '最大文件上传大小(字节)，默认50MB'),
('invite_code_expire_days', '30', '邀请码默认有效天数'),
('notification_keep_days', '90', '通知保留天数'),
('resource_allow_types', 'PDF,WORD,EXCEL,PPT,IMAGE,VIDEO,ZIP', '允许上传的资源类型');

-- =====================================================
-- 5. 数据库初始化完成
-- =====================================================
-- 查看所有表
SHOW TABLES;

-- 查看数据库字符集
SELECT @@character_set_database, @@collation_database;

-- 提示信息
SELECT 'Database initialization completed successfully!' AS Message;
SELECT 'Default admin account - username: admin, password: admin123' AS Info;
