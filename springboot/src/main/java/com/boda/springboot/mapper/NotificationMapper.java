package com.boda.springboot.mapper;

import com.boda.springboot.entity.Notification;
import com.boda.springboot.vo.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知Mapper
 */
@Mapper
public interface NotificationMapper {

    /**
     * 保存通知
     */
    void save(Notification notification);

    /**
     * 根据ID查询
     */
    Notification selectById(Long notificationId);

    /**
     * 分页查询通知列表
     */
    List<NotificationVO> selectPageList(@Param("userId") Long userId,
                                        @Param("isRead") Integer isRead,
                                        @Param("notificationType") String notificationType);

    /**
     * 查询未读通知数量
     */
    Integer countUnread(@Param("userId") Long userId);

    /**
     * 标记通知已读
     */
    void markAsRead(@Param("notificationId") Long notificationId);

    /**
     * 标记所有通知已读
     */
    void markAllAsRead(@Param("userId") Long userId);

    /**
     * 删除过期通知
     */
    void deleteExpired(@Param("days") Integer days);
}

