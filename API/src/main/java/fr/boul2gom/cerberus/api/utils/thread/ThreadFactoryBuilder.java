package fr.boul2gom.cerberus.api.utils.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactoryBuilder {

    private String name = "Thread - %d";
    private boolean daemon = false;

    public ThreadFactoryBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public ThreadFactory build() {
        final AtomicInteger count = new AtomicInteger();

        return runnable -> {
            final Thread thread = new Thread(runnable, String.format(name, count.getAndIncrement()));

            thread.setDaemon(daemon);
            return thread;
        };
    }
}
