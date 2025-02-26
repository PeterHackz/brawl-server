package com.brawl.logic.data;

import com.brawl.logic.csv.LogicDataTables;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LogicSkinData extends LogicData {

    private LogicSkinConfsData skinConfsData;
    LogicCharacterData characterData;

    private int cost;

    private String name;

    public void setName(String name) {
        this.name = name;
        for (LogicData data : LogicDataTables.getDataTable(LogicDataTables.CHARACTERS)) {
            LogicCharacterData characterData = ((LogicCharacterData) data);
            if (characterData.getDefaultSkin().equals(name)) {
                characterData.setDefaultSkinData(this);
                this.characterData = characterData;
                break;
            }
        }
    }

    public void setConf(String conf) {
        skinConfsData = LogicDataTables.getSkinConfsDataByName(conf);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj instanceof LogicSkinData) {
            LogicSkinData skinData = (LogicSkinData) obj;
            return skinData.getDataId() == this.getDataId();
        }
        return false;
    }

}
