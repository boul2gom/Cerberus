package fr.boul2gom.cerberus.common.scheduler;

import fr.boul2gom.cerberus.api.scheduler.IScheduledTask;
import fr.boul2gom.cerberus.api.scheduler.IScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScheduledTask implements IScheduledTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(IScheduledTask.class);

    private final IScheduler scheduler;

    private final int id;
    private int iterations;

    private final Runnable task;
    private Runnable thenTask;

    private final long delay;
    private final long period;
    private final int wantedIterations;
    private final AtomicBoolean running;

    public ScheduledTask(IScheduler scheduler, int id, Runnable task, int iterations, long delay, long period, TimeUnit timeUnit) {
        this.scheduler = scheduler;

        this.id = id;
        this.wantedIterations = iterations;

        this.task = task;

        this.delay = timeUnit.toMillis(delay);
        this.period = timeUnit.toMillis(period);
        this.running = new AtomicBoolean(true);
    }

    @Override
    public void then(Runnable runnable) {
        this.thenTask = runnable;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }

    @Override
    public void setRunning(boolean running) {
        this.running.set(running);
    }

    @Override
    public Runnable getTask() {
        return this.task;
    }

    @Override
    public void cancel() {
        final boolean wasRunning = this.running.getAndSet(false);

        if (wasRunning) {
            this.scheduler.cancel(this);
        }
    }

    @Override
    public void run() {
        if (this.delay > 0) {
            this.sleep(this.delay);
        }

        while (this.running.get()) {
            if (this.wantedIterations != -1 && this.iterations >= this.wantedIterations) {
                break;
            }

            try {
                this.task.run();
                this.iterations++;
            } catch (Exception e) {
                LOGGER.error("Task {} encountered an exception", this.id, e);
                return;
            }

            if (this.period <= 0) {
                break;
            }

            this.sleep(this.period);
        }

        if (this.thenTask != null) {
            this.thenTask.run();
        }

        this.cancel();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error("Task {} was interrupted", this.id, e);
        }
    }
}
