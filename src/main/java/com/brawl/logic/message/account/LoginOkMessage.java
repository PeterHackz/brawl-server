package com.brawl.logic.message.account;

import com.brawl.logic.LogicVersion;
import com.brawl.logic.home.notifications.LogicNotificationFactory;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class LoginOkMessage extends PiranhaMessage {

    public LoginOkMessage() {
        super(400);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();

        var player = clientConnection.getLogicPlayer();

        int hi = player.getHighId();
        int lo = player.getLowId();

        stream.writeInt(hi);
        stream.writeInt(lo);

        stream.writeInt(hi);
        stream.writeInt(lo);

        stream.writeString(player.getToken());

        stream.writeString(); // Facebook ID
        stream.writeString(); // GameCenter ID

        stream.writeInt(LogicVersion.getMajorVersion());
        stream.writeInt(LogicVersion.getBuildVersion());
        stream.writeInt(LogicVersion.getMinorVersion());

        stream.writeString(LogicVersion.getEnvironment());

        stream.writeInt(0); // sessions count
        stream.writeInt(0); // playtime

        stream.writeInt(0); // account creation date

        stream.writeString(); // was used for discord ID
        stream.writeString();
        stream.writeString();

        stream.writeInt(0);

        stream.writeString();

        stream.writeString("LB");

        stream.writeString("");
        stream.writeInt(2);
        stream.writeString("");
        stream.writeInt(2);

        stream.writeString();
        stream.writeString();

        stream.writeInt(2);

        // promo popup url
        stream.writeString(LogicNotificationFactory.PROMO_POPUP_ASSETS_URL);
        stream.writeString(LogicNotificationFactory.PROMO_POPUP_ASSETS_URL);
    }

    @Override
    public int getMessageType() {
        return 20104;
    }

    @Override
    public int getMessageVersion() {
        return 1;
    }

}
