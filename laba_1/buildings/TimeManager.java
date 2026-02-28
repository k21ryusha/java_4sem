package laba_1.buildings;

public class TimeManager {
    public static final int MILLIS_PER_GAME_MINUTE = 100;

    public static long getCurrentGameTimeMillis() {
        return System.currentTimeMillis();
    }
}
