package laba_1.model.Units;

public class Cavalry extends Unit {
    public Cavalry(int x, int y) {
        super(4, 110, 18, 2, x, y);
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
