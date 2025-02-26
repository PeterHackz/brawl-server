package com.brawl.logic.message.home;

import java.util.Collection;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class PlayerProfileMessage extends PiranhaMessage {

    private LogicPlayer player;

    public PlayerProfileMessage(LogicPlayer player) {
        super(2000);
        this.player = player;
    }

    private void encodeHeroEntry(ByteStream stream, LogicHeroData heroData) {
        stream.writeDataReference(heroData.getCharacterData());
        stream.writeVInt(0);
        stream.writeVInt(heroData.getTrophies()); // trophies
        stream.writeVInt(heroData.getHighestTrophies()); // max trophies
        stream.writeVInt(heroData.getLevel()); // level
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();

        stream.writeVInt(player.getHighId());
        stream.writeVInt(player.getLowId());

        stream.writeVInt(0);

        Collection<LogicHeroData> heroes = player.getHeroes().values();

        stream.writeVInt(heroes.size());

        for (LogicHeroData heroData : heroes) {
            encodeHeroEntry(stream, heroData);
        }

        stream.writeVInt(16);

        stream.writeVInt(1);
        stream.writeVInt(0); // 3v3 wins

        stream.writeVInt(2);
        stream.writeVInt(117750); // exp points

        stream.writeVInt(3);
        stream.writeVInt(player.getTrophies()); // Trophies

        stream.writeVInt(4);
        stream.writeVInt(player.getHighestTrophies()); // highest trophies

        stream.writeVInt(5);
        stream.writeVInt(heroes.size()); // Unlocked Brawlers count
        stream.writeVInt(6);
        stream.writeVInt(0);

        stream.writeVInt(7);
        stream.writeVInt(player.getSelectedThumbnail().getGlobalId()); // profile icon

        stream.writeVInt(8);
        stream.writeVInt(0); // solo wins

        stream.writeVInt(9);
        stream.writeVInt(0); // best robo rumble time

        stream.writeVInt(10);
        stream.writeVInt(0); // best time as big brawler

        stream.writeVInt(11);
        stream.writeVInt(0); // duo wins

        stream.writeVInt(12);
        stream.writeVInt(0); // highest boss fight time
        stream.writeVInt(13);
        stream.writeVInt(0); // highest power play points

        stream.writeVInt(14);
        stream.writeVInt(0); // highest power play rank

        stream.writeVInt(15);
        stream.writeVInt(0); // most challange wins

        stream.writeVInt(16);
        stream.writeVInt(0); // Highest rampage level passed

        stream.writeString(player.getName());
        stream.writeVInt(117750); // exp
        stream.writeVInt(player.getSelectedThumbnail().getGlobalId()); // avatar id
        stream.writeVInt(player.getSelectedNameColor().getGlobalId()); // name color
        stream.writeVInt(-1); // name color, -1 if bp not activated...?

        // club data
        stream.writeBoolean(false);
        stream.writeVInt(0);
    }

    @Override
    public int getMessageType() {
        return 24113;
    }

}
