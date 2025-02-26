package com.brawl.server.network;

import com.brawl.logic.LogicPlayer;
import com.brawl.server.Ticker;
import com.brawl.titan.Messaging.State;
import com.brawl.titan.PepperCrypto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ClientConnection {

    private Connection connection;
    @Getter
    private String address, ip;
    @Getter
    private MessageManager messageManager;
    private PepperCrypto pepperCrypto;
    private State state;

    private LogicPlayer logicPlayer;

    ClientConnection(Connection connection) {
        this.connection = connection;
        messageManager = new MessageManager(this);
        state = State.PEPPER_AUTH;
    }

    public void setLogicPlayer(LogicPlayer player) {
        player.setClientConnection(this);
        logicPlayer = player;
        logicPlayer.setGainedStarpoints(0)
                .setGainedTrophies(0);
        player.setServerTick(Ticker.getServerTick());
    }

    public void update() throws Exception {
        if (logicPlayer != null) {
            logicPlayer.update();
        }
        messageManager.update();
    }

    public void onOpen() {
        address = connection.getChannel().remoteAddress().toString().replace("/", "");
        ip = address.split(":")[0];
    }

    public void onClose() {
        if (logicPlayer != null) {
            logicPlayer.executePendingEvents();
            logicPlayer.save();
            logicPlayer.setClientConnection(null);
        }
    }

}
