package laba_1.model.Units;

import laba_1.model.Player;

public class Paladin extends Unit {
    public Paladin(Player owner) {
        super("Маг", 5, 150, 75, 2, 0, 0, 200, owner, 2000);
    }
}