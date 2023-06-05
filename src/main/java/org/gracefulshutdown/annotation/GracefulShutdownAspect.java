package org.gracefulshutdown.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.gracefulshutdown.hook.ControllerMonitorHook;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class GracefulShutdownAspect {
    /**
     * 当前处于活跃状态的请求
     */
    private static final AtomicInteger REMAINING_REQUEST_NUM = new AtomicInteger(0);

    @Before(value = "org.gracefulshutdown.annotation.ShutdownPointCut.executeShutdown()")
    public void shutdownBefore(JoinPoint joinPoint) {
        REMAINING_REQUEST_NUM.getAndIncrement();
        ControllerMonitorHook.count.getAndIncrement();
    }

    @After(value = "org.gracefulshutdown.annotation.ShutdownPointCut.executeShutdown()")
    public void shutdownAfter(JoinPoint joinPoint) {
        REMAINING_REQUEST_NUM.decrementAndGet();
        ControllerMonitorHook.count.decrementAndGet();
        if (ControllerMonitorHook.hookCondition != null) {
            if (ControllerMonitorHook.count.get() == 0) {
                ControllerMonitorHook.hookCondition.signal();
            }
        }
    }

    public static AtomicInteger getActiveRequest() {
        return REMAINING_REQUEST_NUM;
    }
}
