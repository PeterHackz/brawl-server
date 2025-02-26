package com.brawl.logic.message.home;

import com.brawl.logic.LogicEventsManager;
import com.brawl.logic.LogicPlayer;
import com.brawl.logic.battle.LogicBattleState;
import com.brawl.logic.battle.LogicBattleType;
import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.data.LogicLocationData;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.battle.StartLoadingMessage;
import com.brawl.logic.message.battle.UdpConnectionInfoMessage;
import com.brawl.logic.message.battle.StartLoadingMessage.Player;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class MatchmakeRequestMessage extends PiranhaMessage {

    private int eventIdx;

    @Override
    public void decode() throws Exception {
        var stream = this.getByteStream();
        stream.readDataReference();
        eventIdx = stream.readVInt();
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        clientConnection.getLogicPlayer()
                .setBattleType(LogicBattleType.MULTIPLAYER)
                .setBattleState(LogicBattleState.BATTLE)
                .setBattleLocationData(LogicEventsManager.getEventByID(eventIdx)
                        .locationData());

        clientConnection
                .setState(State.BATTLE);
        // .getMessageManager().sendMessage(new StartBattleMessage());

        StartLoadingMessage startLoadingMessage = new StartLoadingMessage();

        startLoadingMessage.getPlayers().add(new Player(clientConnection.getLogicPlayer())
                .setIndex(0)
                .setTeam(0));

        startLoadingMessage.getPlayers().add(new Player(LogicPlayer.builder().name("Bot")
                .highId(9)
                .lowId(10)
                .selectedHero(new LogicHeroData(LogicCharacterData.getDefaultCharacter()))
                .build())
                .setIndex(1)
                .setTeam(1));

        LogicLocationData locationData = LogicDataTables.getDataById(LogicDataTables.LOCATIONS, 5);
        startLoadingMessage.setLocationData(locationData);
        startLoadingMessage.setGamemodeVariation(locationData.getGamemodeVariation());
        clientConnection.getMessageManager().sendMessage(startLoadingMessage);
        clientConnection.getMessageManager().sendMessage(new UdpConnectionInfoMessage());
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
