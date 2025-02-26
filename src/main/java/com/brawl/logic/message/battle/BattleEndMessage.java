package com.brawl.logic.message.battle;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.battle.LogicBattleResult;
import com.brawl.logic.battle.LogicBattleResult.Player;
import com.brawl.logic.data.LogicLocationData;
import com.brawl.logic.home.Leaderboard;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

import java.util.HashMap;

public class BattleEndMessage extends PiranhaMessage {

    private static final HashMap<Integer, int[]> DuoTrophiesList = new HashMap<>() {
        {
            put(0, new int[]{9, 7, 4, 0, 0});
            put(50, new int[]{9, 7, 4, 0, -1});
            put(100, new int[]{9, 7, 3, -1, -2});
            put(200, new int[]{9, 7, 2, -2, -3});
            put(300, new int[]{9, 7, 1, -3, -4});
            put(400, new int[]{9, 7, 0, -4, -5});
            put(500, new int[]{9, 7, 0, -5, -6});
            put(600, new int[]{9, 7, 0, -6, -8});
            put(700, new int[]{7, 6, -1, -8, -9});
            put(800, new int[]{7, 6, -3, -8, -9});
            put(900, new int[]{5, 4, -3, -9, -11});
            put(1000, new int[]{5, 4, -4, -10, -11});
            put(1100, new int[]{5, 4, -4, -11, -12});
            put(Integer.MAX_VALUE, new int[]{4, 3, -5, -11, -12});
        }
    };

    private static final HashMap<Integer, int[]> TrioTrophiesList = new HashMap<>() {
        {
            put(0, new int[]{8, 0});
            put(50, new int[]{8, -2});
            put(100, new int[]{8, -3});
            put(200, new int[]{8, -4});
            put(300, new int[]{8, -5});
            put(400, new int[]{8, -6});
            put(500, new int[]{8, -7});
            put(600, new int[]{8, -8});
            put(700, new int[]{7, -9});
            put(800, new int[]{6, -10});
            put(900, new int[]{5, -11});
            put(1000, new int[]{4, -12});
            put(Integer.MAX_VALUE, new int[]{3, -12});
        }
    };

    private static final HashMap<Integer, int[]> SoloTrophiesList = new HashMap<>() {
        {
            put(0, new int[]{10, 8, 7, 6, 4, 2, 2, 1, 0, 0});
            put(50, new int[]{10, 8, 7, 6, 3, 2, 2, 0, -1, -2});
            put(100, new int[]{10, 8, 7, 63, 1, 0, -1, -2, -2});
            put(200, new int[]{10, 8, 6, 5, 3, 1, 0, -2, -3, -3});
            put(300, new int[]{10, 8, 6, 5, 2, 0, 0, -3, -4, -4});
            put(400, new int[]{10, 8, 6, 5, 2, -1, -2, -3, -5, -5});
            put(500, new int[]{10, 8, 6, 4, 2, -1, -2, -5, -6, -6});
            put(600, new int[]{10, 8, 6, 4, 1, -2, -2, -5, -7, -8});
            put(700, new int[]{10, 8, 6, 4, 1, -3, -4, -5, -8, -9});
            put(800, new int[]{8, 6, 4, 1, -1, -3, -6, -8, -10, -11});
            put(900, new int[]{6, 5, 3, 1, -2, -5, -6, -9, -11, -12});
        }
    };
    private final LogicBattleResult battleResult;

    public BattleEndMessage(LogicBattleResult battleResult) {
        super(400);
        this.battleResult = battleResult;
    }

    public static int calculateCharacterTrophies(HashMap<Integer, int[]> trophiesList, int brawlerTrophies,
                                                 int result) {
        int[] trophyVals = null;
        for (int key : trophiesList.keySet()) {
            if (brawlerTrophies >= key) {
                trophyVals = trophiesList.get(key);
            } else {
                break;
            }
        }
        return trophyVals == null ? 0 : trophyVals[result - 1];
    }

