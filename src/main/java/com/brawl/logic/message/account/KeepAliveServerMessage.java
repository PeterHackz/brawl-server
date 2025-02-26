package com.brawl.logic.message.account;

import com.brawl.logic.message.PiranhaMessage;

public class KeepAliveServerMessage extends PiranhaMessage {

    public KeepAliveServerMessage() {
        super(0);
    }

    @Override
    public int getMessageType() {
        return 20108;
    }

}
