package com.hao.util;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

//@EnableAspectJAutoProxy(proxyTargetClass = true)
@Aspect
@Component
public class ExceptionAdvisor {
    @AfterThrowing(pointcut = "execution(* com.hao..* (..))", throwing = "ex")
    public void errorInterceptor(Throwable ex) {
        System.err.println("========Error Message Interceptor started");
        // DO SOMETHING HERE WITH EX
        System.err.println( ex.getCause().getMessage());
        System.err.println("------------Error Message Interceptor finished.");
    }
}
