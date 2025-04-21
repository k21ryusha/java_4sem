package laba_1.MapController;

import laba_1.model.TerrainType;
import laba_1.model.Tile;

public class Map {
    private int x;
    private int y;
    private Tile[][] tiles;

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

}

