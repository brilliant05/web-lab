package com.boda.springboot.handler;


import com.boda.springboot.common.Result;
import com.boda.springboot.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(Exception.class)
    public Result<?> globalExceptionHandler(Exception ex){
        log.error("系统异常: {}", ex.getMessage(), ex);
        return Result.serverError("服务器内部错误");
    }
}
