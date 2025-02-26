package com.brawl.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.brawl.logic.battle.LogicBattleState;
import com.brawl.logic.battle.LogicBattleType;
import com.brawl.logic.command.server.LogicKeyPoolChangedCommand;
import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicCardData;
import com.brawl.logic.data.LogicCardData.MetaType;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.data.LogicLocationData;
import com.brawl.logic.data.LogicNameColorData;
import com.brawl.logic.data.LogicPlayerThumbnailData;
import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.logic.home.notifications.BaseNotification;
import com.brawl.logic.home.notifications.LogicNotificationFactory;
import com.brawl.logic.math.LogicBitList;
import com.brawl.logic.message.home.AvailableServerCommandMessage;
import com.brawl.logic.utils.HashTagCodeGenerator;
import com.brawl.server.ServerConfiguration;
import com.brawl.server.Ticker;
import com.brawl.server.DataBase.DataBaseManager;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;
import com.mongodb.client.model.Updates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
// NOTE: this class needs lot of refactoring
// TODO: stop using synchronized and use RW lock instead
// ! the current code is not guaranteed to be thread safe, it is spaghetti!
public class LogicPlayer {

    private interface SaveCallback {
        Bson run(LogicPlayer player);
    }

    private int highId, lowId;

    private String token, name;

    @Default
    private int trophies = 0,
            highestTrophies = 0,
            starPoints = 0,
            gold = 100,
            diamonds = 0,
            trophyRoadCollectedRewards = 0,
            draws = 0, // for boxes
            expPoints = 0,
            tokenDoublers = 0,
            tokens = 0,
            availableBattleTokens = 200,
            brawlPassSeason = ServerConfiguration.BRAWLPASS_SEASON,
            brawl_boxes = 0, big_boxes = 0, mega_boxes = 0; // future stats reason

    @Default
    private LogicBitList freeBrawlPass = new LogicBitList(128),
            premiumBrawlPass = new LogicBitList(128);

    private boolean premiumBrawlPassBought;

    @Default
    private LogicHeroData selectedHero = new LogicHeroData(LogicCharacterData.getDefaultCharacter());

    private LogicLocationData selectedLocation;

    @Default
    private HashMap<Integer, LogicHeroData> heroes = new HashMap<>();

    @Default
    private ArrayList<LogicSkinData> unlockedSkins = new ArrayList<>();

    @Default
    private List<LogicCardData> unlockedCards = new ArrayList<>();

    private ClientConnection clientConnection;

    @Default
    private LogicPlayerThumbnailData selectedThumbnail = LogicDataTables.getDataById(LogicDataTables.PLAYER_THUMBNAILS,
            0);

    @Default
    private LogicNameColorData selectedNameColor = LogicDataTables.getDataById(LogicDataTables.NAME_COLORS, 0);

    @Default
    private ArrayList<BaseNotification> notifications = new ArrayList<>();

    @Default
    private HashMap<Integer, Integer> eventsSeen = new HashMap<>();

    @Default
    private long serverStartTime = 0,
            lastLogin = 0,
            nextBattleTokensReset = -1;
    // * Data that is not for the database, aka temporary
    private LogicLocationData battleLocationData;

    private boolean isBot, admin, hasChangeInNotifications; // we do not want to save notifications without changes on

    // every .save()
    @Default
    private int serverTick = Ticker.getServerTick(),
            gainedTrophies = 0,
            gainedStarpoints = 0;

    @Default
    private long lastDbSync = System.currentTimeMillis();

    private static final long DB_SYNC_MS = TimeUnit.MINUTES.toMillis(10);

    @Default
    private HashMap<String, SaveCallback> saves = new HashMap<>();
    private LogicBattleType battleType;
    private LogicBattleState battleState;

    private String hashTag;

    public LogicPlayer(int high, int low) {
        this.highId = high;
        this.lowId = low;
        heroes = new HashMap<>();
        unlockedSkins = new ArrayList<>();
        unlockedCards = new ArrayList<>();
        notifications = new ArrayList<>();
        premiumBrawlPass = new LogicBitList(128);
        freeBrawlPass = new LogicBitList(128);
        // eventsSeen = new HashMap<>();
        tokens = 200;
        nextBattleTokensReset = -1;
    }

