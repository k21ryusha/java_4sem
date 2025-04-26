package test;

import laba_1.MapController.Map;
import laba_1.game.BotController;
import laba_1.model.*;
import laba_1.model.Buildings.*;
import laba_1.util.Constants;
import laba_1.view.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import static org.junit.jupiter.api.Assertions.*;

class BotControllerTest {
    private Player bot;
    private Player player;
    private Map map;
    private Console console;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private BotController botController;

    @BeforeEach
    void setUp() {
        bot = new Player("Bot", 2000);
        player = new Player("Player", 2000);
        map = new Map(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        console = new Console();

        Castle castle = new Castle(bot, 5, 5);
        bot.setCastle(castle);
        map.getTiles()[5][5].setOccupant(castle);

        System.setOut(new PrintStream(outputStream));
        botController = new BotController(bot, map, console);
    }

    @Test
    void performTurn_shouldPurchaseHeroWhenPossible() {
        bot.setGold(700);
        bot.getCastle().addBuilding(new Tavern());

        botController.performTurn(player);

        // Проверки
        assertNotNull(bot.getHero());
        assertEquals(0, bot.getGold());
        assertTrue(outputStream.toString().contains("Bot нанял героя"));
    }

    @Test
    void purchaseRandomUnit_shouldBuildRequiredBuilding() {
        bot.setGold(500);
        bot.setHero(new Hero("BotHero", 4, 4, bot));

        botController.purchaseRandomUnit();

        boolean hasAnyBuilding = bot.getCastle().getBuildings().stream()
                .anyMatch(b -> b instanceof Watchtower
                        || b instanceof CrossbowTower
                        || b instanceof Armory
                        || b instanceof Arena
                        || b instanceof Cathedral);

        assertTrue(hasAnyBuilding);
        assertTrue(bot.getHero().getArmy().size() > 0 ||
                outputStream.toString().contains("не имеет достаточного золота"));
    }

    @Test
    void moveUnitsTowards_shouldMoveCorrectly() {
        Hero botHero = new Hero("BotHero", 5, 5, bot);
        bot.setHero(botHero);
        map.getTiles()[5][5].setOccupant(botHero);

        Castle playerCastle = new Castle(player, 7, 7);
        player.setCastle(playerCastle);
        map.getTiles()[7][7].setOccupant(playerCastle);

        botController.moveUnitsTowards(player);

        assertEquals(6, botHero.getX());
        assertEquals(6, botHero.getY());
        assertSame(botHero, map.getTiles()[6][6].getOccupant());
    }

    @Test
    void purchaseHero_shouldPlaceCorrectly() {
        bot.setGold(700);

        botController.purchaseHero();

        // Проверки
        Hero hero = bot.getHero();
        assertNotNull(hero);
        assertEquals(4, hero.getX());
        assertEquals(4, hero.getY());
        assertSame(hero, map.getTiles()[4][4].getOccupant());
    }
}
