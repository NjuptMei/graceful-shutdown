package org.gracefulshutdown.hook;

import org.gracefulshutdown.business.AsyncThreadMonitor;
import org.gracefulshutdown.thread.util.ExecutorsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AsncThreadMonitorHook extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(AsncThreadMonitorHook.class);

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
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 异步线程AsyncThreadMonitorHook执行停机等待, 开始时间为：{}",
                    sdf.format(calendar.getTime()));
            ExecutorsUtils.singleThreadPool().submit(new AsyncThreadMonitor(hookLock, hookCondition));
            hookCondition.await(120, TimeUnit.SECONDS);
            calendar.setTimeInMillis(System.currentTimeMillis());
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 异步线程AsyncThreadMonitorHook完成停机等待, 结束时间为：{}",
                    sdf.format(calendar.getTime()));
        } catch (Exception ee) {
            logger.error("AsyncThreadMonitorHook异步停机执行异常", ee);
        } finally {
            hookLock.unlock();
        }
    }
}
