package com.brawl.logic.home.notifications;

import org.bson.Document;

import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.utils.LogicDocumentObject;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public abstract class BaseNotification implements LogicDocumentObject {

    private long notificationTime;

    private boolean notificationRead;

    private String message;

    private int notificationId;

    public abstract int getNotificationType();

    public void encode(ByteStream stream) {
        stream.writeBoolean(this.isNotificationRead());
        stream.writeInt((int) (System.currentTimeMillis() - this.getNotificationTime()) / 1000);
        stream.writeString(this.getMessage());
    }

    public Document toDocument() {
        return new Document("type", getNotificationType())
                .append("nt", notificationTime)
                .append("nid", notificationId)
                .append("msg", message)
                .append("nrd", notificationRead);
    }

}