    public static LogicPlayer fromDocument(Document doc) {
        LogicPlayer player = new LogicPlayerBuilder()
                .build()
                .init(false);

        player.token = doc.getString("token");

        player.trophies = doc.getInteger("tr", 0);
        player.highestTrophies = doc.getInteger("htr", 0);

        player.name = doc.getString("nm");

        player.starPoints = doc.getInteger("res_sp", 0);

        player.gold = doc.getInteger("res_g", 0);

        player.diamonds = doc.getInteger("res_d", 0);

        player.trophyRoadCollectedRewards = doc.getInteger("rew_tr", 0);

        player.draws = doc.getInteger("draws", 0);

        player.expPoints = doc.getInteger("exp", 0);

        player.tokenDoublers = doc.getInteger("dblrs", 0);

        player.tokens = doc.getInteger("tokens", 0);

        player.availableBattleTokens = doc.getInteger("avbt", 0);

        player.brawlPassSeason = doc.getInteger("bpSeason", ServerConfiguration.BRAWLPASS_SEASON);

        HashMap<Integer, Integer> eventsSeen = player.getEventsSeen();
        Document events = ((Document) doc.get("events"));
        if (events != null)
            for (var entry : events.entrySet())
                eventsSeen.put(Integer.parseInt(entry.getKey()), (Integer) entry.getValue());

        List<Integer> bpFree = doc.getList("res_bpFree", Integer.class);
        if (bpFree != null)
            player.freeBrawlPass.setData(bpFree.stream().mapToInt(Integer::intValue).toArray());

        List<Integer> bpPrem = doc.getList("res_bpPrem", Integer.class);
        if (bpPrem != null)
            player.premiumBrawlPass.setData(bpPrem.stream().mapToInt(Integer::intValue).toArray());

        player.premiumBrawlPassBought = doc.getBoolean("has_prem", false);

        player.selectedLocation = LogicDataTables.getDataByGlobalId(doc.getInteger("s_loc", -1));

        player.selectedThumbnail = LogicDataTables.getDataByGlobalId(doc.getInteger("icon", -1));
        if (player.selectedThumbnail == null)
            player.selectedThumbnail = LogicDataTables.getDataById(LogicDataTables.PLAYER_THUMBNAILS, 0);

        player.selectedNameColor = LogicDataTables.getDataByGlobalId(doc.getInteger("nc", -1));
        if (player.selectedNameColor == null)
            player.selectedNameColor = LogicDataTables.getDataById(LogicDataTables.NAME_COLORS, 0);

        List<Integer> cards = doc.getList("cards", Integer.class);
        if (cards != null) {
            List<LogicCardData> playerCards = player.getUnlockedCards();
            for (int card : cards)
                playerCards.add(LogicDataTables.getDataByGlobalId(card));
        }

        Document heroes = (Document) doc.get("heroes");
        HashMap<Integer, LogicHeroData> playerHeroes = player.getHeroes();
        for (var hero : heroes.entrySet())
            playerHeroes.put(Integer.parseInt((String) hero.getKey()),
                    LogicHeroData.fromDocument((Document) hero.getValue()));

        player.selectedHero = player.heroes
                .get(doc.getInteger("s_hero", LogicCharacterData.getDefaultCharacter().getDataId()));

        List<Integer> skins = doc.getList("skins", Integer.class);
        if (skins != null) {
            ArrayList<LogicSkinData> playerSkins = player.getUnlockedSkins();
            for (int skin : skins)
                playerSkins.add(LogicDataTables.getDataByGlobalId(skin));
        }

        List<Document> notifs = doc.getList("notifs", Document.class);
        if (notifs != null) {
            ArrayList<BaseNotification> notifications = player.getNotifications();
            for (Document nf : notifs) {
                notifications.add(LogicNotificationFactory.fromBson(nf));
            }
        }

        player.unlockHero(LogicDataTables.getCharacterDataByName("HookDude"));

        return player;
    }

    public LogicPlayer init(boolean isNew) {
        if (isNew)
            unlockHero(selectedHero);
        hashTag = HashTagCodeGenerator.toCode(highId, lowId);
        return this;
    }

    public LogicPlayer init() {
        return init(true);
    }

