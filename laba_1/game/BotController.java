package laba_1.game;

import laba_1.MapController.Map;
import laba_1.model.Buildings.*;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.TerrainType;
import laba_1.model.Tile;
import laba_1.model.Units.*;
import laba_1.util.Constants;
import laba_1.util.MovementCalculator;
import laba_1.view.Console;
import laba_1.battle.*;

import java.util.HashMap;
import java.util.Random;

public class BotController {
    private final Player bot;
    private final Map map;
    private final Console console;
    private final Random random = new Random();

    public BotController(Player bot, Map map, Console console) {
        this.bot = bot;
        this.map = map;
        this.console = console;
    }

    public void performTurn(Player player) {
        if(!bot.isDefeated()) {
            if (bot.getHero() == null) {
                if (!bot.getCastle().hasBuilding(Tavern.class)) {
                    purchaseBuilding(Tavern.class);
                }
                if (bot.getGold() >= 700) {
                    purchaseHero();
                }
            }
            purchaseRandomUnit();
            moveUnitsTowards(player);
        }
        else{
            System.out.println("Вы победили! У противника нет ни юнитов, ни золота.");
        }
    }


    private void moveUnitsTowards(Player player) {
        Castle botCastle = bot.getCastle();
        Hero botHero = bot.getHero();
        if (botHero != null && botCastle != null) {
            Castle playerCastle = player.getCastle();
            if (playerCastle == null) {
                System.out.println("У игрока нет замка!");
                return;
            }
            int targetX = playerCastle.getX();
            int targetY = playerCastle.getY();
            int currentX = botHero.getX();
            int currentY = botHero.getY();
            int dx = Integer.compare(targetX, currentX);
            int dy = Integer.compare(targetY, currentY);
            int newX = currentX + dx;
            int newY = currentY + dy;
            if (newX < 0 || newX >= Constants.MAP_WIDTH || newY < 0 || newY >= Constants.MAP_HEIGHT) {
                System.out.println("Bot hero не может выйти за границы карты!");
                return;
            }
            Tile newTile = map.getTiles()[newX][newY];
            if (newTile.getOccupant() != null) {
                System.out.println("Bot hero не может переместиться, клетка занята.");
                return;
            }
            map.getTiles()[currentX][currentY].setOccupant(null);
            botHero.setX(newX);
            botHero.setY(newY);
            newTile.setOccupant(botHero);
            int penalty = MovementCalculator.calculatePenalty(newTile);
            bot.setGold(bot.getGold() - penalty);
            System.out.println("Герой бота переместился в (" + newX + ", " + newY + "). Штраф: " + penalty + " золота.");
        }
    }

    public void purchaseRandomUnit() {
        Class<? extends Unit>[] unitTypes = new Class[]{Spearman.class, Crossbowman.class, Swordsman.class, Cavalry.class, Paladin.class};

        HashMap<Class<? extends Unit>, Class<? extends Building>> buildingMap = new HashMap<>();
        buildingMap.put(Spearman.class, Watchtower.class);
        buildingMap.put(Crossbowman.class, CrossbowTower.class);
        buildingMap.put(Swordsman.class, Armory.class);
        buildingMap.put(Cavalry.class, Arena.class);
        buildingMap.put(Paladin.class, Cathedral.class);

        int index = random.nextInt(unitTypes.length);
        Class<? extends Unit> selectedUnitClass = unitTypes[index];
        Class<? extends Building> requiredBuildingClass = buildingMap.get(selectedUnitClass);

        Castle castle = bot.getCastle();
        if (castle == null) {
            System.out.println("У бота отсутствует замок.");
            return;
        }

        if (!castle.hasBuilding(requiredBuildingClass)) {
            try {
                Building building = requiredBuildingClass.getDeclaredConstructor().newInstance();
                if (bot.getGold() >= building.getCost()) {
                    bot.setGold(bot.getGold() - building.getCost());
                    castle.addBuilding(building);
                    System.out.println("Bot построил " + requiredBuildingClass.getSimpleName());
                } else {
                    System.out.println("Bot не имеет достаточного золота для постройки " + requiredBuildingClass.getSimpleName());
                    return;
                }
            } catch (Exception e) {
                System.out.println("Ошибка при создании здания " + requiredBuildingClass.getSimpleName() + ": " + e.getMessage());
                return;
            }
        }
        try {
            Unit tempUnit = selectedUnitClass.getDeclaredConstructor(Player.class).newInstance(bot);
            int cost = tempUnit.getCost();
            if (bot.getGold() >= cost) {
                bot.setGold(bot.getGold() - cost);
                bot.getHero().addUnit(tempUnit);
                System.out.println("Bot нанял " + selectedUnitClass.getSimpleName());
            } else {
                System.out.println("Bot не имеет достаточного золота для найма " + selectedUnitClass.getSimpleName());
            }
        } catch (Exception e) {
            System.out.println("Ошибка при найме юнита " + selectedUnitClass.getSimpleName() + ": " + e.getMessage());
        }
    }

