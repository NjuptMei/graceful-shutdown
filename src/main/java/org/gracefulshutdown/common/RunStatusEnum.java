package org.gracefulshutdown.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RunStatusEnum {
    RUNNING("running"),

    MAINTAIN("maintain"),

    UNKNOW("unknow"),

    STOP("stop");

    private final String status;

    RunStatusEnum(String status) {
        this.status = status;
    }

    public static RunStatusEnum of(String status) {
        return Arrays.stream(RunStatusEnum.values()).filter(r -> r.getStatus().equalsIgnoreCase(status))
                .findFirst().orElse(null);
    }
}
