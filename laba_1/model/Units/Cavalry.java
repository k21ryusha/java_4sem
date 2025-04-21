package laba_1.model.Units;

import laba_1.model.Player;

public class Cavalry extends Unit {
    public Cavalry(Player owner) {
        super("Кавалерист", 4, 130, 65, 2, 0, 0, 175, owner, 1500);
    }
}