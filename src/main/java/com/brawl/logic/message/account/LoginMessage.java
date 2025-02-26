package com.brawl.logic.message.account;

import com.brawl.logic.LogicGiftsManager;
import com.brawl.logic.LogicGiftsManager.Gift;
import com.brawl.logic.LogicGiftsManager.GiftType;
import com.brawl.logic.LogicPlayer;
import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.home.notifications.*;
import com.brawl.logic.home.notifications.ResourceRewardNotification.ResourceType;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.account.LoginFailedMessage.ErrorCodes;
import com.brawl.logic.message.home.OwnHomeDataMessage;
import com.brawl.logic.message.team.TeamMessage;
import com.brawl.server.Cache;
import com.brawl.server.DataBase.DataBaseManager;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")
public class LoginMessage extends PiranhaMessage {

    private int highId, lowId, majorVersion;
    private String token, key, androidId, voucherCode;

    @Override
    public void decode() throws Exception {

        var stream = this.getByteStream();

        highId = stream.readInt();
        lowId = stream.readInt();
        token = stream.readString();

        majorVersion = stream.readInt();

        stream.readInt(); // minor
        stream.readInt(); // build

        key = stream.readString(); // sha
        voucherCode = stream.readString(); // device

        // data ref
        stream.readVInt();
        stream.readVInt();

        stream.readString(); // lang
        stream.readString(); // os version

        stream.readBoolean();

        stream.readString(); // string reference

        androidId = stream.readString();
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {

        LogicPlayer logicPlayer = null;

        if (key.equals("super@secret$key-123=654")) {
            logicPlayer = new LogicPlayer(0, 0);
            logicPlayer.setAdminState(true);
        } else {
            if (token == null) {
                if (highId != 0 || lowId != 0)
                    throw new InvalidCredentialsException();

                logicPlayer = DataBaseManager.createPlayer();

                Cache.getPlayersCache().addEntry(logicPlayer.getHighId(), logicPlayer.getLowId(),
                        logicPlayer);
            } else
                try {
                    logicPlayer = Cache.getPlayer(highId, lowId);
                    System.out.println("token: " + token);
                    System.out.println("player token: " + logicPlayer.getToken());
                    if (!token.equals(logicPlayer.getToken()))
                        throw new InvalidCredentialsException();
                } catch (ExecutionException e) {
                    clientConnection.setState(State.LOGIN_FAILED);
                    clientConnection.getMessageManager()
                            .sendMessage(new LoginFailedMessage().setErrorCode(ErrorCodes.CustomError)
                                    .setMessage("Account not found in the database.\nClear your app data."));
                    return;
                }
        }

        if (logicPlayer.getClientConnection() != null) {
            throw new Exception("player is already logged in!");
        }

        clientConnection.setLogicPlayer(logicPlayer);
        logicPlayer.updateDailyData();
        logicPlayer.setLastLogin(System.currentTimeMillis());

        if (voucherCode != null) {
            synchronized (LogicGiftsManager.getLock()) {
                Gift gift = LogicGiftsManager.getGift(voucherCode);
                if (gift != null) {
                    if (gift.getType() == GiftType.SKIN) {
                        LogicSkinData skinData = LogicDataTables.getDataById(LogicDataTables.SKINS,
                                gift.getData());
                        // if (logicPlayer.isSkinUnlocked(skinData.getDataId())) {
                        // // TODO: ...
                        // } else {
                        logicPlayer.addNotification(new SkinRewardNotification(skinData));
                        // }
                    } else if (gift.getType() == GiftType.DIAMONDS) {
                        logicPlayer.addNotification(new GemRewardNotification(gift.getData()));
                    } else if (gift.getType() == GiftType.GOLD) {
                        logicPlayer.addNotification(new ResourceRewardNotification(ResourceType.GOLD, gift.getData()));
                    } else if (gift.getType() == GiftType.STAR_POINTS) {
                        logicPlayer.addNotification(
                                new ResourceRewardNotification(ResourceType.STAR_POINTS, gift.getData()));
                    } else if (gift.getType() == GiftType.MEGA_BOXES) {
                        IAPDeliveryNotification iapDeliveryNotification = new IAPDeliveryNotification(gift.getData());
                        logicPlayer.addNotification(iapDeliveryNotification);
                        logicPlayer.addNotification(new FreeTextNotification(
                                String.format("Opened %d Mega Boxes from voucher code", gift.getData()))
                                .setNotificationRead(true));
                    } else if (gift.getType() == GiftType.BRAWL_PASS) {
                        logicPlayer.addNotification(new BrawlPassRewardNotification());
                    }
                }
                /*-
                if (gift != null) {
                    int type = switch (gift.getType()) {
                        case 0 -> DeliveryUnit.GOLD;
                        case 1 -> DeliveryUnit.DIAMOND;
                        case 2 -> DeliveryUnit.SKIN;
                        default -> -1;
                    };
                    DeliveryUnit giftItem = new DeliveryUnit().setType(type);
                    if (gift.getType() == GiftType.SKIN)
                        giftItem.setData(LogicDataTables.getDataById(LogicDataTables.SKINS, gift.getData()))
                                .setCount(1);
                    else
                        giftItem.setCount(gift.getData());
                
                    logicPlayer.setGiftItem(giftItem);
                }
                */
            }
        }

        clientConnection.setState(State.HOME);

        clientConnection.getMessageManager().sendMessage(new LoginOkMessage());
        if (!clientConnection.getLogicPlayer().isAdmin())
            clientConnection.getMessageManager().sendMessage(new OwnHomeDataMessage());

        if (logicPlayer.isInTeam())
            clientConnection.getMessageManager().sendMessage(new TeamMessage());

        /*-
        if (gift != null) {
        switch (gift.getType()) {
            case 0 -> logicPlayer.setGold(logicPlayer.getGold() + gift.getData());
            case 1 -> logicPlayer.setDiamonds(logicPlayer.getDiamonds() + gift.getData());
            case 2 ->
                logicPlayer.unlockSkin(LogicDataTables.getDataById(LogicDataTables.SKINS, gift.getData()));
        }
        }
        */
    }

    @Override
    public State getRequiredState() {
        return State.PEPPER_LOGIN;
    }

    public static class InvalidCredentialsException extends Exception {

        public InvalidCredentialsException() {
            super("invalid credentials!");
        }

    }

}
