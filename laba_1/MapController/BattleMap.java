package laba_1.MapController;

import laba_1.model.Buildings.Castle;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.TerrainType;
import laba_1.model.Tile;
import laba_1.model.Units.*;

import java.util.HashMap;


public class BattleMap extends Map {
    public BattleMap(int x, int y) {
        super(x, y); // Вызов конструктора родительского класса
    }

    public void placeUnit(Unit unit, int x, int y) {
        Tile tile = getTiles()[x][y];
        if (tile != null) {
            tile.setOccupant(unit);
            unit.setX(x);
            unit.setY(y);
        }
    }


    public void initializeBattleMap(Player player, Player bot) {
        for (int x = 0; x < getX(); x++) {
            for (int y = 0; y < getY(); y++) {
                getTiles()[x][y].setOccupant(null);
            }
        }
        placeUnits(player, 0, 0); // Игрок в левом верхнем углу
        placeUnits(bot, getX() - 4, getY() - 4);
    }

    private void placeUnits(Player owner, int startX, int startY) {
        Hero hero = owner.getHero();
        if (hero != null && hero.getArmy() != null) {
            int armyIndex = 0;
            for (Unit unit : hero.getArmy()) {
                // Рассчитываем позицию юнита относительно стартовой точки
                int x = startX + (armyIndex % 3); // 3 юнита в ряд
                int y = startY + (armyIndex / 3);
                if (x < getX() && y < getY()) {
                    placeUnit(unit, x, y);
                    armyIndex++;
                } else {
                    System.out.println("Недостаточно места для юнита " + unit.getClass().getSimpleName());
                    break;
                }
            }
        }
    }

    public void displayBattleMap(Player bot, Player player) {
        System.out.println("\n=== Игровая карта ===");
        HashMap<Class<? extends Unit>, String> unitEmojis = new HashMap<>();
        unitEmojis.put(Spearman.class, "⚔️");               // Мечник
        unitEmojis.put(Crossbowman.class, "\uD83C\uDFF9"); // Лучник (🏹)
        unitEmojis.put(Swordsman.class, "\uD83D\uDDE1");   // Мечник (🗡)
        unitEmojis.put(Cavalry.class, "\uD83D\uDC0E");     // Кавалерия (🐎)
        unitEmojis.put(Paladin.class, "\uD83E\uDD34");     // Паладин (🤴)

        for (int y = 0; y < getY(); y++) {
            for (int x = 0; x < getX(); x++) {
                Tile tile = getTiles()[x][y];
                Object occupant = tile.getOccupant();
                String emoji = "・"; // Пустая клетка

                if (occupant instanceof Hero) {
                    emoji = "\uD83D\uDC35"; //
                } else if (occupant instanceof Castle) {
                    emoji = "\uD83C\uDFF0"; //
                } else if (occupant instanceof Unit unit) {
                    String unitEmoji = unitEmojis.getOrDefault(unit.getClass(), "\uD83D\uDC7E"); // 👾 (неизвестный)
                    if (unit.getOwner() != null) {
                        emoji = unit.getOwner().equals(player) ? unitEmoji : unitEmoji + "!";
                    } else {
                        emoji = "?"; // Владелец не указан
                    }
                }

                System.out.print(emoji + "\t");
            }
            System.out.println();
        }
    }
}
