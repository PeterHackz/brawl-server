package com.brawl.server;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.message.account.LoginMessage.InvalidCredentialsException;
import com.brawl.logic.utils.LogicLong;
import com.brawl.server.DataBase.DataBaseManager;
import com.brawl.titan.TasksManager;
import lombok.Getter;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

public class Cache {

    @Getter
    private static final IDBasedCache<LogicPlayer> playersCache = new IDBasedCache<>() {
        @Override
        public LogicPlayer loadEntry(long id) throws Exception {
            int highId = LogicLong.getHigherInt(id);
            int lowId = LogicLong.getLowerInt(id);
            Document doc = DataBaseManager.getPlayers().get(highId, lowId);
            if (doc == null)
                throw new InvalidCredentialsException();
            LogicPlayer player = LogicPlayer.fromDocument(doc);
            player.setHighId(highId);
            player.setLowId(lowId);
            System.out.println("loaded player");
            return player;
        }
    };

    public static LogicPlayer getPlayer(int highId, int lowId) throws Exception {
        return playersCache.getEntry(LogicLong.getId(highId, lowId));
    }

    public static class IDBasedCache<T> {

        // id-based map, uses high id as key for entries then low id.
        private final HashMap<Integer, HashMap<Integer, SoftReference<T>>> cacheStorage;

        private final Object cacheEntriesLock;

        public IDBasedCache() {
            cacheStorage = new HashMap<>();
            cacheEntriesLock = new Object();
            TasksManager.setInterval(this::doCleanup, TimeUnit.HOURS.toMillis(1));
        }

        public int getEntriesCount() {
            synchronized (cacheEntriesLock) {
                int count = 0;
                for (HashMap<Integer, SoftReference<T>> lowIdMap : cacheStorage.values()) {
                    count += lowIdMap.size();
                }
                return count;
            }
        }

        public T getEntry(int highId, int lowId) throws Exception {
            return getEntry(LogicLong.getId(highId, lowId));
        }

        public T getEntry(long id) throws Exception {
            synchronized (cacheEntriesLock) {
                T t = null;
                int highId = LogicLong.getHigherInt(id);
                HashMap<Integer, SoftReference<T>> lowIdMap = cacheStorage.get(highId);
                if (lowIdMap != null) {
                    SoftReference<T> weak = lowIdMap.get(LogicLong.getLowerInt(id));
                    t = weak == null ? null : weak.get();
                }
                if (t != null)
                    return t;

                t = loadEntry(id);
                return addEntry(id, t);
            }
        }

        public T getWithoutLoading(long id) {
            synchronized (cacheEntriesLock) {
                int highId = LogicLong.getHigherInt(id);
                int lowId = LogicLong.getLowerInt(id);
                HashMap<Integer, SoftReference<T>> lowIdMap = cacheStorage.get(highId);
                return lowIdMap == null ? null : lowIdMap.get(lowId).get();
            }
        }

        public T getWithoutLoading(int highId, int lowId) {
            return getWithoutLoading(LogicLong.getId(highId, lowId));
        }

        public T addEntry(int highId, int lowId, T entry) {
            return addEntry(LogicLong.getId(highId, lowId), entry);
        }

        public T addEntry(Long id, T entry) {
            synchronized (cacheEntriesLock) {
                int highId = LogicLong.getHigherInt(id);
                int lowId = LogicLong.getLowerInt(id);
                HashMap<Integer, SoftReference<T>> lowIdMap = cacheStorage.computeIfAbsent(highId,
                        k -> new HashMap<>());
                lowIdMap.put(lowId, new SoftReference<>(entry));
            }
            return entry;
        }

        // should be overridden!
        public T loadEntry(long id) throws Exception {
            return null;
        }

        public void doCleanup() {
            synchronized (cacheEntriesLock) {
                Iterator<Map.Entry<Integer, HashMap<Integer, SoftReference<T>>>> iterator = cacheStorage.entrySet()
                        .iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, HashMap<Integer, SoftReference<T>>> entry = iterator.next();
                    entry.getValue().entrySet().removeIf(lowIdEntry -> lowIdEntry.getValue().get() == null);
                    if (entry.getValue().isEmpty())
                        iterator.remove();
                }
            }
        }
    }

}
