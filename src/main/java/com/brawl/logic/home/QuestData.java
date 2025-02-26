package com.brawl.logic.home;

import java.util.ArrayList;

import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.math.LogicRandom;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@SuppressWarnings("unused")
public class QuestData {

    // TODO: remove other special gamemodes types and define the special gamemodes too
    private static final int[] GAMEMODES_VARIATIONS_MODE_NORMAL = new int[]{3, 0, 2, 5, 6, 11, 14, 15, 16, 17};
    private LogicCharacterData characterData;
    private int gamemodeVariation,
            currentLevel,
            goalProgress,
            maxGoal,
            tokensReward,
            questType;

    /*
         case BOUNTYHUNTER -> 3;
         case COINRUSH -> 0;
         case ATTACKDEFEND -> 2;
         case LASERBALL -> 5;
         case BATTLEROYALE -> 6;
         case BOSSFIGHT -> 10;
         case ROBOWARS -> 11;
         case BOSSRACE -> 14;
         case SOLOBOUNTY -> 15;
         case CAPTURETHEFLAG -> 16;
         case KINGOFHILL -> 17;
         case RAID_TOWNCRUSH -> 18;
                      */
    private boolean isDailyQuest,
            isPremiumSeasonQuest,
            isQuestSeen;

    public static QuestData makeTestQuest() {
        return new QuestData()
                .setQuestType(1)
                .setTokensReward(500)
                .setGoalProgress(0)
                .setMaxGoal(8)
                .setDailyQuest(false)
                .setGamemodeVariation(0)
                .setCharacterData(null)
                .setPremiumSeasonQuest(true);
    }

    // 597EEC
    public void encode(ByteStream stream) {
        stream.writeVInt(0);
        stream.writeVInt(0);
        // 3 => deal x points of damage in ..., 2 => defeat x enemies, 1 => win x games,
        // 4 => heal x points
        stream.writeVInt(questType); // type
        stream.writeVInt(goalProgress); // goal
        stream.writeVInt(maxGoal); // max goal
        stream.writeVInt(tokensReward); // tokens reward
        stream.writeVInt(0);
        stream.writeVInt(currentLevel); // current level
        stream.writeVInt(currentLevel != -1 ? 5 : 0); // max level for special events
        stream.writeVInt(isDailyQuest ? 2 : -1); // 2 => daily quest, else => season quest
        stream.writeBooleans(
                isPremiumSeasonQuest, // is premium pass quest
                isQuestSeen// is quest seen
        );
        stream.writeDataReference(characterData); // character data
        stream.writeVInt(gamemodeVariation); // gamemode variation, -1 => null
        stream.writeVInt(0);
        stream.writeVInt(0);
    }

    public QuestData generateQuest(ArrayList<LogicHeroData> unlockedHeroes, boolean isDailyQuest) {
        switch (LogicRandom.rangedInt(1, 4)) {
            case QuestType.WIN_GAMES -> {
                QuestData questData = new QuestData()
                        .setQuestType(QuestType.WIN_GAMES);
                switch (LogicRandom.rangedInt(0, 2)) {
                    case 0 -> {
                        questData.setCharacterData(unlockedHeroes.get(LogicRandom.rangedInt(0, unlockedHeroes.size() - 1)).getCharacterData());
                        questData.setGamemodeVariation(-1);
                    }
                    case 1 -> {

                        // questData.setGamemodeVariation(swi);
                    }
                }
                return questData;
            }
        }
        return null;
    }

    public static class QuestType {
        public static final int WIN_GAMES = 1,
                DEFEAT_ENEMIES = 2,
                DEAL_DAMAGE = 3,
                HEAL = 4;
    }

}
