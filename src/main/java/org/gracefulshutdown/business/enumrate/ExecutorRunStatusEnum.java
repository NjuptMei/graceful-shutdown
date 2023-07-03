package org.gracefulshutdown.business.enumrate;

import lombok.Getter;

/**
 * 线程池的运行状态
 */
@Getter
public enum ExecutorRunStatusEnum {
    /**
     * 线程池处于运行状态，可以接受新的任务并处理已提交的任务
     */
    RUNNING("Running"),

    /**
     * 调用了线程池的shutdown()方法后，线程池进入关闭中状态。线程池不再接受新的任务，但会继续处理已提交的任务直到任务队列为空。
     */
    SHUTDOWN("Shutdown"),

    /**
     * 调用了线程池的shutdownNow()方法后，线程池进入停止中状态。在该状态下，线程池不再接受新的任务，并且尝试中断正在执行的任务。
     */
    STOP("Stop"),

    /**
     * 线程池完全终止的状态。当线程池完成关闭过程并且所有任务都已完成或丢弃时，线程池进入已终止状态。
     */
    TERMINATED("Terminated");

    private String status;

    ExecutorRunStatusEnum(String status) {
        this.status = status;
    }
}
