package laba_1.battle;

import laba_1.MapController.BattleMap;
import laba_1.MapController.Map;
import laba_1.game.Game;
import laba_1.model.Buildings.Castle;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.Tile;
import laba_1.model.Units.Unit;
import laba_1.util.Constants;
import laba_1.view.Console;

import java.util.Scanner;

public class Battle {
    private final Player player;
    private final Player bot;
    private final BattleMap bmap;
    private final Console console;
    private final Scanner scanner = new Scanner(System.in);
    private Game game;
    private Map map;
    private boolean isFinalBattle = false;
    private Hero hero;

    public Battle(Player player, Player bot, Console console, Map map, Game game) {
        this.player = player;
        this.bot = bot;
        this.console = console;
        this.map = map;
        this.bmap = new BattleMap(8, 8);
        this.game = game;
    }

    public void setFinalBattle(boolean isFinalBattle) {
        this.isFinalBattle = isFinalBattle;
    }

    private boolean isInRange(Unit attacker, Unit target) {
        int dx = Math.abs(attacker.getX() - target.getX());
        int dy = Math.abs(attacker.getY() - target.getY());
        return dx <= attacker.getMovement() && dy <= attacker.getMovement();
    }

    private void attackUnit(Unit attacker, Unit defender, Tile defenderTile) {
        System.out.println(attacker.getName() + "( " + attacker.getOwner().getName() + " )" + " (HP: " + attacker.getHp() + ") атакует " +
                defender.getName() + "( " + defender.getOwner().getName() + " )"+ " (HP: " + defender.getHp() + ")");
        defender.setHp(defender.getHp() - attacker.getDamage());
        if (attacker.getHp() <= 0) {
            System.out.println(attacker.getName() + "( " + attacker.getOwner().getName() + " )"+ " был убит (HP: " + attacker.getHp() + ") " + defender.getName());
            defenderTile.setOccupant(null);
            attacker.getOwner().getHero().addDeadUnit(attacker);
            attacker.getOwner().setGold(attacker.getOwner().getGold() + defender.getReward());
        } else if (defender.getHp() <= 0) {
            System.out.println(defender.getName() + "( " + defender.getOwner().getName() + " )"+ " был убит (HP: " + defender.getHp() + ") " + attacker.getName());
            defender.getOwner().getHero().addDeadUnit(defender);
            defenderTile.setOccupant(null);
            attacker.getOwner().setGold(defender.getOwner().getGold() + defender.getReward());
        }
    }

    public void startFinalBattle() {
        System.out.println("\n=== ФИНАЛЬНАЯ БИТВА ЗА ЗАМОК ===");
        System.out.println("Герои сошлись в решающей схватке!");

        if(player.getHero().getArmy() == null || bot.getHero().getArmy() == null || player.getHero().getArmy().isEmpty()|| bot.getHero().getArmy().isEmpty()) {
            System.out.println("Вы не закупили армию, хотя у вас была такая возможность....");
            endFinalBattle();
        }
        bmap.initializeBattleMap(player, bot);
        Battle finalBattle = new Battle(player, bot, console, bmap,game);
        finalBattle.setFinalBattle(true);

        while (!isFinalBattleFinished()) {
            playerTurn();
            if (isFinalBattleFinished()) endFinalBattle();
            botTurn();
            if (isFinalBattleFinished()) endFinalBattle();
        }
    }

    public void startBattle(){
    System.out.println("Битва началась! Удачи!");
        bmap.initializeBattleMap(player, bot);
        checkDefeat(player);
        checkDefeat(bot);
        if(player.getHero().getArmy() == null || bot.getHero().getArmy() == null) {
            System.out.println("Вы не закупили армию, хотя у вас была такая возможность....");
            game.endGameFor(player);
        }
        while (!isBattleFinished()) {
            playerTurn();
            if (isBattleFinished()) break;
            botTurn();
            if (isBattleFinished()) break;
        }

        endBattle();
    }

