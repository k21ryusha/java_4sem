package laba_1.model.Units;

public class Crossbowman extends Unit {
    public Crossbowman(int x, int y) {
        super(2, 80, 15, 1, x, y);
    }

    @Override
    public void move(int newX, int newY) {
        setX(newX);
        setY(newY);
    }

    @Override
    public void attack(Unit enemy) {
        enemy.setHp(enemy.getHp() - getDamage());
    }
}
