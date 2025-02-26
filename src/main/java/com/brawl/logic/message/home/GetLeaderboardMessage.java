package com.brawl.logic.message.home;

import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.Leaderboard;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class GetLeaderboardMessage extends PiranhaMessage {

    private int isLocal;
    private LogicCharacterData characterData;

    @Override
    public void decode() throws Exception {
        ByteStream stream = this.getByteStream();
        isLocal = stream.readVInt();
        stream.readVInt(); // type
        characterData = stream.readDataReference();
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        Leaderboard.getLeaderboard(characterData == null ? -1 : characterData.getDataId())
                .getPlayers(players -> {
                    LeaderboardMessage message;
                    if (characterData == null) {
                        message = new LeaderboardMessage(players, null, isLocal);
                    } else {
                        message = new LeaderboardMessage(players, characterData, isLocal == 1 ? 5 : 4);
                    }
                    clientConnection.getMessageManager().sendMessage(message);
                });
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
