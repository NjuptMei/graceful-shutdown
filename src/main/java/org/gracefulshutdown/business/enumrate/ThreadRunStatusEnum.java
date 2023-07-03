package org.gracefulshutdown.business.enumrate;

import lombok.Getter;

/**+
 * 线程的运行状态
 */
@Getter
public enum ThreadRunStatusEnum {
    /**
     * 新建，对象被创建但尚未调用start()方法
     */
    NEW("New"),

    /**
     * 线程调用start()方法后，它进入可运行状态
     */
    RUNNABLE("Runnable"),

    /**
     * 线程因为等待某个监视器锁（通过synchronized关键字实现）而被阻塞时，它进入阻塞状态
     */
    BLOCKED("Blocked"),

    /**
     * 当线程调用wait()方法、join()方法或LockSupport.park()方法时，它进入等待状态。
     * 线程在等待状态下会一直等待，直到被其他线程调用notify()、notifyAll()或者等待时间结束
     */
    WAITING("Waiting"),

    /**
     * 当线程调用带有超时参数的sleep()方法、join()方法或LockSupport.parkNanos()方法时，它进入计时等待状态。
     * 线程在计时等待状态下会在指定的时间内等待，超过时间后会自动返回可运行状态。
     */
    TIMED_WAITING("Timed Waiting"),

    /**
     * 线程执行完run()方法的任务或发生未捕获的异常导致线程终止时，它进入终止状态。
     */
    TERMINATED("Terminated");

    private String status;

    ThreadRunStatusEnum(String status) {
        this.status = status;
    }
}
