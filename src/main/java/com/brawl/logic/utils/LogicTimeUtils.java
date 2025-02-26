package com.brawl.logic.utils;

import java.util.concurrent.TimeUnit;

public class LogicTimeUtils {

    public static String formatTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) - (days * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - (days * 24 * 60) - (hours * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
                - (days * 24 * 60 * 60)
                - (hours * 60 * 60)
                - (minutes * 60);
        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

}