package laba_1.model.Units;

public class Swordsman extends Unit {
    public Swordsman(int x, int y) {
        super(3, 120, 12, 1, x, y);
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
