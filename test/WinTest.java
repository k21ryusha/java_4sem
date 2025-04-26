package test;

import laba_1.MapController.Map;
import laba_1.battle.Battle;
import laba_1.game.Game;
import laba_1.model.Buildings.Castle;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.TerrainType;
import laba_1.model.Tile;
import laba_1.model.Units.Paladin;
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
        // Подготовка карты и замков
        testMap = new Map(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        dummyConsole = new Console();

        Player player = new Player("Игрок",1000);
        Player bot = new Player("Игрок",1000);

        game = new Game(testMap, dummyConsole);

        Castle playerCastle = new Castle(player, 0,0);
        Castle botCastle = new Castle(bot,Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1);

        player.setCastle(playerCastle);
        bot.setCastle(botCastle);

        testMap.getTiles()[0][0] = new Tile(0, 0, TerrainType.PLAYER_CASTLE);
        testMap.getTiles()[0][0].setOccupant(playerCastle);

        testMap.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1] =
                new Tile(Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1, TerrainType.BOT_CASTLE);
        testMap.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1].setOccupant(botCastle);

        game.getPlayer().setHero(new Hero("PlayerHero", 0, 0, game.getPlayer()));
        game.getBot().setHero(new Hero("BotHero", Constants.MAP_WIDTH-1, Constants.MAP_HEIGHT-1, game.getBot()));


        battle = new Battle(player,bot,dummyConsole,testMap,game);
    }
    @BeforeEach
    void resetGameState() {
        Game.setGameOver(false);
    }
    @Test
    void testPlayerVictoryByEndGameFor() {
        Player bot = game.getBot();
        bot.setCastle(null);
        game.endGameFor(bot);

        assertTrue(Game.isGameOver());
    }
    @Test
    void testPlayerDefeatDueToGoldAndNoHero() {
        Player player = game.getPlayer();
        player.setGold(0);
        player.setHero(null);

        game.checkVictoryConditions();

        assertTrue(Game.isGameOver());
    }
    @Test
    void testPlayerDefeatDueToGoldAndEmptyArmy() {
        Player player = game.getPlayer();

        Hero hero = new Hero("Test", 1, 1, player);
        player.setHero(hero);

        hero.getArmy().clear();

        player.setGold(0);
        assertTrue(hero.getArmy() != null, "Армия не пуста");
        assertTrue(player.getGold() < 100, "Золото достаточно для покупки");

        game.checkVictoryConditions();

        assertTrue(Game.isGameOver(), "Игра не завершена, хотя должна быть");
    }
    @Test
    void testBotDefeatByRemovingHeroAndCastle() {
        Player bot = game.getPlayer();
        bot.setHero(null);
        bot.setCastle(null);

        game.checkHeroEncounter();

        assertFalse(Game.isGameOver());
    }
    @Test
    void testFinalBattlePlayerVictory() {
        Player player = game.getPlayer();
        Player bot = game.getBot();

        Hero playerHero = player.getHero();
        Hero botHero = bot.getHero();


        playerHero.addUnit(new Paladin(player));
        botHero.getArmy().clear();

        playerHero.setX(Constants.MAP_WIDTH-1);
        playerHero.setY(Constants.MAP_HEIGHT-2);
        testMap.getTiles()[Constants.MAP_WIDTH-1][Constants.MAP_HEIGHT-2].setOccupant(playerHero);

        game.checkHeroEncounter();

        assertTrue(Game.isGameOver(), "Игра должна завершиться после финальной битвы");
        assertNotNull(player.getCastle(), "Замок игрока должен остаться");
        assertNull(bot.getCastle(), "Замок бота должен быть разрушен");
    }
    @Test
    void testFinalBattleBotVictory() {
        Player player = game.getPlayer();
        Player bot = game.getBot();

        Hero playerHero = player.getHero();
        Hero botHero = bot.getHero();


        botHero.addUnit(new Paladin(bot));
        playerHero.getArmy().clear();

        botHero.setX(1);
        botHero.setY(0);
        testMap.getTiles()[1][0].setOccupant(botHero);

        game.checkHeroEncounter();

        assertTrue(Game.isGameOver(), "Игра должна завершиться после финальной битвы");
        assertNotNull(bot.getCastle(), "Замок игрока должен остаться");
        assertNull(player.getCastle(), "Замок бота должен быть разрушен");
    }
}