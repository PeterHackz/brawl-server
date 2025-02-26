package com.brawl.logic.command.client;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.LogicGoals;
import com.brawl.logic.home.LogicMilestoneReward;
import com.brawl.server.ServerConfiguration;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicPurchaseBrawlPassProgressCommand extends LogicCommand {

    @Override
    public void decode(ByteStream stream) {
        stream.readVInt(); // season id
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {
        LogicPlayer player = clientConnection.getLogicPlayer();

        if (player.getDiamonds() < ServerConfiguration.BRAWLPASS_PROGRESS_COST)
            throw new Exception("no enough gems to buy brawl pass progress!");

        LogicMilestoneReward nextReward = LogicGoals.getNextTier(player.getTokens());
        if (nextReward != null) {
            player.setDiamonds(player.getDiamonds() - ServerConfiguration.BRAWLPASS_PROGRESS_COST);
            player.setTokens(nextReward.getRequiredProgress());
        }
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
