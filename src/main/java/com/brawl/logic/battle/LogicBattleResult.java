package com.brawl.logic.battle;

import java.util.ArrayList;

import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.data.LogicLocationData;
import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.datastream.ByteStream;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LogicBattleResult {

    private ArrayList<Player> players;
    private int result, playersCount, percentage, hi, lo;
    private LogicLocationData locationData;

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Player {
        private boolean isBot;
        private LogicSkinData skinData;
        private LogicCharacterData characterData;
        private int team, highId, lowId;
        private String name;

        public Player(String name, boolean isBot, LogicCharacterData characterData, LogicSkinData skinData, int team,
                int hi, int lo) {
            this.isBot = isBot;
            this.characterData = characterData;
            this.skinData = skinData;
            this.team = team;
            this.name = name;
            highId = hi;
            lowId = lo;
        }

        public boolean hasSkin() {
            return skinData != null;
        }

    }

    public LogicBattleResult(int result, int percentage, int map, int count) {
        players = new ArrayList<>(count);
        this.result = result;
        locationData = LogicDataTables.getDataById(LogicDataTables.LOCATIONS, map);
        this.playersCount = count;
        this.percentage = percentage;
    }

    public void decodePlayerEntry(ByteStream stream) throws Exception {
        LogicCharacterData characterData = stream.readDataReference();
        LogicSkinData skinData = stream.readDataReference();
        int team = stream.readVInt();
        stream.readVInt();
        String name = stream.readString();
        players.add(new Player(name, players.size() > 0,
                characterData, skinData, team,
                -1, -1));
    }

    public int getSpecialEventPercentageResult() {
        return percentage;
    }

}