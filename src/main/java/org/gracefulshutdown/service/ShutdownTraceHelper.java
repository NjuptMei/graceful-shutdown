package org.gracefulshutdown.service;

import org.gracefulshutdown.thread.util.ExecutorsUtils;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ShutdownTraceHelper {
    @Test
    public void getThreadTaskTrace() {
        ExecutorsUtils.singleThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);

        for (ThreadInfo threadInfo : threadInfos) {
            String threadName = threadInfo.getThreadName();
            Thread.State threadState = threadInfo.getThreadState();

            // 根据线程名称或其他属性判断是否为线程池相关的线程
            if (threadName.contains("pool") || threadName.contains("executor")) {
                System.out.println("Thread Name: " + threadName);
                System.out.println("Thread State: " + threadState);
                // 可以根据需要打印其他线程信息
            }
        }
    }
}
