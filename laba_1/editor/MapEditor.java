package laba_1.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import laba_1.MapController.Map;
import laba_1.model.ObstacleType;
import laba_1.model.buildings.Cafe;
import laba_1.model.buildings.Castle;
import laba_1.model.TerrainType;
import laba_1.model.Tile;
import laba_1.view.Console;

import java.io.*;
import java.text.BreakIterator;
import java.util.*;

public class MapEditor {
    private final Scanner scanner = new Scanner(System.in);
    private final String mapsDirectory = "maps";
    private final Console console = new Console();
    private java.util.Map<String, ObstacleType> customObstacleSymbols;

    public MapEditor() {
        File dir = new File(mapsDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void launchEditor() {
        while (true) {
            System.out.println("\n=== Редактор карт ===");
            System.out.println("1. Создать новую карту");
            System.out.println("2. Загрузить карту");
            System.out.println("3. Удалить карту");
            System.out.println("0. Назад в главное меню");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Введите ширину карты: ");
                    int w = scanner.nextInt();
                    System.out.print("Введите высоту карты: ");
                    int h = scanner.nextInt();
                    scanner.nextLine();
                    Map map = new Map(w, h);
                    interactiveEditMap(map);
                }
                case 2 -> loadMapForEdit();
                case 3 -> deleteMap();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    public void interactiveEditMap(Map map) {
        Tile[][] tiles = map.getTiles();
        int width = map.getX();
        int height = map.getY();
        int x = 0, y = 0;

        tiles[0][0].setTerrainType(TerrainType.PLAYER_CASTLE);
        tiles[0][0].setOccupant(new Castle(null, 0, 0));

        tiles[width - 1][height - 1].setTerrainType(TerrainType.BOT_CASTLE);
        tiles[width - 1][height - 1].setOccupant(new Castle(null, width - 1, height - 1));

        int cafeX = 5;
        int cafeY = 3;
        tiles[cafeX][cafeY].setTerrainType(TerrainType.URBAN);
        tiles[cafeX][cafeY].setOccupant(new Cafe(cafeX, cafeY));
        System.out.println("Кафе «Сырники от тети Глаши» установлено на (" + cafeX + ", " + cafeY + ")");

        Tile[][] originalTiles = new Tile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Tile original = tiles[i][j];
                Tile copy = new Tile(original.getX(), original.getY(), original.getTerrainType());
                if (original.getOccupant() instanceof Castle) {
                    copy.setOccupant(new Castle(null, copy.getX(), copy.getY()));
                }
                originalTiles[i][j] = copy;
            }
        }

        while (true) {
            clearScreen();
            displayMapWithCursor(map, x, y);
            System.out.println("\nw/a/s/d — перемещение | R — дорога | T — озеро | Y — нейтраль | U - зона игрока | I - зона бота | M — сохранить | O — поставить кастомное препятствие");

            String input = scanner.nextLine().toUpperCase();
            switch (input) {
                case "W" -> y = Math.max(0, y - 1);
                case "S" -> y = Math.min(height - 1, y + 1);
                case "A" -> x = Math.max(0, x - 1);
                case "D" -> x = Math.min(width - 1, x + 1);
                case "R", "T", "Y", "U", "I", "O" -> {
                    // Проверка: нельзя изменять клетки замков
                    if ((x == 0 && y == 0) || (x == width - 1 && y == height - 1)) {
                        System.out.println("❗ Нельзя изменять клетку с замком!");
                        break;
                    }
                    if ((x == 0 && y == 0) || (x == width - 1 && y == height - 1) || (x == cafeX && y == cafeY)) {
                        System.out.println("❗ Нельзя изменять клетку с замком или кафе!");
                        break;
                    }
                    switch (input) {
                        case "R" -> tiles[x][y].setTerrainType(TerrainType.ROAD);
                        case "T" -> tiles[x][y].setTerrainType(TerrainType.LAKE);
                        case "Y" -> tiles[x][y].setTerrainType(TerrainType.NEUTRAL);
                        case "U" -> tiles[x][y].setTerrainType(TerrainType.PLAYER_ZONE);
                        case "I" -> tiles[x][y].setTerrainType(TerrainType.BOT_ZONE);
                        case "O" -> {
                            System.out.println("Выберите тип препятствия:");
                            System.out.println("1 - Без штрафа");
                            System.out.println("2 - Со штрафом");
                            System.out.println("3 - Непроходимое");
                            System.out.println("4 - Непроходимое со штрафом");
                            System.out.print("Введите номер: ");
                            int obstacleTypeInput = scanner.nextInt();
                            scanner.nextLine();

                            ObstacleType type = ObstacleType.NONE;
                            switch (obstacleTypeInput) {
                                case 1 -> type = ObstacleType.NO_PENALTY;
                                case 2 -> type = ObstacleType.WITH_PENALTY;
                                case 3 -> type = ObstacleType.IMPASSABLE;
                                case 4 -> type = ObstacleType.PENALTY_BLOCK;
                                default -> System.out.println("Неверный выбор типа препятствия.");
                            }

                            if (type != ObstacleType.NONE) {
                                System.out.print("Введите символ для препятствия (один или несколько символов): ");
                                String symbol = scanner.nextLine();
                                tiles[x][y].setObstacle(type);
                                tiles[x][y].setObstacleSymbol(symbol);
                            }
                        }
                    }
                }
                case "M" -> {
                    boolean changed = isMapChanged(originalTiles, tiles);
                    if (!changed) {
                        System.out.println("Изменений не обнаружено. Сохранение не требуется.");
                        return;
                    } else {
                        System.out.print("Введите имя карты: ");
                        String name = scanner.nextLine();
                        saveMapToFile(name, map);
                        saveMapReportAsJson(name, map);
                        System.out.println("Карта сохранена.");
                        return;
                    }
                }
            }
        }
    }

    private void displayMapWithCursor(Map map, int cursorX, int cursorY) {
        Tile[][] tiles = map.getTiles();
        System.out.println("=== Режим редактирования карты ===");
        for (int y = 0; y < map.getY(); y++) {
            for (int x = 0; x < map.getX(); x++) {
                if (x == cursorX && y == cursorY) {
                    System.out.print("[" + getEditorSymbol(tiles[x][y]) + "]");
                } else {
                    System.out.print(" " + getEditorSymbol(tiles[x][y]) + " ");
                }
            }
            System.out.println();
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    public void saveMapToFile(String name, Map map) {
        saveVisualMap(name, map);
    }

    private void saveVisualMap(String name, Map map) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(mapsDirectory + "/" + name + ".txt"))) {
            writer.println(map.getX() + " " + map.getY());
            for (int y = 0; y < map.getY(); y++) {
                for (int x = 0; x < map.getX(); x++) {
                    writer.print(getEditorSymbol(map.getTiles()[x][y]) + " ");
                }
                writer.println();
            }
            writer.println("Ширина карты: " + map.getX() + "Высота карты: " + map.getY());
            writer.println("\nЛегенда:");
            writer.println("🏰 — Замок игрока");
            writer.println("🏯 — Замок бота");
            writer.println("\uD83D\uDEE3 — Дорога");
            writer.println("\uD83C\uDF0A — Озеро");
            writer.println("\uD83C\uDF00 — Нейтральная клетка");
            writer.println("♣\uFE0F — Зона игрока");
            writer.println("♦\uFE0F — Зона бота");
            writer.println("... — Пользовательские препятствия (свои символы)");
            saveCustomObstaclesLegend(name, map);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении визуальной карты.");
        }
    }

