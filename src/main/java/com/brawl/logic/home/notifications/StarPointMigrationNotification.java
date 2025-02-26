package com.brawl.logic.home.notifications;

import org.bson.Document;

import com.brawl.logic.datastream.ByteStream;

public class StarPointMigrationNotification extends BaseNotification {

    public static final int NOTIFICATION_TYPE = 80;

    private int amount;

    public StarPointMigrationNotification(int amount) {
        this.amount = amount;
    }

    @Override
    public void encode(ByteStream stream) {
        this.setMessage("test");
        super.encode(stream);
        stream.writeVInt(amount);
    }

    @Override
    public Document toDocument() {
        return super.toDocument()
                .append("a", amount);
    }

    @Override
    public int getNotificationType() {
        return NOTIFICATION_TYPE;
    }

}
