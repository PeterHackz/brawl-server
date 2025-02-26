package com.brawl.titan;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TasksManager {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public static void setInterval(Runnable task, long ms) {
        executor.scheduleAtFixedRate(task, 0, ms, TimeUnit.MILLISECONDS);
    }

    public static void queue(Runnable task) {
        executor.execute(task);
    }

}
