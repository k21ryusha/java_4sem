package test;

import laba_1.model.TerrainType;
import laba_1.model.Tile;
import laba_1.util.Constants;
import laba_1.util.MovementCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MovementCalculatorTest {
    @Test
    void calculatePenalty_road_returnsRoadPenalty() {
        Tile tile = new Tile(0, 0, TerrainType.ROAD);

        int penalty = MovementCalculator.calculatePenalty(tile);

        assertEquals(Constants.ROAD_PENALTY, penalty);
    }

    @Test
    void calculatePenalty_neutralZone_returnsNeutralPenalty() {
        Tile tile = new Tile(0, 0, TerrainType.NEUTRAL);
        assertEquals(Constants.NEUTRAL_ZONE_PENALTY, MovementCalculator.calculatePenalty(tile));
    }

    @Test
    void calculatePenalty_playerZone_returnsPlayerZonePenalty() {
        Tile tile = new Tile(0, 0, TerrainType.PLAYER_ZONE);
        assertEquals(Constants.PLAYER_ZONE_PENALTY, MovementCalculator.calculatePenalty(tile));
    }

    @Test
    void calculatePenalty_botZone_returnsOpponentZonePenalty() {
        Tile tile = new Tile(0, 0, TerrainType.BOT_ZONE);
        assertEquals(Constants.OPPONENTS_ZONE_PENALTY, MovementCalculator.calculatePenalty(tile));
    }
}
