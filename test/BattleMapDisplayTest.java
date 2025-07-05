package test;

import laba_1.MapController.BattleMap;
import laba_1.model.*;
import laba_1.model.units.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class BattleMapDisplayTest {
        private BattleMap battleMap;
        private Player player;
        private Player bot;
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private final PrintStream originalOut = System.out;

        @BeforeEach
        void setUp() {

            battleMap = new BattleMap(8, 8);
            player = new Player("Player", 1000);
            bot = new Player("Bot", 1000);

            Hero playerHero = new Hero("PlayerHero", 0, 0, player);
            Hero botHero = new Hero("BotHero", 0, 0, bot);
            player.setHero(playerHero);
            bot.setHero(botHero);

            System.setOut(new PrintStream(outputStream));
        }

        @Test
        void displayBattleMap_emptyMap_shouldShowDots() {
            battleMap.displayBattleMap(bot, player);
            String output = outputStream.toString();

            long dotCount = output.chars().filter(ch -> ch == '・').count();
            assertEquals((long) battleMap.getWidth() * battleMap.getHeight(), dotCount,
                    "Пустая карта должна содержать только точки");
        }

        @Test
        void displayBattleMap_withPlayerUnits_shouldShowCorrectSymbols() {
            player.getHero().addUnit(new Spearman(player));
            player.getHero().addUnit(new Crossbowman(player));
            battleMap.initializeBattleMap(player, bot);

            battleMap.displayBattleMap(bot, player);
            String output = outputStream.toString();

            assertAll(
                    () -> assertTrue(output.contains("⚔️"), "Должен отображаться мечник"),
                    () -> assertTrue(output.contains("\uD83C\uDFF9"), "Должен отображаться лучник"),
                    () -> assertFalse(output.contains("!"), "Не должно быть вражеских меток")
            );
        }

        @Test
        void displayBattleMap_withBotUnits_shouldShowEnemyMarkers() {
            bot.getHero().addUnit(new Swordsman(bot));
            bot.getHero().addUnit(new Cavalry(bot));
            battleMap.initializeBattleMap(player, bot);

            battleMap.displayBattleMap(bot, player);
            String output = outputStream.toString();

            assertAll(
                    () -> assertTrue(output.contains("\uD83D\uDDE1!"), "Должен отображаться мечник врага"),
                    () -> assertTrue(output.contains("\uD83D\uDC0E!"), "Должен отображаться кавалерист врага")
            );
        }

        @Test
        void КdisplayBattleMap_mixedUnits_shouldCorrectlyDisplay() {
            player.getHero().addUnit(new Paladin(player));
            bot.getHero().addUnit(new Crossbowman(bot));
            battleMap.initializeBattleMap(player, bot);

            battleMap.displayBattleMap(bot, player);
            String output = outputStream.toString();

            assertAll(
                    () -> assertTrue(output.contains("\uD83E\uDD34"), "Должен отображаться паладин"),
                    () -> assertTrue(output.contains("\uD83C\uDFF9!"), "Должен отображаться вражеский лучник")
            );
        }

        @AfterEach
        void tearDown() {
            System.setOut(originalOut);
        }
}