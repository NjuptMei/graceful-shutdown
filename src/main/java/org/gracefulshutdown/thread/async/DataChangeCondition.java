package org.gracefulshutdown.thread.async;

import org.gracefulshutdown.hook.ControllerMonitorHook;
import org.gracefulshutdown.thread.util.ExecutorsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class DataChangeCondition {

    public static ReentrantLock lock = new ReentrantLock();

    public static Condition condition;

    private static final AtomicInteger count = new AtomicInteger(0);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Logger logger = LoggerFactory.getLogger(DataChangeCondition.class);

    @PostConstruct
    public void executeDataChange() {
        condition = lock.newCondition();
        ExecutorsUtils.singleThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    condition.await();
                    logger.info("当前执行停机，业务正常流转");
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  数据被唤醒");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    ExecutorsUtils.singleThreadPool().submit(new Runnable() {
                        @Override
                        public void run() {
                            ReentrantLock hookLock = ControllerMonitorHook.hookLock;
                            Condition hockCondition = ControllerMonitorHook.hookCondition;
                            hookLock.lock();
                            try {
                                while (count.get() <= 60) {
                                    if (count.get() % 10 == 0) {
                                        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  当前count为：{}，执行时间：{}", count, sdf.format(calendar.getTime()));
                                    }
                                    count.getAndIncrement();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        logger.error("线程休眠异常");
                                        throw new RuntimeException(e);
                                    }
                                }
                                hockCondition.signal();
                            } catch (Exception ee) {
                                logger.error("业务执行异常，继续执行停机操作", ee);
                            } finally {
                                hookLock.unlock();
                            }
                        }
                    });
                } catch (Exception ee) {
                    logger.error("执行异常", ee);
                } finally {
                    lock.unlock();
                }
            }
        });
    }
}
