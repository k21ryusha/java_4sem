package laba_1.game;

import laba_1.MapController.Map;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.TerrainType;
import laba_1.model.Tile;
import laba_1.model.Units.Unit;
import laba_1.util.Constants;
import laba_1.util.MovementCalculator;
import laba_1.view.Console;

import java.util.Random;

import java.util.Scanner;


public class PlayerController {
    private final Scanner scanner;
    private final Player player;
    private final Console console;
    private final Map map;

    public PlayerController(Player player, Map map, Console console) {
        this.player = player;
        this.map = map;
        this.console = console;
        this.scanner = new Scanner(System.in);
    }

    public void moveHero() {
        if (player.getHero() == null) {
            System.out.println("У вас нет героя! Сначала купите его.");
            return;
        }
        Hero hero = player.getHero();
        System.out.println("Текущая позиция героя: (" + hero.getX() + ", " + hero.getY() + ")");
        System.out.println("Выберите направление:");
        System.out.println("1 - вверх, 2 - вниз, 3 - влево, 4 - вправо, 5 - вправо и вниз, 6 - влево и вниз, " +
                "7 - вправо и вверх, 8 - влево и вверх");
        System.out.print("Ваш выбор: ");

        String direction = scanner.next().toUpperCase();
        int newX = hero.getX();
        int newY = hero.getY();

        switch (direction) {
            case "1":
                newY--;
                break;
            case "2":
                newY++;
                break;
            case "3":
                newX--;
                break;
            case "4":
                newX++;
                break;
            case "5":
                newX++;
                newY++;
                break;
            case "6":
                newX--;
                newY++;
                break;
            case "7":
                newX++;
                newY--;
                break;
            case "8":
                newX--;
                newY--;
                break;
            default:
                System.out.println("Некорректный ввод! Используйте 1, 2, 3, 4, 5, 6, 7, 8.");
                return;
        }

        // Проверяем, находится ли новое положение в пределах карты
        if (newX < 0 || newX >= Constants.MAP_WIDTH || newY < 0 || newY >= Constants.MAP_HEIGHT) {
            System.out.println("Герой не может выйти за границы карты!");
            return;
        }

        Tile newTile = map.getTiles()[newX][newY];
        if (newTile.getOccupant() != null) {
            System.out.println("На этой клетке уже находится объект! Выберите другое направление.");
            return;
        }
        if(newTile.getTerrainType() == TerrainType.LAKE) {
            System.out.println("Нельзя ходить по озеру!");
            return;
        }

        map.getTiles()[hero.getX()][hero.getY()].setOccupant(null);
        hero.setX(newX);
        hero.setY(newY);
        newTile.setOccupant(hero);
        canRevive();

        int penalty = MovementCalculator.calculatePenalty(newTile);
        player.setGold(player.getGold() - penalty);

        System.out.println("Герой переместился в (" + newX + ", " + newY + "). Штраф: " + penalty + " золота.");
        balance();
    }

    public void balance() {
        System.out.println("\nВаш баланс: " + player.getGold());
    }

    public void canRevive() {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0)
                    continue;
                int newX = player.getHero().getX() + dx;
                int newY = player.getHero().getY() + dy;
                if (newX < 0 || newX >= Constants.MAP_WIDTH || newY < 0 || newY >= Constants.MAP_HEIGHT)
                    continue;
                Tile tile = map.getTiles()[newX][newY];
                if (tile.getTerrainType() == TerrainType.LAKE) {
                    if ((player.getHero().getDeadArmy() == null || player.getHero().getDeadArmy().isEmpty()) ) {
                        System.out.println("Вам некого воскрешать!");
                        return;
                    }else{
                        revive();
                    }
                }
            }
        }
    }

    public void revive() {
        System.out.println("У вас появилась возможность воскресить погибшего юнита! Данный обряд может совершить только некромант сидящий на дне озера.");
        System.out.println("Сейчас мы узнаем, захочет ли он вам помочь.");
        int lucky = new Random().nextInt(2);
        if (lucky == 0) {
            System.out.println("Увы, вам не повезло, приходите в следующий раз!.");
        }else if (lucky == 1) {
            System.out.println("Удача сегодня на вашей стороне. Некромант вылез из озера, а вот сможет он вам помочь или не сможет, решит монетка. " +
                    "\nОрел - воскресит, Решка - не воскресит");
            int lucky1 = new Random().nextInt(2);
            if (lucky1 == 0) {
                System.out.println("Выпала решка, увы, вам не повезло, приходите в следующий раз!.");
            }else if (lucky1 == 1) {
                System.out.println("Удача сегодня на вашей стороне. Некромант воскресил 1 вашего юнита.");
                for(Unit unit : player.getHero().getDeadArmy()){
                    player.getHero().getDeadArmy().remove(unit);
                    player.getHero().addReviveUnit(unit);
                    player.getHero().addUnit(unit);
                    break;
                }
            }
        }
    }
}
