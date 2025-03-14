package com.gray.auth.config.ui;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

//@Component
//@Aspect
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Define a pointcut for all methods in ui and its sub-packages
    @Pointcut("execution(* com.gray.auth.config.ui..*.*(..))")
    public void applicationPackagePointcut() {
    }

    @Before("applicationPackagePointcut()")
    public void logMethodCall(JoinPoint joinPoint) {
        logger.info("Entering method: {} in class: {} with arguments: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getSimpleName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "applicationPackagePointcut()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        logger.info("Exiting method: {} in class: {} with result: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getSimpleName(),
                result);
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in method: {} in class: {} with message: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getSimpleName(),
                exception.getMessage(), exception);
    }

    @Around("applicationPackagePointcut()")
    public Object logPerformanceMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        logger.info("Execution time of method {} in class {}: {} ms",
                joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getSimpleName(),
                elapsedTime);
        return result;
    }
}
