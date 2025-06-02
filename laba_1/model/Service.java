package laba_1.model;

public class Service {
    private final String name;
    private final int durationMinutes;
    private final String bonus;

    public Service(String name, int durationMinutes, String bonus) {
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.bonus = bonus;
    }

    public String getName() {
        return name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getBonus() {
        return bonus;
    }
}
