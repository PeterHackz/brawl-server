package com.brawl.logic.message.admin;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.utils.LogicTimeUtils;
import com.brawl.server.Cache;
import com.brawl.server.Ticker;
import com.brawl.server.network.ClientConnection;
import com.brawl.server.network.TCPServer;

public class StatsDataMessage extends PiranhaMessage {

    public StatsDataMessage() {
        super(50);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();

        stream.writeVInt(7);

        stream.writeString("Connections Count");
        stream.writeString(Integer.toString(TCPServer.getConnections().get() - 1));

        stream.writeString("Cached Players Count");
        stream.writeString(Long.toString(Cache.getPlayersCache().getEntriesCount()));

        stream.writeString("Server Tick");
        stream.writeString(Integer.toString(Ticker.getServerTick()));

        stream.writeString("Threads Count");
        stream.writeString(Integer.toString(Thread.getAllStackTraces().size()));

        stream.writeString("JVM Memory Commit");
        stream.writeString((int) Runtime.getRuntime().totalMemory() / (1024 * 1024) + "mb");

        stream.writeString("Memory Used");
        stream.writeString((int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024))
                + "mb");

        stream.writeString("Uptime");
        stream.writeString(
                LogicTimeUtils.formatTime(java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime()));
    }

    @Override
    public int getMessageType() {
        return 20777;
    }

}
