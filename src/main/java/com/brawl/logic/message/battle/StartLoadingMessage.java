package com.brawl.logic.message.battle;

import java.util.ArrayList;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.data.LogicLocationData;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
public class StartLoadingMessage extends PiranhaMessage {

    private ArrayList<Player> players;

    private int gamemodeVariation;

    private LogicLocationData locationData;

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Player {
        private int team, index;

        private LogicPlayer logicPlayer;

        public Player(LogicPlayer logicPlayer) {
            team = -1;
            this.logicPlayer = logicPlayer;
        }
    }

    public StartLoadingMessage() {
        super(200);
        players = new ArrayList<>();
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        int index = 0;
        var ownPlayer = clientConnection.getLogicPlayer();
        for (Player plr : players) {
            if (plr.getLogicPlayer().getHighId() == ownPlayer.getHighId()
                    && plr.getLogicPlayer().getLowId() == ownPlayer.getLowId())
                break;
            index++;
        }

        var stream = this.getByteStream();

        /*
         * Why do we reset the offset?
         * basically, to avoid making alot of ByteStream instances for 1
         * StartLoadingMessage, we
         * make one message and encode it for all players
         * so we reset the offset in buffer on every encode
         * being able to reuse it.
         */
        stream.setOffset(0);

        int ownTeam = players.get(index).getTeam();
        stream.writeInt(players.size());
        stream.writeInt(players.get(index).getIndex());
        stream.writeInt(ownTeam); // side
        stream.writeInt(players.size());

        for (Player player : players) {
            encodePlayer(player);
        }

        {
            stream.writeInt(0);
        }

        {
            stream.writeInt(0);
        }

        stream.writeInt(0);

        stream.writeVInt(gamemodeVariation);

        stream.writeVInt(1);
        stream.writeVInt(1);

        stream.writeBoolean(true);

        stream.writeVInt(0);
        stream.writeVInt(0);

        stream.writeDataReference(locationData);

        stream.writeBoolean(false);
        stream.writeBoolean(false);
    }

    private void encodePlayer(Player player) {
        LogicPlayer logicPlayer = player.getLogicPlayer();
        var stream = this.getByteStream();
        int team = player.getTeam();
        stream.writeInt(logicPlayer.getHighId());
        stream.writeInt(logicPlayer.getLowId());
        stream.writeVInt(player.getIndex());
        stream.writeVInt(team == -1 ? 0 : team); // side
        stream.writeVInt(0);
        stream.writeInt(0);
        stream.writeDataReference(logicPlayer.getSelectedHero().getCharacterData());
        stream.writeDataReference(logicPlayer.getSelectedHero().getSelectedSkin());
        stream.writeBoolean(false);
        /*
         * stream.writeBoolean(true); did not work
         * stream.writeDataReference(52, 4);
         * stream.writeDataReference(52, 2);
         * stream.writeDataReference(52, 9);
         * stream.writeDataReference(52, 6);
         */
        stream.writeString(logicPlayer.getName());
        stream.writeVInt(100);
        stream.writeVInt(logicPlayer.getSelectedThumbnail().getGlobalId());
        stream.writeVInt(logicPlayer.getSelectedNameColor().getGlobalId());
        stream.writeVInt(-1);
        stream.writeBoolean(false);
    }

    @Override
    public int getMessageType() {
        return 20559;
    }

}
