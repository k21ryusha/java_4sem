package laba_1.laba_4_buildings;

import laba_1.MapController.Map;
import laba_1.editor.MapEditor;
import laba_1.game.Game;
import laba_1.model.Player;
import laba_1.model.Tile;
import laba_1.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Simulator {
    private final Player player;
    private final Map map;
    private final Game game;
    List<Resort> resorts = new ArrayList<>();

    private final List<Visitor> visitors = new ArrayList<>();
    private final Thread npcThread;
    private final NpcSimulator npcSimulator;



    public Simulator(Player player, Game game, Map map) {
        this.map = map;
        this.player = player;
        this.game = game;

        visitors.add(new Visitor("Игрок", true, player));
        for (int i = 1; i <= 10; i++) {
            visitors.add(new Visitor("NPC-" + i, false, player));
        }
        for (int x = 0; x < Constants.MAP_WIDTH; x++) {
            for (int y = 0; y < Constants.MAP_HEIGHT; y++) {
                Tile tile = map.getTiles()[x][y];
                if (tile.getOccupant() instanceof Resort resort) {
                    resorts.add(resort);
                }
            }
        }

        npcSimulator = new NpcSimulator(visitors, resorts);
        npcThread = new Thread(npcSimulator);
        npcThread.setDaemon(true);
        npcThread.start();
    }

    public void startSimulation() {
        final double changeChance = 0.25;
        long startMs = game.getGameStartTimeMs();
        while (!Game.isGameOver()) {
            try {
                Thread.sleep(1000);

                long currentTime = TimeManager.getCurrentGameTimeMillis();
                for (Resort resort : resorts) {
                    resort.update(currentTime);
                }
                map.randomizeCustomObstacles(changeChance, map.getTiles(), startMs);
                MapEditor.saveVisualMap(map.getName(),map);
                MapEditor.saveMapToFile(map.getName(),map);
                MapEditor.saveMapReportAsJson(map.getName(),map);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopSimulation() {
        npcSimulator.stop();
        try {
            npcThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}