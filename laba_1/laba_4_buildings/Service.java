package laba_1.laba_4_buildings;

import laba_1.model.Player;

import java.util.function.Consumer;

public class Service {
    private final String name;
    private final int durationMinutes;
    private final Consumer<Player> bonusAction;

    public Service(String name, int durationMinutes,Consumer<Player> bonusAction) {
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.bonusAction = bonusAction;
    }

    public String getName() {
        return name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void applyBonus(Player player) {
        bonusAction.accept(player);
    }
}