package test;

import laba_1.battle.Battle;
import laba_1.MapController.BattleMap;
import laba_1.MapController.Map;
import laba_1.model.*;
import laba_1.model.buildings.Castle;
import laba_1.model.units.*;
import laba_1.util.Constants;
import laba_1.view.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import laba_1.game.Game;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class BattleTest {
    private Battle battle;
    private Player player;
    private Player bot;
    private Console console;
    private BattleMap bmap;
    private Map map;
    private Game game;

    @BeforeEach
    void setUp() {
        map = new Map(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        console = new Console();

        player = new Player("Player", 1000);
        bot = new Player("Bot", 1000);

        Castle playerCastle = new Castle(player, 0, 0);
        Castle botCastle = new Castle(bot, Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1);
        map.getTiles()[0][0].setOccupant(playerCastle);
        map.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1].setOccupant(botCastle);
        player.setCastle(playerCastle);
        bot.setCastle(botCastle);

        Hero playerHero = new Hero("PlayerHero", 1, 0, player);
        Hero botHero = new Hero("BotHero", Constants.MAP_WIDTH - 2, Constants.MAP_HEIGHT - 1, bot);
        map.getTiles()[1][0].setOccupant(playerHero);
        map.getTiles()[Constants.MAP_WIDTH - 2][Constants.MAP_HEIGHT - 1].setOccupant(botHero);
        player.setHero(playerHero);
        bot.setHero(botHero);

        // Игра
        game = new Game(map, console, player.getName());
        game.setTestMode(true); // Тестовый режим без System.exit

        // Battle
        bmap = new BattleMap(10, 10);
        battle = new Battle(player, bot, console, bmap, game);
    }

    @Test
    void isInRange_unitInRange_returnsTrue() {
        Unit attacker = new Spearman(player);
        attacker.setX(2);
        attacker.setY(2);
        attacker.setMovement(3);

        Unit target = new Spearman(bot);
        target.setX(4);
        target.setY(3);

        assertTrue(battle.isInRange(attacker, target));
    }

    @Test
    void isInRange_unitOutOfRange_returnsFalse() {
        Unit attacker = new Crossbowman(player);
        attacker.setX(0);
        attacker.setY(0);
        attacker.setMovement(2);

        Unit target = new Swordsman(bot);
        target.setX(5);
        target.setY(5);

        assertFalse(battle.isInRange(attacker, target));
    }

    @Test
    void attackUnit_validAttack_reducesTargetHp() {
        Unit attacker = new Paladin(player);
        attacker.setDamage(30);
        attacker.setHp(100);

        Unit defender = new Cavalry(bot);
        defender.setHp(50);
        defender.setReward(20);

        BattleTile defenderTile = new BattleTile(0, 0);
        defenderTile.setOccupant(defender);

        int initialHp = defender.getHp();

        battle.attackUnit(attacker, defender, defenderTile);

        assertEquals(initialHp - attacker.getDamage(), defender.getHp());
    }

    @Test
    void attackUnit_killTarget_removesFromTileAndAddsReward() {
        Unit attacker = new Swordsman(player);
        attacker.setDamage(50);
        attacker.setHp(100);

        Unit defender = new Spearman(bot);
        defender.setHp(40);
        defender.setReward(25);

        BattleTile defenderTile = new BattleTile(0, 0);
        defenderTile.setOccupant(defender);

        int initialGold = attacker.getOwner().getGold();

        battle.attackUnit(attacker, defender, defenderTile);

        assertNull(defenderTile.getOccupant());
        assertEquals(initialGold + defender.getReward(), attacker.getOwner().getGold());
        assertTrue(defender.getOwner().getHero().getDeadArmy().contains(defender));
    }

    @Test
    void playerTurn_attackInRange_performsAttack() {
        BattleMap testMap = new BattleMap(8, 8);
        Unit playerUnit = new Swordsman(player);
        playerUnit.setX(2);
        playerUnit.setY(2);
        playerUnit.setMovement(2);
        player.getHero().addUnit(playerUnit);

        Unit botUnit = new Spearman(bot);
        botUnit.setX(3);
        botUnit.setY(2);
        botUnit.setHp(100);
        bot.getHero().addUnit(botUnit);

        testMap.getTiles()[2][2].setOccupant(playerUnit);
        testMap.getTiles()[3][2].setOccupant(botUnit);

        battle.bmap = testMap;

        String simulatedInput = "2 2\n4\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);

        Scanner originalScanner = battle.getScanner();
        battle.setScanner(testScanner);

        int initialHp = botUnit.getHp();
        battle.playerTurn();
        assertTrue(botUnit.getHp() < initialHp, "HP вражеского юнита должно уменьшиться после атаки");

        battle.setScanner(originalScanner);
    }

    @Test
    void isBattleFinished_noPlayerUnits_returnsTrue() {
        player.getHero().getArmy().clear();
        bot.getHero().addUnit(new Spearman(bot));

        assertTrue(battle.isBattleFinished());
    }

    @Test
    void attackUnit_unitDies_shouldRemoveFromTileAndAddToDeadArmy() {
        Unit attacker = new Swordsman(player);
        attacker.setDamage(50);
        attacker.setHp(100);

        Unit defender = new Spearman(bot);
        defender.setHp(40);
        defender.setReward(25);

        BattleTile defenderTile = new BattleTile(0, 0);
        defenderTile.setOccupant(defender);

        int initialGold = attacker.getOwner().getGold();

        battle.attackUnit(attacker, defender, defenderTile);

        assertAll(
                () -> assertNull(defenderTile.getOccupant(), "Юнит должен быть удален с клетки"),
                () -> assertTrue(bot.getHero().getDeadArmy().contains(defender),
                        "Юнит должен быть добавлен в список погибших"),
                () -> assertEquals(initialGold + defender.getReward(), player.getGold(),
                        "Атакующий должен получить награду")
        );
    }
}