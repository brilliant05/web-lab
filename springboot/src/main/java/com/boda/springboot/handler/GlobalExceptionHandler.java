package com.boda.springboot.handler;


import com.boda.springboot.common.Result;
import com.boda.springboot.exception.NotLoginException;
import com.boda.springboot.exception.PermissionException;
import com.boda.springboot.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Result<?> serviceExceptionHandler(ServiceException ex){
        log.error("业务异常: code={}, message={}", ex.getCode(), ex.getMessage());
        try {
            Integer code = Integer.parseInt(ex.getCode());
            return Result.error(code, ex.getMessage());
        } catch (NumberFormatException e) {
            return Result.error(ex.getMessage());
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<?> runtimeExceptionHandler(RuntimeException ex){
        log.error("运行时异常: {}", ex.getMessage(), ex);
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> globalExceptionHandler(Exception ex){
        log.error("系统异常: {}", ex.getMessage(), ex);
        return Result.serverError("服务器内部错误");
    }

    @ExceptionHandler(PermissionException.class)
    public Result<?> permissionExceptionHandler(PermissionException ex) {
        log.error("权限异常: {}", ex.getMessage());
        return Result.forbidden(ex.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public Result<?> notLoginExceptionHandler(NotLoginException ex) {
        log.error("未登录异常: {}", ex.getMessage());
        return Result.unauthorized(ex.getMessage());
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> maxUploadSizeExceptionHandler(MaxUploadSizeExceededException ex) {
        log.error("文件上传大小超限: {}", ex.getMessage());
        return Result.error("文件大小超过限制，单个文件最大支持 50MB");
    }
}
