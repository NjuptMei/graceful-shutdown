package org.gracefulshutdown.business.enumrate;

public enum FilterThreadEnum {
    /**
     * 停机钩子线程
     */
    SHUTDOWN_HOOK("SpringContextShutdownHook"),

    /**
     * 服务默认jvm进出口线程
     */
    DESTROY_JVM("DestroyJavaVM");

    String name;

    FilterThreadEnum(String name) {
        this.name = name;
    }


}
