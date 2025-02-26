package com.brawl.logic.message.home;

import java.util.concurrent.PriorityBlockingQueue;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class LeaderboardMessage extends PiranhaMessage {

    private PriorityBlockingQueue<LogicPlayer> players;
    private LogicCharacterData characterData;
    private int type;

    public LeaderboardMessage(PriorityBlockingQueue<LogicPlayer> players, LogicCharacterData characterData, int type) {
        super(10000);
        this.players = players;
        this.characterData = characterData;
        this.type = type;
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        ByteStream stream = this.getByteStream();

        LogicPlayer player = clientConnection.getLogicPlayer();
        stream.writeVInt(type < 4 ? 1 : 0);

        stream.writeVInt(0);

        stream.writeDataReference(characterData);

        stream.writeString(type == 5 || type == 1 ? "LB" : null); // region

        int ownIdx = 0;
        int idx = 0;
        int hi, lo, playerHi = player.getHighId(), playerLo = player.getLowId();

        stream.writeVInt(players.size());

        int characterId = characterData == null ? -1 : characterData.getDataId();

        for (LogicPlayer plr : players) {
            idx++;
            hi = plr.getHighId();
            lo = plr.getLowId();
            if (lo == playerLo && hi == playerHi)
                ownIdx = idx;

            stream.writeVInt(hi);
            stream.writeVInt(lo);

            stream.writeVInt(1);

            if (type >= 4) {
                stream.writeVInt(plr.getHero(characterId).getTrophies());
            } else {
                stream.writeVInt(plr.getTrophies());
            }

            stream.writeVInt(1);
            stream.writeString(); // club name

            stream.writeString(plr.getName());

            stream.writeVInt(0);
            stream.writeVInt(plr.getSelectedThumbnail().getGlobalId());
            stream.writeVInt(plr.getSelectedNameColor().getGlobalId());

            // use special name color
            // -2 = frida hacc it
            stream.writeVInt(-1); // bp name color
            stream.writeVInt(0);
        }

        stream.writeVInt(0);
        stream.writeVInt(ownIdx);
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeString("LB");
    }

    @Override
    public int getMessageType() {
        return 24403;
    }

}
