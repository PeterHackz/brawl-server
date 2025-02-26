package com.brawl.logic.message;

import com.brawl.logic.message.account.*;
import com.brawl.logic.message.admin.AskForStatsMessage;
import com.brawl.logic.message.admin.CreateGiftMessage;
import com.brawl.logic.message.admin.StartShutdownMessage;
import com.brawl.logic.message.avatar.ChangeAvatarNameMessage;
import com.brawl.logic.message.battle.AskForBattleEndMessage;
import com.brawl.logic.message.battle.GoHomeFromOfflinePractiseMessage;
import com.brawl.logic.message.home.*;
import com.brawl.logic.message.team.TeamCreateMessage;
import com.brawl.logic.message.team.TeamLeaveMessage;
import com.brawl.logic.message.team.TeamSetLocationMessage;
import com.brawl.logic.message.team.TeamSetMemberReadyMessage;

public class LogicLaserMessageFactory {

    public static PiranhaMessage createMessageByType(int messageType) {
        return switch (messageType) {
            case 10100 -> new ClientHelloMessage();
            case 10101 -> new LoginMessage();
            case 10108 -> new KeepAliveMessage();
            case 10212 -> new ChangeAvatarNameMessage();
            case 10229 -> new StartShutdownMessage();
            case 10666 -> new CreateGiftMessage();
            case 10777 -> new AskForStatsMessage();
            case 14102 -> new EndClientTurnMessage();
            case 14103 -> new MatchmakeRequestMessage();
            case 14109 -> new GoHomeFromOfflinePractiseMessage();
            case 14110 -> new AskForBattleEndMessage();
            case 14113 -> new GetPlayerProfileMessage();
            case 14277 -> new GetSeasonRewardsMessage();
            case 14350 -> new TeamCreateMessage();
            case 14353 -> new TeamLeaveMessage();
            case 14355 -> new TeamSetMemberReadyMessage();
            case 14363 -> new TeamSetLocationMessage();
            case 14403 -> new GetLeaderboardMessage();
            case 16939 -> new StartTrainingMessage();
            default -> null;
        };
    }

}
