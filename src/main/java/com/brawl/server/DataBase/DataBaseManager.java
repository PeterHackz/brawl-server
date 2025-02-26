package com.brawl.server.DataBase;

import static com.mongodb.client.model.Filters.eq;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.debug.Debugger;
import com.brawl.logic.math.LogicRandom;
import com.brawl.server.IDManager;
import com.brawl.server.IDManager.ID;
import com.brawl.server.ServerConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import lombok.Getter;

public class DataBaseManager {

        private static MongoClient connection;
        private static MongoDatabase db;

        @Getter
        private static DataCollection players,
                        gifts;

        public static void init() {
                Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
                connection = new MongoClient(new MongoClientURI(ServerConfiguration.DATABASE_URL));
                db = connection.getDatabase(ServerConfiguration.DATABASE_NAME);
                players = new DataCollection(db.getCollection("players"));
                gifts = new DataCollection(db.getCollection("gifts"));
                try {
                        int highestHighId = players
                                        .getCollection()
                                        .find()
                                        .sort(Sorts.descending("hi"))
                                        .limit(1)
                                        .first()
                                        .getInteger("hi", 0);
                        int highestLowId = players
                                        .getCollection()
                                        .find(eq("hi", highestHighId))
                                        .sort(Sorts.descending("lo"))
                                        .limit(1)
                                        .first()
                                        .getInteger("lo", 0);
                        IDManager.setPlayerIdManager(new IDManager(highestHighId, highestLowId + 1));
                } catch (NullPointerException e) {
                        IDManager.setPlayerIdManager(new IDManager(0, 1));
                }
                // clubs = new DataBase("clubs");
                Debugger.info(String.format("DataBase {\n\tPlayersCount -> %d\n}", players.getCollection()
                                .countDocuments()));
        }

        public static LogicPlayer createPlayer() {
                String token = LogicRandom.randString(20);
                ID playerId = IDManager.getPlayerIdManager().generateNewID();
                LogicPlayer player = LogicPlayer.builder()
                                .highId(playerId.getHighId())
                                .lowId(playerId.getLowId())
                                .token(token)
                                .build()
                                .init();
                players.getCollection()
                                .insertOne(new Document()
                                                .append("hi", playerId.getHighId())
                                                .append("lo", playerId.getLowId())
                                                .append("token", token));
                return player;
        }

}