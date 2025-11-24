package com.boda.springboot.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final String code;

    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public  ServiceException(String msg) {
        super(msg);
        this.code = "500";
    }

}
