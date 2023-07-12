package org.gracefulshutdown.thread.util;

import org.gracefulshutdown.thread.ExcutorRejectThread;
import org.gracefulshutdown.thread.ThreadConfig;
import org.gracefulshutdown.thread.ThreadPoolMdcTrackTaskExcutor;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池工具类，默认核心线程数为10，
 */
public class ExecutorsUtils {

    private static final Map<Integer, ExecutorService> EXECUTOR_MAP = new ConcurrentHashMap<>();

    public static ExecutorService fastThreadPool() {
        return fastThreadPool(ThreadConfig.CORE_POOL_SIZE, ThreadConfig.FAST_QUEUE_SIZE, null);

    }

    public static ExecutorService fastThreadPool(String name) {
        return fastThreadPool(ThreadConfig.CORE_POOL_SIZE, ThreadConfig.FAST_QUEUE_SIZE, name);

    }

    public static ExecutorService fastThreadPool(Integer coreSize) {
        return fastThreadPool(coreSize, ThreadConfig.FAST_QUEUE_SIZE, null);

    }

    public static ExecutorService fastThreadPool(Integer coreSize, Integer queueSize, String name) {
        ExecutorService executorService= new ThreadPoolMdcTrackTaskExcutor(ThreadConfig.CORE_POOL_SIZE, ThreadConfig.MAX_POOL_SIZE,
                ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<>(ThreadConfig.FAST_QUEUE_SIZE),
                new NamedThreadFactory(StringUtils.hasText(name) ? name : "快线程池fastThreadPool-线程thread-") {},
                new ExcutorRejectThread() {});
        EXECUTOR_MAP.put(executorService.hashCode(), executorService);
        return executorService;
    }

    public static ExecutorService slowThreadPool() {
        return slowThreadPool(ThreadConfig.CORE_POOL_SIZE, ThreadConfig.SLOW_QUEUE_SIZE);
    }

    public static ExecutorService slowThreadPool(Integer coreSize) {
        return slowThreadPool(coreSize, ThreadConfig.SLOW_QUEUE_SIZE);
    }

    public static ExecutorService slowThreadPool(Integer coreSize, Integer queueSize) {
        ExecutorService executorService = new ThreadPoolMdcTrackTaskExcutor(coreSize, ThreadConfig.MAX_POOL_SIZE,
                ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<>(queueSize),
                new NamedThreadFactory("慢线程池slowThreadPool-线程thread-") {}, new ExcutorRejectThread() {});
        EXECUTOR_MAP.put(executorService.hashCode(), executorService);
        return executorService;
    }

    public static ExecutorService fixedThreadPool(Integer coreSize) {
        return fixedThreadPool(coreSize, 0);
    }

    public static ExecutorService fixedThreadPool(Integer coreSize, Integer queueSize) {
        ExecutorService executorService = new ThreadPoolMdcTrackTaskExcutor(coreSize, ThreadConfig.MAX_POOL_SIZE,
                ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<>(queueSize),
                new NamedThreadFactory("固定线程池fixedThreadPool-线程thread-") {}, new ExcutorRejectThread() {});
        EXECUTOR_MAP.put(executorService.hashCode(), executorService);
        return executorService;
    }

    public static ExecutorService newCachedThreadPool() {
        ExecutorService executorService = new ThreadPoolMdcTrackTaskExcutor(0, Integer.MAX_VALUE, ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new NamedThreadFactory("缓存线程池newCachedThreadPool-线程thread-") {});
        EXECUTOR_MAP.put(executorService.hashCode(), executorService);
        return executorService;
    }

    public static ExecutorService singleThreadPool() {
        ExecutorService executorService = new ThreadPoolMdcTrackTaskExcutor(1, 1, ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), new NamedThreadFactory("单线程池singleThreadPool-线程thread-") {});
        EXECUTOR_MAP.put(executorService.hashCode(), executorService);
        return executorService;
    }

    public static ExecutorService singleThreadPool(String name) {
        ExecutorService executorService = new ThreadPoolMdcTrackTaskExcutor(1, 1, ThreadConfig.KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), new NamedThreadFactory(name) {});
        EXECUTOR_MAP.put(executorService.hashCode(), executorService);
        return executorService;
    }

    public static ScheduledExecutorService scheduleSingleThreadPool() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor( new NamedThreadFactory("定时单线程池singleThreadPool-线程thread-") {});
        EXECUTOR_MAP.put(executorService.hashCode(), executorService);
        return executorService;
    }

    public static ExecutorService scheduleSingleThreadPool(String name) {
        ExecutorService executorService = Executors.newScheduledThreadPool(1,  new NamedThreadFactory(name) {});
        EXECUTOR_MAP.put(executorService.hashCode(), executorService);
        return executorService;
    }

    public static Map<Integer, ExecutorService> getExecutorInfoMap() {
        return EXECUTOR_MAP;
    }

    public static void shutdownAll() {
        EXECUTOR_MAP.values().forEach(ExecutorService::shutdown);
    }
}
