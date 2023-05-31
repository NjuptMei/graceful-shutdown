package org.gracefulshutdown.thread.util;

import org.gracefulshutdown.thread.ExcutorRejectThread;
import org.gracefulshutdown.thread.ThreadConfig;
import org.gracefulshutdown.thread.ThreadPoolMdcTrackTaskExcutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类，默认核心线程数为10，
 */
public class ExecutorsUtils {

    public static ExecutorService fastThreadPool() {
        return fastThreadPool(ThreadConfig.CORE_POOL_SIZE, ThreadConfig.FAST_QUEUE_SIZE);

    }

    public static ExecutorService fastThreadPool(Integer coreSize) {
        return fastThreadPool(coreSize, ThreadConfig.FAST_QUEUE_SIZE);

    }

    public static ExecutorService fastThreadPool(Integer coreSize, Integer queueSize) {
        return new ThreadPoolMdcTrackTaskExcutor(ThreadConfig.CORE_POOL_SIZE, ThreadConfig.MAX_POOL_SIZE,
                ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<>(ThreadConfig.FAST_QUEUE_SIZE),
                new NamedThreadFactory("快线程池fastThreadPool-线程thread-") {}, new ExcutorRejectThread() {});

    }

    public static ExecutorService slowThreadPool() {
        return slowThreadPool(ThreadConfig.CORE_POOL_SIZE, ThreadConfig.SLOW_QUEUE_SIZE);
    }

    public static ExecutorService slowThreadPool(Integer coreSize) {
        return slowThreadPool(coreSize, ThreadConfig.SLOW_QUEUE_SIZE);
    }

    public static ExecutorService slowThreadPool(Integer coreSize, Integer queueSize) {
        return new ThreadPoolMdcTrackTaskExcutor(coreSize, ThreadConfig.MAX_POOL_SIZE,
                ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<>(queueSize),
                new NamedThreadFactory("慢线程池slowThreadPool-线程thread-") {}, new ExcutorRejectThread() {});
    }

    public static ExecutorService fixedThreadPool(Integer coreSize) {
        return fixedThreadPool(coreSize, 0);
    }

    public static ExecutorService fixedThreadPool(Integer coreSize, Integer queueSize) {
        return new ThreadPoolMdcTrackTaskExcutor(coreSize, ThreadConfig.MAX_POOL_SIZE,
                ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<>(queueSize),
                new NamedThreadFactory("固定线程池fixedThreadPool-线程thread-") {}, new ExcutorRejectThread() {});

    }

    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolMdcTrackTaskExcutor(0, Integer.MAX_VALUE, ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new NamedThreadFactory("缓存线程池newCachedThreadPool-线程thread-") {});
    }

    public static ExecutorService singleThreadPool() {
        return new ThreadPoolMdcTrackTaskExcutor(1, 1, ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), new NamedThreadFactory("单线程池singleThreadPool-线程thread-") {});
    }
}
