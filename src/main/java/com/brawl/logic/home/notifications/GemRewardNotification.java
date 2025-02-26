package com.brawl.logic.home.notifications;

import org.bson.Document;

import com.brawl.logic.datastream.ByteStream;

import lombok.Getter;

@Getter
public class GemRewardNotification extends BaseNotification {

    public static final int NOTIFICATION_TYPE = 89;

    private int amount;

    public GemRewardNotification(int amount) {
        this.amount = amount;
    }

    @Override
    public void encode(ByteStream stream) {
        super.encode(stream);
        stream.writeVInt(0);
        stream.writeVInt(amount);
    }

    @Override
    public String getMessage() {
        if (isNotificationRead())
            return "Voucher: redeemed";
        return "Voucher gift";
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
