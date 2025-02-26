package com.brawl.logic.message.admin;

import com.brawl.logic.LogicGiftsManager;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class CreateGiftMessage extends PiranhaMessage {

    private int type, data;

    @Override
    public void decode() throws Exception {
        ByteStream stream = this.getByteStream();
        type = this.getByteStream().readVInt();
        data = stream.readVInt();
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        String code = LogicGiftsManager.addGift(type, data);

        // XXX:experimental
        clientConnection.getMessageManager()
                .sendMessage(new GiftCodeMessage(
                        "https://peterhackz.github.io/link/multi-brawl/?action=voucher&code=" + code));
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
