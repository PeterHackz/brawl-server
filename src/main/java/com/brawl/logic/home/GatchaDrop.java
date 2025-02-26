package com.brawl.logic.home;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicCardData;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.data.LogicCharacterData.Rarity;

import java.util.*;

public class GatchaDrop {

    @SuppressWarnings("unused")
    private static final int RARE_CHANCE = 559, SUPER_RARE_CHANCE = 252, EPIC_CHANCE = 114, MYTHIC_CHANCE = 52,
            LEGENDARY_CHANCE = 23, STAR_POWER_CHANCE = 10, ACCESSORY_CHANCE = 20, PP_CHANCE = 932,
            NEW_HERO_CHANCE = 48; // from client_globals.csv

    public static int getRandomWithProba(Random random, double... probabilities) {
        double rand = random.nextDouble(100);
        double cumulativeProbability = 0.0;

        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            if (rand < cumulativeProbability)
                return i + 1;
        }

        return -1; // proba did not end up 1!!?
    }

    private static double clamp(double num, double min, double max) {
        return Math.min(Math.max(num, min), max);
    }

    private static double roundf(double num) {
        return (double) Math.round(num * 10000) / 10000;
    }

    public static LogicCardData getRandomCardWithType(Random random, LogicPlayer player,
                                                      Collection<LogicHeroData> heroes,
                                                      int type) {
        LogicCardData lastCard = null;
        for (LogicHeroData hero : heroes)
            for (LogicCardData cardData : hero.getCharacterData().getCards())
                if (cardData.getMetaType() == type)
                    if (!player.isCardUnlocked(cardData)) {
                        if (random.nextBoolean())
                            return cardData;
                        lastCard = cardData;
                    }
        return lastCard;
    }

    public static LogicCharacterData getRandomHeroWithRarity(Random random, ArrayList<LogicCharacterData> heroes,
                                                             Rarity rarity) {
        LogicCharacterData lastHero = null;
        for (LogicCharacterData characterData : heroes)
            if (characterData.getRarity() == rarity
                    || (characterData.getRarity() == Rarity.CHROMATIC && rarity == Rarity.EPIC)) {
                if (random.nextBoolean())
                    return characterData;
                lastHero = characterData;
            }
        return lastHero;
    }

    public static boolean hasRemainingHeroesInGatchaWithRarity(Collection<LogicCharacterData> heroes, Rarity rarity) {
        for (LogicCharacterData hero : heroes)
            if (hero.getRarity() == rarity)
                return true;
        return false;
    }

    public static boolean hasRemainingPowerPointsInGatcha(Collection<LogicHeroData> heroes) {
        int maxedPoints = LogicHeroData.getMaxedPoints();
        for (LogicHeroData hero : heroes)
            if (hero.getPowerPoints() < maxedPoints)
                return true;
        return false;
    }

    public static LogicHeroData getRandomHeroForPowerPoints(Random random, Collection<LogicHeroData> heroes) {
        int maxedPoints = LogicHeroData.getMaxedPoints();
        LogicHeroData lastHero = null;
        for (LogicHeroData hero : heroes)
            if (hero.getPowerPoints() < maxedPoints) {
                lastHero = hero;
                if (random.nextBoolean())
                    return hero;
            }
        return lastHero;
    }

    public static boolean hasRemainingStarPowersInGatcha(LogicPlayer player, Collection<LogicHeroData> heroes) {
        for (LogicHeroData hero : heroes)
            if (!player.areAllStarPowersUnlocked(hero))
                return false;
        return false;
    }

    public static boolean hasRemainingGadgetsInGatcha(LogicPlayer player, Collection<LogicHeroData> heroes) {
        for (LogicHeroData hero : heroes)
            if (!player.areAllGadgetsUnlocked(hero))
                return false;
        return false;
    }

    public static ArrayList<DeliveryUnit> create(BoxType boxType, LogicPlayer player) {
        ArrayList<DeliveryUnit> items = new ArrayList<DeliveryUnit>();

        ArrayList<LogicCharacterData> heroes = new ArrayList<LogicCharacterData>();
        for (LogicCharacterData characterData : LogicDataTables.getAvailableCharacters()) {
            if (player.getHero(characterData.getDataId()) == null)
                heroes.add(characterData);
        }

        Random random = new Random();

        int rewards = switch (boxType) {
            case SMALL -> 2 + random.nextInt(2);
            case MEDIUM -> 3 + random.nextInt(2);
            case BIG -> 5 + random.nextInt(6);
        };
        int draws = switch (boxType) {
            case SMALL -> 1;
            case MEDIUM -> 3;
            case BIG -> 10;
        };

        ArrayList<LogicCharacterData> newHeroes = new ArrayList<>();

        for (int i = 0; i < rewards; i++)
            draw(random, items, heroes, player, newHeroes, draws);

        if (newHeroes.size() > 0)
            player.setDraws(0);
        else
            player.setDraws(player.getDraws() + draws);

        for (LogicCharacterData hero : newHeroes)
            player.unlockHero(hero);

        Collections.sort(items, new Comparator<DeliveryUnit>() {
            @Override
            public int compare(DeliveryUnit o1, DeliveryUnit o2) {
                // Coins always come first
                if (o1.getType() == DeliveryUnit.GOLD)
                    return -1;
                if (o2.getType() == DeliveryUnit.GOLD)
                    return 1;

                // Power points come next, sorted by count
                if (o1.getType() == DeliveryUnit.POWER_POINT && o2.getType() == DeliveryUnit.POWER_POINT) {
                    return Integer.compare(o1.getCount(), o2.getCount());
                }
                if (o1.getType() == DeliveryUnit.POWER_POINT)
                    return -1;
                if (o2.getType() == DeliveryUnit.POWER_POINT)
                    return 1;

                // Heroes come last, sorted by rarity
                if (o1.getType() == DeliveryUnit.CHARACTER && o2.getType() == DeliveryUnit.CHARACTER) {
                    LogicCharacterData hero1 = (LogicCharacterData) o1.getData();
                    LogicCharacterData hero2 = (LogicCharacterData) o2.getData();
                    return hero1.getRarity().compareTo(hero2.getRarity());
                }
                return 0;
            }
        });

        if (random.nextInt(50) > 45) {
            DeliveryUnit tokenDoublers = new DeliveryUnit().setType(DeliveryUnit.TOKEN_DOUBLER);
            items.add(tokenDoublers);
            tokenDoublers.setCount(getRandomWithProba(random, 50, 40, 0, 10) * 100);
            player.setTokenDoublers(player.getTokenDoublers() + tokenDoublers.getCount());
        }

        return items;
    }

    public static void draw(Random random, ArrayList<DeliveryUnit> items, ArrayList<LogicCharacterData> lockedHeroes,
                            LogicPlayer player, ArrayList<LogicCharacterData> newHeroes, int draws) {
        double powerPointsAndCoinsChance = 0.0,
                rareHeroChance = 0.0,
                superRareHeroChance = 0.0,
                epicHeroChance = 0.0,
                mythicHeroChance = 0.0,
                legendaryHeroChance = 0.0,
                starPowerChance = 0.0,
                gadgetChance = 0.0;

        Collection<LogicHeroData> heroes = player.getHeroes().values();

        {
            final int v12 = player.getDraws() / 30; // every 30 draws increase the chance by 1
            int v9 = NEW_HERO_CHANCE;

            double v137 = clamp((RARE_CHANCE - v12) * v9, 0, 0x186a0);

            int v136 = SUPER_RARE_CHANCE;
            int v15 = EPIC_CHANCE;
            int v16 = MYTHIC_CHANCE;

            // int v17 = LEGENDARY_CHANCE;
            double v138 = clamp((LEGENDARY_CHANCE + v12) * v9, 0, 0x186a0);
            int v140 = STAR_POWER_CHANCE;
            double v19 = 0.0;
            int v20 = ACCESSORY_CHANCE;

            double v135 = 1000000 - (v137 + v136 * v9 + v15 * v9 + v16 * v9 + v138 + 1000 * v140 + 1000 * v20);

            int v144 = 0; // isProd

            int v21 = v15 * v9;

            // boolean hasRemainingPowerPointsInGatcha = true;

            boolean hasRemainingHeroesInGatchaWithRarity = hasRemainingHeroesInGatchaWithRarity(lockedHeroes,
                    Rarity.RARE); // rare

            boolean v25 = hasRemainingHeroesInGatchaWithRarity(lockedHeroes, Rarity.SUPER_RARE); // hasRemainingHeroesInGatchaWithRarity
            // super rare

            double v28 = 0.0;
            if (hasRemainingHeroesInGatchaWithRarity(lockedHeroes, Rarity.EPIC)) {
                v28 = v21 * 0.0001;
            }

            double v29 = 0.0;
            double v30 = 0.0;
            if (v25)
                v29 = v136 * v9 * 0.0001;
            double v31 = v16 * v9;
            if (hasRemainingHeroesInGatchaWithRarity)
                v30 = v137 * 0.0001;
            if (v144 != 0) {
                v30 = v137 * 0.0001;
                v29 = v136 * v9 * 0.0001;
            }
            double v33 = 0.0;
            if (hasRemainingHeroesInGatchaWithRarity(lockedHeroes, Rarity.MYTHIC)) {
                // hasRemainingHeroesInGatchaWithRarity mythic
                v33 = v31 * 0.0001;
            }
            if (v144 != 0)
                v28 = v21 * 0.0001;
            double v34 = 0.0;
            if (hasRemainingHeroesInGatchaWithRarity(lockedHeroes, Rarity.LEGENDARY)) {
                v34 = v138 * 0.0001;
            }
            if (v144 != 0)
                v33 = v31 * 0.0001;
            if (v144 != 0)
                v34 = v138 * 0.0001;
            boolean hasRemainingStarPowersInGatcha = hasRemainingStarPowersInGatcha(player, heroes);
            double v36 = 0.0;
            // int v139 = v35;
            if (hasRemainingStarPowersInGatcha)
                v36 = 1000 * v140 * 0.0001;
            boolean hasRemainingGadgetsInGatcha = hasRemainingGadgetsInGatcha(player, heroes);
            double v38 = v30 + v135 * 0.0001 + v29 + v28 + v33 + v34 + v36;
            if (hasRemainingGadgetsInGatcha)
                v19 = 1000 * v20 * 0.0001;
            // int v141 = v37;
            double v39 = v30 * 1000000.0;
            double v40 = v38 + v19;
            double roundf = roundf(v39 / (v38 + v19));
            double v41 = roundf;
            double v42 = v29 * 1000000.0;
            double v43 = v41;
            double roundf1 = roundf(v42 / v40);
            double v44 = roundf1;
            double v45 = v44;
            double v46 = roundf((v28 * 1000000.0) / v40);
            double v47 = v33 * 1000000.0;
            double v48 = v19 * 1000000.0;
            double v49 = v46;
            double roundf2 = roundf(v47 / v40);
            double v50 = roundf2;
            double v51 = v50;
            double v52 = v49 * 0.0001;
            double v53 = roundf((v34 * 1000000.0) / v40);
            double v54 = v53 * 0.0001;
            double v55 = roundf((v36 * 1000000.0) / v40);
            double v56 = v55;
            double v57 = v55 * 0.0001;
            double v58 = roundf(v48 / v40);
            powerPointsAndCoinsChance = roundf(100.0 - v43 * 0.0001 - v45 * 0.0001 - v52 - v51 * 0.0001 - v54 - v57
                    - v58 * 0.0001);
            rareHeroChance = roundf(v43 * 0.0001);
            superRareHeroChance = roundf(v45 * 0.0001);
            epicHeroChance = roundf(v52);
            mythicHeroChance = roundf(v51 * 0.0001);
            legendaryHeroChance = roundf(v54);
            starPowerChance = roundf(v56 * 0.0001);
            gadgetChance = roundf(v58 * 0.0001);
        }

        double[] probabilities = new double[]{powerPointsAndCoinsChance, rareHeroChance,
                superRareHeroChance,
                epicHeroChance, mythicHeroChance, legendaryHeroChance, starPowerChance,
                gadgetChance};

        int randomWithProba = getRandomWithProba(random, probabilities);
        if (randomWithProba == 1) {
            DeliveryUnit coins;
            if (items.isEmpty()) {
                coins = new DeliveryUnit().setType(DeliveryUnit.GOLD);
                items.add(coins);
            } else if (!hasRemainingPowerPointsInGatcha(heroes))
                coins = items.get(0);
            else
                coins = null; // player will get power points

            if (coins != null) {
                int amount = 0;
                amount += 7 + random.nextInt(63); // minimum 7, max 70
                amount *= coins.getCount() > 0 ? draws / 3 : draws;
                coins.setCount(coins.getCount() + amount);
                player.setGold(player.getGold() + amount);
                return;
            } else {
                LogicHeroData heroData = getRandomHeroForPowerPoints(random, heroes);
                DeliveryUnit powerPointsForHero = null;
                if (items.size() == 1) { // coins only
                    powerPointsForHero = new DeliveryUnit().setType(DeliveryUnit.POWER_POINT);
                    items.add(powerPointsForHero);
                } else {
                    int heroId = heroData.getCharacterData().getDataId();
                    for (DeliveryUnit item : items) {
                        if (item.getType() == DeliveryUnit.POWER_POINT && item.getData().getDataId() == heroId) {
                            powerPointsForHero = item;
                            break;
                        }
                    }
                    if (powerPointsForHero == null) {
                        powerPointsForHero = new DeliveryUnit().setType(DeliveryUnit.POWER_POINT);
                        items.add(powerPointsForHero);
                    }
                }
                int currentPoints = heroData.getPowerPoints();
                int maxPoints = LogicHeroData.getMaxedPoints();
                int points = 5 + random.nextInt(Math.min(maxPoints - currentPoints, 15));
                points *= powerPointsForHero.getCount() > 0 ? draws / 3 : draws;
                if (points + currentPoints > maxPoints)
                    points = maxPoints - currentPoints;
                powerPointsForHero.setData(heroData.getCharacterData());
                powerPointsForHero.setCount(powerPointsForHero.getCount() + points); // minimum 1, max 4
                player.setHeroPowerPoints(heroData, heroData.getPowerPoints() + points);
            }
        } else if (randomWithProba >= 2 && randomWithProba <= 6) { // some type of hero
            Rarity rarity = switch (randomWithProba) {
                case 2 -> Rarity.RARE;
                case 3 -> Rarity.SUPER_RARE;
                case 4 -> Rarity.EPIC;
                case 5 -> Rarity.MYTHIC;
                case 6 -> Rarity.LEGENDARY;
                default -> null;
            };
            LogicCharacterData hero = getRandomHeroWithRarity(random, lockedHeroes, rarity);
            if (hero != null) {
                DeliveryUnit newHero = new DeliveryUnit().setType(DeliveryUnit.CHARACTER).setData(hero);
                items.add(newHero);
                newHeroes.add(hero);
                lockedHeroes.remove(hero);
            } else {
                // coins fallback
                DeliveryUnit coins;
                if (items.size() > 0 && items.get(0).getType() == DeliveryUnit.GOLD)
                    coins = items.get(0);
                else {
                    coins = new DeliveryUnit().setType(DeliveryUnit.GOLD);
                    items.add(coins);
                }
                int amount = 7 + random.nextInt(63); // minimum 7, max 70
                amount *= coins.getCount() > 0 ? draws / 3 : draws;
                coins.setCount(coins.getCount() + amount);
                player.setGold(player.getGold() + amount);
            }
        } else { // star power / gadget
            LogicCardData cardData = getRandomCardWithType(random, player, heroes, randomWithProba == 7
                    ? LogicCardData.MetaType.UNIQUE
                    : LogicCardData.MetaType.ACCESSORY);
            player.unlockCard(cardData);
            System.out.println("CARD");
            // TODO: cards in delivery items
            // DeliveryItem newCard = new
            // DeliveryItem().setType(DeliveryItem.).setData(cardData);
        }
    }

    public static enum BoxType {
        SMALL,
        MEDIUM,
        BIG;
    }
}