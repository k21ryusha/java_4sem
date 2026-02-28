package laba_1.MapController;

import laba_1.buildings.TimeManager;
import laba_1.model.ObstacleType;
import laba_1.model.TerrainType;
import laba_1.model.Tile;

import java.io.Serializable;
import java.util.Random;

public class Map implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int x;
    private final int y;
    private Tile[][] tiles;
    private String name;
    private final int INITIAL_GAME_HOUR = 8;

    public Map(int x, int y) {
        this.x = x;
        this.y = y;
        tiles = new Tile[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                tiles[i][j] = new Tile(i, j, TerrainType.NEUTRAL); // Теперь все ячейки заполнены
            }
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void randomizeCustomObstacles(double changeChance,Tile[][] tiles, long gameStartTimeMs) {
        long nowMs = TimeManager.getCurrentGameTimeMillis();
        long elapsedMs = nowMs - gameStartTimeMs;
        long elapsedMin = elapsedMs / TimeManager.MILLIS_PER_GAME_MINUTE;
        long displayMin = elapsedMin + INITIAL_GAME_HOUR * 60;
        int gameHour = (int) ((displayMin / 60) % 24);

        if (gameHour < 9 || gameHour > 12) return;

        Random rnd = new Random();
        ObstacleType[] types = new ObstacleType[]{
                ObstacleType.NO_PENALTY,
                ObstacleType.WITH_PENALTY,
                ObstacleType.IMPASSABLE,
                ObstacleType.PENALTY_BLOCK
        };

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                Tile tile = tiles[x][y];
                String sym = tile.getObstacleSymbol();
                if (sym != null && !sym.isEmpty()) {
                    if (rnd.nextDouble() < changeChance) {
                        ObstacleType current = tile.getObstacle();
                        ObstacleType next;
                        do {
                            next = types[rnd.nextInt(types.length)];
                        } while (next == current);
                        tile.setObstacle(next);
                    }
                }
            }
        }
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

