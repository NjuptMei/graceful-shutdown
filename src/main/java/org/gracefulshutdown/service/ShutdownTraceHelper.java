package org.gracefulshutdown.service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ShutdownTraceHelper {
    public void getThreadTaskTrace() {
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
