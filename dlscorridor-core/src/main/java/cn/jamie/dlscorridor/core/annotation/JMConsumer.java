package cn.jamie.dlscorridor.core.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface JMConsumer {
    String service();
    String version() default "";
    String group() default "DEFAULT_GROUP";
}
