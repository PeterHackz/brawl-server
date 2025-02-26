package com.brawl.logic.math;

public class LongBoolean {

    private long data;

    public LongBoolean() {
        this.data = 0L;
    }

    public LongBoolean(long data) {
        this.data = data;
    }

    public void set(int position, boolean value) {
        if (value)
            data |= (1L << position);
        else
            data &= ~(1L << position);
    }

    public boolean get(int position) {
        return (data & (1L << position)) != 0;
    }

    public long getData() {
        return data;
    }
}