package org.gracefulshutdown.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class ShutdownPointCut {

    @Pointcut("execution(* org.gracefulshutdown.controller.*.*(..))")
    public void executeShutdown() {}

}
