package com.brawl.server;

import java.util.concurrent.atomic.AtomicInteger;

public class Ticker {

    private static final AtomicInteger serverTick = new AtomicInteger();

    public static int updateServerTick() {
        synchronized (serverTick) {
            int tick = serverTick.get();
            tick += 1;
            tick %= 64;
            serverTick.set(tick);
            return tick;
        }
    }

    public static int getServerTick() {
        return serverTick.get();
    }

}
