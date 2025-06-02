package test;
import laba_1.MapController.Map;
import laba_1.editor.MapEditor;
import laba_1.model.Tile;
import laba_1.model.TerrainType;
import laba_1.model.ObstacleType;
import org.junit.jupiter.api.*;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class MapSaveAndLoadTest {
    private MapEditor mapEditor;
    private static final String TEST_MAP_NAME = "testMap";

    @BeforeEach
    void setUp() {
        mapEditor = new MapEditor();
    }

    @Test
    void testSaveAndLoadSimpleMap() {
        Map map = new Map(3, 3);
        map.getTiles()[0][0].setTerrainType(TerrainType.PLAYER_CASTLE);
        map.getTiles()[1][1].setTerrainType(TerrainType.BOT_CASTLE);
        map.getTiles()[2][2].setTerrainType(TerrainType.LAKE);

        mapEditor.saveMapToFile(TEST_MAP_NAME, map);
        Map loadedMap = mapEditor.loadMapFromFile(TEST_MAP_NAME);

        assertNotNull(loadedMap, "Карта должна быть загружена");
        assertEquals(map.getX(), loadedMap.getX(), "Ширина карты должна совпадать");
        assertEquals(map.getY(), loadedMap.getY(), "Высота карты должна совпадать");

        Tile[][] original = map.getTiles();
        Tile[][] loaded = loadedMap.getTiles();
        for (int x = 0; x < map.getX(); x++) {
            for (int y = 0; y < map.getY(); y++) {
                assertEquals(original[x][y].getTerrainType(), loaded[x][y].getTerrainType(),
                        String.format("Несоответствие типа клетки [%d][%d]", x, y));
            }
        }

        deleteTestFiles(TEST_MAP_NAME);
    }

    @Test
    void testSaveAndLoadMapWithCustomObstacles() {
        Map map = new Map(3, 3);
        map.getTiles()[0][0].setObstacle(ObstacleType.IMPASSABLE);
        map.getTiles()[0][0].setObstacleSymbol("X");

        mapEditor.saveMapToFile(TEST_MAP_NAME, map);
        mapEditor.saveCustomObstaclesLegend(TEST_MAP_NAME, map);

        mapEditor.loadCustomObstaclesLegend(TEST_MAP_NAME);
        Map loadedMap = mapEditor.loadMapFromFile(TEST_MAP_NAME);

        Tile loadedTile = loadedMap.getTiles()[0][0];
        assertEquals(ObstacleType.IMPASSABLE, loadedTile.getObstacle(), "Тип препятствия должен совпадать");
        assertEquals("X", loadedTile.getObstacleSymbol(), "Символ препятствия должен совпадать");

        deleteTestFiles(TEST_MAP_NAME);
    }

    @Test
    void testEmptyMapSaveAndLoad() {
        Map map = new Map(2, 2);
        mapEditor.saveMapToFile(TEST_MAP_NAME, map);
        Map loadedMap = mapEditor.loadMapFromFile(TEST_MAP_NAME);

        assertNotNull(loadedMap, "Карта должна быть загружена");
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                assertEquals(TerrainType.NEUTRAL, loadedMap.getTiles()[x][y].getTerrainType(),
                        String.format("Тайл [%d][%d] должен быть NEUTRAL", x, y));
                assertEquals(ObstacleType.NONE, loadedMap.getTiles()[x][y].getObstacle(),
                        String.format("Тайл [%d][%d] не должен иметь препятствий", x, y));
            }
        }

        deleteTestFiles(TEST_MAP_NAME);
    }

    private void deleteTestFiles(String mapName) {
        new File("maps/" + mapName + ".txt").delete();
        new File("maps/" + mapName + "_legend.json").delete();
        new File("maps/" + mapName + "_report.json").delete();
    }
}