    private List<String> extractSymbols(String line, int expectedCount) {
        List<String> symbols = new ArrayList<>();
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(line);
        int start = it.first();
        int count = 0;

        for (int end = it.next(); end != BreakIterator.DONE && count < expectedCount; start = end, end = it.next()) {
            String grapheme = line.substring(start, end).trim();
            if (!grapheme.isEmpty()) {
                symbols.add(grapheme);
                count++;
            }
        }
        return symbols;
    }

    public Map loadMapFromFile(String name) {
        try (BufferedReader reader = new BufferedReader(new FileReader(mapsDirectory + "/" + name + ".txt"))) {
            loadCustomObstaclesLegend(name);
            String[] size = reader.readLine().split(" ");
            int width = Integer.parseInt(size[0]);
            int height = Integer.parseInt(size[1]);

            Map map = new Map(width, height);
            Tile[][] tiles = new Tile[width][height];

            for (int y = 0; y < height; y++) {
                String line = reader.readLine();
                if (line == null) continue;
                List<String> symbols = extractSymbols(line, width);

                for (int x = 0; x < symbols.size(); x++) {
                    String symbol = symbols.get(x);
                    Tile tile = new Tile(x, y, TerrainType.NEUTRAL);

                    if (customObstacleSymbols != null && customObstacleSymbols.containsKey(symbol)) {
                        tile.setObstacle(customObstacleSymbols.get(symbol));
                        tile.setObstacleSymbol(symbol);
                    } else {
                        TerrainType type = switch (symbol) {
                            case "\uD83D\uDEE3" -> TerrainType.ROAD;
                            case "\uD83C\uDF0A" -> TerrainType.LAKE;
                            case "🏰" -> TerrainType.PLAYER_CASTLE;
                            case "🏯" -> TerrainType.BOT_CASTLE;
                            case "\uD83C\uDF00" -> TerrainType.NEUTRAL;
                            case "♣\uFE0F" -> TerrainType.PLAYER_ZONE;
                            case "♦\uFE0F" -> TerrainType.BOT_ZONE;
                            default -> TerrainType.NEUTRAL;
                        };
                        tile.setTerrainType(type);

                        if ("🏰".equals(symbol)) tile.setOccupant(new Castle(null, x, y));
                        if ("🏯".equals(symbol)) tile.setOccupant(new Castle(null, x, y));
                    }

                    tiles[x][y] = tile;
                }
            }

            map.setTiles(tiles);
            return map;
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке карты.");
            return null;
        }
    }
    public void loadMapForEdit() {
        List<String> maps = listMaps();
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт.");
            return;
        }

