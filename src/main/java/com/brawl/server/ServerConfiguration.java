package com.brawl.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ServerConfiguration {

    public static final boolean IS_DEV_SERVER = true;

    public static final String DATABASE_NAME = IS_DEV_SERVER ? "MB-Dev" : "MB-Prod";

    public static final String MB_SITE = "https://multibrawl.com";

    public static final String TELEGRAM_LINK = "https://telegram.multibrawl.com"; // a redirect from MB_SITE

    public static final String PROMO_POPUP_DEEPLINK = "multibrawl://extlink?page=" + TELEGRAM_LINK;

    public static final int BRAWLPASS_SEASON = 2,
            BRAWLPASS_PROGRESS_COST = 30,
            BRAWLPASS_BUNDLE_TOKENS = 500,
            BRAWLPASS_COST = 100,
            BRAWL_PASS_BUNDLE_COST = 150;

    public static final long SERVER_START = System.currentTimeMillis(); // used by events

    public static final boolean IS_LOCAL_DEBUG_BUILD = true; // P.S: DISABLE WHEN HOSTING

    public static final String DATABASE_URL = !IS_LOCAL_DEBUG_BUILD
            ? "<--- ~REDACTED~ SET YOUR DB URL HERE --->"
            : "mongodb://localhost:27017/";

    public static final String SEASON_END_DATE_STRING = "2024-03-03 12:00:00";
    public static final long ONE_DAY = TimeUnit.DAYS.toMillis(1);
    public static final HashMap<String, String> CLUSTERS = new HashMap<>() {
        {
            ;
        }
    };
    private static final ZoneId EET_ZONE = ZoneId.of("EET");
    public static long SEASON_END_DATE = 0;

    public static void init() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("EET")); // Eastern European Time (UTC+2)
        SEASON_END_DATE = dateFormat.parse(SEASON_END_DATE_STRING).getTime();
    }

    public static long getDayStart() {
        LocalDateTime now = LocalDateTime.now(EET_ZONE);

        // Set the start time of the day to 8:00 am
        LocalDateTime startOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.of(8, 0));

        return startOfDay.atZone(EET_ZONE).toInstant().toEpochMilli();
    }

    public static long addTimeToDayTillNow(long ms) {
        long dayStart = getDayStart() + ms;
        long now = System.currentTimeMillis();
        while (dayStart < now) {
            dayStart += ms;
        }
        return dayStart;
    }

    public static long getTimePassed() {
        return System.currentTimeMillis() - getDayStart();
    }

    public static long getTimeLeft() {
        return (ONE_DAY + getDayStart()) - System.currentTimeMillis();
    }

}
