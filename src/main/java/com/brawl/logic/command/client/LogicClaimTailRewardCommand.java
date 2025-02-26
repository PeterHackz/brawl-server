package com.brawl.logic.command.client;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.command.server.LogicGiveDeliveryItemsCommand;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.DeliveryItem;
import com.brawl.logic.home.DeliveryUnit;
import com.brawl.logic.home.GatchaDrop;
import com.brawl.logic.home.GatchaDrop.BoxType;
import com.brawl.logic.home.LogicGoals;
import com.brawl.logic.message.home.AvailableServerCommandMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicClaimTailRewardCommand extends LogicCommand {

    @Override
    public void decode(ByteStream stream) {
        stream.readVInt(); // box type??
        stream.readVInt(); // season id
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {
        LogicPlayer player = clientConnection.getLogicPlayer();

        int lastTierRequiredProgress = LogicGoals.getLastTierRequiredProgress();
        int tokens = player.getTokens();

        tokens -= lastTierRequiredProgress;
        if (tokens < 500)
            throw new Exception("no enough tokens to claim tail reward!");

        player.setTokens(player.getTokens() - 500);
        clientConnection.getMessageManager().sendMessage(new AvailableServerCommandMessage(
                new LogicGiveDeliveryItemsCommand(new DeliveryItem(
                        GatchaDrop.create(BoxType.MEDIUM, player)
                                .toArray(new DeliveryUnit[0])))
                        .setType(12)
                        .setForcedDrop(true)));
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
