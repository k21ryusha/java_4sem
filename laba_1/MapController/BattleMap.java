package laba_1.MapController;

import laba_1.model.BattleTile;
import laba_1.model.buildings.Castle;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.Tile;
import laba_1.model.units.*;

import java.util.HashMap;
import java.util.List;


public class BattleMap  {
    private final int width;
    private final int height;
    private final BattleTile[][] tiles;

    public BattleMap(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new BattleTile[width][height];

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                tiles[x][y] = new BattleTile(x, y);
    }

    public void placeUnits(List<Unit> army, int startX, int startY) {
        int index = 0;
        for (Unit unit : army) {
            int x = startX + (index % 3);
            int y = startY + (index / 3);
            if (x < width && y < height) {
                tiles[x][y].setOccupant(unit);
                unit.setX(x);
                unit.setY(y);
                index++;
            } else {
                System.out.println("Недостаточно места для юнита " + unit.getName());
            }
        }
    }

    public void initializeBattleMap(Player player, Player bot) {
            clearMap();
            placeUnits(player.getHero().getArmy(), 0, 0);
            placeUnits(bot.getHero().getArmy(), width - 4, height - 4);
    }

    public void clearMap() {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                tiles[x][y].setOccupant(null);
    }


    public void displayBattleMap(Player bot, Player player) {
        HashMap<Class<? extends Unit>, String> unitIcons = new HashMap<>();
        unitIcons.put(Spearman.class, "⚔️");
        unitIcons.put(Crossbowman.class, "🏹");
        unitIcons.put(Swordsman.class, "🗡");
        unitIcons.put(Cavalry.class, "🐎");
        unitIcons.put(Paladin.class, "🤴");

        System.out.println("\n=== Боевая карта ===");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Unit unit = tiles[x][y].getOccupant();
                if (unit == null) {
                    System.out.print("・\t");
                } else {
                    String icon = unitIcons.getOrDefault(unit.getClass(), "👾");
                    System.out.print((unit.getOwner().equals(player) ? icon : icon + "!") + "\t");
                }
            }
            System.out.println();
        }
    }

    public BattleTile[][] getTiles() {
        return tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
