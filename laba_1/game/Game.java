package laba_1.game;

import laba_1.model.*;
import laba_1.model.Units.*;
import laba_1.util.*;
import laba_1.model.Map;
import laba_1.view.*;

import java.util.Scanner;

import static laba_1.model.Map.getTiles;

public class Game {
    private boolean isGameOver = false;
    private final Scanner scanner = new Scanner(System.in);
    private final Console console;
    private Player player;
    private Player bot;
    private final Map map;

    public Game(Map map, Console console) {
        this.console = console;
        this.map = map;
        init();
    }
    public void init(){
        Player player = new Player("Player", 1000);
        Player bot = new Player("Bot", 1000);
        this.player = player;
        this.bot = bot;
    }
    public void buyHero() {
        if (player.getHero() != null) {
            System.out.println("У вас уже есть герой!");
            return;
        }

        System.out.println("Покупка героя стоит 500 золота.");
        if (player.getGold() < 500) {
            System.out.println("Недостаточно золота!");
            return;
        }

        player.setGold(player.getGold() - 500);
        Hero hero = new Hero("Ваш герой", 1, 1, 0);
        player.setHero(hero);
        map.getTiles()[1][1].setOccupant(hero); // Размещаем героя на карте
        console.displayGameMap(map); // Обновляем карту после действия
        System.out.println("Герой куплен!");

    }
    public void balance(){
        System.out.println("Ваш баланс: " + player.getGold());
    }
    public void buyUnits() {
        if (player.getHero() == null) {
            System.out.println("Сначала купите героя!");
            return;
        }

        System.out.println("Выберите юнита для покупки:");
        System.out.println("1. Копейщик (100 золота)");
        System.out.println("2. Арбалетчик (150 золота)");
        System.out.print("Ваш выбор: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        Unit unit = null;
        int cost = 0;

        switch (choice) {
            case 1:
                unit = new Spearman(1,1);
                cost = 100;
                break;
            case 2:
                unit = new Crossbowman(1,1);
                cost = 150;
                break;
            default:
                System.out.println("Неккоректный выбор.");
                return;
        }

        if (player.getGold() < cost) {
            System.out.println("Недостаточно золота!");
            return;
        }

        player.setGold(player.getGold() - cost);
        player.getHero().addUnit(unit);
        System.out.println("Юнит куплен!");
        console.displayGameMap(map);
    }

    public void endTurn() {
        System.out.println("Ход завершен. Передача управления боту...");
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

        // Проверяем, свободна ли клетка для перемещения
        Tile newTile = getTiles()[newX][newY];
        if (newTile.getOccupant() != null) {
            System.out.println("На этой клетке уже находится объект! Выберите другое направление.");
            return;
        }

        // Очищаем текущую клетку и перемещаем героя
        getTiles()[hero.getX()][hero.getY()].setOccupant(null);
        hero.setX(newX);
        hero.setY(newY);
        newTile.setOccupant(hero);
        console.displayGameMap(map);
        System.out.println("Герой переместился в (" + newX + ", " + newY + ")");
    }
}
