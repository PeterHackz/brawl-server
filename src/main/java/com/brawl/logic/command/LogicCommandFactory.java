package com.brawl.logic.command;

import com.brawl.logic.command.client.LogicClaimDailyRewardCommand;
import com.brawl.logic.command.client.LogicClaimRankUpRewardCommand;
import com.brawl.logic.command.client.LogicClaimTailRewardCommand;
import com.brawl.logic.command.client.LogicDeleteNotificationCommand;
import com.brawl.logic.command.client.LogicGatchaCommand;
import com.brawl.logic.command.client.LogicLevelUpCommand;
import com.brawl.logic.command.client.LogicPurchaseBrawlPassCommand;
import com.brawl.logic.command.client.LogicPurchaseBrawlPassProgressCommand;
import com.brawl.logic.command.client.LogicSelectSkinCommand;
import com.brawl.logic.command.client.LogicSetPlayerNameColorCommand;
import com.brawl.logic.command.client.LogicSetPlayerThumbnailCommand;
import com.brawl.logic.command.client.LogicViewInboxNotificationCommand;

public class LogicCommandFactory {

    public static LogicCommand createCommandByType(int commandType) {
        System.out.println(commandType);
        return switch (commandType) {
            case 500 -> new LogicGatchaCommand();
            case 503 -> new LogicClaimDailyRewardCommand();
            case 505 -> new LogicSetPlayerThumbnailCommand();
            case 506 -> new LogicSelectSkinCommand();
            case 514 -> new LogicDeleteNotificationCommand();
            case 517 -> new LogicClaimRankUpRewardCommand();
            case 520 -> new LogicLevelUpCommand();
            case 527 -> new LogicSetPlayerNameColorCommand();
            case 528 -> new LogicViewInboxNotificationCommand();
            case 534 -> new LogicPurchaseBrawlPassCommand();
            case 535 -> new LogicClaimTailRewardCommand();
            case 536 -> new LogicPurchaseBrawlPassProgressCommand();
            default -> null;
        };
    }

}
