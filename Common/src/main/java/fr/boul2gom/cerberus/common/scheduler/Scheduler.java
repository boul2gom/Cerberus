package fr.boul2gom.cerberus.common.scheduler;

import fr.boul2gom.cerberus.api.scheduler.IScheduledTask;
import fr.boul2gom.cerberus.api.scheduler.IScheduler;
import fr.boul2gom.cerberus.api.utils.thread.ThreadFactoryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler implements IScheduler {

    private final ExecutorService executor;

    private final AtomicInteger taskCount;
    private final Map<Integer, IScheduledTask> tasks;

    public Scheduler() {
        this.executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setName("Scheduler #%d -> ").setDaemon(true).build());

        this.taskCount = new AtomicInteger(0);
        this.tasks = new HashMap<>();
    }

    @Override
    public void shutdown() {
        this.tasks.values().forEach(this::cancel);
        this.executor.shutdown();
    }

    @Override
    public void cancel(int id) {
        final IScheduledTask task = this.tasks.get(id);

        this.cancel(task);
    }

    @Override
    public void cancel(IScheduledTask task) {
        if (task != null && task.isRunning()) {
            task.cancel();

            this.tasks.remove(task.getId());
        }
    }

    @Override
    public IScheduledTask async(Runnable runnable) {
        return this.createTask(runnable, 1, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    public IScheduledTask schedule(Runnable runnable, long delay, TimeUnit unit) {
        return this.createTask(runnable, -1, delay, 0, unit);
    }

    @Override
    public IScheduledTask schedule(Runnable runnable, int iterations, long delay, TimeUnit unit) {
        return this.createTask(runnable, iterations, delay, 0, unit);
    }

    @Override
    public IScheduledTask schedule(Runnable runnable, int iterations, long delay, long period, TimeUnit unit) {
        return this.createTask(runnable, iterations, delay, period, unit);
    }

    private IScheduledTask createTask(Runnable runnable, int iterations, long delay, long period, TimeUnit unit) {
        final IScheduledTask task = new ScheduledTask(this, this.taskCount.getAndIncrement(), runnable, iterations, delay, period, unit);
        this.executor.submit(task);

        this.tasks.put(task.getId(), task);

        return task;
    }
}
