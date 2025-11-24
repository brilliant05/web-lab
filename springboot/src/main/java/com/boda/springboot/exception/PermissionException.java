package com.boda.springboot.exception;

public class PermissionException extends RuntimeException {

    public PermissionException(String msg) {
        super(msg);
    }
}
