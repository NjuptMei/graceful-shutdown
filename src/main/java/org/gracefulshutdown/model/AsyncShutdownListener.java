package org.gracefulshutdown.model;

import lombok.Getter;
import lombok.Setter;

import java.lang.management.ThreadInfo;
import java.util.*;

@Getter
@Setter
public class AsyncShutdownListener {
    Map<String, List<ThreadInfo>> threadMap;

    List<ThreadInfo> filterThreadList = new ArrayList<>();

    public AsyncShutdownListener filter(ThreadInfo... threadInfo) {
        if (!Objects.isNull(threadInfo)) {
            Arrays.stream(threadInfo).forEach(t -> {
                if (!filterThreadList.contains(t)) {
                    filterThreadList.add(t);
                }
            });
        }
        return this;
    }

    public void handler() {

    }
}
