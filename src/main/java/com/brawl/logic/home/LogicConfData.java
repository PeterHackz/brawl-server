package com.brawl.logic.home;

import java.util.HashMap;

import com.brawl.logic.LogicEventsManager;
import com.brawl.logic.LogicPlayer;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.server.ServerConfiguration;

public class LogicConfData {

    public static void encode(ByteStream stream, LogicPlayer player) {
        stream.writeVInt(0); // Shop Timestamp

        stream.writeVInt(100); // Tokens for Brawl Box

        stream.writeVInt(10); // Tokens for Big Box

        stream.writeVInt(30);

        stream.writeVInt(3);

        stream.writeVInt(80);

        stream.writeVInt(10);

        stream.writeVInt(50);

        stream.writeVInt(1000);

        stream.writeVInt(500);

        stream.writeVInt(50);

        stream.writeVInt(999900);

        stream.writeVInt(0);

        // LOGIC EVENTS //
        var events = LogicEventsManager.getEvents();
        stream.writeVInt(events.size());
        for (var event : events.keySet()) {
            stream.writeVInt(event);
        }

        HashMap<Integer, Integer> eventsSeen = player.getEventsSeen();

        // events Array
        stream.writeVInt(events.size()); // count
        for (var event : events.entrySet()) {
            stream.writeVInt(event.getKey());
            stream.writeVInt(event.getKey()); // index
            stream.writeVInt(-1);
            stream.writeVInt(event.getValue().getTimeLeft());
            stream.writeVInt(10);
            stream.writeDataReference(event.getValue().locationData());
            stream.writeVInt(eventsSeen.getOrDefault(event.getKey(), 0) == event.getValue().changeTime() ? 3 : 1);
            stream.writeString();
            stream.writeVInt(0);
            stream.writeVInt(0); // pp games left
            stream.writeVInt(0); // pp total games
            stream.writeVInt(0); // modifiers
            stream.writeVInt(4); // challenge player total wins
            stream.writeVInt(0); // challenge ID
        }

        // upcoming events Array
        stream.writeVInt(0); // count

        /*-  for (var event : events.entrySet()) {
            stream.writeVInt(event.getKey());
            stream.writeVInt(event.getKey()); // index
            stream.writeVInt(event.getValue().getTimeLeft());
            stream.writeVInt(0);
            stream.writeVInt(10);
            stream.writeDataReference(15, event.getValue().locationData().getDataId() + 1);
            stream.writeVInt(3);
            stream.writeString();
            stream.writeVInt(0);
            stream.writeVInt(0); // pp games left
            stream.writeVInt(3); // pp total games
            stream.writeVInt(0); // modifiers
            stream.writeVInt(4); // challenge player total wins
            stream.writeVInt(0); // challenge ID
        }*/

        stream.writeIntArray(20, 35, 75, 140, 290, 480, 800, 1250);

        stream.writeIntArray(1, 2, 3, 4, 5, 10, 15, 20);

        stream.writeIntArray(10, 30, 80);

        stream.writeIntArray(6, 20, 60);

        stream.writeIntArray(20, 50, 140, 280);

        stream.writeIntArray(150, 400, 1200, 2600);

        stream.writeVInt(0);

        stream.writeVInt(200); // max battle tokens

        stream.writeVInt(20); // tokens per refresh

        stream.writeVInt(0);

        stream.writeVInt(10);

        stream.writeVInt(0);

        stream.writeByte(0);

        stream.writeVInt(0);

        stream.writeVInt(0);

        stream.writeBoolean(true);

        stream.writeVInt(0);

        // menu theme array

        // LogicConfData int value entry
        stream.writeVInt(12);

        stream.writeInt(1); // menu theme
        stream.writeInt(41000000);

        stream.writeInt(3); // required level to unlock freindly games
        stream.writeInt(0);

        stream.writeInt(14); // double token event state
        stream.writeInt(0);

        stream.writeInt(28); // challenge lives left
        stream.writeInt(4);

        stream.writeInt(38); // premium pass cost
        stream.writeInt(ServerConfiguration.BRAWLPASS_COST);

        stream.writeInt(40); // premium pass bundle cost
        stream.writeInt(ServerConfiguration.BRAWL_PASS_BUNDLE_COST);

        stream.writeInt(41); // buy pass progress cost
        stream.writeInt(ServerConfiguration.BRAWLPASS_PROGRESS_COST);

        stream.writeInt(42); // brawl pass bundle tokenss
        stream.writeInt(ServerConfiguration.BRAWLPASS_BUNDLE_TOKENS);

        stream.writeInt(47);
        stream.writeInt(3600); // time for next daily quests refresh

        stream.writeInt(48);
        stream.writeInt(3600 * 2); // time for new season quests

        stream.writeInt(49);
        stream.writeInt(2); // count of daily quests

        stream.writeInt(50);
        stream.writeInt(2); // number of new season quests after timer ends

        // LogicConfData int value entry end

        stream.writeVInt(0);

        stream.writeVInt(0);
    }
}