package com.brawl.logic.data;

import com.brawl.logic.csv.LogicDataTables;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogicPlayerThumbnailData extends LogicData {

    private int requiredExpLevel,
            requiredTotalTrophies;

    private LogicCharacterData requiredCharacter;

    public void setRequiredHero(String hero) {
        requiredCharacter = LogicDataTables.getCharacterDataByName(hero);
    }

    public boolean requiresCharacter() {
        return requiredCharacter != null;
    }

}
