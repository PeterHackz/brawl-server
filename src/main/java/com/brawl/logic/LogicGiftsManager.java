package com.brawl.logic;

import static com.mongodb.client.model.Filters.eq;

import java.util.UUID;

import org.bson.Document;

import com.brawl.server.DataBase.DataBaseManager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LogicGiftsManager {

    @Getter
    private static final Object lock = new Object();

    public static class GiftType {
        public static final int GOLD = 0,
                DIAMONDS = 1,
                SKIN = 2,
                STAR_POINTS = 3,
                MEGA_BOXES = 4,
                BRAWL_PASS = 5;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static final class Gift {

        private int type, data;

        private String uuid;

        public Gift(int type, int data, String uuid) {
            this.type = type;
            this.data = data;
            this.uuid = uuid;
        }
    }

    public static String addGift(int type, int data) {
        synchronized (lock) {
            String uuid = UUID.randomUUID().toString();
            DataBaseManager.getGifts().getCollection().insertOne(new Document("type", type)
                    .append("data", data)
                    .append("uuid", uuid));
            return uuid;
        }
    }

    public static Gift getGift(String code) {
        Document doc = DataBaseManager.getGifts().getCollection().findOneAndDelete(eq("uuid", code));
        if (doc == null)
            return null;
        return new Gift(doc.getInteger("type"), doc.getInteger("data"), doc.getString("uuid"));
    }

}
