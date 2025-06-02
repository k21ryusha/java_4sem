package laba_1.model;

public class Tile {
    public int x;
    public int y;
    public TerrainType terrainType;
    private Occupant occupant;
    private ObstacleType obstacle = ObstacleType.NONE;
    private String obstacleSymbol = "";


    public Tile(){}

    public Tile(int x, int y, TerrainType terrainType) {
        this.x = x;
        this.y = y;
        this.terrainType = terrainType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
        this.occupant = (Occupant) occupant;
    }

    public ObstacleType getObstacle() {
        return obstacle;
    }

    public void setObstacle(ObstacleType obstacle) {
        this.obstacle = obstacle;
        if (obstacle == ObstacleType.NONE) {
            this.obstacleSymbol = "";
        }
    }

    public String getObstacleSymbol() {
        return obstacleSymbol;
    }

    public void setObstacleSymbol(String obstacleSymbol) {
        this.obstacleSymbol = obstacleSymbol;
    }
}
