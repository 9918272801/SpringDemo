package org.xsz.spring.Annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XSZAutowired {
    String value() default "";
}
