package laba_1.game;

import laba_1.MapController.BattleMap;
import laba_1.MapController.Map;
import laba_1.battle.Battle;
import laba_1.model.Buildings.*;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.Tile;
import laba_1.model.Units.*;
import laba_1.util.Constants;
import laba_1.view.Console;

import java.util.Scanner;


public class Game {
    private final Scanner scanner = new Scanner(System.in);
    private final Console console;
    private final Map map;
    private static boolean isGameOver = false;
    private Player player;
    private Player bot;
    private PlayerController playerController;
    private BotController botController;
    private Battle battle;

    public Game(Map map, Console console) {
        this.console = console;
        this.map = map;
        console.initializeMap(map);
        init();
    }

    public void init() {
        player = new Player("Player", 2000);
        bot = new Player("Bot", 2000);
        Castle playerCastle = (Castle) map.getTiles()[0][0].getOccupant();
        Castle botCastle = (Castle) map.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1].getOccupant();
        playerCastle.setOwner(player);
        botCastle.setOwner(bot);
        player.setCastle(playerCastle);
        bot.setCastle(botCastle);
        playerController = new PlayerController(player, map, console);
        botController = new BotController(bot, map, console);
        this.battle = new Battle(player, bot, console, map, this);
    }

    public void startGame() {
        while (!isGameOver) {
            boolean turnEnded = false;
            while (!turnEnded && !isGameOver) {
                console.displayGameMap(map);
                System.out.println("\n=== Главное меню ===");
                System.out.println("1. Войти в замок (игрок)");
                System.out.println("2. Совершить ход (переместить героя)");
                System.out.println("3. Завершить ход");
                System.out.println("0. Выход из игры");
                System.out.println("Ваш баланс: " + player.getGold() + " золота.");
                System.out.println("Баланс бота: " + bot.getGold() + " золота.");
                System.out.println("Недовольство вашей армии: " + player.getCastle().getDiscontent() + "%");
                System.out.print("Выберите действие: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // очистка буфера ввода
                switch (choice) {
                    case 1:
                        enterCastle();
                        break;
                    case 2:
                        playerController.moveHero();
                        break;
                    case 3:
                        turnEnded = true;
                        break;
                    case 0:
                        isGameOver = true;
                        System.out.println("Выход из игры...");
                        return;
                    default:
                        System.out.println("Некорректный ввод. Попробуйте снова.");
                        break;
                }
            }
            endTurn();
            botController.performTurn(player);
            checkHeroEncounter();
            checkVictoryConditions();
            checkDiscontent();
        }
        scanner.close();
    }

    public Player getPlayer() {
        return player;
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
        if (player.getHero().getArmy() != null  && !player.getHero().getArmy().isEmpty()) {
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
                if(!player.getHero().getReviveArmy().contains(unit)) {
                    player.getHero().getArmy().remove(choice - 1);
                    player.getHero().getSackedArmy().add(unit);
                    System.out.print("Юнит " + unit.getName() + " был уволен.");
                    player.getCastle().setDiscontent(player.getCastle().getDiscontent()+ ((float) 1 /i)*100);
                    System.out.print("Недовольство армии увеличилось на " + player.getCastle().getDiscontent()+ "%");
                }
                else{
                    System.out.println("Этого юнита нельзя уволить! Он находится в списке воскресших.  ");
                }
            } else {
                System.out.println("Неверный выбор!");
            }
        }else{
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
            Hero hero = new Hero("Новый герой", castle.getX() + 1, castle.getY() + 1,player);
            castle.getOwner().setHero(hero);

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

    private void checkVictoryConditions() {
        if (isPlayerDefeated(player)) {
            System.out.println("Вы проиграли! У вас нет ни юнитов, ни золота.");
            isGameOver = true;
            scanner.close();
        }
    }

    private boolean isPlayerDefeated(Player p) {
        return (p.getHero().getArmy() == null && p.getGold() < 100) || (p.getHero() == null && p.getGold() < 700);
    }


    public void endGameFor(Player defeatedPlayer) {
        isGameOver = true;
        if (defeatedPlayer == player) {
            System.out.println("Вы проиграли! Игра окончена.");
            endGame(false);
        } else {
            System.out.println("Поздравляем! Вы победили.");
            endGame(true);
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


    private void checkHeroEncounter() {
        Hero playerHero = player.getHero();
        Hero botHero = bot.getHero();

        if (player.getCastle() == null || bot.getCastle() == null) {
            System.out.println("Один из замков разрушен - битва не нужна!");
            endGame(player.getCastle() != null);
            return;
        }
        if (playerHero != null && botHero != null) {
            Castle botCastle = bot.getCastle();
            if (botCastle != null && isAdjacentEnemyCastle(playerHero, botHero)) {
                System.out.println("Герой противника защищает свой замок!");
                battle.startFinalBattle();
            }
            if (player.getCastle() != null && isAdjacentEnemyCastle(botHero, playerHero)) {
                System.out.println("Вам предстоит защитить свой замок!");
                battle.startFinalBattle();
            }
            if (isAdjacent(playerHero, botHero)) {
                    Battle battle = new Battle(player, bot, console, map, this);
                    battle.startBattle();
            }
        }
    }
    public void checkDiscontent() {
        System.out.println(player.getCastle().getDiscontent());
        if(player.getCastle().getDiscontent() >= 100.0){
            System.out.println("Недовольство армии достигло 100%, вся ваша армия перешла к сопернику.");
            for(Unit unit : player.getHero().getArmy()){
                bot.getHero().addUnit(unit);
                player.getHero().getArmy().remove(unit);
            }
        }
    }

        public void endGame(boolean playerWon) {
            isGameOver = true;
            if (playerWon) {
                System.out.println("\n=================================");
                System.out.println("=== ПОЗДРАВЛЯЕМ С ПОБЕДОЙ! ===");
                System.out.println("=================================");
            } else {
                System.out.println("\n=================================");
                System.out.println("=== ИГРА ОКОНЧЕНА. ВЫ ПРОИГРАЛИ ===");
                System.out.println("=================================");
            }
            scanner.close();
            System.exit(0);
        }
    }