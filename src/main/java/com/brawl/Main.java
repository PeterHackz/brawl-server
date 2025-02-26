package com.brawl;

import com.brawl.logic.LogicEventsManager;
import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.home.Leaderboard;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.logic.home.notifications.LogicNotificationFactory;
import com.brawl.logic.math.LogicRandom;
import com.brawl.server.DataBase.DataBaseManager;
import com.brawl.server.ServerConfiguration;
import com.brawl.server.network.TCPServer;
import jnr.constants.platform.Signal;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import jnr.posix.SignalHandler;
import jnr.posix.util.DefaultPOSIXHandler;

public class Main {

    public static void main(String[] args) throws Exception {
        TCPServer.init();
        LogicDataTables.init();
        LogicRandom.init();
        LogicEventsManager.init();
        LogicHeroData.init();
        DataBaseManager.init();
        Leaderboard.init();
        LogicNotificationFactory.init();
        ServerConfiguration.init();
        POSIX posix = POSIXFactory.getPOSIX(new DefaultPOSIXHandler(), true);
        posix.signal(Signal.SIGINT, new SignalHandler() {
            @Override
            public void handle(int i) {
                TCPServer.shutdown();
            }
        });
        TCPServer.start();
    }

}