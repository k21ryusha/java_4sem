package laba_1.util;

import laba_1.model.TerrainType;
import laba_1.model.Tile;

public class MovementCalculator {
    public static int calculatePenalty(Tile tile) {
        if (tile.getTerrainType() == TerrainType.ROAD) {
            return Constants.ROAD_PENALTY;
        } else if (tile.getTerrainType() == TerrainType.NEUTRAL) {
            return Constants.NEUTRAL_ZONE_PENALTY;
        } else if (tile.getTerrainType() == TerrainType.PLAYER_ZONE) {
            return Constants.PLAYER_ZONE_PENALTY;
        } else if (tile.getTerrainType() == TerrainType.BOT_ZONE) {
            return Constants.OPPONENTS_ZONE_PENALTY;
        }
        return 0;
    }
}
