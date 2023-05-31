package org.gracefulshutdown.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class ExcutorRejectThread implements RejectedExecutionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExcutorRejectThread.class);
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.warn("当前任务处理慢，部分数据被拒绝，线程名：{}", r.getClass().getName());
        // 如果当前被拒绝的任务是读取kafka数据，则把被拒绝的数据打印出来并再次执行
        throw new RejectedExecutionException("线程： " + r.toString() + " ， 被线程池拒绝执行： " +  executor.toString());
    }
}
