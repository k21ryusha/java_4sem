package laba_1.model;
import java.util.Random;

public class Map {
    private int x;
    private int y;
    private static Tile [][] tiles;

    public Map(int x, int y) {
        this.x = x;
        this.y = y;
        this.tiles = new Tile[x][y];
    }
    public static Tile[][] getTiles() {
        return tiles;
    }
    public void setTiles(Tile[][] tile) {
        this.tiles = tile;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
}

