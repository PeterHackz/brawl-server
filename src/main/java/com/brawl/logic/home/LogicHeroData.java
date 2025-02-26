package com.brawl.logic.home;

import org.bson.Document;

import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicCardData;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.data.LogicSkinData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class LogicHeroData {

    public static final int[] UPGRADE_POINTS = new int[] { 20, 30, 50, 80, 130, 210, 340, 550 };

    public static final int[] UPGRADE_COST = new int[] { 20, 35, 75, 140, 290, 480, 800, 1250 };

    public static int[] LEVEL_POINTS = new int[UPGRADE_COST.length];
    private LogicCharacterData characterData;
    private int powerPoints, trophies, highestTrophies, level;
    private LogicCardData selectedStarPower, selectedGadget;
    private LogicSkinData selectedSkin;

    public LogicHeroData(LogicCharacterData characterData, int powerPoints, LogicCardData selectedStarPower,
            LogicCardData selectedGadget, LogicSkinData selectedSkin, int level) {
        this.characterData = characterData;
        this.powerPoints = powerPoints;
        this.selectedStarPower = selectedStarPower;
        this.selectedGadget = selectedGadget;
        this.selectedSkin = selectedSkin;
        this.level = level;
    }

    public LogicHeroData(LogicCharacterData characterData) {
        this(characterData, 0, characterData.getStarPower(), characterData.getGadget(),
                characterData.getDefaultSkinData(), 0);
    }

    public static void init() {
        int points = 0;
        for (int i = 0; i < UPGRADE_POINTS.length; i++) {
            points += UPGRADE_POINTS[i];
            LEVEL_POINTS[i] = points;
        }
    }

    public static int getMaxedPoints() {
        return LEVEL_POINTS[7];
    }

    public boolean isMaxed() {
        return level == 8;
    }

    public int getMaxPoints() {
        return LEVEL_POINTS[level];
    }

    public int getLevelPoints() {
        if (level == 8)
            return LEVEL_POINTS[7]; // max level
        if (level > 0 && powerPoints - LEVEL_POINTS[level] > 0)
            return powerPoints - LEVEL_POINTS[level];
        return powerPoints;
    }

    public Document toDocument() {
        return new Document("ch", characterData.getGlobalId())
                .append("pp", powerPoints)
                .append("sp",
                        selectedStarPower == null ? -1 : selectedStarPower.getGlobalId())
                .append("gd",
                        selectedGadget == null ? -1 : selectedGadget.getGlobalId())
                .append("sk",
                        selectedSkin == null ? -1 : selectedSkin.getGlobalId())
                .append("lvl", level)
                .append("tr", trophies)
                .append("htr", highestTrophies);
    }

    public static LogicHeroData fromDocument(Document doc) {
        System.out.println(doc.toJson());
        return new LogicHeroData(
                LogicDataTables.getDataByGlobalId(doc.getInteger("ch")),
                doc.getInteger("pp", 0),
                LogicDataTables.getDataByGlobalId(doc.getInteger("sp", -1)),
                LogicDataTables.getDataByGlobalId(doc.getInteger("gd", -1)),
                LogicDataTables.getDataByGlobalId(doc.getInteger("sk", -1)),
                doc.getInteger("lvl", 0))
                .setTrophies(doc.getInteger("doc", getMaxedPoints()))
                .setHighestTrophies(doc.getInteger("htr", 0));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (obj == this)
            return true;
        else if (obj instanceof LogicHeroData other) {
            return other.characterData.getDataId() == characterData.getDataId();
        }
        return false;
    }

}
