package test;

import laba_1.MapController.Map;
import laba_1.editor.MapEditor;
import laba_1.model.TerrainType;
import laba_1.model.Tile;
import org.junit.jupiter.api.*;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class MapEditorLoadTest {
    private MapEditor mapEditor;
    private static final String TEST_MAP_NAME = "testMapLoad";

    @BeforeEach
    void setUp() {
        mapEditor = new MapEditor();
    }

    @Test
    void testLoadMapByName() {
        Map mapToSave = new Map(3, 3);
        mapToSave.getTiles()[0][0].setTerrainType(TerrainType.ROAD);
        mapToSave.getTiles()[1][1].setTerrainType(TerrainType.LAKE);
        mapToSave.getTiles()[2][2].setTerrainType(TerrainType.BOT_CASTLE);

        mapEditor.saveMapToFile(TEST_MAP_NAME, mapToSave);

        Map loadedMap = mapEditor.loadMapFromFile(TEST_MAP_NAME);
        assertNotNull(loadedMap, "Карта должна быть загружена");

        // Проверка размеров карты
        assertEquals(mapToSave.getX(), loadedMap.getX(), "Ширина карты должна совпадать");
        assertEquals(mapToSave.getY(), loadedMap.getY(), "Высота карты должна совпадать");

        Tile[][] savedTiles = mapToSave.getTiles();
        Tile[][] loadedTiles = loadedMap.getTiles();
        for (int x = 0; x < mapToSave.getX(); x++) {
            for (int y = 0; y < mapToSave.getY(); y++) {
                assertEquals(savedTiles[x][y].getTerrainType(), loadedTiles[x][y].getTerrainType(),
                        "Тайл на координатах [" + x + "][" + y + "] должен совпадать");
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
