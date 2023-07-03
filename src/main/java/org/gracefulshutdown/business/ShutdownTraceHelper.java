package org.gracefulshutdown.business;

import org.gracefulshutdown.thread.ThreadPoolMdcTrackTaskExcutor;
import org.gracefulshutdown.thread.util.ExecutorsUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class ShutdownTraceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownTraceHelper.class);

    private static final String RUN_STATUS = "RUN_STATUS";

    static {
        // 执行在所有类加载完毕后需要执行的逻辑
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // 获取项目中的所有类
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            // 过滤所有类
            scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));
            for (BeanDefinition bd : scanner.findCandidateComponents("com.example")) {
                String className = bd.getBeanClassName();
                // 触发类加载
                Class<?> clazz = classLoader.loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            // 处理异常情况
        }
    }

    public static String getAppRunStatus() {
        String runStatus = "";
        String[] command = {"/bin/bash", "-c", "source ~/.bashrc && echo $" + RUN_STATUS};
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            while ((runStatus = reader.readLine()) != null) {
                sb.append(runStatus);
            }
            process.waitFor();
            runStatus = sb.toString();
        } catch (Exception ee) {
            logger.error("获取环境变量中app运行状态异常", ee);
        }
        return runStatus;
    }

    @Test
    public void getThreadTaskTrace() {
        // 1. 带名称的Executor框架执行类
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

        // 2.不带名称的线程池框架 - 此处要分两种情况讨论，是否要等待future
        ExecutorService executor = new ThreadPoolExecutor(5,10,0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.submit(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Future<String> future = executor.submit(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return "执行完毕";
        });

        // 3. 不带名字的new Thread方法
        new Thread(
                () -> {
                    for (int i = 0; i < 100; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        ).start();

        // 4. 带名字的new Thread方法
        new Thread(
                () -> {
                    for (int i = 0; i < 100; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, "测试线程"
        ).start();

        // 5. stream流式异步
        CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                return null;
            }
        });

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);

        for (ThreadInfo threadInfo : threadInfos) {
            String threadName = threadInfo.getThreadName();
            Thread.State threadState = threadInfo.getThreadState();
            System.out.println("线程名称: " + threadName);
            System.out.println("线程状态: " + threadState);
        }
    }
}
