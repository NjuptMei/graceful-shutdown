package org.gracefulshutdown.business;

import org.gracefulshutdown.hook.AsncThreadMonitorHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;
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
            Thread.sleep(10000L);
            this.condition.signal();
        } catch (Exception ee) {
            logger.error("执行唤醒异常", ee);
        } finally {
            this.lock.unlock();
        }
    }
}
