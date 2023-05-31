package org.gracefulshutdown.thread.test;

import org.gracefulshutdown.hook.TransactionMonitorHook;
import org.gracefulshutdown.thread.util.ExecutorsUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TestHook {

    @PostConstruct
    public void addCusomHook() {
        Runtime.getRuntime().addShutdownHook(new TransactionMonitorHook());
    }
}
