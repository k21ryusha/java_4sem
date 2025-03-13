package laba_1.util;

public class MovementCalculator {
    public static int calculateMovementCost(int x1, int y1, int x2, int y2, int baseCost) {
        return (Math.abs(x1 - x2) + Math.abs(y1 - y2)) * baseCost;
    }
}
