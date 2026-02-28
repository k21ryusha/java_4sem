package laba_1.game;

import laba_1.MapController.Map;
import laba_1.buildings.*;
import laba_1.model.*;
import laba_1.model.units.Unit;
import laba_1.util.Constants;
import laba_1.util.MovementCalculator;
import laba_1.view.Console;
import logs.GameLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.Scanner;


public class PlayerController {
    private final Scanner scanner;
    private final Player player;
    private final Console console;
    private final Map map;
    private final Game game;
    private Simulator simulator;

    public PlayerController(Player player, Map map, Console console, Game game, Simulator simulator) {
        this.player = player;
        this.map = map;
        this.console = console;
        this.game = game;
        this.scanner = new Scanner(System.in);
        this.simulator = simulator;
    }

    public void moveHero(Scanner scanner) {
        try {
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
            if (newTile.getTerrainType() == TerrainType.LAKE) {
                System.out.println("Нельзя ходить по озеру!");
                return;
            }
            if (newTile.getObstacle() == ObstacleType.IMPASSABLE) {
                System.out.println("Препятствие! Прохода нет!");
                return;
            }
            if (newTile.getObstacle() == ObstacleType.PENALTY_BLOCK) {
                System.out.println("Препятствие со штрафом! Прохода нет! Ваш штраф: " + MovementCalculator.calculatePenalty(newTile));
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
            interactNearResorts(simulator);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    if ((player.getHero().getDeadArmy() == null || player.getHero().getDeadArmy().isEmpty())) {
                        System.out.println("Вам некого воскрешать!");
                        GameLogger.logWarning("Пользователь попытался воскресить юнита, хотя у него нет умерших юнитов");
                        return;
                    } else {
                        revive();
                        return;
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
        } else if (lucky == 1) {
            System.out.println("Удача сегодня на вашей стороне. Некромант вылез из озера, а вот сможет он вам помочь или не сможет, решит монетка. " +
                    "\nОрел - воскресит, Решка - не воскресит");
            int lucky1 = new Random().nextInt(2);
            if (lucky1 == 0) {
                System.out.println("Выпала решка, увы, вам не повезло, приходите в следующий раз!.");
                return;
            }

            System.out.println("Удача сегодня на вашей стороне. Некромант воскресил 1 вашего юнита.");
            for (Unit unit : player.getHero().getDeadArmy()) {
                player.getHero().getDeadArmy().remove(unit);
                player.getHero().addReviveUnit(unit);
                player.getHero().addUnit(unit);
                if (game != null) {
                    game.incrementResurrectedUnits();
                }
                break;
            }
        }
    }

    public void interactNearResorts(Simulator simulator) {
        Hero hero = player.getHero();
        if (hero == null) {
            System.out.println("У вас нет героя!");
            return;
        }

        List<Resort> nearbyResorts = new ArrayList<>();
        List<int[]> resortCoords = new ArrayList<>();

        for (
                int dx = -1;
                dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int x = hero.getX() + dx;
                int y = hero.getY() + dy;

                if (x < 0 || x >= Constants.MAP_WIDTH || y < 0 || y >= Constants.MAP_HEIGHT)
                    continue;

                Tile tile = map.getTiles()[x][y];
                if (tile.getOccupant() instanceof Resort resort) {
                    nearbyResorts.add(resort);
                    resortCoords.add(new int[]{x, y});
                }
            }
        }

        if (nearbyResorts.isEmpty()) {
            System.out.println("Поблизости нет зданий для взаимодействия.");
            return;
        }

        System.out.println("Поблизости находятся здания:");
        for (
                int i = 0; i < nearbyResorts.size(); i++) {
            int[] coords = resortCoords.get(i);
            System.out.println(i + " — " + nearbyResorts.get(i).getName() + " (" + coords[0] + ", " + coords[1] + ")");
        }

        System.out.print("Хотите взаимодействовать с ближайшим зданием? (да/нет): ");
        if (!scanner.nextLine().

                trim().

                equalsIgnoreCase("да")) {
            return;
        }

        System.out.print("Выберите здание для взаимодействия: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (
                NumberFormatException e) {
            System.out.println("Неверный ввод.");
            return;
        }

        if (choice < 0 || choice >= nearbyResorts.size()) {
            System.out.println("Неверный номер.");
            return;
        }

        Resort resort = nearbyResorts.get(choice);
        List<Service> services = resort.getAvailableServices();

        System.out.println("Доступные услуги:");
        for (
                int i = 0; i < services.size(); i++) {
            System.out.println(i + " — " + services.get(i).getName());
        }

        System.out.print("Выберите услугу: ");
        int serviceIndex;
        try {
            serviceIndex = Integer.parseInt(scanner.nextLine());
        } catch (
                NumberFormatException e) {
            System.out.println("Неверный ввод.");
            return;
        }

        if (serviceIndex < 0 || serviceIndex >= services.size()) {
            System.out.println("Неверный номер.");
            return;
        }

        long now = TimeManager.getCurrentGameTimeMillis();
        resort.printStatus(now);

        Visitor visitor = new Visitor("Игрок", true, player);
        resort.update(now);

        if (resort.isAvailable()) {
            resort.serveVisitor(visitor, services.get(serviceIndex), now);
            System.out.println("Вы начали " + services.get(serviceIndex).getName() + ". Ожидайте завершения...");

            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                resort.update(TimeManager.getCurrentGameTimeMillis());
                if (!resort.isVisitorInside(visitor)) {
                    System.out.println("Отдых завершён.");
                    break;
                }
            }
        } else {
            System.out.println("Все места заняты. Ждёте? (да/нет)");
            if (scanner.nextLine().trim().equalsIgnoreCase("да")) {
                boolean queued = resort.enqueueVisitor(visitor, services.get(serviceIndex), TimeManager.getCurrentGameTimeMillis());
                if (!queued) {
                    System.out.println("Не удалось добавить вас в очередь.");
                }
            System.out.println("Вы встали в очередь. Ожидайте освобождения места...");
            while (resort.isVisitorQueued(visitor)) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                resort.update(TimeManager.getCurrentGameTimeMillis());
            }
            System.out.println("Для вас освободилось место. Услуга началась...");
            while (resort.isVisitorInside(visitor)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                resort.update(TimeManager.getCurrentGameTimeMillis());
            }
            System.out.println("Услуга оказана.");
        } else{
            System.out.println("Вы отказались от ожидания.");
            }
        }
    }
}

