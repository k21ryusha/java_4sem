package laba_1.model;

import laba_1.model.units.Unit;

public class BattleTile {
    private final int x, y;
    private Unit occupant;

    public BattleTile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Unit getOccupant() {
        return occupant;
    }

    public void setOccupant(Unit occupant) {
        this.occupant = occupant;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
