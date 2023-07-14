package org.gracefulshutdown.business;

import org.gracefulshutdown.thread.util.ExecutorsUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

@Component
public class TestThreadPoolShutdown {
    private static final Logger logger = LoggerFactory.getLogger(TestThreadPoolShutdown.class);

    @PostConstruct
    public void doShutdown() {
        logger.info("线程池测试，开始执行");
        ExecutorService singleExecutor = ExecutorsUtils.singleThreadPool("优雅停机 | keepAlive=1min | 单线程池测试-");
        ExecutorsUtils.setAllowCoreThreadTimeout(singleExecutor);
        // 单线程池
        singleExecutor.execute(() -> {
            Integer count = 100;
            while(-- count > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (count % 10 == 0) {
                    logger.info("当前： 单 线程池，执行等待时间为： {} s", count);
                }
            }
        });

        ExecutorService fixedExecutor = ExecutorsUtils.fastThreadPool("优雅停机 | keepAlive=10min | 固定线程池测试-");
        ExecutorsUtils.setAllowCoreThreadTimeout(fixedExecutor);
        // 固定线程池
        fixedExecutor.execute(() -> {
            Integer count = 100;
            while(-- count > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (count % 10 == 0) {
                    logger.info("当前： 固定 线程池，执行等待时间为： {} s", count);
                }
            }
        });

        ExecutorService scheduledExecutorService = ExecutorsUtils.scheduleSingleThreadPool("优雅停机 | 定时线程池测试-");
        // 固定线程池
        if (scheduledExecutorService instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor)scheduledExecutorService).scheduleAtFixedRate(() -> {
                Integer count = 200;
                while (--count > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("发生异常");
                        throw new RuntimeException(e);
                    }
                    if (count % 10 == 0) {
                        logger.info("当前： 定时 线程池，执行等待时间为： {} s", count);
                    }
                }
            }, 10, 2, TimeUnit.SECONDS);
        }
    }
}
