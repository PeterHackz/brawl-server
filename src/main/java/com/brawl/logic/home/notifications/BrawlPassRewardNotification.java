package com.brawl.logic.home.notifications;

import com.brawl.logic.datastream.ByteStream;
import com.brawl.server.ServerConfiguration;

import lombok.Getter;

@Getter
public class BrawlPassRewardNotification extends BaseNotification {

    public static final int NOTIFICATION_TYPE = 73;

    public BrawlPassRewardNotification() {
    }

    @Override
    public void encode(ByteStream stream) {
        super.encode(stream);
        stream.writeVInt(ServerConfiguration.BRAWLPASS_SEASON);
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
