package cn.jamie.dlscorridor.core.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface JMConsumer {
    String service() default "";
    String version() default "";
}
