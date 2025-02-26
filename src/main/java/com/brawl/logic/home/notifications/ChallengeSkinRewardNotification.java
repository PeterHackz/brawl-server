package com.brawl.logic.home.notifications;

import org.bson.Document;

import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.datastream.ByteStream;

public class ChallengeSkinRewardNotification extends BaseNotification {

    public static final int NOTIFICATION_TYPE = 75;

    private LogicSkinData skinData;

    public ChallengeSkinRewardNotification(LogicSkinData skinData) {
        this.skinData = skinData;
    }

    @Override
    public void encode(ByteStream stream) {
        super.encode(stream);
        stream.writeVInt(skinData.getGlobalId());
        stream.writeVInt(0); // challenge type
    }

    @Override
    public Document toDocument() {
        return super.toDocument()
                .append("sd", skinData.getGlobalId());
    }

    @Override
    public int getNotificationType() {
        return NOTIFICATION_TYPE;
    }

}
