package com.brawl.logic.command.client;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.command.server.LogicGiveDeliveryItemsCommand;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.DeliveryItem;
import com.brawl.logic.home.DeliveryUnit;
import com.brawl.logic.home.GatchaDrop;
import com.brawl.logic.home.GatchaDrop.BoxType;
import com.brawl.logic.home.notifications.BaseNotification;
import com.brawl.logic.home.notifications.IAPDeliveryNotification;
import com.brawl.logic.message.home.AvailableServerCommandMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicDeleteNotificationCommand extends LogicCommand {

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

        AvailableServerCommandMessage availableServerCommandMessage = null;

        if (notification instanceof IAPDeliveryNotification) {
            IAPDeliveryNotification iapDeliveryNotification = ((IAPDeliveryNotification) notification);
            DeliveryItem[] deliveryItems = new DeliveryItem[iapDeliveryNotification.getAmount()];
            for (int i = 0; i < deliveryItems.length; i++) {
                deliveryItems[i] = new DeliveryItem(
                        GatchaDrop.create(BoxType.BIG, player).toArray(new DeliveryUnit[0]));
            }
            availableServerCommandMessage = new AvailableServerCommandMessage(
                    new LogicGiveDeliveryItemsCommand(deliveryItems)
                            .setType(11)
                            .setForcedDrop(true));
            player.removeNotification(notification);
        }

        if (availableServerCommandMessage != null) {
            clientConnection.getMessageManager().sendMessage(availableServerCommandMessage);
        }

    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
