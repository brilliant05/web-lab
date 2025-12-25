package com.boda.springboot.mapper;

import com.boda.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    @Select("SELECT * FROM user WHERE username = #{username} and is_deleted = 0")
    User selectByUsername(String username);

    /**
     * 根据学号查询用户信息
     * @param studentId
     * @return
     */
    @Select("SELECT * FROM user WHERE student_id = #{studentId} and is_deleted = 0")
    User selectByStudentId(String studentId);

    /**
     * 根据用户ID查询用户信息
     * @param userId
     * @return
     */
    @Select("SELECT * FROM user WHERE user_id = #{userId} and is_deleted = 0")
    User selectById(Long userId);
    /**
     * 根据id查询学生信息
     * @param userId
     * @return
     */
    @Select("SELECT username,real_name,email,phone,avatar_url,student_id,college,profile,role FROM user WHERE user_id = #{userId} AND role = 'STUDENT' and is_deleted = 0")
    User selectStudentById(Long userId);
    /**
     * 根据id查询教师信息
     * @param userId
     * @return
     */    @Select("SELECT username,real_name,email,phone,avatar_url,job_title,college,profile,role FROM user WHERE user_id = #{userId} AND role = 'TEACHER' and is_deleted = 0")
    User selectTeacherById(Long userId);
    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(String email);

    /**
     * 保存用户信息
     * @param user
     */
    void save(User user);

    /**
     * 更新用户密码
     * @param userId
     * @param newPassword
     */
    @Update("UPDATE user SET password = #{newPassword}, update_time = NOW() WHERE user_id = #{userId}")
    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
    /**
     * 更新用户信息
     * @param user
     */
    void update(User user);

    /**
     * 统计学生总数
     * @return 学生总数
     */
    @Select("SELECT COUNT(*) FROM user WHERE role = 'STUDENT' AND is_deleted = 0")
    Integer countStudents();

    /**
     * 统计教师总数
     * @return 教师总数
     */
    @Select("SELECT COUNT(*) FROM user WHERE role = 'TEACHER' AND is_deleted = 0")
    Integer countTeachers();

    /**
     * 查询所有用户的ID（用于批量发送通知）
     * @return 用户ID列表
     */
    @Select("SELECT user_id FROM user WHERE is_deleted = 0 AND status = 1")
    List<Long> selectAllUserIds();

}
