package laba_1.game;

import com.google.gson.Gson;
import laba_1.MapController.BattleMap;
import laba_1.MapController.Map;
import laba_1.battle.Battle;
import laba_1.buildings.*;
import laba_1.model.*;
import laba_1.model.buildings.*;
import laba_1.model.units.*;
import laba_1.records.RecordManager;
import laba_1.records.Record;
import laba_1.save.GameState;
import laba_1.util.Constants;
import laba_1.util.JsonUtil;
import laba_1.view.Console;
import logs.GameLogger;

import java.io.*;
import java.util.*;

import static laba_1.util.FileName.generateSaveFileName;


public class Game {
    private final Scanner scanner = new Scanner(System.in);
    private final Console console;
    private final Map map;
    private boolean testMode = false;
    private static boolean isGameOver = false;
    private static boolean isGameClosed = false;
    private Player player;
    private Player bot;
    private PlayerController playerController;
    private BotController botController;
    private Battle battle;
    private final String playerName;
    private int turnCounter = 0;
    private int goldEarnedFromKills = 0;
    private final int totalBattleVictories = 0;
    private int battleVictories = 0;
    private long startTimeMillis;
    private int resurrectedUnitsCount = 0;
    private BattleMap bmap;
    private int turnsToVictory = Integer.MAX_VALUE;
    private Simulator simulator;
    private long gameStartTimeMs;


    public Game(Map map, Console console, String playerName) {
        this.console = console;
        this.map = map;
        this.playerName = playerName;
        init();
    }

    public static boolean isGameOver() {
        return isGameOver;
    }

    public static void setGameOver(boolean gameOver) {
        Game.isGameOver = gameOver;
    }

    public Map getMap() {
        return map;
    }