    public void updateDailyData() {
        if (notifications != null) {
            notifications.sort((a, b) -> (int) (b.getNotificationTime() - a.getNotificationTime()));
        }
        if (availableBattleTokens != 200) {
            while (System.currentTimeMillis() >= nextBattleTokensReset && availableBattleTokens != 200) {
                availableBattleTokens = Math.min(200, availableBattleTokens + 20);
                nextBattleTokensReset += TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(30);
            }
            if (availableBattleTokens == 200) {
                nextBattleTokensReset = -1;
            }
        }
    }

    public void update() throws IOException {
        if (availableBattleTokens != 200 && System.currentTimeMillis() > nextBattleTokensReset) {
            if ((availableBattleTokens = Math.min(availableBattleTokens + 20, 200)) == 200) {
                nextBattleTokensReset = -1;
            } else {
                nextBattleTokensReset = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)
                        + TimeUnit.MINUTES.toMillis(30);
            }
            if (clientConnection.getState() == State.HOME)
                clientConnection.getMessageManager()
                        .sendMessage(new AvailableServerCommandMessage(new LogicKeyPoolChangedCommand(this)));
        }
        if (lastDbSync + DB_SYNC_MS < System.currentTimeMillis()) {
            synchronized (this) {
                save();
                lastDbSync = System.currentTimeMillis();
            }
        }
    }

    public void removeNotification(BaseNotification notification) {
        notifications.remove(notification);
        hasChangeInNotifications = true;
    }

    public void replaceNotifications(BaseNotification oldNotification, BaseNotification newNotification) {
        int idx = 0;
        int id = oldNotification.getNotificationId();
        for (BaseNotification notification : notifications) {
            if (notification.getNotificationId() == id) {
                break;
            }
            idx++;
        }
        notifications.set(idx, newNotification);
        hasChangeInNotifications = true;
    }

    public void addNotification(BaseNotification notification) {
        notifications.add(notification);
        if (notifications.size() >= 40) {
            long oldestTime = System.currentTimeMillis();
            int oldestId = 0;
            int id = 0;
            for (BaseNotification notify : notifications) {
                if (oldestTime < notify.getNotificationTime()) {
                    oldestTime = notify.getNotificationTime();
                    oldestId = id;
                }
                id++;
            }
            notifications.remove(oldestId);
        }
        notification.setNotificationId(notifications.size());
        notification.setNotificationTime(System.currentTimeMillis());

        hasChangeInNotifications = true;

        notifications.sort((a, b) -> (int) (b.getNotificationTime() - a.getNotificationTime()));
    }

    public boolean isBot() {
        return isBot;
    }

    public boolean isCardSelected(LogicCardData card) {
        int heroId = card.getCharacterData().getDataId();
        LogicHeroData heroData = heroes.get(heroId);
        if (heroData == null)
            return false;
        if (card.getMetaType() == MetaType.ACCESSORY)
            return heroData.getSelectedGadget() != null && heroData.getSelectedGadget().getDataId() == card.getDataId();
        else
            return heroData.getSelectedStarPower() != null
                    && heroData.getSelectedStarPower().getDataId() == card.getDataId();
    }

    public boolean areAllStarPowersUnlocked(LogicHeroData heroData) {
        LogicCardData[] heroCards = heroData.getCharacterData().getCards();
        int level = heroData.getLevel();
        for (LogicCardData card : heroCards)
            if (card.getMetaType() == LogicCardData.MetaType.UNIQUE && level == 9)
                if (!unlockedCards.contains(card))
                    return false;
        return true;
    }

    public boolean isCardUnlocked(LogicCardData card) {
        return unlockedCards.contains(card);
    }

    public boolean areAllGadgetsUnlocked(LogicHeroData heroData) {
        LogicCardData[] heroCards = heroData.getCharacterData().getCards();
        int level = heroData.getLevel();
        for (LogicCardData card : heroCards)
            if (card.getMetaType() == LogicCardData.MetaType.ACCESSORY && level > 7)
                if (!unlockedCards.contains(card))
                    return false;
        return true;
    }

    public boolean areAllHeroCardsUnlocked(LogicHeroData heroData) {
        return areAllStarPowersUnlocked(heroData) && areAllGadgetsUnlocked(heroData);
    }

    public LogicPlayer unlockCard(LogicCardData card) {
        synchronized (this) {
            unlockedCards.add(card);
            int characterId = card.getCharacterData().getDataId();
            saves.putIfAbsent("cards", (LogicPlayer player) -> {
                int[] cards = new int[unlockedCards.size()];
                int idx = 0;
                for (LogicCardData cardData : unlockedCards) {
                    cards[idx++] = cardData.getGlobalId();
                }
                return Updates.set("cards", cards);
            });
            if (!isCardSelected(card)) {
                selectCard(getHero(characterId), card);
            }
            return this;
        }
    }

    public LogicPlayer selectCard(LogicHeroData heroData, LogicCardData card) {
        int metaType = card.getMetaType();

        if (metaType == MetaType.ACCESSORY)
            setSelectedGadget(heroData, card);
        else
            setSelectedStarPower(heroData, card);

        return this;
    }

    public LogicHeroData getHero(int id) {
        synchronized (this) {
            return heroes.get(id);
        }
    }

    public void markHeroesModified() {
        saves.putIfAbsent("heroes", (LogicPlayer player) -> {
            HashMap<Integer, LogicHeroData> playerHeroes = player.getHeroes();
            HashMap<String, Document> heroes = new HashMap<>(playerHeroes.size());
            for (LogicHeroData hero : playerHeroes.values()) {
                heroes.put(Integer.toString(hero.getCharacterData().getDataId()),
                        hero.toDocument());
            }
            return Updates.set("heroes", heroes);
        });
    }

    public LogicPlayer unlockHero(LogicHeroData heroData) {
        synchronized (this) {
            heroes.put(heroData.getCharacterData().getDataId(), heroData);
            markHeroesModified();
        }
        return this;
    }

    public LogicPlayer unlockHero(LogicCharacterData characterData) {
        unlockHero(new LogicHeroData(characterData));
        return this;
    }

    public LogicPlayer unlockSkin(LogicSkinData skinData) {
        synchronized (this) {
            unlockedSkins.add(skinData);
            saves.putIfAbsent("skins", (LogicPlayer player) -> {
                ArrayList<LogicSkinData> playerSkins = player.getUnlockedSkins();
                int[] skins = new int[playerSkins.size()];
                int idx = 0;
                for (LogicSkinData skin : playerSkins) {
                    skins[idx++] = skin.getGlobalId();
                }
                return Updates.set("skins", skins);
            });
        }
        return this;
    }

    public void saveHeroModification(int heroId, String key, int value) {
        // String k = String.format("heroes.%d.%s", heroId, key);
        // saves.putIfAbsent(k, (LogicPlayer player) -> {
        // return Updates.set(k, value);
        // });
        markHeroesModified();
    };

    public LogicPlayer setSelectedSkin(LogicHeroData heroData, LogicSkinData skinData) {
        synchronized (this) {
            heroData.setSelectedSkin(skinData);
            saveHeroModification(heroData.getCharacterData().getDataId(), "sk", skinData.getGlobalId());
            return this;
        }
    }

    public LogicPlayer setHeroPowerPoints(LogicHeroData heroData, int powerPoints) {
        synchronized (this) {
            heroData.setPowerPoints(powerPoints);
            saveHeroModification(heroData.getCharacterData().getDataId(), "pp", powerPoints);
            return this;
        }
    }

    public LogicPlayer setHeroLevel(LogicHeroData heroData, int level) {
        synchronized (this) {
            heroData.setLevel(level);
            saveHeroModification(heroData.getCharacterData().getDataId(), "lvl", level);
            return this;
        }
    }

    public LogicPlayer setSelectedGadget(LogicHeroData heroData, LogicCardData gadget) {
        synchronized (this) {
            heroData.setSelectedGadget(gadget);
            saveHeroModification(heroData.getCharacterData().getDataId(), "gd", gadget.getGlobalId());
            return this;
        }
    }

    public LogicPlayer setSelectedStarPower(LogicHeroData heroData, LogicCardData starPower) {
        synchronized (this) {
            heroData.setSelectedStarPower(starPower);
            saveHeroModification(heroData.getCharacterData().getDataId(), "sp", starPower.getGlobalId());
            return this;
        }
    }

    public LogicPlayer setHeroTrophies(LogicHeroData heroData, int trophies) {
        synchronized (this) {
            heroData.setTrophies(trophies);
            saveHeroModification(heroData.getCharacterData().getDataId(), "tr", trophies);
            saveHeroModification(heroData.getCharacterData().getDataId(), "htr", heroData.getHighestTrophies());
            return this;
        }
    }

    public void setAdminState(boolean admin) {
        this.admin = admin;
    }

    public boolean isInTeam() {
        return selectedLocation != null;
    }

    public boolean isSkinUnlocked(int skinId) {
        for (LogicSkinData skinData : unlockedSkins)
            if (skinData.getDataId() == skinId)
                return true;
        return false;
    }

    public void gainStarpoints(int starpoints) {
        gainedStarpoints += starpoints;
    }

    public void gainTrophies(int trophies) {
        gainedTrophies += trophies;
    }

    public void resetGainedTrophies() {
        gainedTrophies = 0;
    }

    public void resetGainedStarpoints() {
        gainedStarpoints = 0;
    }

    public boolean isInBattle() {
        return battleType != LogicBattleType.NONE;
    }

    public boolean isNameSet() {
        return name != null;
    }

    public void setTrophies(int trophies) {
        synchronized (this) {
            this.trophies = trophies;
            if (trophies > highestTrophies)
                setHighestTrophies(trophies);
            saves.putIfAbsent("trophies", (LogicPlayer player) -> {
                return Updates.set("tr", player.getHighestTrophies());
            });
        }
    }

    public LogicPlayer setHighestTrophies(int highestTrophies) {
        synchronized (this) {
            this.highestTrophies = highestTrophies;
            saves.putIfAbsent("highestTrophies", (LogicPlayer player) -> {
                return Updates.set("htr", player.getHighestTrophies());
            });
        }
        return this;
    }

    public LogicPlayer setName(String name) {
        synchronized (this) {
            this.name = name;
            saves.putIfAbsent("name", (LogicPlayer player) -> {
                return Updates.set("nm", player.getName());
            });
        }
        return this;
    }

    public LogicPlayer setStarPoints(int starPoints) {
        synchronized (this) {
            this.starPoints = starPoints;
            saves.putIfAbsent("starPoints", (LogicPlayer player) -> {
                return Updates.set("res_sp", player.getStarPoints());
            });
        }
        return this;
    }

    public LogicPlayer setGold(int gold) {
        synchronized (this) {
            this.gold = gold;
            saves.putIfAbsent("gold", (LogicPlayer player) -> {
                return Updates.set("res_g", player.getGold());
            });
        }
        return this;
    }

    public LogicPlayer setDiamonds(int diamonds) {
        synchronized (this) {
            this.diamonds = diamonds;
            saves.putIfAbsent("diamonds", (LogicPlayer player) -> {
                return Updates.set("res_d", player.getDiamonds());
            });
        }
        return this;
    }

    public LogicPlayer setTrophyRoadCollectedRewards(int trophyRoadCollectedRewards) {
        synchronized (this) {
            this.trophyRoadCollectedRewards = trophyRoadCollectedRewards;
            saves.putIfAbsent("trRewards", (LogicPlayer player) -> {
                return Updates.set("rew_tr", player.getTrophyRoadCollectedRewards());
            });
        }
        return this;
    }

    public LogicPlayer setDraws(int draws) {
        synchronized (this) {
            this.draws = draws;
            saves.putIfAbsent("draws", (LogicPlayer player) -> {
                return Updates.set("draws", player.getDraws());
            });
        }
        return this;
    }

    public LogicPlayer setExpPoints(int expPoints) {
        synchronized (this) {
            this.expPoints = expPoints;
            saves.putIfAbsent("expPoints", (LogicPlayer player) -> {
                return Updates.set("exp", player.getExpPoints());
            });
        }
        return this;
    }

    public LogicPlayer setTokenDoublers(int tokenDoublers) {
        synchronized (this) {
            this.tokenDoublers = tokenDoublers;
            saves.putIfAbsent("tokenDoublers", (LogicPlayer player) -> {
                return Updates.set("dblrs", player.getTokenDoublers());
            });
        }
        return this;
    }

    public LogicPlayer setTokens(int tokens) {
        synchronized (this) {
            this.tokens = tokens;
            saves.putIfAbsent("tokens", (LogicPlayer player) -> {
                return Updates.set("tks", player.getTokens());
            });
        }
        return this;
    }

    public LogicPlayer setAvailableBattleTokens(int availableBattleTokens) {
        synchronized (this) {
            this.availableBattleTokens = availableBattleTokens;
            saves.putIfAbsent("avBattleTokens", (LogicPlayer player) -> {
                return Updates.set("avbt", player.getAvailableBattleTokens());
            });
        }
        return this;
    }

    public LogicPlayer setBrawlPassSeason(int brawlPassSeason) {
        synchronized (this) {
            this.brawlPassSeason = brawlPassSeason;
            saves.putIfAbsent("brawlPassSeason", (LogicPlayer player) -> {
                return Updates.set("bpSeason", player.getBrawlPassSeason());
            });
        }
        return this;
    }

    public LogicPlayer markChangeInEventsSeen() {
        synchronized (this) {
            saves.putIfAbsent("eventsSeen", (LogicPlayer player) -> {
                HashMap<String, Integer> events = new HashMap<>();
                for (var entry : player.getEventsSeen().entrySet()) {
                    events.put(Integer.toString(entry.getKey()), entry.getValue());
                }
                return Updates.set("events", events);
            });
        }
        return this;
    }

    public LogicPlayer markFreePassRewardsModified() {
        synchronized (this) {
            saves.putIfAbsent("freeBrawlPass", (LogicPlayer player) -> {
                return Updates.set("rew_bpFree", player.getFreeBrawlPass().getList());
            });
        }
        return this;
    }

    public LogicPlayer markPremPassRewardsModified() {
        synchronized (this) {
            saves.putIfAbsent("premiumBrawlPass", (LogicPlayer player) -> {
                return Updates.set("rew_bpPrem", player.getPremiumBrawlPass().getList());
            });
        }
        return this;
    }

    public LogicPlayer setPremiumBrawlPassBought(boolean isPremiumBrawlPassBought) {
        synchronized (this) {
            this.premiumBrawlPassBought = isPremiumBrawlPassBought;
            saves.putIfAbsent("bpState", (LogicPlayer player) -> {
                return Updates.set("has_prem", player.isPremiumBrawlPassBought());
            });
        }
        return this;
    }

    public LogicPlayer setSelectedHero(LogicHeroData hero) {
        synchronized (this) {
            this.selectedHero = hero;
            saves.putIfAbsent("selectedHero", (LogicPlayer player) -> {
                return Updates.set("s_hero", player.getSelectedHero().getCharacterData().getDataId());
            });
        }
        return this;
    }

    public LogicPlayer setSelectedLocation(LogicLocationData locationData) {
        synchronized (this) {
            this.selectedLocation = locationData;
            saves.putIfAbsent("selectedLocation", (LogicPlayer player) -> {
                return Updates.set("s_loc",
                        player.getSelectedLocation() == null ? -1 : player.getSelectedLocation().getGlobalId());
            });
        }
        return this;
    }

    public LogicPlayer setSelectedThumbnail(LogicPlayerThumbnailData thumbnailData) {
        synchronized (this) {
            this.selectedThumbnail = thumbnailData;
            saves.putIfAbsent("icon", (LogicPlayer player) -> {
                return Updates.set("icon", player.getSelectedThumbnail().getGlobalId());
            });
        }
        return this;
    }

    public LogicPlayer setSelectedTNameColor(LogicNameColorData nameColorData) {
        synchronized (this) {
            this.selectedNameColor = nameColorData;
            saves.putIfAbsent("nameColor", (LogicPlayer player) -> {
                return Updates.set("nc", player.getSelectedNameColor().getGlobalId());
            });
        }
        return this;
    }

    public void executePendingEvents() {
    }

    public void save() {
        if (isAdmin())
            return;
        synchronized (this) {
            Bson[] updates = new Bson[saves.size() + (hasChangeInNotifications ? 1 : 0)];
            int idx = 0;
            if (hasChangeInNotifications) {
                idx++;
                Document[] notifs = new Document[notifications.size()];
                int i = 0;
                for (BaseNotification baseNotification : notifications)
                    notifs[i++] = baseNotification.toDocument();

                updates[0] = Updates.set("notifs", notifs);
            }
            for (SaveCallback callback : saves.values()) {
                updates[idx++] = callback.run(this);
                System.out.println(updates[idx - 1].toString());
            }
            if (updates.length > 0)
                DataBaseManager.getPlayers().updateBson(highId, lowId, Updates.combine(updates));
            saves.clear();
        }
        System.out.println("saved player");
    }

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof LogicPlayer other))
            return false;
        return other.getHighId() == highId && other.getLowId() == lowId;
    }

}