    private void purchaseHero() {
        int cost = 700;
        if (bot.getGold() < cost) {
            System.out.println("Bot не имеет достаточного золота для найма героя.");
            return;
        }
        bot.setGold(bot.getGold() - cost);
        int x = bot.getCastle().getX() - 1;
        int y = bot.getCastle().getY() - 1;
        Hero hero = new Hero("Bot Hero", x, y,bot);
        bot.setHero(hero);
        map.getTiles()[x][y].setOccupant(hero);
        System.out.println("Bot нанял героя.");
        console.displayGameMap(map);
    }

    private void purchaseBuilding(Class<? extends Building> buildingClass) {
        try {
            Building building = buildingClass.getDeclaredConstructor().newInstance();
            if (bot.getGold() >= building.getCost()) {
                bot.setGold(bot.getGold() - building.getCost());
                bot.getCastle().addBuilding(building);
                System.out.println("Bot построил " + building.getName() + ".");
            } else {
                System.out.println("Bot не имеет достаточного золота для постройки " + building.getName() + ".");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при создании здания: " + e.getMessage());
        }
    }

    public boolean canRevive() {
        if ((bot.getHero().getDeadArmy() == null || bot.getHero().getDeadArmy().isEmpty())) {
            System.out.println("Вам некого воскрешать!");
            return false;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0)
                    continue;
                int newX = bot.getHero().getX() + dx;
                int newY = bot.getHero().getY() + dy;
                if (newX < 0 || newX >= Constants.MAP_WIDTH || newY < 0 || newY >= Constants.MAP_HEIGHT)
                    continue;
                Tile tile = map.getTiles()[newX][newY];
                if (tile.getTerrainType() == TerrainType.LAKE) {
                    revive();
                }
            }
        }
        return false;
    }
    public void revive() {
        System.out.println("У боту появилась возможность воскресить погибшего юнита! Данный обряд может совершить только некромант сидящий на дне озера.");
        System.out.println("Сейчас мы узнаем, захочет ли он боту помочь.");
        int lucky = new Random().nextInt(2);
        if (lucky == 0) {
            System.out.println("Увы, боту не повезло!");
        } else if (lucky== 1) {
            System.out.println("Удача сегодня на его стороне. Некромант вылез из озера, а вот сможет он ему помочь или не сможет, решит монетка. " +
                    "\nОрел - воскресит, Решка - не воскресит");
            int lucky1 = new Random().nextInt(2);
            if (lucky1 == 0) {
                System.out.println("Выпала решка, увы, боту не повезло!");
            } else if (lucky1 == 1) {
                System.out.println("Удача сегодня на его стороне. Некромант воскресил 1 юнита бота .");
                for (Unit unit : bot.getHero().getDeadArmy()) {
                    bot.getHero().getDeadArmy().remove(unit);
                    bot.getHero().addReviveUnit(unit);
                    bot.getHero().addUnit(unit);
                    break;
                }
            }
        }
    }
}
