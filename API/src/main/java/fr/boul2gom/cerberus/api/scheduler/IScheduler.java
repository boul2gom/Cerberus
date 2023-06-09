package fr.boul2gom.cerberus.api.scheduler;

import java.util.concurrent.TimeUnit;

public interface IScheduler {

    void shutdown();

    void cancel(int id);

    void cancel(IScheduledTask task);

    IScheduledTask async(Runnable runnable);

    IScheduledTask schedule(Runnable runnable, long delay, TimeUnit unit);

    IScheduledTask schedule(Runnable runnable, int iterations, long delay, TimeUnit unit);

    IScheduledTask schedule(Runnable runnable, int iterations, long delay, long period, TimeUnit unit);
}
