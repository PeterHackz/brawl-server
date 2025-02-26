package com.brawl.logic.home;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.debug.Debugger;
import com.brawl.server.Cache;
import com.brawl.server.DataBase.DataBaseManager;
import com.mongodb.client.FindIterable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.bson.Document;

public class Leaderboard {

    private static final int MAX_PLAYERS = 200;
    private static final HashMap<Integer, Leaderboard> leaderboards = new HashMap<>();
    private final PriorityBlockingQueue<LogicPlayer> players;

    public Leaderboard(int characterId) {

        Comparator<LogicPlayer> comparator;
        if (characterId == -1)
            comparator = Comparator.comparing(LogicPlayer::getTrophies).reversed();
        else
            comparator = Comparator.comparing((LogicPlayer player) -> player.getHero(characterId).getTrophies())
                    .reversed();

        players = new PriorityBlockingQueue<>(MAX_PLAYERS, comparator);

        FindIterable<Document> playersSorted;
        if (characterId == -1) {
            playersSorted = DataBaseManager.getPlayers().getSorted("tr", 200);
        } else {
            playersSorted = DataBaseManager.getPlayers().getSorted(
                    String.format("heroes.%d.trophies", characterId),
                    200);
        }
        Cache.IDBasedCache<LogicPlayer> playersCache = Cache.getPlayersCache();
        for (Document player : playersSorted) {
            LogicPlayer plr;
            if (((plr = playersCache.getWithoutLoading(player.getInteger("hi"), player.getInteger("lo"))) == null)) {
                plr = LogicPlayer.fromDocument(player);
                playersCache.addEntry(player.getInteger("hi"), player.getInteger("lo"), plr);
            }
            players.add(plr);
        }
    }

    public static Leaderboard getLeaderboard(int characterId) {
        return leaderboards.get(characterId);
    }

    public static void init() {
        Debugger.info("Loading players leaderboard...");
        leaderboards.put(-1, new Leaderboard(-1));

        LogicCharacterData[] characters = LogicDataTables.getAvailableCharacters();
        Debugger.info("Loading leaderboards for %d characters...", characters.length);

        for (LogicCharacterData character : characters) {
            leaderboards.put(character.getDataId(), new Leaderboard(character.getDataId()));
        }

        Debugger.info("Leaderboards loaded succesfully");
    }

    public void onUpdate(LogicPlayer player) {
        players.remove(player);
        players.add(player);
        while (players.size() > MAX_PLAYERS)
            players.poll();
    }

    public void getPlayers(PlayersCallback callback) throws Exception {
        synchronized (players) {
            callback.run(players);
        }
    }

    @FunctionalInterface
    public interface PlayersCallback {
        void run(PriorityBlockingQueue<LogicPlayer> players) throws Exception;
    }

}