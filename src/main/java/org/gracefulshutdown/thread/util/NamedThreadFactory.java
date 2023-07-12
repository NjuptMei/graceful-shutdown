package org.gracefulshutdown.thread.util;

import java.util.UUID;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    /**
     * 线程名前缀
     */
    private final String prefix;

    /**
     * 线程编号
     */
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);

    /**
     * 线程唯一标识uuid
     */
    private final String id;

    /**
     * 创建线程工厂
     *
     * @param prefix 线程名前缀
     */
    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
        this.id = "ThreadPoolId-".concat(UUID.randomUUID().toString());
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(null, r, id.concat(" | ") + prefix + NamedThreadFactory.THREAD_NUMBER.getAndIncrement());
    }

    public String getName() {
        return id.concat(" | ") + prefix + NamedThreadFactory.THREAD_NUMBER.get();
    }

}