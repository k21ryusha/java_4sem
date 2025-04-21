package laba_1.model.Units;

import laba_1.model.Player;

public class Swordsman extends Unit {
    public Swordsman(Player owner) {
        super("Мечник", 3, 120, 60, 1, 0, 0, 150, owner, 1000);
    }
}