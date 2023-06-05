package org.gracefulshutdown.hook;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ApplicationHookInitial {

    @PostConstruct
    public void addCusomHook() {
        Runtime.getRuntime().addShutdownHook(new ControllerMonitorHook());
    }
}
