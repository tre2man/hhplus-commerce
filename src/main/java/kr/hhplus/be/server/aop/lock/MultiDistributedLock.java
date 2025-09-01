package kr.hhplus.be.server.aop.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiDistributedLock {
    String keyPrefix() default "";
    String keyExpression() default "";
    long waitTime() default 3L;
    long leaseTime() default 10L;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
