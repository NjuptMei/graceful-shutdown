package org.gracefulshutdown.business;

import org.gracefulshutdown.thread.util.ExecutorsUtils;
import org.gracefulshutdown.thread.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 针对异步线程、线程池等资源进行监控，需要保证在所有类加载完毕（spring对象、非spring对象）后执行监控初始化动作
 *
 *
 */
public class AsyncThreadMonitor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AsyncThreadMonitor.class);

    private static ThreadMXBean threadMXBean;

    private final Condition condition;

    private final ReentrantLock lock;

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("定时线程-线程池停机状态监控"));

    public AsyncThreadMonitor(ReentrantLock lock, Condition condition) {
        this.condition = condition;
        this.lock = lock;
    }

    @Override
    public void run() {
        this.lock.lock();
        try {
            // 获取当前jvm线程状态
            getJvmThreadStatus();

            CountDownLatch latch = new CountDownLatch(1);
            // 1. 线程池对象执行停机
            doExecutorShutdown(latch);

            // 2. 线程对象执行等待

            // 3. 定时器处理
            latch.await(5, TimeUnit.MINUTES);
            logger.info("异步线程池状态获取完成");
            Thread.sleep(3000);
            executorService.shutdown();
            // 获取当前jvm线程状态
            getJvmThreadStatus();
            Thread.sleep(10000L);
            this.condition.signal();
        } catch (Exception ee) {
            logger.error("执行唤醒异常", ee);
        } finally {
            this.lock.unlock();
        }
    }

    private void doExecutorShutdown(CountDownLatch latch) {
        StopWatch watch = new StopWatch();
        watch.start();
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> doExecutorShutdown 线程池开始执行停机 >>>>>>>>>>>>>>>>>>>>>>>>>>>");
        ExecutorsUtils.shutdownAll();
        AtomicBoolean running = new AtomicBoolean(true);
        executorService.scheduleAtFixedRate(() -> {
            long shutdownNum = ExecutorsUtils.getExecutorInfoMap().values().stream().filter(ExecutorService::isShutdown).count();
            long activeNum = ExecutorsUtils.getExecutorInfoMap().values().stream().mapToInt(r -> {
                if (r instanceof ThreadPoolExecutor) {
                    ThreadPoolExecutor executor = (ThreadPoolExecutor) r;
                    if (executor.getThreadFactory() instanceof NamedThreadFactory) {
                        String threadName = ((NamedThreadFactory)executor.getThreadFactory()).getName();
                        logger.info("当前运行线程为：{}，其活跃线程数为：{}", threadName, executor.getActiveCount());
                    }
                    return executor.getActiveCount();
                }
                return 0;
            }).sum();
            logger.info("当前线程池总数为：{}；关闭的线程池个数为：{}，处于运行中的线程个数为：{}",
                    ExecutorsUtils.getExecutorInfoMap().values().size(), shutdownNum, activeNum);
            if (running.get() && ExecutorsUtils.getExecutorInfoMap().values().size() <= shutdownNum && activeNum == 0) {
                running.set(false);
                watch.stop();
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> doExecutorShutdown 线程池完成停机，总计耗时：{}s >>>>>>>>>>>>>>>>>>>>>>>>>>>",
                        watch.getTotalTimeSeconds());
                latch.countDown();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private static void getJvmThreadStatus() {
        threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);
        Map<Thread.State, List<ThreadInfo>> threadInfoMap = new HashMap<>();
        Arrays.stream(threadInfos).forEach(t -> {
            threadInfoMap.putIfAbsent(t.getThreadState(), new ArrayList<>());
            threadInfoMap.get(t.getThreadState()).add(t);
            logger.info("线程：{}", t.getThreadName());
        });
        threadInfoMap.forEach((k, v) -> logger.info("处于 {} 状态的线程数一共 {} 个", k.name(), v.size()));
    }
}
