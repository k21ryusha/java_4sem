package test;

import laba_1.battle.Battle;
import laba_1.MapController.BattleMap;
import laba_1.MapController.Map;
import laba_1.model.*;
import laba_1.model.Buildings.Castle;
import laba_1.model.Units.*;
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
    private Map map;
    private Game game;
    private Castle castle;
    private Tile tile;

    @BeforeEach
    void setUp() {
        player = new Player("Player", 1000);
        bot = new Player("Bot", 1000);
        console = new Console();
        map = new Map(10, 10);
        game = new Game(map, console);
        BattleMap bmap = new BattleMap(8, 8);

        battle = new Battle(player, bot, console, map, game);
        Castle playerCastle = new Castle(player, 0,0);
        Castle botCastle = new Castle(bot, Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1);

        player.setCastle(playerCastle);
        bot.setCastle(botCastle);
        Hero playerHero = new Hero("PlayerHero", 0, 0, player);
        Hero botHero = new Hero("BotHero", 0, 0, bot);
        player.setHero(playerHero);
        bot.setHero(botHero);
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

        Tile defenderTile = new Tile(0, 0, null);
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

        Tile defenderTile = new Tile(0, 0, null);
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
        battle = new Battle(player, bot, console, map, game);

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

        assertTrue(botUnit.getHp() < initialHp,
                "HP вражеского юнита должно уменьшиться после атаки");

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

        Tile defenderTile = new Tile(0, 0, null);
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

