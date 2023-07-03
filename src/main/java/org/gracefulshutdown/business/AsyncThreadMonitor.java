package org.gracefulshutdown.business;

import org.gracefulshutdown.common.RunStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.Objects;

/**
 * 针对异步线程、线程池等资源进行监控，需要保证在所有类加载完毕（spring对象、非spring对象）后执行监控初始化动作
 *
 *
 */
public class AsyncThreadMonitor {

    private static final Logger logger = LoggerFactory.getLogger(AsyncThreadMonitor.class);

    private static ThreadMXBean threadMXBean;

    public void init() {
        // 初始化方法中，这里需要保证整个项目运行起来后，再来加载当前所有的Thread异步线程信息

    }

    // 获取项目中线程的运行快照情况，用于进行实时监控
    public void getThreadSnapshot() {
        String appRunStatus = ShutdownTraceHelper.getAppRunStatus();
        // 仅当维护态时，执行停机监控动作 ： 此时表示已经执行停机脚本在等待程序执行中
        if (StringUtils.hasText(appRunStatus) && RunStatusEnum.MAINTAIN.equals(RunStatusEnum.of(appRunStatus))) {
            threadMXBean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);
        }
    }

    public static Map<String, ThreadInfo[]> getAppThreadInfos() {

    }
}
