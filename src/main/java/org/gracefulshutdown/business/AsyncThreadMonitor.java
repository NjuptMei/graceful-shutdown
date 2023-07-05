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
            Thread BB =  new Thread(
                    () -> {
                        for (int i = 0; i < 100; i++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, "测试NEW线程2222"
            );
            BB.start();
            new TestNonymousThread().startRunner();
            threadMXBean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);
            ThreadInfo[] threadInfos2 = threadMXBean.dumpAllThreads(false, false);
            Map<Thread.State, List<ThreadInfo>> threadInfoMap = new HashMap<>();
            Arrays.stream(threadInfos).forEach(t -> {
                threadInfoMap.putIfAbsent(t.getThreadState(), new ArrayList<>());
                threadInfoMap.get(t.getThreadState()).add(t);
            });
            for (ThreadInfo threadInfo : threadInfos) {
                String threadName = threadInfo.getThreadName();
                Thread.State threadState = threadInfo.getThreadState();
                System.out.println("线程名称: " + threadName);
                System.out.println("线程ID: " + threadInfo.getThreadId());
                System.out.println("线程状态: " + threadState);
            }
            StopWatch watch = new StopWatch();
            watch.start();
            CountDownLatch latch = new CountDownLatch(1);
            // 1. 线程池对象执行停机
            doExecutorShutdown(latch);

            // 2. 线程对象执行等待

            // 3. 定时器处理

            latch.await(5, TimeUnit.MINUTES);
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
            if (running.get() && ExecutorsUtils.getExecutorInfoMap().values().size() == shutdownNum) {running.set(false);
                latch.countDown();
                watch.stop();
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> doExecutorShutdown 线程池完成停机，总计耗时：{}s >>>>>>>>>>>>>>>>>>>>>>>>>>>",
                        watch.getTotalTimeSeconds());
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Component
    class TestNonymousThread {
        @PostConstruct
        public void startRunner() {
            new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, "匿名内部类线程").start();
        }
    }
}
