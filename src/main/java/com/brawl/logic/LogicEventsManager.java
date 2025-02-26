package com.brawl.logic;

import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicLocationData;
import com.brawl.logic.data.LogicLocationData.Gamemode;
import com.brawl.logic.debug.Debugger;
import com.brawl.logic.math.LogicRandom;
import com.brawl.server.ServerConfiguration;
import com.brawl.server.Ticker;
import com.brawl.titan.TasksManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class LogicEventsManager {

    private static final ArrayList<LogicLocationData> specialEvents = new ArrayList<>();
    private static final ArrayList<Showdown> showdown = new ArrayList<>();
    private static final ArrayList<LogicLocationData> dailyEvents = new ArrayList<>(); // brawl ball
    private static final ArrayList<LogicLocationData> teamEvents = new ArrayList<>(); // 3v3
    private static final ArrayList<LogicLocationData> teamEvents2 = new ArrayList<>();
    private static final ArrayList<LogicLocationData> gemGrab = new ArrayList<>();
    @Getter
    private static final HashMap<Integer, Event> events = new HashMap<>();
    private static final ArrayList<LogicLocationData> soloEvents = new ArrayList<>();

    public static void init() {

        // special events
        specialEvents.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.RAID));
        specialEvents.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.BOSSFIGHT));
        specialEvents.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.RAID_TOWNCRUSH));
        specialEvents.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.SURVIVAL));

        // showdown
        ArrayList<LogicLocationData> showdownMaps = LogicDataTables.getEnabledLocationsByType(Gamemode.BATTLEROYALE);
        for (LogicLocationData showdownMap : showdownMaps) {
            Showdown map = new Showdown(showdownMap);
            if (map.isValid())
                showdown.add(map);
        }

        gemGrab.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.COINRUSH));

        teamEvents.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.ATTACKDEFEND));
        teamEvents.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.BOUNTYHUNTER));

        teamEvents2.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.KINGOFHILL));
        teamEvents2.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.ROBOWARS));

        teamEvents2.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.CAPTURETHEFLAG));

        dailyEvents.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.LASERBALL));

        soloEvents.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.BOSSRACE));
        soloEvents.addAll(LogicDataTables.getEnabledLocationsByType(Gamemode.SOLOBOUNTY));

        showdown.clear();
        specialEvents.clear();
        teamEvents.clear();
        teamEvents2.clear();
        dailyEvents.clear();
        soloEvents.clear();
        gemGrab.clear();

        gemGrab.add(LogicDataTables.getDataById(LogicDataTables.LOCATIONS, 7));

        initTimers();

        Debugger.info("LogicEventsManager: events ready");
    }

    public static Event getEventByID(int id) {
        return events.get(id);
    }

    private static void initTimers() {
        if (gemGrab.size() > 0)
            TasksManager.setInterval(
                    () -> changeGemgrabMap(Ticker.updateServerTick()),
                    RotationTime.GemGrab * 1000);
        if (showdown.size() > 0)
            TasksManager.setInterval(
                    () -> changeShowdowmMap(Ticker.updateServerTick()),
                    RotationTime.Showdown * 1000);
        if (dailyEvents.size() > 0)
            TasksManager.setInterval(
                    () -> changeDailyEventsMap(Ticker.updateServerTick()),
                    RotationTime.DailyEvents * 1000);
        if (teamEvents.size() > 0 && teamEvents2.size() > 0)
            TasksManager.setInterval(
                    () -> {
                        changeTeamEventsMap(
                                Ticker.updateServerTick());
                        changeTeamEvents2Map(
                                Ticker.updateServerTick());
                    },
                    RotationTime.TeamEvents * 1000);
        if (specialEvents.size() > 0)
            TasksManager.setInterval(
                    () -> changeSpecialEventsMap(Ticker.updateServerTick()),
                    RotationTime.SpecialEvents * 1000);
        if (soloEvents.size() > 0)
            TasksManager.setInterval(
                    () -> changeSoloEventsMap(Ticker.updateServerTick()),
                    RotationTime.SoloEvents * 1000);
    }

    private static LogicLocationData getRandomValueFromIntListWithout(ArrayList<LogicLocationData> list, int excluded) {
        if (list.size() == 1)
            return list.get(0);
        LogicLocationData value;
        do {
            int idx = LogicRandom.nextInt(list.size());
            value = list.get(idx);
        } while (value.getDataId() == excluded);
        return value;
    }

    private static void changeGemgrabMap(int serverTick) {
        synchronized (events) {
            Event currentMap = events.get(1);
            LogicLocationData newMap = getRandomValueFromIntListWithout(gemGrab,
                    currentMap != null ? currentMap.locationData().getDataId() : -1);
            events.put(1, new Event(newMap, RotationTime.GemGrab, serverTick));
        }
    }

    private static void changeShowdowmMap(int serverTick) {
        synchronized (events) {
            Event solo = events.get(2);
            int match = solo == null ? -1 : solo.locationData().getDataId();
            int value;
            Showdown sd;
            do {
                int idx = LogicRandom.nextInt(showdown.size());
                sd = showdown.get(idx);
                value = sd.getSoloLocationData().getDataId();
            } while (value == match);
            events.put(2, new Event(sd.getSoloLocationData(), RotationTime.Showdown, serverTick));
            events.put(5, new Event(sd.getDuoLocationData(), RotationTime.Showdown, Ticker.getServerTick()));
        }
    }

    private static void changeDailyEventsMap(int serverTick) {
        synchronized (events) {
            Event currentMap = events.get(3);
            LogicLocationData newMap = getRandomValueFromIntListWithout(dailyEvents,
                    currentMap != null ? currentMap.locationData().getDataId() : -1);
            events.put(3, new Event(newMap, RotationTime.DailyEvents, serverTick));
        }
    }

    private static void changeTeamEventsMap(int serverTick) {
        synchronized (events) {
            Event currentMap = events.get(4);
            LogicLocationData newMap = getRandomValueFromIntListWithout(teamEvents,
                    currentMap != null ? currentMap.locationData().getDataId() : -1);
            events.put(4, new Event(newMap, RotationTime.TeamEvents, serverTick));
        }
    }

    private static void changeTeamEvents2Map(int serverTick) {
        synchronized (events) {
            Event currentMap = events.get(6);
            LogicLocationData newMap = getRandomValueFromIntListWithout(teamEvents2,
                    currentMap != null ? currentMap.locationData().getDataId() : -1);
            events.put(6, new Event(newMap, RotationTime.TeamEvents, serverTick));
        }
    }

    private static void changeSpecialEventsMap(int serverTick) {
        synchronized (events) {
            Event currentMap = events.get(7);
            LogicLocationData newMap = getRandomValueFromIntListWithout(
                    specialEvents, currentMap != null ? currentMap.locationData().getDataId() : -1);
            events.put(7, new Event(newMap, RotationTime.SpecialEvents, serverTick));
        }
    }

    private static void changeSoloEventsMap(int serverTick) {
        synchronized (events) {
            Event currentMap = events.get(8);
            LogicLocationData newMap = getRandomValueFromIntListWithout(soloEvents,
                    currentMap != null ? currentMap.locationData().getDataId() : -1);
            events.put(8, new Event(newMap, RotationTime.SoloEvents, serverTick));
        }
    }

    public record Event(LogicLocationData locationData, int changeTime, int serverTickOnEventSet) {
        public Event(LogicLocationData locationData, int changeTime, int serverTickOnEventSet) {
            this.changeTime = (int) (ServerConfiguration.addTimeToDayTillNow(changeTime * 1000L) / 1000);
            this.locationData = locationData;
            this.serverTickOnEventSet = serverTickOnEventSet;
        }

        public int getTimeLeft() {
            return changeTime - (int) (System.currentTimeMillis() / 1000);
        }
    }

    private static final class RotationTime {
        public static final int GemGrab = 3600 * 8,
                Showdown = 3600 * 6,
                DailyEvents = 3600 * 4,
                TeamEvents = 3600 * 6,
                SpecialEvents = 3600 * 24,
                SoloEvents = 3600 * 12;
    }

    private static final class Showdown {
        private final LogicLocationData solo;
        private final LogicLocationData duo;

        public Showdown(LogicLocationData soloLocationData) {
            solo = soloLocationData;
            duo = LogicDataTables.getSecondaryLocationData(soloLocationData);
        }

        public LogicLocationData getSoloLocationData() {
            return solo;
        }

        public LogicLocationData getDuoLocationData() {
            return duo;
        }

        public boolean isValid() {
            return solo != null && duo != null;
        }

    }
}