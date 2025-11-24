package com.boda.springboot.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // 值为null的字段不序列化
public class Result<T> {
    
    private Integer code;       // 状态码: 200-成功, 400-参数错误, 401-未认证, 403-无权限, 500-服务器错误
    private String message;     // 消息
    private T data;             // 数据
    private Long timestamp;     // 时间戳
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // ========== 成功响应 ==========
    
    /**
     * 成功(无数据)
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }
    
    /**
     * 成功(有数据)
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    /**
     * 成功(自定义消息)
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(200, message, null);
    }
    
    /**
     * 成功(自定义消息和数据)
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    // ========== 失败响应 ==========
    
    /**
     * 失败(默认400)
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(400, message, null);
    }
    
    /**
     * 失败(自定义状态码)
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    
    /**
     * 失败(自定义状态码和数据)
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }
    
    // ========== 常用HTTP状态码 ==========
    
    /**
     * 参数错误 400
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }
    
    /**
     * 未认证 401
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }
    
    /**
     * 无权限 403
     */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message, null);
    }
    
    /**
     * 资源不存在 404
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null);
    }
    
    /**
     * 服务器错误 500
     */
    public static <T> Result<T> serverError(String message) {
        return new Result<>(500, message, null);
    }
}
