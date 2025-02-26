package com.brawl.logic.data;

import java.util.ArrayList;

import com.brawl.logic.csv.LogicDataTables;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LogicCharacterData extends LogicData {

    public static enum Rarity {
        COMMON,
        RARE,
        SUPER_RARE,
        EPIC,
        MYTHIC,
        LEGENDARY,
        CHROMATIC
    }

    private static LogicCharacterData defaultCharacter;

    public static LogicCharacterData getDefaultCharacter() {
        if (defaultCharacter == null)
            defaultCharacter = LogicDataTables.getCharacterDataByName("ShotgunGirl");
        return defaultCharacter;
    }

    private Rarity rarity;

    private String name, defaultSkin;

    private boolean hero, disabled, lockedForChronos;

    private LogicCardData cardData, gadget, starPower;

    private LogicCardData[] cards; // contains both first and second gadgets and sps

    private LogicSkinData defaultSkinData;

    public void setName(String name) {
        this.name = name;

        if ((cardData = LogicDataTables.getCardDataByTarget(name, LogicCardData.MetaType.UNLOCK)) == null)
            return;
        starPower = LogicDataTables.getCardDataByTarget(name, LogicCardData.MetaType.UNIQUE);
        gadget = LogicDataTables.getCardDataByTarget(name, LogicCardData.MetaType.ACCESSORY);

        ArrayList<LogicCardData> cards = new ArrayList<>();
        LogicDataTables.getCardsDataByTarget(name, LogicCardData.MetaType.UNIQUE, cards);
        LogicDataTables.getCardsDataByTarget(name, LogicCardData.MetaType.ACCESSORY, cards);

        this.cards = cards.toArray(new LogicCardData[0]);

        cardData.setCharacterData(this);

        if (cardData.getDynamicRarityStartSeason() != 0)
            rarity = Rarity.CHROMATIC;
        else
            rarity = switch (cardData.getRarity().toLowerCase()) {
                case "common" -> Rarity.COMMON;
                case "rare" -> Rarity.RARE;
                case "super_rare" -> Rarity.SUPER_RARE;
                case "epic" -> Rarity.EPIC;
                case "mega_epic" -> Rarity.MYTHIC;
                case "legendary" -> Rarity.LEGENDARY;
                default -> null;
            };

        if (name.equals("Sherryl"))
            defaultCharacter = this;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public void setLockedForChronos(Boolean lockedForChronos) {
        this.lockedForChronos = lockedForChronos;
    }

    public void setType(String type) {
        hero = type.toLowerCase().equals("hero");
    }

}
