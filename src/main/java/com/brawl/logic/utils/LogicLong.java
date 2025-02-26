package com.brawl.logic.utils;

public class LogicLong {

    public static long getId(int highId, int lowId) {
        return ((long) lowId * 256) + highId;
    }

    public static int getHigherInt(long id) {
        return (int) (id & 0xFF);
    }

    public static int getLowerInt(long id) {
        return (int) (id >> 8);
    }

}
