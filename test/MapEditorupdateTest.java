package test;

import laba_1.MapController.Map;
import laba_1.editor.MapEditor;
import laba_1.model.TerrainType;
import laba_1.model.ObstacleType;
import laba_1.model.Tile;
import org.junit.jupiter.api.*;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class MapEditorupdateTest {
    private MapEditor mapEditor;
    private static final String TEST_MAP_NAME = "testMapUpdate";

    @BeforeEach
    void setUp() {
        mapEditor = new MapEditor();
    }

    @Test
    void testMapUpdate() {
        Map originalMap = new Map(3, 3);
        originalMap.getTiles()[0][0].setTerrainType(TerrainType.ROAD);
        originalMap.getTiles()[1][1].setTerrainType(TerrainType.LAKE);
        mapEditor.saveMapToFile(TEST_MAP_NAME, originalMap);

        Map loadedMap = mapEditor.loadMapFromFile(TEST_MAP_NAME);
        assertNotNull(loadedMap, "Карта должна быть загружена");

        loadedMap.getTiles()[0][0].setTerrainType(TerrainType.BOT_CASTLE);
        loadedMap.getTiles()[1][1].setObstacle(ObstacleType.IMPASSABLE);
        loadedMap.getTiles()[1][1].setObstacleSymbol("X");
        loadedMap.getTiles()[2][2].setTerrainType(TerrainType.PLAYER_CASTLE);

        mapEditor.saveMapToFile(TEST_MAP_NAME, loadedMap);
        mapEditor.saveCustomObstaclesLegend(TEST_MAP_NAME, loadedMap);

        mapEditor.loadCustomObstaclesLegend(TEST_MAP_NAME);
        Map updatedMap = mapEditor.loadMapFromFile(TEST_MAP_NAME);

        assertNotNull(updatedMap, "Изменённая карта должна быть загружена");

        Tile[][] tiles = updatedMap.getTiles();
        assertEquals(TerrainType.BOT_CASTLE, tiles[0][0].getTerrainType(), "Тайл [0][0] должен быть BOT_CASTLE");
        assertEquals(ObstacleType.IMPASSABLE, tiles[1][1].getObstacle(), "Тайл [1][1] должен иметь препятствие");
        assertEquals("X", tiles[1][1].getObstacleSymbol(), "Символ препятствия на [1][1] должен быть X");
        assertEquals(TerrainType.PLAYER_CASTLE, tiles[2][2].getTerrainType(), "Тайл [2][2] должен быть PLAYER_CASTLE");

        deleteTestFiles(TEST_MAP_NAME);
    }

    private void deleteTestFiles(String mapName) {
        new File("maps/" + mapName + ".txt").delete();
        new File("maps/" + mapName + "_legend.json").delete();
        new File("maps/" + mapName + "_report.json").delete();
    }
}

