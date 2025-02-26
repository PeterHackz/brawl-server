package com.brawl.server;

import lombok.Getter;
import lombok.Setter;

public class IDManager {

    @Getter
    @Setter
    private static IDManager playerIdManager;
    private final Object lock;
    private int currentHighId,
            currentLowId;

    public IDManager(int high, int low) {
        if (low == 50000) {
            high += 1;
            low = 1;
        }
        currentHighId = high;
        currentLowId = low;
        lock = new Object();
    }

    public ID generateNewID() {
        synchronized (lock) {
            int highId = currentHighId;
            int lowId = currentLowId;
            currentLowId += 1;
            if (currentLowId == 50000) {
                currentLowId = 1;
                currentHighId += 1;
            }
            return new ID(highId, lowId);
        }
    }

    @Getter
    public static final class ID {
        private final int highId, lowId;

        public ID(int highId, int lowId) {
            this.highId = highId;
            this.lowId = lowId;
        }
    }

}
