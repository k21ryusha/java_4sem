package laba_1.view;

import laba_1.MapController.Map;
import laba_1.model.Buildings.Castle;
import laba_1.model.Hero;
import laba_1.model.TerrainType;
import laba_1.model.Tile;
import laba_1.util.Constants;

import java.util.Scanner;


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
        return tile.getTerrainType().getSymbol();
    }


    public void displayGameMap(Map map) {
        Tile[][] tiles = map.getTiles();
        System.out.println("\n=== Игровая карта ===");
        for (int y = 0; y < map.getX(); y++) {
            for (int x = 0; x < map.getY(); x++) {
                Tile tile = tiles[x][y];
                System.out.print(getSymbolForTile(tile) + " ");
            }
            System.out.println();
        }
    }

    public void initializeMap(Map map) {
        Tile[][] tiles = new Tile[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            for (int j = 0; j < Constants.MAP_HEIGHT / 2; j++) {
                tiles[i][j] = new Tile(i, j, TerrainType.PLAYER_ZONE);
            }
        }
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            for (int j = Constants.MAP_HEIGHT / 2; j < Constants.MAP_HEIGHT; j++) {
                tiles[i][j] = new Tile(i, j, TerrainType.BOT_ZONE);
            }
        }
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            for (int j = Constants.MAP_HEIGHT / 2 - 1; j < Constants.MAP_HEIGHT / 2 + 1; j++) {
                tiles[i][j] = new Tile(i, j, TerrainType.NEUTRAL);
            }
        }
        for (int i = 0; i < Constants.MAP_HEIGHT; i++) {
            tiles[i][i].setTerrainType(TerrainType.ROAD);
        }
        Castle playerCastle = new Castle(null, 0, 0);
        tiles[0][0] = new Tile(0, 0, TerrainType.PLAYER_CASTLE);
        tiles[0][0].setOccupant(playerCastle);

        Castle botCastle = new Castle(null, Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1); // Владелец пока null
        tiles[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1] = new Tile(Constants.MAP_WIDTH - 1, Constants.MAP_HEIGHT - 1, TerrainType.BOT_CASTLE);
        tiles[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1].setOccupant(botCastle);

        tiles[2][5] = new Tile(2, 5, TerrainType.LAKE);
        tiles[2][4] = new Tile(2, 4, TerrainType.LAKE);
        tiles[1][5] = new Tile(1, 5, TerrainType.LAKE);
        tiles[1][4] = new Tile(1, 4, TerrainType.LAKE);

        map.setTiles(tiles);

    }

}
