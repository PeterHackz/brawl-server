package com.brawl.logic.message.home;

import java.util.Collection;
import java.util.List;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicCardData;
import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.home.LogicConfData;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.logic.home.LogicQuests;
import com.brawl.logic.home.QuestData;
import com.brawl.logic.home.notifications.BaseNotification;
import com.brawl.logic.home.notifications.LogicNotificationFactory;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.ServerConfiguration;
import com.brawl.server.Ticker;
import com.brawl.server.network.ClientConnection;

public class OwnHomeDataMessage extends PiranhaMessage {

    public OwnHomeDataMessage() {
        super(2000);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();

        LogicPlayer player = clientConnection.getLogicPlayer();
        Collection<LogicHeroData> heroes = player.getHeroes().values();

        //// var objects = player.getSelectedObjectsData();
        //// var selectedSkins = objects.get(0);
        //// var selectedSpg = objects.get(1);
        //// var selectedPins = objects.get(2);

        player.setServerTick(Ticker.getServerTick());

        stream.writeVInt(0);

        stream.writeVInt(0);

        stream.writeVInt(player.getTrophies());

        stream.writeVInt(player.getHighestTrophies());

        stream.writeVInt(0);

        stream.writeVInt(player.getTrophyRoadCollectedRewards() + 1); // Trophy Road Reward

        stream.writeVInt(100); // exp points

        stream.writeDataReference(28, 0); // thumbnail

        stream.writeDataReference(43, 0); // name color

        stream.writeVInt(20); // GameModes Battle Tips
        for (int x = 0; x < 20; x++)
            stream.writeVInt(x);

        // selected skins Array
        stream.writeVInt(heroes.size());
        for (LogicHeroData hero : heroes) {
            stream.writeDataReference(hero.getSelectedSkin());
        }

        // unlocked skins array
        List<LogicSkinData> skins = player.getUnlockedSkins();
        stream.writeVInt(skins.size());
        for (LogicSkinData skin : skins)
            stream.writeDataReference(skin);

        stream.writeVInt(0);

        stream.writeVInt(0);

        stream.writeVInt(0);

        stream.writeVInt(0);

        stream.writeBoolean(false);

        stream.writeVInt(1);

        stream.writeVInt(player.getTokenDoublers());

        stream.writeVInt(86400); // Trophy League End Timer

        stream.writeVInt(3600);

        // season end timer
        stream.writeVInt(
                Math.max((int) ((ServerConfiguration.SEASON_END_DATE - System.currentTimeMillis()) / 1000), 0));

        stream.writeVInt(0);

        stream.writeBoolean(false);

        stream.writeVInt(0);

        stream.writeByte(4);

        stream.writeVInt(2);

        stream.writeVInt(2);

        stream.writeVInt(2);

        stream.writeVInt(1); // Name Change Cost

        stream.writeVInt(0); // Name Change Timer

        //// LogicShopBundles.encode(stream, player);
        stream.writeVInt(0);

        stream.writeVInt(0); // ???

        stream.writeVInt(player.getAvailableBattleTokens()); // battle tokens

        // time left for next tokens
        stream.writeVInt(player.getNextBattleTokensReset() == -1 ? 0
                : (int) ((player.getNextBattleTokensReset() - System.currentTimeMillis()) / 1000));

        stream.writeVInt(0); // array

        stream.writeVInt(1); // Tickets

        stream.writeVInt(player.getDraws() / 30); // box draws, every 30 are one.

        stream.writeDataReference(player.getSelectedHero().getCharacterData()); // selected brawler

        stream.writeString("LB");

        stream.writeString("Multi-Brawl");

        // LogicDailyData int value entry
        stream.writeVInt(3); // count

        stream.writeInt(3);
        stream.writeInt(0); // gained tokens

        stream.writeInt(4); // gained trophies
        stream.writeInt(player.getGainedTrophies() > 0 ? player.getGainedTrophies() : 0);
        player.resetGainedTrophies();

        stream.writeInt(8); // gained starpoints
        stream.writeInt(player.getGainedStarpoints());
        player.resetGainedStarpoints();
        // LogicDailyData int value entry end

        stream.writeVInt(0);

        // brawl pass season state
        if ((ServerConfiguration.SEASON_END_DATE - System.currentTimeMillis()) > 0) {
            stream.writeVInt(1);
            stream.writeVInt(ServerConfiguration.BRAWLPASS_SEASON); // current season milestone id
            stream.writeVInt(player.getTokens()); // brawl pass tokens
            stream.writeBoolean(player.isPremiumBrawlPassBought()); // premium pass activated
            stream.writeVInt(0);

            stream.writeVInt(1); // has bit list data
            player.getPremiumBrawlPass().encode(stream);

            stream.writeVInt(1); // has bit list data
            player.getFreeBrawlPass().encode(stream);
        } else
            stream.writeVInt(0);

        stream.writeVInt(0);

        // Quests Array
        stream.writeBoolean(true);
        LogicQuests logicQuests = new LogicQuests();
        logicQuests.getQuests().add(QuestData.makeTestQuest());
        logicQuests.encode(stream);
        // stream.writeVInt(0);

        // Emotes Array

        stream.writeBoolean(true);

        int pins = LogicDataTables.getDataTable(LogicDataTables.EMOTES).length;

        stream.writeVInt(pins);
        for (int pin = 0; pin < pins; pin++) {
            stream.writeDataReference(52, pin);
            stream.writeVInt(1);
            stream.writeVInt(1); //// selectedPins.contains(pin) ? 2 : 1);
            stream.writeVInt(1);
        }

        LogicConfData.encode(stream, player);

        int hi = player.getHighId();
        int lo = player.getLowId();

        stream.writeInt(hi); // high id

        stream.writeInt(lo); // low id

        stream.writeVInt(
                (LogicNotificationFactory.PROMO_POPUP_HASH == null ? 0 : 1) + player.getNotifications().size());

        for (BaseNotification notification : player.getNotifications()) {
            stream.writeVInt(notification.getNotificationType());
            stream.writeInt(notification.getNotificationId());
            notification.encode(stream);
        }

        if (LogicNotificationFactory.PROMO_POPUP_HASH != null) {
            stream.writeVInt(83);
            stream.writeInt(0);
            stream.writeBoolean(false);
            stream.writeInt(0);
            stream.writeString();
            stream.writeInt(0);
            stream.writeString("Multi Brawl");
            stream.writeInt(0);
            stream.writeString(
                    "Did you know Multi Brawl has an official Telegram Channel?\nClick \"JOIN\" if you are not in it yet!");
            stream.writeInt(0);
            stream.writeString("JOIN");
            stream.writeString(LogicNotificationFactory.PROMO_POPUP_IMAGE_NAME);
            stream.writeString(LogicNotificationFactory.PROMO_POPUP_HASH);
            stream.writeString(ServerConfiguration.PROMO_POPUP_DEEPLINK);
            stream.writeVInt(0);
        }

        stream.writeVInt(1);

        stream.writeBoolean(true);

        stream.writeVInt(0);

        stream.writeVInt(0);

        for (int i = 0; i < 3; i++) {
            stream.writeVInt(hi);
            stream.writeVInt(lo);
        }

        stream.writeString(player.getName());
        stream.writeVInt(player.isNameSet() ? 1 : 0);

        stream.writeInt(0);

        stream.writeVInt(8);

        // unlocked brawlers
        stream.writeVInt(4 + heroes.size());
        for (LogicHeroData hero : heroes) {
            stream.writeDataReference(hero.getCharacterData().getCardData());
            stream.writeVInt(1);
        }

        stream.writeVInt(5);
        stream.writeVInt(1);
        stream.writeVInt(0);

        stream.writeVInt(5);
        stream.writeVInt(8);
        stream.writeVInt(player.getGold()); // gold

        stream.writeVInt(5);
        stream.writeVInt(9);
        stream.writeVInt(0);

        stream.writeVInt(5);
        stream.writeVInt(10);
        stream.writeVInt(player.getStarPoints());

        // brawler trophies
        stream.writeVInt(heroes.size());
        for (LogicHeroData heroData : heroes) {
            stream.writeDataReference(heroData.getCharacterData());
            stream.writeVInt(heroData.getTrophies());
        }

        // brawler trophies for rank
        stream.writeVInt(heroes.size());
        for (LogicHeroData heroData : heroes) {
            stream.writeDataReference(heroData.getCharacterData());
            stream.writeVInt(heroData.getHighestTrophies());
        }

        stream.writeVInt(0);

        // power points
        stream.writeVInt(heroes.size());
        for (LogicHeroData heroData : heroes) {
            stream.writeDataReference(heroData.getCharacterData());
            stream.writeVInt(heroData.getLevelPoints());
        }

        // power levels
        stream.writeVInt(heroes.size());
        for (LogicHeroData heroData : heroes) {
            stream.writeDataReference(heroData.getCharacterData());
            stream.writeVInt(heroData.getLevel());
        }

        // unlocked cards
        List<LogicCardData> unlockedCards = player.getUnlockedCards();
        if (unlockedCards == null)
            stream.writeVInt(0);
        else {
            stream.writeVInt(unlockedCards.size());
            for (LogicCardData cardData : unlockedCards) {
                stream.writeDataReference(cardData);
                stream.writeVInt(player.isCardSelected(cardData) ? 2 : 1);
            }
        }

        // new brawler tag
        stream.writeVInt(0);

        stream.writeVInt(player.getDiamonds()); // gems

        stream.writeVInt(0);
        stream.writeVInt(1);

        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(2);
        stream.writeVInt(0);
    }

    @Override
    public int getMessageType() {
        return 24101;
    }

}
