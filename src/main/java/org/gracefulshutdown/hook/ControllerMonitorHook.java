/*
package org.gracefulshutdown.hook;

import org.gracefulshutdown.aspect.GracefulShutdownAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ControllerMonitorHook extends Thread {

    public static AtomicInteger count  = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(ControllerMonitorHook.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static ReentrantLock hookLock = new ReentrantLock();

    public static Condition hookCondition;

    @Override
    public void run() {
        hookCondition = hookLock.newCondition();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        hookLock.lock();
        try {
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 当前应用执行停机, 停机开始时间为：{}", sdf.format(calendar.getTime()));
            // 判断是否需要进行等待
            if (isAwait()) {
                logger.info("当前需进行等待");
                hookCondition.await(120, TimeUnit.SECONDS);
            }
            calendar.setTimeInMillis(System.currentTimeMillis());
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 当前应用执行停机, 停机结束时间为：{}", sdf.format(calendar.getTime()));
        } catch (Exception ee) {
            logger.error("执行停机过程异常，业务未正常结束", ee);
        } finally {
            hookLock.unlock();
        }

    }

    */
/**
     * 判断当前等待条件，在这里添加
     *
     * @return
     *//*

    private boolean isAwait() {
        // 1. controller停机策略，当request请求结束后执行停机
        return GracefulShutdownAspect.getActiveRequest().get() != 0;
    }
}
*/
