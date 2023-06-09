package fr.boul2gom.cerberus.api.scheduler;

public interface IScheduledTask extends Runnable {

    void then(Runnable runnable);

    int getId();

    boolean isRunning();
    void setRunning(boolean running);

    Runnable getTask();

    void cancel();
}
