package laba_1.view;

import laba_1.model.*;

import java.util.Scanner;
import laba_1.game.*;
import laba_1.util.Constants;
import laba_1.model.*;

public class Console {
    private final Scanner scanner = new Scanner(System.in);

    public void displayMainMenu(Game game) {
        while (true) {
            System.out.println("\n=== Главное меню ===");
            System.out.println("1. Купить героя");
            System.out.println("2. Купить юнитов");
            System.out.println("3. Завершить ход");
            System.out.println("0. Выход из игры");
            game.balance();
            System.out.print("Выберите действие: ");


            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера

            switch (choice) {
                case 1:
                    game.buyHero();
                    break;
                case 2:
                    game.buyUnits();
                    break;
                case 3:
                    game.moveHero();
                    break;
                case 4:
                    game.endTurn();
                    break;
                case 0:
                    System.out.println("Выход из игры...");
                    return;
                default:
                    System.out.println("Некорректный ввод, попробуйте снова.");
            }
        }
    }

    private String getSymbolForTile(Tile tile) {
        if (tile == null) {
            return " ";
        }
        if (tile.getOccupant() != null) {
            if (tile.getOccupant() instanceof Hero) {
                return "H"; // Символ героя
            }
            if (tile.getOccupant() instanceof Castle castle) {
                return (castle.getOwner().getName().equals("Player")) ? "P" : "B";
            }
        }
        return tile.getTerrainType().getSymbol();
    }


    public void displayGameMap(Map gameMap) {
        Tile[][] tiles = Map.getTiles();
        System.out.println("\n=== Игровая карта ===");
        for (int y = 0; y < gameMap.getX(); y++) {
            for (int x = 0; x < gameMap.getY(); x++) {
                Tile tile = tiles[x][y];
                System.out.print(getSymbolForTile(tile) + " ");
            }
            System.out.println();
        }
    }
    public void initializeMap(Map map){
        Tile[][] tiles = new Tile[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
        for (int i = 0; i < Constants.MAP_WIDTH ; i++) {
            for (int j = 0; j < Constants.MAP_HEIGHT/2; j++){
                tiles[i][j] = new Tile(i,j,TerrainType.PLAYER_ZONE);
            }
        }
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            for (int j = Constants.MAP_HEIGHT /2; j < Constants.MAP_HEIGHT; j++){
                tiles[i][j] = new Tile(i,j,TerrainType.BOT_ZONE);
            }
        }
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            for (int j = Constants.MAP_HEIGHT/2 - 1; j < Constants.MAP_HEIGHT/2 + 1; j++) {
                tiles[i][j] = new Tile(i, j, TerrainType.NEUTRAL);
            }
        }
        for (int i = 0; i < Constants.MAP_HEIGHT; i++) {
            tiles[i][i].setTerrainType(TerrainType.ROAD);
        }

        map.setTiles(tiles);
        Player player = new Player("Player", 1000);
        Player bot = new Player("Bot", 1000);
        Castle playerCastle = new Castle(player,0,0);
        Castle botCastle = new Castle(bot,Constants.MAP_WIDTH-1,Constants.MAP_HEIGHT-1);
        Map.getTiles()[0][0].setOccupant(playerCastle);
        Map.getTiles()[Constants.MAP_WIDTH - 1][Constants.MAP_HEIGHT - 1].setOccupant(botCastle);
    }
}
