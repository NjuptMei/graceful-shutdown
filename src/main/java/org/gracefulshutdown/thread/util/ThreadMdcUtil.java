package org.gracefulshutdown.thread.util;

import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

public class ThreadMdcUtil {
    public static void setTraceIdIfAbsent(String traceId) {

    }

    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent(null);
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent(null);
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
