package org.gracefulshutdown.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.gracefulshutdown.hook.TransactionMonitorHook;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GracefulShutdownAspect {

    @Before(value = "org.gracefulshutdown.annotation.ShutdownPointCut.executeShutdown()")
    public void shutdownBefore(JoinPoint joinPoint) {

    }

    @AfterReturning(value = "org.gracefulshutdown.annotation.ShutdownPointCut.executeShutdown()", returning = "obj")
    public void shutdownAfterReturning(JoinPoint joinPoint, Object obj) {
        if (TransactionMonitorHook.hookCondition != null) {
            if (TransactionMonitorHook.count.get() == 0) {
                TransactionMonitorHook.hookCondition.signalAll();
            }
        }
    }
}
