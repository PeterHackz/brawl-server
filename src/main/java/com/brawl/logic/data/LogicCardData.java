package com.brawl.logic.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LogicCardData extends LogicData {

    private LogicCharacterData characterData;
    private String target, rarity;
    private int metaType, dynamicRarityStartSeason, index;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof LogicCardData other))
            return false;
        if (obj == this)
            return true;
        return other.getGlobalId() == this.getGlobalId();
    }

    public static final class MetaType {
        public static final int UNLOCK = 0,
                HP = 1,
                WEAPON_SKILL = 2,
                ULTI_SKILL = 3,
                UNIQUE = 4,
                ACCESSORY = 5;
    }

}