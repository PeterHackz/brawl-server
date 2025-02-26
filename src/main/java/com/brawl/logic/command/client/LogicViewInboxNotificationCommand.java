package com.brawl.logic.command.client;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.command.server.LogicGiveDeliveryItemsCommand;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.DeliveryItem;
import com.brawl.logic.home.DeliveryUnit;
import com.brawl.logic.home.GatchaDrop;
import com.brawl.logic.home.GatchaDrop.BoxType;
import com.brawl.logic.home.notifications.*;
import com.brawl.logic.home.notifications.ResourceRewardNotification.ResourceType;
import com.brawl.logic.message.home.AvailableServerCommandMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicViewInboxNotificationCommand extends LogicCommand {

    private int notificationId;

    @Override
    public void decode(ByteStream stream) {
        notificationId = stream.readVInt();
        stream.readVInt();
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {
        LogicPlayer player = clientConnection.getLogicPlayer();

        BaseNotification notification = null;
        for (BaseNotification notify : player.getNotifications()) {
            if (notify.getNotificationId() == notificationId) {
                notification = notify;
                break;
            }
        }

        if (notification == null || notification.isNotificationRead())
            return;

        player.setHasChangeInNotifications(true);

        notification.setNotificationRead(true);

        AvailableServerCommandMessage availableServerCommandMessage = null;

        if (notification instanceof SkinRewardNotification) {
            LogicSkinData skinData = ((SkinRewardNotification) notification).getSkinData();
            if (player.isSkinUnlocked(skinData.getDataId())) {
                LogicCharacterData characterData = skinData.getSkinConfsData().getCharacterData();
                DeliveryUnit hero = null;
                if (player.getHero(skinData.getDataId()) == null) {
                    hero = new DeliveryUnit()
                            .setType(DeliveryUnit.CHARACTER)
                            .setData(characterData)
                            .setCount(1);
                }
                DeliveryItem deliveryItem;
                if (hero != null) {
                    deliveryItem = new DeliveryItem(hero, new DeliveryUnit()
                            .setType(DeliveryUnit.SKIN)
                            .setCount(1)
                            .setData(skinData));
                } else {
                    deliveryItem = new DeliveryItem(new DeliveryUnit()
                            .setType(DeliveryUnit.SKIN)
                            .setCount(1)
                            .setData(skinData));
                }
                availableServerCommandMessage = new AvailableServerCommandMessage(
                        new LogicGiveDeliveryItemsCommand(deliveryItem)
                                .setType(100)
                                .setForcedDrop(true));
                player.unlockSkin(skinData);
            } else {
                availableServerCommandMessage = new AvailableServerCommandMessage(
                        new LogicGiveDeliveryItemsCommand(
                                new DeliveryItem(new DeliveryUnit()
                                        .setType(DeliveryUnit.GOLD)
                                        .setCount(5000)))
                                .setType(100)
                                .setForcedDrop(true));
                player.setGold(player.getGold() + 5000);
            }
        } else if (notification instanceof GemRewardNotification gemRewardNotification) {
            player.setDiamonds(player.getDiamonds() + gemRewardNotification.getAmount());
            availableServerCommandMessage = new AvailableServerCommandMessage(
                    new LogicGiveDeliveryItemsCommand(
                            new DeliveryItem(new DeliveryUnit()
                                    .setType(DeliveryUnit.DIAMOND)
                                    .setCount(gemRewardNotification.getAmount())))
                            .setType(100)
                            .setForcedDrop(true));
        } else if (notification instanceof ResourceRewardNotification resourceRewardNotification) {
            DeliveryUnit deliveryUnit = null;

            int amount = resourceRewardNotification.getAmount();

            switch (resourceRewardNotification.getResourceType()) {
                case ResourceType.GOLD:
                    player.setGold(player.getGold() + amount);
                    deliveryUnit = new DeliveryUnit().setType(DeliveryUnit.GOLD);
                    break;
                case ResourceType.STAR_POINTS:
                    // there is no DeliveryUnit with type of star points
                    // so we just update them here.
                    player.setStarPoints(player.getStarPoints() + amount);
                    break;
            }
            if (deliveryUnit != null) {
                deliveryUnit.setCount(amount);
                availableServerCommandMessage = new AvailableServerCommandMessage(
                        new LogicGiveDeliveryItemsCommand(
                                new DeliveryItem(deliveryUnit))
                                .setType(100)
                                .setForcedDrop(true));
            }
        } else if (notification instanceof BrawlPassRewardNotification) {
            if (player.isPremiumBrawlPassBought()) {
                availableServerCommandMessage = new AvailableServerCommandMessage(
                        new LogicGiveDeliveryItemsCommand(
                                new DeliveryItem(new DeliveryUnit()
                                        .setType(DeliveryUnit.DIAMOND)
                                        .setCount(100)))
                                .setType(100)
                                .setForcedDrop(true));
                player.setDiamonds(player.getDiamonds() + 100);
            } else {
                player.setPremiumBrawlPassBought(true);
            }
        } else if (notification instanceof IAPDeliveryNotification iapDeliveryNotification) {
            DeliveryItem[] deliveryItems = new DeliveryItem[iapDeliveryNotification.getAmount()];
            for (int i = 0; i < deliveryItems.length; i++) {
                deliveryItems[i] = new DeliveryItem(
                        GatchaDrop.create(BoxType.BIG, player).toArray(new DeliveryUnit[0]));
            }
            availableServerCommandMessage = new AvailableServerCommandMessage(
                    new LogicGiveDeliveryItemsCommand(deliveryItems));
            player.removeNotification(notification);
        }

        if (availableServerCommandMessage != null) {
            clientConnection.getMessageManager().sendMessage(availableServerCommandMessage);
        }

        /*- LogicSkinData skinData = LogicDataTables.getDataById(29, 0);
        LogicCharacterData characterData = skinData.getSkinConfsData().getCharacterData();
        clientConnection.getMessageManager()
                .sendMessage(new AvailableServerCommandMessage(
                        new LogicGiveDeliveryItemsCommand(
                                new DeliveryItem()
                                        .setType(DeliveryItem.CHARACTER)
                                        .setCount(1)
                                        .setData(characterData),
                                new DeliveryItem()
                                        .setType(DeliveryItem.SKIN)
                                        .setCount(1)
                                        .setData(skinData))
                                .setType(100).setForcedDrop(true)));
                                */
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
