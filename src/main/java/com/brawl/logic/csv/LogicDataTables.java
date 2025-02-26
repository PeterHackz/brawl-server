package com.brawl.logic.csv;

import java.util.ArrayList;
import java.util.HashMap;

import com.brawl.logic.data.LogicCardData;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.data.LogicData;
import com.brawl.logic.data.LogicEmoteData;
import com.brawl.logic.data.LogicLocationData;
import com.brawl.logic.data.LogicMilestoneData;
import com.brawl.logic.data.LogicNameColorData;
import com.brawl.logic.data.LogicSkinConfsData;
import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.data.LogicPlayerThumbnailData;
import com.brawl.logic.home.LogicGoals;

public class LogicDataTables {

    public static final int LOCATIONS = 15,
            CHARACTERS = 16,
            CARDS = 23,
            PLAYER_THUMBNAILS = 28,
            SKINS = 29,
            MILESTONES = 39,
            NAME_COLORS = 43,
            SKIN_CONFS = 44,
            EMOTES = 52;

    private static HashMap<Integer, LogicData[]> tables = new HashMap<>();

    private static LogicCharacterData[] availableCharacters;

    public static void init() throws Exception {
        tables.put(EMOTES, DataLoaderCSV.load(LogicEmoteData.class, EMOTES, "DataTables/csv_logic/emotes.csv"));
        tables.put(CARDS, DataLoaderCSV.load(LogicCardData.class, CARDS, "DataTables/csv_logic/cards.csv"));
        tables.put(CHARACTERS,
                DataLoaderCSV.load(LogicCharacterData.class, CHARACTERS, "DataTables/csv_logic/characters.csv"));

        tables.put(LOCATIONS,
                DataLoaderCSV.load(LogicLocationData.class, LOCATIONS, "DataTables/csv_logic/locations.csv"));

        tables.put(SKIN_CONFS,
                DataLoaderCSV.load(LogicSkinConfsData.class, SKIN_CONFS, "DataTables/csv_logic/skin_confs.csv"));
        tables.put(SKINS, DataLoaderCSV.load(LogicSkinData.class, SKINS, "DataTables/csv_logic/skins.csv"));
        setUnlockedCharacters();

        tables.put(PLAYER_THUMBNAILS,
                DataLoaderCSV.load(LogicPlayerThumbnailData.class, PLAYER_THUMBNAILS,
                        "DataTables/csv_logic/player_thumbnails.csv"));

        tables.put(NAME_COLORS,
                DataLoaderCSV.load(LogicNameColorData.class, NAME_COLORS,
                        "DataTables/csv_logic/name_colors.csv"));

        tables.put(MILESTONES,
                DataLoaderCSV.load(LogicMilestoneData.class, MILESTONES, "DataTables/csv_logic/milestones.csv"));

        LogicGoals.init();
    }

    public static LogicData[] getDataTable(int table) {
        return tables.get(table);
    }

    @SuppressWarnings("unchecked")
    public static <E> E getDataById(int classId, int dataId) {
        return ((E) tables.get(classId)[dataId]);
    }

    public static <E> E getDataByGlobalId(int globalId) {
        if (globalId == -1)
            return null;
        return getDataById(globalId / 0xF4240, globalId % 0xF4240);
    }

    public static LogicCardData getCardDataByTarget(String target, int metaType) {
        for (LogicData data : tables.get(CARDS)) {
            LogicCardData card = ((LogicCardData) data);
            if (card.getTarget().equals(target) && metaType == card.getMetaType())
                return card;
        }
        return null;
    }

    public static void getCardsDataByTarget(String target, int metaType, ArrayList<LogicCardData> cards) {
        for (LogicData data : tables.get(CARDS)) {
            LogicCardData card = ((LogicCardData) data);
            if (card.getTarget().equals(target) && metaType == card.getMetaType())
                cards.add(card);
        }
    }

    public static LogicCharacterData getCharacterDataByName(String name) {
        name = name.trim();
        for (LogicData data : tables.get(CHARACTERS)) {
            LogicCharacterData characterData = ((LogicCharacterData) data);
            if (characterData.getName().trim().equals(name))
                return characterData;
        }
        return null;
    }

    public static LogicSkinData getSkinDataByName(String name) {
        for (LogicData data : tables.get(SKINS)) {
            LogicSkinData skinData = ((LogicSkinData) data);
            if (skinData.getName().equals(name))
                return skinData;
        }
        return null;
    }

    public static LogicSkinConfsData getSkinConfsDataByName(String name) {
        for (LogicData data : tables.get(SKIN_CONFS)) {
            LogicSkinConfsData skinConfsData = ((LogicSkinConfsData) data);
            if (skinConfsData.getName().equals(name))
                return skinConfsData;
        }
        return null;
    }

    private static void setUnlockedCharacters() {
        ArrayList<LogicCharacterData> characters = new ArrayList<>();
        for (LogicData data : tables.get(CHARACTERS)) {
            LogicCharacterData characterData = ((LogicCharacterData) data);
            if (characterData.isHero() && !characterData.isDisabled() && !characterData.isLockedForChronos())
                characters.add(characterData);
        }
        availableCharacters = characters.toArray(new LogicCharacterData[0]);
    }

    public static LogicCharacterData[] getAvailableCharacters() {
        return availableCharacters;
    }

    public static ArrayList<LogicLocationData> getEnabledLocationsByType(LogicLocationData.Gamemode type) {
        ArrayList<LogicLocationData> list = new ArrayList<>();
        for (LogicData data : tables.get(LOCATIONS)) {
            LogicLocationData locationData = ((LogicLocationData) data);
            if (!locationData.isDisabled() && locationData.getGamemode() == type)
                list.add(locationData);
        }
        return list;
    }

    // get duo location data
    public static LogicLocationData getSecondaryLocationData(LogicLocationData locationData) {
        for (LogicData data : tables.get(LOCATIONS)) {
            LogicLocationData ldata = ((LogicLocationData) data);
            if (!ldata.isDisabled() && ldata.getMapName().equals(locationData.getMapName())
                    && ldata.getGamemode() != locationData.getGamemode())
                return ldata;
        }
        return null;
    }

    public static void deleteTable(int table) {
        tables.remove(table);
    }

}
