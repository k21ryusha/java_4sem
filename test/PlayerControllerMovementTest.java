package test;

import laba_1.MapController.Map;
import laba_1.game.Game;
import laba_1.game.PlayerController;
import laba_1.laba_4_buildings.Simulator;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.TerrainType;
import laba_1.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;


public class PlayerControllerMovementTest {
        private Player player;
        private Map map;
        private PlayerController controller;
        private Game game;

        @BeforeEach
        void setUp() {
            player = new Player("TestPlayer", 1000);
            map = new Map(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
            controller = new PlayerController(player, map, null,game,new Simulator(player,game,map));

            Hero hero = new Hero("TestHero", 5, 5, player);
            player.setHero(hero);
            map.getTiles()[5][5].setOccupant(hero);
        }

        private Scanner createTestScanner(String input) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        return new Scanner(inputStream);
        }

        @Test
        void moveHero_toRoad_deductsRoadPenalty() {
            // Подготовка дороги
            map.getTiles()[5][6].setTerrainType(TerrainType.ROAD);

            int initialGold = player.getGold();
            Scanner testScanner = createTestScanner("2\n");
            controller.moveHero(testScanner);

            assertEquals(initialGold - Constants.ROAD_PENALTY, player.getGold());
        }

        @Test
        void moveHero_toBotZone_deductsOpponentPenalty() {
            map.getTiles()[4][5].setTerrainType(TerrainType.BOT_ZONE);

            int initialGold = player.getGold();
            Scanner testScanner = createTestScanner("3\n");
            controller.moveHero(testScanner);

            assertEquals(initialGold - Constants.OPPONENTS_ZONE_PENALTY, player.getGold());
        }

        @Test
        void moveHero_toLake_doesNotMove() {
            map.getTiles()[5][6].setTerrainType(TerrainType.LAKE);

            int initialGold = player.getGold();
            Scanner testScanner = createTestScanner("2\n");
            controller.moveHero(testScanner);

            assertEquals(initialGold, player.getGold());
            assertEquals(5, player.getHero().getX());
            assertEquals(5, player.getHero().getY());
        }


    @Test
    void moveHero_outOfBounds_doesNotMove() {
        player.getHero().setX(0);
        player.getHero().setY(0);
        map.getTiles()[0][0].setOccupant(player.getHero());

        int initialGold = player.getGold();
        Scanner testScanner = createTestScanner("3\n");
        controller.moveHero(testScanner);

        assertEquals(initialGold, player.getGold());
        assertEquals(0, player.getHero().getX());
    }

    @Test
    void moveHero_toOccupiedTile_doesNotMove() {
        map.getTiles()[5][6].setOccupant(player.getHero());

        int initialGold = player.getGold();
        Scanner testScanner = createTestScanner("2\n");
        controller.moveHero(testScanner);

        assertEquals(initialGold, player.getGold());
        assertEquals(5, player.getHero().getX());
    }
    }

