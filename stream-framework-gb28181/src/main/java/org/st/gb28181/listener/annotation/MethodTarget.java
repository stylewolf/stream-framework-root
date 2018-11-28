package org.st.gb28181.listener.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodTarget {
    /**
     * 命令名称
     * @return
     */
    String name() default "";

    /**
     * 命令执行成功后顺序
     * @return
     */
    int cSeq() default -1;
}