    private void playerTurn() {
        bmap.displayBattleMap(bot, player);
        System.out.println("Выберите юнита для перемещения (введите координаты x y):");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера
        Tile tile = bmap.getTiles()[x][y];
        if (tile.getOccupant() instanceof Unit unit && unit.getOwner() == player) {
            System.out.println("Выберите направление для перемещения ():");
            System.out.println("1 - вверх, 2 - вниз, 3 - влево, 4 - вправо, 5 - вправо и вниз, 6 - влево и вниз, " +
                    "7 - вправо и вверх, 8 - влево и вверх");
            System.out.print("Ваш выбор: ");
            String direction = scanner.nextLine().toUpperCase();

            int newX = x;
            int newY = y;
            int movement = unit.getMovement();
            switch (direction) {
                case "1":
                    newY -= movement;
                    break;
                case "2":
                    newY += movement;
                    break;
                case "3":
                    newX -= movement;
                    break;
                case "4":
                    newX += movement;
                    break;
                case "5":
                    newX += movement;
                    newY += movement;
                    break;
                case "6":
                    newX -= movement;
                    newY += movement;
                    break;
                case "7":
                    newX += movement;
                    newY -= movement;
                    break;
                case "8":
                    newX -= movement;
                    newY -= movement;
                    break;
                default:
                    System.out.println("Некорректный ввод! Используйте 1, 2, 3, 4, 5, 6, 7, 8.");
                    return;
            }

            if (newX >= 0 && newX < bmap.getX() && newY >= 0 && newY < bmap.getY()) {
                Tile targetTile = bmap.getTiles()[newX][newY];
                Tile startTile = bmap.getTiles()[x][y];
                if (targetTile.getOccupant() == null && startTile.getOccupant() == unit) {
                    tile.setOccupant(null);
                    targetTile.setOccupant(unit);
                    unit.setX(newX);
                    unit.setY(newY);
                    System.out.println("Юнит перемещен.");
                    for (int i = 0; i < bmap.getX(); i++) {
                        for (int j = 0; j < bmap.getY(); j++) {
                            Tile target = bmap.getTiles()[i][j];
                            if (target.getOccupant() instanceof Unit enemy && enemy.getOwner() == bot && isInRange(unit, enemy)) {
                                attackUnit(unit, enemy, target);
                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("Целевая клетка занята.");
                }
            } else {
                System.out.println("Невозможно выйти за пределы карты.");
            }
        } else {
            System.out.println("Выбранная клетка не содержит вашего юнита.");
        }
    }

    private void botTurn() {
        bmap.displayBattleMap(bot, player);
        // Простейшая логика для бота: перемещает первого доступного юнита в случайном направлении
        for (int x = 0; x < bmap.getX(); x++) {
            for (int y = 0; y < bmap.getY(); y++) {
                Tile tile = bmap.getTiles()[x][y];
                if (tile.getOccupant() instanceof Unit unit && unit.getOwner() == bot) {
                    int direction = (int) (Math.random() * 4);
                    int newX = x;
                    int newY = y;
                    int movement = unit.getMovement();
                    switch (direction) {
                        case 1:
                            newY -= movement;
                            break;
                        case 2:
                            newY += movement;
                            break;
                        case 3:
                            newX -= movement;
                            break;
                        case 4:
                            newX += movement;
                            break;
                        case 5:
                            newX += movement;
                            newY += movement;
                            break;
                        case 6:
                            newX -= movement;
                            newY += movement;
                            break;
                        case 7:
                            newX += movement;
                            newY -= movement;
                            break;
                        case 8:
                            newX -= movement;
                            newY -= movement;
                            break;
                    }

                    if (newX >= 0 && newX < bmap.getX() && newY >= 0 && newY < bmap.getY()) {
                        Tile targetTile = bmap.getTiles()[newX][newY];
                        if (targetTile.getOccupant() == null) {
                            tile.setOccupant(null);
                            targetTile.setOccupant(unit);
                            unit.setX(newX);
                            unit.setY(newY);
                            System.out.println("Бот переместил юнита.");
                            for (int i = 0; i < bmap.getX(); i++) {
                                for (int j = 0; j < bmap.getY(); j++) {
                                    Tile target = bmap.getTiles()[i][j];
                                    if (target.getOccupant() instanceof Unit enemy && enemy.getOwner() == player && isInRange(unit, enemy)) {
                                        attackUnit(unit, enemy, target);
                                        return;
                                    }
                                }
                            }
                            return;
                        }
                        else{
                            System.out.println("Бот не переместил юнита.");
                        }
                    }
                }
            }
        }
    }

    private boolean isBattleFinished() {
        boolean playerHasUnits = false;
        boolean botHasUnits = false;

        for (int x = 0; x < bmap.getX(); x++) {
            for (int y = 0; y < bmap.getY(); y++) {
                Tile tile = bmap.getTiles()[x][y];
                if (tile.getOccupant() instanceof Unit unit) {
                    if (unit.getOwner() == player) {
                        playerHasUnits = true;
                    } else if (unit.getOwner() == bot) {
                        botHasUnits = true;
                    }
                }
            }
        }

        if (!playerHasUnits) {
            System.out.println("Все ваши юниты уничтожены. Вы проиграли!");
            moveLoserHeroToStart(player);
            player.getCastle().setDiscontent(player.getCastle().getDiscontent() + (float) 5.0);
            player.getHero().setArmy(null);
            return true;
        } else if (!botHasUnits) {
            System.out.println("Все юниты бота уничтожены. Вы победили!");
            bot.getHero().setArmy(null);
            moveLoserHeroToStart(bot);
            return true;
        }
        return false;
    }

    private void moveLoserHeroToStart(Player loser) {
        Hero hero = loser.getHero();
        Castle castle = loser.getCastle();
        int startX;
        int startY;

        if (hero != null && castle != null) {
            // Убираем героя с текущей клетки
            map.getTiles()[hero.getX()][hero.getY()].setOccupant(null);

            if (hero.getOwner() == bot) {
                startX = castle.getX() - 1;
                startY = castle.getY() - 1;
            }else{
                startX = castle.getX() + 1;
                startY = castle.getY() + 1;
            }
            hero.setX(startX);
            hero.setY(startY);
            map.getTiles()[startX][startY].setOccupant(hero);

            System.out.println("Герой игрока " + loser.getName() + " возвращён к замку.");
        }
    }

    private void checkDefeat(Player playerToCheck) {
        Hero hero = playerToCheck.getHero();
        boolean noUnits = hero == null || hero.getArmy() == null;
        boolean noGold = playerToCheck.getGold() < 100; // Меньше чем минимальная цена за юнита

        if (noUnits && noGold) {
            System.out.println(playerToCheck.getName() + " проиграл игру! У него нет золота и армии.");
            game.endGameFor(playerToCheck); // вызовем метод из текущего экземпляра игры
        }

    }

    private void endFinalBattle() {
        if (bot.getHero().getArmy() == null) {
            System.out.println("\n=== ВАША ПОБЕДА ===");
            System.out.println("Вы захватили замок противника и победили в игре!");
            game.endGame(true);
        } else {
            System.out.println("\n=== ВАШЕ ПОРАЖЕНИЕ ===");
            System.out.println("Ваш герой пал в финальной битве...");
            game.endGame(false);
        }
    }

    private boolean isFinalBattleFinished() {
        boolean playerHasUnits = false;
        boolean botHasUnits = false;
        for (int x = 0; x < bmap.getX(); x++) {
            for (int y = 0; y < bmap.getY(); y++) {
                Tile tile = bmap.getTiles()[x][y];
                if (tile.getOccupant() instanceof Unit unit) {
                    if (unit.getOwner() == player) {
                        playerHasUnits = true;
                    } else if (unit.getOwner() == bot) {
                        botHasUnits = true;
                    }
                }
            }
        }

        if (!playerHasUnits) {;
            return true;

        } else if (!botHasUnits) {;
            return true;
        }
        return false;
    }
    private void endBattle () {
        System.out.println("Битва завершена.");
    }
}