    private static int[] calculateResources(int result, LogicPlayer player, LogicLocationData location) {
        LogicHeroData heroData = player.getSelectedHero();
        int trophies = heroData.getTrophies();
        int highestTrophies = heroData.getHighestTrophies();
        int type = location.getGamemodeType();
        int trophiesWon = 0;
        int starPoints = 0;
        if (type == 5) {
            trophiesWon = calculateCharacterTrophies(DuoTrophiesList, trophies, result);
            result = switch (result) {
                case 1 -> 25; // incr duo
                case 2 -> 20; // incr duo
                case 3 -> 10;
                case 4 -> 5;
                default -> 0;
            };
        } else if (type == 1) {
            trophiesWon = result == 2 ? 0 : calculateCharacterTrophies(TrioTrophiesList, trophies, result == 0 ? 1 : 2);
            starPoints = switch (result) {
                case 0 -> 20; // incr team
                case 1 -> 10;
                case 2 -> 5;
                default -> 0;
            };
        } else if (type == 2) {
            trophiesWon = calculateCharacterTrophies(SoloTrophiesList, trophies, result);
            starPoints = switch (result) {
                case 1 -> 30; // incr solo
                case 2 -> 25; // incr solo
                case 3 -> 20; // if (tr < 1000) incr solo
                case 4 -> 15;
                case 5 -> 10;
                case 6 -> 5;
                case 7 -> 2;
                default -> 0;
            };
        } else {
            starPoints = switch (result) {
                case 0 -> location.getPlayersCount() == 6 ? 20 : 50;
                case 1 -> 5;
                default -> 0;
            };
        }
        int oldTrophies = trophies;
        int oldHighestTrophies = highestTrophies;
        if (starPoints > 0)
            player.setStarPoints(player.getStarPoints() + starPoints);
        if (type != 3 && type != 4) {
            trophies += trophiesWon;
            player.setHeroTrophies(heroData, trophies);
            if (highestTrophies < trophies)
                highestTrophies = trophies;
            player.setTrophies(player.getTrophies() + trophiesWon);
            player.gainTrophies(trophiesWon);
        }
        player.gainStarpoints(starPoints);
        return new int[]{oldTrophies, oldHighestTrophies, trophiesWon, starPoints};
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void encode(ClientConnection clientConnection) {
        LogicLocationData location = battleResult.getLocationData();
        LogicPlayer ownPlayer = clientConnection.getLogicPlayer();

        int percentage = battleResult.getSpecialEventPercentageResult(),
                result = battleResult.getResult();

        if (battleResult.getPlayersCount() != 6 &&
                location.isSpecialEvent()) {
            result = switch (location.getGamemode()) {
                case BOSSFIGHT, RAID_TOWNCRUSH -> percentage == 0 ? 0 : 1;
                case SURVIVAL -> percentage == 0 ? 1 : 0;
                default -> result;
            };
        }

        int[] data = new int[]{0, 0, 0, 0};

        if (!ownPlayer.isInTeam()) {
            data = calculateResources(result, ownPlayer,
                    location);
            Leaderboard.getLeaderboard(-1).onUpdate(ownPlayer);
            Leaderboard.getLeaderboard(ownPlayer.getSelectedHero().getCharacterData().getDataId()).onUpdate(ownPlayer);
        }

        var stream = this.getByteStream();

        stream.writeVInt(location.getGamemodeType());

        stream.writeVInt(result);

        stream.writeVInt(data[3]); // tokens gained
        stream.writeVInt(data[2]); // gained/lost trophies
        stream.writeVInt(0);
        stream.writeVInt(0); // double tokens
        stream.writeVInt(0); // double tokens event
        stream.writeVInt(0); // token doubler
        stream.writeVInt(0); // ticketed event time
        stream.writeVInt(0);
        stream.writeVInt(0); // challange level passed
        stream.writeVInt(0); // challenge reward type
        stream.writeVInt(0); // challenge reward ammount
        stream.writeVInt(0); // championship looses
        stream.writeVInt(0); // championship maximum looses
        stream.writeVInt(0); // coinshower event
        stream.writeVInt(0); // underdog trophies
        boolean pvp = !ownPlayer.isInTeam();
        stream.writeBooleans(
                false, // star token
                true, // exp cap
                true, // tokens cap
                !pvp, // show exit button
                pvp, // pvp/proceed button
                false, // spectate
                false // powerplay
        );
        stream.writeVInt(-1);
        stream.writeBoolean(false);

        // players entry
        var players = battleResult.getPlayers();

        stream.writeVInt(players.size());

        Player player;

        int team = 0;
        for (Player value : players)
            if (!value.isBot())
                team = value.getTeam();

        for (Player value : players) {
            player = value;
            stream.writeBooleans(
                    !player.isBot(),
                    player.getTeam() != team,
                    false // star player
            );
            stream.writeDataReference(player.getCharacterData());
            stream.writeDataReference(player.getSkinData());
            if (player.isBot()) {
                stream.writeVInt(0); // Brawler Trophies
                stream.writeVInt(0); // power play points
                stream.writeVInt(10); // Brawler Level
                stream.writeBoolean(false);
            } else {
                stream.writeVInt(data[0]);
                stream.writeVInt(data[1]);
                stream.writeVInt(ownPlayer.getHero(
                        player.getCharacterData().getDataId()).getLevel());
                stream.writeBoolean(true);
                stream.writeInt(ownPlayer.getHighId());
                stream.writeInt(ownPlayer.getLowId());
            }
            stream.writeString(player.getName());
            stream.writeVInt(100); // exp
            stream.writeVInt(28000000);
            // }
            stream.writeVInt(43000000);
            stream.writeVInt(-1);
        }

        // Experience Entry Array
        stream.writeVInt(2); // Count
        stream.writeVInt(0); // Normal Experience ID
        stream.writeVInt(0); // Normal Experience Gained
        stream.writeVInt(8); // Star Player Experience ID
        stream.writeVInt(0); // Star Player Experience Gained
        // Experience Entry Array End

        // Milestone Rewards Array
        stream.writeVInt(0); // Milestones Count
        // Milestone Rewards Array End

        // Milestone Progress Array
        stream.writeVInt(2); // Count
        stream.writeVInt(1); // Milestone ID
        stream.writeVInt(data[0]); // Brawler Trophies
        stream.writeVInt(data[1]); // Brawler Highest Trophies
        stream.writeVInt(5); // Milestone ID
        stream.writeVInt(0); // Player Experience Points
        stream.writeVInt(0); // Player Experience Points
        // Milestone Progress Array End

        stream.writeDataReference(28, 0);
        stream.writeBooleans(
                false, // play again
                false // quests
        );
    }

    @Override
    public int getMessageType() {
        return 23456;
    }

}