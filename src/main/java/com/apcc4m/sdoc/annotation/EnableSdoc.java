package com.apcc4m.sdoc.annotation;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;


@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@ComponentScan(basePackages = { "com.apcc4m.sdoc"})
public @interface EnableSdoc {
}
