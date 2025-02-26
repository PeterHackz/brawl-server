package com.brawl.server.DataBase;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;

import lombok.Getter;

@Getter
public class DataCollection {

    private MongoCollection<Document> collection;

    public DataCollection(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public Document get(int high, int low) {
        return collection.find(and(eq("hi", high), eq("lo", low))).first();
    }

    public Document getByDiscordId(String discordId) {
        return collection.find(eq("discordId", discordId)).first();
    }

    public void update(int high_id, int low_id, String key, Object value) {
        collection.updateOne(and(eq("hi", high_id), eq("lo", low_id)), Updates.set(key, value));
    }

    public void updateBson(int high_id, int low_id, Bson bson) {
        collection.updateOne(and(eq("hi", high_id), eq("lo", low_id)), bson);
    }

    public void updateNested(int high_id, int low_id, String key, Object value) {
        collection.updateOne(and(eq("hi", high_id), eq("lo", low_id)), new Document("$set", new Document(key, value)));
    }

    public void delete(int high_id, int low_id) {
        collection.deleteOne(and(eq("hi", high_id), eq("lo", low_id)));
    }

    public FindIterable<Document> getSorted(String key, int max) {
        return collection.find(gt(key, 0)).sort(Sorts.descending(key)).limit(max);
    }

}
