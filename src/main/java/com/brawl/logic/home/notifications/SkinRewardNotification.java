package com.brawl.logic.home.notifications;

import org.bson.Document;

import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.datastream.ByteStream;

import lombok.Getter;

@Getter
public class SkinRewardNotification extends BaseNotification {

    public static final int NOTIFICATION_TYPE = 94;

    private LogicSkinData skinData;

    public SkinRewardNotification(LogicSkinData skinData) {
        this.skinData = skinData;
    }

    @Override
    public void encode(ByteStream stream) {
        super.encode(stream);
        stream.writeVInt(skinData.getGlobalId());
    }

    @Override
    public Document toDocument() {
        return super.toDocument()
                .append("sd", skinData.getGlobalId());
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