    public void init() {
        if (player == null) player = new Player(playerName, 2000);
        if (bot == null) bot = new Player("Bot", 2000);

        // Используем map из сохранения
        if (map == null) {
            throw new IllegalStateException("Карта не загружена!");
        }

        Castle playerCastle = (Castle) map.getTiles()[0][0].getOccupant();
        if (playerCastle == null) {
            playerCastle = new Castle(player, 0, 0);
            map.getTiles()[0][0].setOccupant(playerCastle);
        }
        Castle botCastle = (Castle) map.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1].getOccupant();
        if (botCastle == null) {
            botCastle = new Castle(bot, Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1);
            map.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1].setOccupant(botCastle);
        }
        Resort hotel = new Hotel();
        Resort cafe = new Cafe();
        Resort barbershop = new Barbershop();

        map.getTiles()[5][0].setOccupant(hotel);
        map.getTiles()[5][3].setOccupant(cafe);
        map.getTiles()[2][7].setOccupant(barbershop); //

        playerCastle.setOwner(player);
        botCastle.setOwner(bot);
        player.setCastle(playerCastle);
        bot.setCastle(botCastle);
        simulator = new Simulator(player,this,map);
        playerController = new PlayerController(player, map, console, this,simulator);
        botController = new BotController(bot, map, console);
        bmap = new BattleMap(5, 5);
        battle = new Battle(player, bot, console, bmap, this);
    }

    public void startGame() {
        GameLogger.logInfo("Игра начата");
        gameStartTimeMs = TimeManager.getCurrentGameTimeMillis();
        simulator = new Simulator(player, this, map);
        Thread simulatorThread = new Thread(() -> {
            try {
                simulator.startSimulation();
            } catch (Exception e) {
                e.printStackTrace();
                GameLogger.logError("Ошибка в симуляторе: " + e.getMessage());
            }
        });
        simulatorThread.setDaemon(true);
        simulatorThread.start();

        try {
            while (!isGameOver) {
                startTimeMillis = System.currentTimeMillis();
                turnCounter++;
                boolean turnEnded = false;
                while (!turnEnded && !isGameOver) {
                    console.displayGameMap(map);

                    long nowMs = TimeManager.getCurrentGameTimeMillis();
                    long elapsedMs = nowMs - gameStartTimeMs;
                    long elapsedMin = elapsedMs / TimeManager.MILLIS_PER_GAME_MINUTE;
                    long minutesSince08 = elapsedMin + 8 * 60;
                    int hours   = (int)((minutesSince08 / 60) % 24);
                    int minutes = (int)(minutesSince08 % 60);
                    String timeStr = String.format("%02d:%02d", hours, minutes);
                    System.out.println("Внутриигровое время: " + timeStr);

                    System.out.println("\n=== Главное меню ===");
                    System.out.println("1. Войти в замок (игрок)");
                    System.out.println("2. Совершить ход (переместить героя)");
                    System.out.println("3. Завершить ход");
                    System.out.println("4. Сохранить игру");
                    System.out.println("0. Выход из игры");
                    System.out.println("Ваш баланс: " + player.getGold() + " золота.");
                    System.out.println("Баланс бота: " + bot.getGold() + " золота.");
                    System.out.println("Недовольство вашей армии: " + player.getCastle().getDiscontent() + "%");
                    System.out.print("Выберите действие: ");

                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    switch (choice) {
                        case 1:
                            enterCastle();
                            break;
                        case 2:
                            playerController.moveHero(scanner);
                            break;
                        case 3:
                            turnEnded = true;
                            break;
                        case 0:
                            isGameClosed = true;
                            System.out.println("Выход из игры...");
                            return;
                        case 4:
                            saveGame();
                            break;
                        default:
                            System.out.println("Некорректный ввод. Попробуйте снова.");
                            GameLogger.logWarning("Пользователь ввёл неверный пункт меню.");
                            break;
                    }
                }
                endTurn();
                botController.performTurn(player);
                checkVictoryConditions();
                if (!isGameOver) {
                    checkHeroEncounter();
                }
                checkDiscontent();
            }
        } catch (Exception e) {
            e.printStackTrace();
            GameLogger.logError("Ошибка при запуске игры: " + e.getMessage());
        }
    }


    public Player getPlayer() {
        return this.player;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public Player getBot() {
        return this.bot;
    }


    public void endTurn() {
        System.out.println("Ход завершен. Передача управления боту...");
    }

    public void balance() {
        System.out.println("\nВаш баланс: " + player.getGold());
    }

    private void manageCastle(Castle castle) {
        while (true) {
            balance();
            System.out.println("Вы находитесь в замке.");
            System.out.println("1. Построить здание");
            System.out.println("2. Нанять героя");
            System.out.println("3. Нанять юнитов");
            System.out.println("4. Уволить юнитов");
            System.out.println("0. Выйти из замка");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    buildStructure(castle);
                    break;
                case 2:
                    hireHero(castle);
                    break;
                case 3:
                    hireUnits(castle);
                    break;
                case 4:
                    sackUnits(player);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Некорректный ввод. Попробуйте снова.");
            }
        }
    }

    public void sackUnits(Player player) {
        if (player.getHero().getArmy() != null && !player.getHero().getArmy().isEmpty()) {
            System.out.println("Список юнитов доступных для увольнения: ");
            int i = 0;
            for (Unit unit : player.getHero().getArmy()) {
                ++i;
                System.out.println(i + " " + unit.getName());
            }

            System.out.print("Ваш выбор: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice > 0 && choice <= player.getHero().getArmy().size()) {
                Unit unit = player.getHero().getArmy().get(choice - 1);
                if (!player.getHero().getReviveArmy().contains(unit)) {
                    player.getHero().getArmy().remove(choice - 1);
                    player.getHero().getSackedArmy().add(unit);
                    System.out.print("Юнит " + unit.getName() + " был уволен.");
                    player.getCastle().setDiscontent(player.getCastle().getDiscontent() + ((float) 1 / i) * 100);
                    System.out.print("Недовольство армии увеличилось на " + player.getCastle().getDiscontent() + "%");
                } else {
                    System.out.println("Этого юнита нельзя уволить! Он находится в списке воскресших.  ");
                }
            } else {
                System.out.println("Неверный выбор!");
            }
        } else {
            System.out.println("У вас нет юнитов доступных для увольнения!");
        }
    }

    public void enterCastle() {
        Tile currentTile = map.getTiles()[0][0];
        if (currentTile != null && currentTile.getOccupant() instanceof Castle castle) {
            if (castle.getOwner() == player) {
                manageCastle(castle);
            }
        } else {
            System.out.println("Вы не находитесь в замке.");
        }
    }

    private void buildStructure(Castle castle) {
        System.out.println("\nВыберите здание для постройки:");
        System.out.println("1. Таверна (позволяет нанимать героев) - 150 золота");
        System.out.println("2. Сторожевой пост (позволяет нанимать юнитов 1 уровня) - 200 золота");
        System.out.println("3. Башня арбалетчиков (позволяет нанимать юнитов 2 уровня) - 250 золота");
        System.out.println("4. Оружейная (позволяет нанимать юнитов 3 уровня) - 300 золота");
        System.out.println("5. Арена (позволяет нанимать юнитов 4 уровня) - 350 золота");
        System.out.println("6. Собор (позволяет нанимать юнитов 5 уровня) - 400 золота");
        System.out.println("0. Назад");
        System.out.print("Ваш выбор: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                if (castle.hasBuilding(Tavern.class)) {
                    System.out.println("Таверна уже построена!");
                } else {
                    Tavern tavern = new Tavern();
                    if (player.getGold() >= tavern.getCost()) {
                        player.setGold(player.getGold() - tavern.getCost());
                        castle.addBuilding(tavern);
                        System.out.println("Таверна построена!");
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                }
                break;
            case 2:
                if (castle.hasBuilding(Watchtower.class)) {
                    System.out.println("Смотровая башня уже построена!");
                } else {
                    Watchtower watchtower = new Watchtower();

                    if (player.getGold() >= watchtower.getCost()) {
                        player.setGold(player.getGold() - watchtower.getCost());
                        castle.addBuilding(watchtower);
                        System.out.println("Сторожевой пост построен!");
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                }
                break;
            case 3:
                if (castle.hasBuilding(CrossbowTower.class)) {
                    System.out.println("Башня арбалетчиков уже построен!");
                } else {
                    CrossbowTower crossbowtower = new CrossbowTower();

                    if (player.getGold() >= crossbowtower.getCost()) {
                        player.setGold(player.getGold() - crossbowtower.getCost());
                        castle.addBuilding(crossbowtower);
                        System.out.println("Башня арбалетчиков построена!");
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                }
                break;
            case 4:
                if (castle.hasBuilding(Armory.class)) {
                    System.out.println("Оружейная уже построена!");
                } else {
                    Armory armory = new Armory();
                    if (player.getGold() >= armory.getCost()) {
                        player.setGold(player.getGold() - armory.getCost());
                        castle.addBuilding(armory);
                        System.out.println("Оружейная построена!");
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                }
                break;
            case 5:
                if (castle.hasBuilding(Arena.class)) {
                    System.out.println("Арена уже построена!");
                } else {
                    Arena arena = new Arena();

                    if (player.getGold() >= arena.getCost()) {
                        player.setGold(player.getGold() - arena.getCost());
                        castle.addBuilding(arena);
                        System.out.println("Арена построена!");
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                }
                break;
            case 6:
                if (castle.hasBuilding(Cathedral.class)) {
                    System.out.println("Собор уже построен!");
                } else {
                    Cathedral cathedral = new Cathedral();
                    if (player.getGold() >= cathedral.getCost()) {
                        player.setGold(player.getGold() - cathedral.getCost());
                        castle.addBuilding(cathedral);
                        System.out.println("Собор построен!");
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                }
                break;
            case 0:
                return;
            default:
                System.out.println("Некорректный выбор.");
        }
    }

    private void hireHero(Castle castle) {
        if (castle.getOwner().getHero() == null) {
            if (!castle.hasBuilding(Tavern.class)) {
                System.out.println("В замке нет таверны!");
                return;
            }
            if (player.getGold() < 700) {
                System.out.println("Недостаточно золота для найма героя!");
                return;
            }
            player.setGold(player.getGold() - 700);
            Hero hero = new Hero("Новый герой", castle.getX() + 1, castle.getY() + 1, player);
            castle.getOwner().setHero(hero);

            player.setHero(hero);
            map.getTiles()[castle.getX() + 1][castle.getY() + 1].setOccupant(hero);
            System.out.println("Герой нанят!");
            console.displayGameMap(map);
        } else {
            System.out.println("Герой уже был нанят!");
        }
    }

    private void hireUnits(Castle castle) {
        if (castle.getOwner().getHero() != null) {
            System.out.println("\nВыберите юнита для найма:");
            System.out.println("1. Копейщик (100 золота)");
            System.out.println("2. Арбалетчик (125 золота)");
            System.out.println("3. Мечник (150 золота)");
            System.out.println("4. Кавалерист (175 золота)");
            System.out.println("5. Паладин (200 золота)");

            System.out.print("Ваш выбор: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    if (castle.hasBuilding(Watchtower.class)) {
                        Spearman spearman = new Spearman(player);
                        player.getHero().addUnit(spearman);
                        if (player.getGold() < spearman.getCost()) {
                            System.out.println("Недостаточно золота!");
                            return;
                        }
                        player.setGold(player.getGold() - spearman.getCost());
                        System.out.println("Юнит нанят!");
                    } else {
                        System.out.println("В замке нет сторожевого поста!");
                    }
                    break;
                case 2:
                    if (castle.hasBuilding(CrossbowTower.class)) {
                        Crossbowman crossbowman = new Crossbowman(player);
                        player.getHero().addUnit(crossbowman);
                        if (player.getGold() < crossbowman.getCost()) {
                            System.out.println("Недостаточно золота!");
                            return;
                        }
                        player.setGold(player.getGold() - crossbowman.getCost());
                        System.out.println("Юнит нанят!");
                    } else {
                        System.out.println("В замке нет башни арбалетчиков!");
                    }
                    break;
                case 3:
                    if (castle.hasBuilding(Armory.class)) {
                        Swordsman swordsman = new Swordsman(player);
                        player.getHero().addUnit(swordsman);
                        if (player.getGold() < swordsman.getCost()) {
                            System.out.println("Недостаточно золота!");
                            return;
                        }
                        player.setGold(player.getGold() - swordsman.getCost());
                        System.out.println("Юнит нанят!");
                    } else {
                        System.out.println("В замке нет оружейной!");
                    }
                    break;
                case 4:
                    if (castle.hasBuilding(Arena.class)) {
                        Cavalry cavalry = new Cavalry(player);
                        player.getHero().addUnit(cavalry);
                        if (player.getGold() < cavalry.getCost()) {
                            System.out.println("Недостаточно золота!");
                            return;
                        }
                        player.setGold(player.getGold() - cavalry.getCost());
                        System.out.println("Юнит нанят!");
                    } else {
                        System.out.println("В замке нет Арены!");
                    }
                    break;
                case 5:
                    if (castle.hasBuilding(Cathedral.class)) {
                        Paladin paladin = new Paladin(player);
                        player.getHero().addUnit(paladin);
                        if (player.getGold() < paladin.getCost()) {
                            System.out.println("Недостаточно золота!");
                            return;
                        }
                        player.setGold(player.getGold() - paladin.getCost());
                        System.out.println("Юнит нанят!");
                    } else {
                        System.out.println("В замке нет Собора!");
                    }
                    break;
            }
        } else {
            System.out.println("Сначала наймите героя!");
        }
    }

    public void checkVictoryConditions() {
        if (isGameOver) return;

        if (isPlayerDefeated(player)) {
            System.out.println("Вы проиграли! У вас нет ни юнитов, ни золота.");
            endGameFor(player);
            return;
        }

        if (isPlayerDefeated(bot)) {
            System.out.println("Вы победили! У противника нет ни юнитов, ни золота.");
            endGameFor(bot);
        }
    }

    private boolean isPlayerDefeated(Player p) {
        Hero hero = p.getHero();
        boolean noHero = hero == null;
        boolean noArmy = hero == null || hero.getArmy() == null || hero.getArmy().isEmpty();
        boolean cannotBuyAnyUnit = !canBuyAnyUnit(p);

        return noHero || (noArmy && cannotBuyAnyUnit);
    }

    private boolean canBuyAnyUnit(Player p) {
        Castle castle = p.getCastle();
        Hero hero = p.getHero();

        if (castle == null || hero == null) {
            return false;
        }

        int minRequiredGold = Integer.MAX_VALUE;

        Spearman spearman = new Spearman(p);
        minRequiredGold = Math.min(minRequiredGold,
                (castle.hasBuilding(Watchtower.class) ? 0 : new Watchtower().getCost()) + spearman.getCost());

        Crossbowman crossbowman = new Crossbowman(p);
        minRequiredGold = Math.min(minRequiredGold,
                (castle.hasBuilding(CrossbowTower.class) ? 0 : new CrossbowTower().getCost()) + crossbowman.getCost());

        Swordsman swordsman = new Swordsman(p);
        minRequiredGold = Math.min(minRequiredGold,
                (castle.hasBuilding(Armory.class) ? 0 : new Armory().getCost()) + swordsman.getCost());

        Cavalry cavalry = new Cavalry(p);
        minRequiredGold = Math.min(minRequiredGold,
                (castle.hasBuilding(Arena.class) ? 0 : new Arena().getCost()) + cavalry.getCost());

        Paladin paladin = new Paladin(p);
        minRequiredGold = Math.min(minRequiredGold,
                (castle.hasBuilding(Cathedral.class) ? 0 : new Cathedral().getCost()) + paladin.getCost());

        return p.getGold() >= minRequiredGold;
    }


    public void endGameFor(Player defeatedPlayer) {
        if (isGameOver) return;

        Game.setGameOver(true);
        isGameOver = true;

        if (defeatedPlayer == player) {
            System.out.println("Вы проиграли! Игра окончена.");
            endGame(false);
        } else {
            boolean botCompletelyDefeated = isPlayerDefeated(bot);
            if (botCompletelyDefeated || bot.getCastle() == null) {
                System.out.println("Поздравляем! Вы победили.");
                endGame(true);
                if (turnCounter < turnsToVictory) {
                    turnsToVictory = turnCounter;
                }
            } else {
                System.out.println("Вы победили в битве! Юниты противника уничтожены.");
            }
        }
    }

    private boolean isAdjacent(Hero hero1, Hero hero2) {
        int dx = Math.abs(hero1.getX() - hero2.getX());
        int dy = Math.abs(hero1.getY() - hero2.getY());
        return dx <= 1 && dy <= 1;
    }

    private boolean isAdjacentEnemyCastle(Hero hero, Hero hero1) {
        int dx = Math.abs(hero.getX() - hero1.getOwner().getCastle().getX());
        int dy = Math.abs(hero.getY() - hero1.getOwner().getCastle().getY());
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
    }


    public void checkHeroEncounter() {
        if (Game.isGameOver()) return;

        if (isPlayerDefeated(player)) {
            endGame(false);
            return;
        }
        if (isPlayerDefeated(bot)) {
            endGame(true);
            return;
        }
        if (player.getCastle() == null && player.getHero() == null) {
            endGame(false); // Игрок проиграл
            return;
        }
        if (bot.getCastle() == null && bot.getHero() == null) {
            endGame(true); // Бот проиграл
            return;
        }

        Hero playerHero = player.getHero();
        Hero botHero = bot.getHero();

        if (playerHero == null || botHero == null) return;

        if (isAdjacentEnemyCastle(playerHero, botHero)) {
            battle.startFinalBattle();
            return;
        }

        if (playerHero.getArmy() == null || playerHero.getArmy().isEmpty()) return;
        if (botHero.getArmy() == null || botHero.getArmy().isEmpty()) return;

        if (isAdjacentEnemyCastle(botHero, playerHero)) {
            battle.startFinalBattle();
            return;
        }

        if (isAdjacent(playerHero, botHero)) {
            battle.startBattle();
        }
    }

    public void checkDiscontent() {
        System.out.println(player.getCastle().getDiscontent());
        if (player.getCastle().getDiscontent() >= 100.0) {
            System.out.println("Недовольство армии достигло 100%, вся ваша армия перешла к сопернику.");
            for (Unit unit : player.getHero().getArmy()) {
                bot.getHero().addUnit(unit);
                player.getHero().getArmy().remove(unit);
            }
        }
    }

    public void endGame(boolean playerWon) {
        if (isGameOver) {
            if (playerWon) {
                Record record = new Record(player.getName());
                long baseStartTime = gameStartTimeMs > 0 ? gameStartTimeMs : startTimeMillis;
                long timeElapsed = Math.max(0, System.currentTimeMillis() - baseStartTime);
                record.setTimeToVictoryMillis(timeElapsed);
                record.setTotalTurns(turnCounter);
                record.setGoldFromKills(goldEarnedFromKills);
                record.setResurrectedUnitsCount(resurrectedUnitsCount);
                record.setBattleVictories(battleVictories);
                RecordManager.updateRecord(record);
                if (simulator != null) {
                    simulator.stopSimulation();
                }
                System.out.println("\n=================================");
                System.out.println("=== ПОЗДРАВЛЯЕМ С ПОБЕДОЙ! ===");
                System.out.println("=================================");
            } else {
                System.out.println("\n=================================");
                System.out.println("=== ИГРА ОКОНЧЕНА. ВЫ ПРОИГРАЛИ ===");
                System.out.println("=================================");
            }
            try {
                scanner.close();  // Закрыть сканер
            } catch (Exception ignored) {}
            if (!testMode) {
                System.exit(0);
            }
        }
    }

    public void saveGame() {
        try {
            File dir = new File("saves");
            if (!dir.exists()) dir.mkdirs();

            Gson gson = JsonUtil.createGameGson();

            GameState state = new GameState();
            state.gameOver = isGameOver;
            state.player = player;
            state.bot = bot;
            List<List<java.util.Map<String, Object>>> simpleMap = new ArrayList<>();
            for (int x = 0; x < Constants.MAP_HEIGHT; x++) {
                List<java.util.Map<String, Object>> row = new ArrayList<>();
                for (int y = 0; y < Constants.MAP_WIDTH; y++) {
                    Tile tile = map.getTiles()[x][y];
                    java.util.Map<String, Object> tileData = new HashMap<>();
                    tileData.put("terrain", tile.getTerrainType().toString());
                    if (tile.getOccupant() != null) {
                        Object occupant = tile.getOccupant();
                        java.util.Map<String, Object> occupantData = new HashMap<>();
                        occupantData.put("type", occupant.getClass().getSimpleName());
                        if (occupant instanceof Hero hero) {
                            occupantData.put("x", hero.getX());
                            occupantData.put("y", hero.getY());
                            occupantData.put("name", hero.getName());
                            occupantData.put("owner", hero.getOwner().getName());
                            occupantData.put("army", hero.getArmy());
                            occupantData.put("deadArmy", hero.getDeadArmy());
                            occupantData.put("reviveArmy", hero.getReviveArmy());
                            occupantData.put("sackedArmy", hero.getSackedArmy());
                        } else if (occupant instanceof Castle castle) {
                            occupantData.put("x", castle.getX());
                            occupantData.put("y", castle.getY());
                            occupantData.put("owner", castle.getOwner().getName());
                            occupantData.put("buildings", castle.getBuildings());
                        }
                        tileData.put("occupant", occupantData);
                    } else {
                        tileData.put("occupant", "None");
                    }

                    row.add(tileData);
                }
                simpleMap.add(row);
            }
            state.map = simpleMap;

            String filename = generateSaveFileName(player.getName());
            try (FileWriter writer = new FileWriter(filename)) {
                gson.toJson(state, writer);
            }
            System.out.println("Игра сохранена в JSON файл.");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    public static Game loadSavedGame(String playerName) {
        File savesDir = new File("saves");

        File[] saves = savesDir.listFiles((dir, name) ->
                name.toLowerCase().startsWith(playerName.toLowerCase() + "_save_") && name.endsWith(".json")
        );

        if (saves == null || saves.length == 0) {
            System.out.println("Сохранения не найдены.");
            return null;
        }

        System.out.println("Доступные сохранения для игрока " + playerName + ":");
        for (int i = 0; i < saves.length; i++) {
            System.out.println((i + 1) + ". " + saves[i].getName());
        }

        System.out.print("Введите номер сохранения: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > saves.length) {
            System.out.println("Неверный выбор.");
            return null;
        }

        File selected = saves[choice - 1];

        try (FileReader reader = new FileReader(selected)) {
            Gson gson = JsonUtil.createGameGson();
            GameState state = gson.fromJson(reader, GameState.class);

            int rows = state.map.size();
            int cols = state.map.get(0).size();
            Tile[][] tiles = new Tile[rows][cols];

            for (int i = 0; i < rows; i++) {
                List<java.util.Map<String, Object>> row = state.map.get(i);
                for (int j = 0; j < cols; j++) {
                    String terrainStr = (String) row.get(j).get("terrain");
                    TerrainType terrain = TerrainType.valueOf(terrainStr);
                    tiles[i][j] = new Tile(i, j, terrain);
                }
            }

            laba_1.MapController.Map gameMap = new laba_1.MapController.Map(cols, rows);
            gameMap.setTiles(tiles);

            Console console = new Console();

            // Создаём Game без вызова init()
            Game game = new Game(gameMap, console, state.player.getName());
            game.player = state.player;
            game.bot = state.bot;

            // Восстанавливаем замки и героев
            for (int i = 0; i < rows; i++) {
                List<java.util.Map<String, Object>> row = state.map.get(i);
                for (int j = 0; j < cols; j++) {
                    Object occupantObj = row.get(j).get("occupant");

                    if (occupantObj instanceof java.util.Map) {
                        java.util.Map<String, Object> occupantData = (java.util.Map<String, Object>) occupantObj;
                        String type = (String) occupantData.get("type");

                        if ("Castle".equals(type)) {
                            String jsonStr = gson.toJson(occupantData);
                            Castle castle = gson.fromJson(jsonStr, Castle.class);
                            if (castle.getBuildings() == null) castle.setBuildings(new ArrayList<>());

                            String ownerName = (String) occupantData.get("owner");
                            Player owner = ownerName.equals(state.player.getName()) ? state.player : state.bot;
                            castle.setOwner(owner);
                            owner.setCastle(castle);
                            tiles[i][j].setOccupant(castle);
                        } else if ("Hero".equals(type)) {
                            String jsonStr = gson.toJson(occupantData);
                            Hero hero = gson.fromJson(jsonStr, Hero.class);
                            String heroOwnerName = (String) occupantData.get("owner");
                            Player owner = heroOwnerName != null && heroOwnerName.equals(state.player.getName()) ? state.player : state.bot;
                            hero.setPlayer(owner);
                            if (hero.getArmy() != null) {
                                for (Unit unit : hero.getArmy()) {
                                    unit.setOwner(owner);
                                }
                            } else {
                                System.out.println("❗ Армия героя пуста! Проверь сериализацию JSON.");
                            }
                            if (hero.getDeadArmy() == null) {
                                hero.setDeadArmy(new ArrayList<>());
                            }
                            if (hero.getReviveArmy() == null) {
                                hero.setReviveArmy(new ArrayList<>());
                            }
                            if (hero.getSackedArmy() == null) {
                                hero.setSackedArmy(new ArrayList<>());
                            }

                            if (hero.getDeadArmy() != null) {
                                for (Unit unit : hero.getDeadArmy()) {
                                    unit.setOwner(owner);
                                }
                            }

                            if (hero.getReviveArmy() != null) {
                                for (Unit unit : hero.getReviveArmy()) {
                                    unit.setOwner(owner);
                                }
                            }

                            if (hero.getSackedArmy() != null) {
                                for (Unit unit : hero.getSackedArmy()) {
                                    unit.setOwner(owner);
                                }
                            }
                            owner.setHero(hero);

                            if (hero.getArmy() != null) for (Unit u : hero.getArmy()) u.setOwner(owner);
                            if (hero.getDeadArmy() != null) for (Unit u : hero.getDeadArmy()) u.setOwner(owner);
                            if (hero.getReviveArmy() != null) for (Unit u : hero.getReviveArmy()) u.setOwner(owner);
                            if (hero.getSackedArmy() != null) for (Unit u : hero.getSackedArmy()) u.setOwner(owner);

                            tiles[hero.getX()][hero.getY()].setOccupant(hero);
                        }
                    }
                }
            }


            // Только после восстановления карты вызываем init
            game.init();

            // Настраиваем контроллеры
            game.playerController = new PlayerController(game.player, gameMap, console, game,new Simulator(game.player,game,gameMap));
            game.botController = new BotController(game.bot, gameMap, console);
            game.battle = new Battle(game.player, game.bot, console, new BattleMap(5, 5), game);

            Game.setGameOver(state.gameOver);

            return game;
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке: " + e.getMessage());
            return null;
        }
    }
        public void addGoldFromKills ( int amount){
            goldEarnedFromKills += amount;
        }
        public void incrementBattleVictories () {
            this.battleVictories++;
        }
        public void incrementResurrectedUnits () {
            this.resurrectedUnitsCount++;
        }

    public Battle getBattle() {
        return battle;
    }

    public long getGameStartTimeMs() {
        return gameStartTimeMs;
    }
}

