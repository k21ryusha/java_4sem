package laba_1.model.Units;

public class Paladin extends Unit {
    public Paladin(int x, int y) {
        super(5, 130, 20, 2, x, y);
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
