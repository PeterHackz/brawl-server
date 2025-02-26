package com.brawl.logic.home.notifications;

import com.brawl.logic.datastream.ByteStream;

public class FreeTextNotification extends BaseNotification {

    public static final int NOTIFICATION_TYPE = 81;

    public FreeTextNotification(String message) {
        setMessage(message);
    }

    @Override
    public void encode(ByteStream stream) {
        super.encode(stream);
        stream.writeVInt(0);
    }

    @Override
    public int getNotificationType() {
        return NOTIFICATION_TYPE;
    }

}
