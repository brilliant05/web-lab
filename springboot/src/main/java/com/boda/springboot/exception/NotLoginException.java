package com.boda.springboot.exception;

public class NotLoginException extends RuntimeException {

    public NotLoginException(String msg) {
        super(msg);
    }
}