        for (int i = 0; i < maps.size(); i++) {
            System.out.println((i + 1) + ". " + maps.get(i));
        }

        System.out.print("Выберите карту: ");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > maps.size()) return;

        Map map = loadMapFromFile(maps.get(index - 1));
        if (map != null) {
            interactiveEditMap(map);
        }
    }
    public void deleteMap() {
        List<String> maps = listMaps();
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт.");
            return;
        }

        for (int i = 0; i < maps.size(); i++) {
            System.out.println((i + 1) + ". " + maps.get(i));
        }

        System.out.print("Выберите карту для удаления: ");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > maps.size()) return;

        File file = new File(mapsDirectory + "/" + maps.get(index - 1) + ".txt");
        if (file.delete()) {
            System.out.println("Карта удалена.");
        } else {
            System.out.println("Ошибка при удалении карты.");
        }
    }

    public List<String> listMaps() {
        File dir = new File(mapsDirectory);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        List<String> names = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                names.add(file.getName().replace(".txt", ""));
            }
        }
        return names;
    }

    private String getEditorSymbol(Tile tile) {
        if (tile == null) return " ";

        if (tile.getObstacle() != ObstacleType.NONE && !tile.getObstacleSymbol().isEmpty()) {
            return tile.getObstacleSymbol();
        }

        if (tile.getOccupant() instanceof Castle) {
            TerrainType type = tile.getTerrainType();
            return switch (type) {
                case PLAYER_CASTLE -> "\uD83C\uDFF0";
                case BOT_CASTLE -> "🏯";
                default -> "";
            };
        }

        return switch (tile.getTerrainType()) {
            case ROAD -> "\uD83D\uDEE3";
            case LAKE -> "\uD83C\uDF0A";
            case PLAYER_CASTLE -> "🏰";
            case BOT_CASTLE -> "🏯";
            case NEUTRAL -> "\uD83C\uDF00";
            case PLAYER_ZONE -> "♣\uFE0F";
            case BOT_ZONE -> "♦\uFE0F";
            case URBAN -> "\uD83C\uDF7D";
            default -> "\uD83C\uDF00";
        };
    }
    private boolean isMapChanged(Tile[][] original, Tile[][] current) {
        if (original.length != current.length || original[0].length != current[0].length)
            return true;

        for (int x = 0; x < original.length; x++) {
            for (int y = 0; y < original[0].length; y++) {
                if (original[x][y].getTerrainType() != current[x][y].getTerrainType() ||
                        !Objects.equals(original[x][y].getObstacleSymbol(), current[x][y].getObstacleSymbol())) {
                    return true;
                }

                boolean originalCastle = original[x][y].getOccupant() instanceof Castle;
                boolean currentCastle = current[x][y].getOccupant() instanceof Castle;

                if (originalCastle != currentCastle) {
                    return true;
                }
            }
        }
        return false;
    }
    public void saveMapReportAsJson(String name, Map map) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<java.util.Map<String, Object>> cellsReport = new ArrayList<>();

        Tile[][] tiles = map.getTiles();
        int width = map.getX();
        int height = map.getY();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = tiles[x][y];
                java.util.Map<String, Object> cellInfo = new HashMap<>();
                cellInfo.put("x", x);
                cellInfo.put("y", y);
                cellInfo.put("terrainType", tile.getTerrainType().toString());
                cellInfo.put("obstacleType", tile.getObstacle().toString());
                cellInfo.put("obstacleSymbol", tile.getObstacleSymbol());
                cellsReport.add(cellInfo);
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(mapsDirectory + "/" + name + "_report.json"))) {
            gson.toJson(cellsReport, writer);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении JSON-отчёта карты.");
        }
    }

    public void saveCustomObstaclesLegend(String name, Map map) {
        java.util.Map<String, ObstacleType> legend = new HashMap<>();
        Tile[][] tiles = map.getTiles();
        for (int x = 0; x < map.getX(); x++) {
            for (int y = 0; y < map.getY(); y++) {
                Tile tile = tiles[x][y];
                if (tile.getObstacle() != ObstacleType.NONE && !tile.getObstacleSymbol().isEmpty()) {
                    legend.put(tile.getObstacleSymbol(), tile.getObstacle());
                }
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (PrintWriter writer = new PrintWriter(new FileWriter(mapsDirectory + "/" + name + "_legend.json"))) {
            gson.toJson(legend, writer);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении легенды пользовательских препятствий.");
        }
    }

    public void loadCustomObstaclesLegend(String name) {
        File file = new File(mapsDirectory + "/" + name + "_legend.json");
        if (!file.exists()) {
            customObstacleSymbols = new HashMap<>();
            return;
        }
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.Map<String, ObstacleType>>(){}.getType();
            customObstacleSymbols = gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке легенды пользовательских препятствий.");
            customObstacleSymbols = new HashMap<>();
        }
    }
}
