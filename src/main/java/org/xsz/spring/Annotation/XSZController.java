package org.xsz.spring.Annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XSZController {
    String value() default "";
}
