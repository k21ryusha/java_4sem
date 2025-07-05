package laba_1.view;

import laba_1.MapController.Map;
import laba_1.model.ObstacleType;
import laba_1.model.buildings.Castle;
import laba_1.model.Hero;
import laba_1.model.TerrainType;
import laba_1.model.Tile;


public class Console {
    private String getSymbolForTile(Tile tile) {
        if (tile == null) {
            return " ";
        }
        if (tile.getOccupant() != null) {
            if (tile.getOccupant() instanceof Hero) {
                return "\uD83D\uDC35"; // Символ героя
            }
            if (tile.getOccupant() instanceof Castle castle) {
                return (castle.getOwner().getName().equals("Player")) ? "\uD83C\uDFF0" : "\uD83C\uDFEF";
            }
        }
            if (tile.getObstacle() != null && tile.getObstacle() != ObstacleType.NONE) {
                String obstacleSymbol = tile.getObstacleSymbol();
                if (obstacleSymbol != null && !obstacleSymbol.isEmpty()) {
                    return obstacleSymbol;
                }
        }
        return tile.getTerrainType().getSymbol();
    }


    public void displayGameMap(Map map) {
        Tile[][] tiles = map.getTiles();
        for (int y = 0; y < map.getY(); y++) {
            for (int x = 0; x < map.getX(); x++) {
                String sym = getSymbolForTile(tiles[x][y]);
                System.out.print(" " + sym + " ");
            }
            System.out.println();
        }
    }
}
