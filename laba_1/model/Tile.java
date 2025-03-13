package laba_1.model;

public class Tile {
    private int x;
    private int y;
    private TerrainType terrainType;
    private Object occupant; // Может быть Unit, Castle и т.д.

    public Tile(int x, int y, TerrainType terrainType) {
        this.x = x;
        this.y = y;
        this.terrainType = terrainType;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public TerrainType getTerrainType() {
        return terrainType;
    }
    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }
    public Object getOccupant() {
        return occupant;
    }
    public void setOccupant(Object occupant) {
        this.occupant = occupant;
    }
}
