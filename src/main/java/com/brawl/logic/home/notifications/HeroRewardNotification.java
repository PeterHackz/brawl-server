package com.brawl.logic.home.notifications;

import org.bson.Document;

import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.datastream.ByteStream;

public class HeroRewardNotification extends BaseNotification {

    public static final int NOTIFICATION_TYPE = 93;

    private LogicCharacterData characterData;

    public HeroRewardNotification(LogicCharacterData characterData) {
        this.characterData = characterData;
    }

    @Override
    public void encode(ByteStream stream) {
        super.encode(stream);
        stream.writeVInt(0);
        stream.writeVInt(characterData.getGlobalId());
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

    @Override
    public Document toDocument() {
        return super.toDocument()
                .append("cd", characterData.getGlobalId());
    }

    @Override
    public String toString() {
        return "HeroRewardNotification [characterData=" + characterData.getGlobalId() + "]";
    }

}
