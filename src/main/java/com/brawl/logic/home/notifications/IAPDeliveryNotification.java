package com.brawl.logic.home.notifications;

import org.bson.Document;

import com.brawl.logic.datastream.ByteStream;

import lombok.Getter;

@Getter
public class IAPDeliveryNotification extends BaseNotification {

    public static final int NOTIFICATION_TYPE = 86;

    private int amount;

    public IAPDeliveryNotification(int amount) {
        this.amount = amount;
    }

    @Override
    public void encode(ByteStream stream) {
        super.encode(stream);
        stream.writeVInt(1);
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
