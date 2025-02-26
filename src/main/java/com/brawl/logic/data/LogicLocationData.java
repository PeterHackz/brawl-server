package com.brawl.logic.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LogicLocationData extends LogicData {

    @Getter
    private int playersCount;
    private boolean disabled;
    private String name, mapName;
    private Gamemode gamemode;
    private int gamemodeType;
    @Getter
    private int gamemodeVariation;

    public boolean isSpecialEvent() {
        return gamemode == Gamemode.RAID_TOWNCRUSH || gamemode == Gamemode.RAID
                || gamemode == Gamemode.BOSSFIGHT
                || gamemode == Gamemode.SURVIVAL;
    }

    public void setGameMode(String gameMode) {
        for (Gamemode type : Gamemode.values())
            if (type.name().equalsIgnoreCase(gameMode)) {
                gamemode = type;
                break;
            }

        if (gamemode == null) {
            throw new IllegalArgumentException("unknown game mode type!: " + gameMode);
        }

        playersCount = switch (gamemode) {
            case BATTLEROYALE, BATTLEROYALETEAM, BOSSRACE, SOLOBOUNTY -> 10;
            case ATTACKDEFEND, BOUNTYHUNTER, CAPTURETHEFLAG, ROBOWARS, LASERBALL, COINRUSH, KINGOFHILL, BOSSFIGHT -> 6;
            case RAID, RAID_TOWNCRUSH, SURVIVAL -> 3;
            case TUTORIAL, TRAINING -> 1;
        };

        gamemodeVariation = switch (gamemode) {
            case BOUNTYHUNTER -> 3;
            case COINRUSH -> 0;
            case ATTACKDEFEND -> 2;
            case LASERBALL -> 5;
            case BATTLEROYALE -> 6;
            case BOSSFIGHT -> 10;
            case ROBOWARS -> 11;
            case BOSSRACE -> 14;
            case SOLOBOUNTY -> 15;
            case CAPTURETHEFLAG -> 16;
            case KINGOFHILL -> 17;
            case RAID_TOWNCRUSH -> 18;
            default -> -1;
        };

        gamemodeType = switch (gamemode) {
            case BATTLEROYALE, BOSSRACE, SOLOBOUNTY -> 2;
            case ATTACKDEFEND, BOUNTYHUNTER, CAPTURETHEFLAG, ROBOWARS, LASERBALL, COINRUSH, KINGOFHILL -> 1;
            case RAID, RAID_TOWNCRUSH, SURVIVAL -> 3;
            case TUTORIAL, TRAINING -> -1;
            case BOSSFIGHT -> 4;
            case BATTLEROYALETEAM -> 5;
        };

    }

    public void setAllowedMaps(String mapName) {
        this.mapName = mapName;
    }

    public enum Gamemode {
        BOUNTYHUNTER, // bounty
        COINRUSH, // Gem Grab
        BATTLEROYALE, // Solo SD
        ATTACKDEFEND, // Heist
        BOSSRACE, // Takedown
        ROBOWARS, // Seige
        BATTLEROYALETEAM, // Duo SD
        LASERBALL, // Brawl Ball
        SOLOBOUNTY, // Lone Star
        CAPTURETHEFLAG, // Present Plunder
        KINGOFHILL, // Hotzone
        TUTORIAL, TRAINING, RAID_TOWNCRUSH, // Dyno
        RAID, // Boss Fight
        BOSSFIGHT, // Big Boss
        SURVIVAL // Robo Rumble
    }

}
