package com.brawl.logic.data;

import com.brawl.logic.csv.LogicDataTables;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LogicSkinConfsData extends LogicData {

    private LogicCharacterData characterData;

    private String name;

    public void setCharacter(String character) {
        this.characterData = LogicDataTables.getCharacterDataByName(character);
    }

}
