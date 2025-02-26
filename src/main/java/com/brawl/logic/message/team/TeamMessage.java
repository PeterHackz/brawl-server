package com.brawl.logic.message.team;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class TeamMessage extends PiranhaMessage {

    public TeamMessage() {
        super(500);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();
        LogicPlayer player = clientConnection.getLogicPlayer();
        LogicHeroData heroData = player.getSelectedHero();

        if (heroData.getSelectedGadget() == null || heroData.getSelectedStarPower() == null) {
            var character = heroData.getCharacterData();
            if (character.getGadget() != null)
                player.setSelectedGadget(heroData, character.getGadget());
            if (character.getStarPower() != null)
                player.setSelectedStarPower(heroData, character.getStarPower());
        }

        stream.writeVInt(1);
        stream.writeBoolean(false);
        stream.writeVInt(1); // capacity
        stream.writeInt(0); // Gameroom high id
        stream.writeInt(1); // Gameroom low id
        stream.writeBoolean(false);
        stream.writeVInt(0);

        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeDataReference(player.getSelectedLocation());

        stream.writeVInt(1); // players count

        {
            stream.writeVInt(1);
            stream.writeInt(player.getHighId());
            stream.writeInt(player.getLowId());
            stream.writeDataReference(heroData.getCharacterData());
            stream.writeDataReference(heroData.getSelectedSkin());
            stream.writeVInt(1);
            stream.writeVInt(1);
            stream.writeVInt(10);
            stream.writeVInt(3); // player state
            stream.writeVInt(0); // ready state
            stream.writeVInt(0); // team (0 blue, 1 red)
            stream.writeVInt(0);
            stream.writeVInt(0);
            stream.writeString(player.getName());
            stream.writeVInt(100);
            stream.writeVInt(28000000); // thumbnail
            stream.writeVInt(43000000); // name color
            stream.writeVInt(-1);
            // selected sp
            stream.writeDataReference(heroData.getSelectedStarPower());
            // selected gadget
            stream.writeDataReference(heroData.getSelectedGadget());
        }

        stream.writeVInt(0); // invitations array
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(6);
    }

    @Override
    public int getMessageType() {
        return 24124;
    }

    public static class AlreadyInATeamException extends Exception {
        public AlreadyInATeamException() {
            super("player is already in a team!");
        }
    }

    public static class NotInATeamException extends Exception {
        public NotInATeamException() {
            super("player is not in a team!");
        }
    }

}
