package test;

import laba_1.MapController.BattleMap;
import laba_1.MapController.Map;
import laba_1.battle.Battle;
import laba_1.game.Game;
import laba_1.model.buildings.Castle;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.TerrainType;
import laba_1.model.Tile;
import laba_1.model.units.Paladin;
import laba_1.util.Constants;
import laba_1.view.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WinTest {
    private Game game;
    private Map testMap;
    private Console dummyConsole;
    private Battle battle;

    @BeforeEach
    void setUp() {
        Game.setGameOver(false);

        testMap = new Map(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        dummyConsole = new Console();

        Player player = new Player("Игрок", 1000);
        Player bot = new Player("Бот", 1000);

        testMap.getTiles()[0][0] = new Tile(0, 0, TerrainType.PLAYER_CASTLE);
        Castle playerCastle = new Castle(player, 0, 0);
        testMap.getTiles()[0][0].setOccupant(playerCastle);

        testMap.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1] =
                new Tile(Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1, TerrainType.BOT_CASTLE);
        Castle botCastle = new Castle(bot, Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1);
        testMap.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1].setOccupant(botCastle);

        game = new Game(testMap, dummyConsole, player.getName());
        game.setTestMode(true);

        game.getPlayer().setCastle(playerCastle);
        game.getBot().setCastle(botCastle);

        game.getPlayer().setHero(new Hero("PlayerHero", 0, 0, game.getPlayer()));
        game.getBot().setHero(new Hero("BotHero", Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1, game.getBot()));

        battle = new Battle(game.getPlayer(), game.getBot(), dummyConsole, new BattleMap(8, 8), game);
    }

    @Test
    void testPlayerVictoryByEndGameFor() {
        Player bot = game.getBot();
        bot.setCastle(null);
        game.endGameFor(bot);

        assertTrue(Game.isGameOver(), "Игра должна завершиться победой игрока");
    }

    @Test
    void testPlayerDefeatDueToGoldAndNoHero() {
        Player player = game.getPlayer();
        player.setGold(0);
        player.setHero(null);

        game.checkVictoryConditions();

        assertTrue(Game.isGameOver(), "Игра должна завершиться поражением игрока");
    }

    @Test
    void testBotDefeatWhenGoldExistsButNoPurchasableUnits() {
        Player bot = game.getBot();

        Hero botHero = new Hero("BotHero", Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1, bot);
        bot.setHero(botHero);
        botHero.getArmy().clear();

        Castle botCastle = bot.getCastle();
        botCastle.getBuildings().clear();
        botCastle.addBuilding(new laba_1.model.buildings.Cathedral());

        bot.setGold(175);

        game.checkVictoryConditions();

        assertTrue(Game.isGameOver(), "Игра должна завершиться, если бот не может купить ни одного юнита");
    }


    @Test
    void testPlayerDefeatDueToGoldAndEmptyArmy() {
        Player player = game.getPlayer();
        Hero hero = new Hero("Test", 1, 1, player);
        player.setHero(hero);
        player.setGold(0);

        // Убедимся, что армия пуста
        assertTrue(hero.getArmy().isEmpty(), "Армия должна быть пуста");
        assertTrue(player.getGold() < 100, "Золото недостаточно для продолжения");

        game.checkVictoryConditions();

        assertTrue(Game.isGameOver(), "Игра должна завершиться поражением игрока");
    }

    @Test
    void testBotDefeatByRemovingHeroAndCastle() {
        Player bot = game.getBot();  // Исправлено
        bot.setHero(null);
        bot.setCastle(null);

        game.checkVictoryConditions();  // Исправлено (нужно проверять именно условия победы)

        assertTrue(Game.isGameOver(), "Игра должна завершиться победой игрока");
    }

    @Test
    void testFinalBattlePlayerVictory() {
        Player player = game.getPlayer();
        Player bot = game.getBot();

        Hero playerHero = player.getHero();
        Hero botHero = bot.getHero();

        playerHero.addUnit(new Paladin(player));
        botHero.getArmy().clear();

        playerHero.setX(Constants.MAP_WIDTH - 1);
        playerHero.setY(Constants.MAP_HEIGHT - 2);
        testMap.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 2].setOccupant(playerHero);

        game.checkHeroEncounter();

        assertTrue(Game.isGameOver(), "Игра должна завершиться после финальной битвы");
    }

    @Test
    void testFinalBattleBotVictory() {
        Player player = game.getPlayer();
        Player bot = game.getBot();

        Hero playerHero = player.getHero();
        Hero botHero = bot.getHero();

        playerHero.getArmy().clear();
        botHero.addUnit(new Paladin(bot));

        botHero.setX(1);
        botHero.setY(0);
        game.getMap().getTiles()[1][0].setOccupant(botHero);

        game.getBattle().setFinalBattle(true);
        game.getBattle().startFinalBattle();

        assertTrue(Game.isGameOver(), "Игра должна завершиться после финальной битвы");
    }
}