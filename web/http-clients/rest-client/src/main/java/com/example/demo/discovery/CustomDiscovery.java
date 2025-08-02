package com.example.demo.discovery;

import java.lang.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Documented
public @interface CustomDiscovery {}
