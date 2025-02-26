package com.brawl.logic.home.notifications;

import org.bson.Document;

import com.brawl.logic.datastream.ByteStream;

import lombok.Getter;

@Getter
public class ResourceRewardNotification extends BaseNotification {

    public static final int NOTIFICATION_TYPE = 90;

    private int amount, resourceType;

    public static final class ResourceType {
        public static final int GOLD = 8,
                STAR_POINTS = 10;
    }

    public ResourceRewardNotification(int resourceType, int amount) {
        this.resourceType = resourceType;
        this.amount = amount;
    }

    @Override
    public void encode(ByteStream stream) {
        super.encode(stream);
        stream.writeVInt(1);
        stream.writeVInt(5000000 + resourceType); // resources.csv
        stream.writeVInt(amount);
    }

    @Override
    public Document toDocument() {
        return super.toDocument()
                .append("a", amount)
                .append("rt", resourceType);
    }

    @Override
    public String getMessage() {
        if (isNotificationRead())
            return "Voucher: redeemed";
        return "Voucher gift";
    }

    @Override
    public int getNotificationType() {
        return NOTIFICATION_TYPE;
    }

}
