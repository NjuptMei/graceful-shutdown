package org.gracefulshutdown.hook;

import org.gracefulshutdown.common.SpringBeanUils;
import org.gracefulshutdown.thread.async.DataChangeCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionMonitorHook extends Thread {

    public static AtomicInteger count  = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(TransactionMonitorHook.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static ReentrantLock hookLock = new ReentrantLock();

    public static Condition hookCondition;

    @Override
    public void run() {
        hookCondition = hookLock.newCondition();
        ReentrantLock lock = DataChangeCondition.lock;
        Condition condition = DataChangeCondition.condition;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        hookLock.lock();
        try {
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 当前应用执行停机, 停机开始时间为：{}", sdf.format(calendar.getTime()));
            logger.info("停机hook挂起，等待业务数据继续运行 >>>>>>>>>>>> DataChangeCondition 业务继续处理");
            lock.lock();
            try {
                condition.signal();
            } catch (Exception ee) {
                logger.error("唤醒业务子线程失败");
            } finally {
                lock.unlock();
            }
            hookCondition.await(120, TimeUnit.SECONDS);
            calendar.setTimeInMillis(System.currentTimeMillis());
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 当前应用执行停机, 停机结束时间为：{}", sdf.format(calendar.getTime()));
        } catch (Exception ee) {
            logger.error("执行停机过程异常，业务未正常结束", ee);
        } finally {
            hookLock.unlock();
        }

    }
}
