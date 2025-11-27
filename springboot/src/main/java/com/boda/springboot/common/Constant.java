package com.boda.springboot.common;

import io.swagger.v3.oas.models.security.SecurityScheme;

public class Constant {
    public final static String ROLE_STUDENT = "STUDENT";
    public final static String ROLE_TEACHER = "TEACHER";
    public final static String ROLE_ADMIN = "ADMIN";
    public final static String RESOURCE_PUBLIC = "PUBLIC";
    public final static String RESOURCE_COURSE_ONLY = "COURSE_ONLY";
    public final static String DEFAULT_PASSWORD = "123456";
    public final static Integer STATUS_ENABLE = 1;
    public final static Integer STATUS_DISABLE = 0;
    public final static Integer DELETE_IS = 1;
    public final static Integer DELETE_NO = 0;
    public final static Integer COURSE_OPEN = 1;
    public final static Integer COURSE_CLOSE = 0;

}
