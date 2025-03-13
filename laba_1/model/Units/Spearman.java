package laba_1.model.Units;

public class Spearman extends Unit {
    public Spearman(int x, int y) {
        super(1, 100, 10, 1, x, y); // пример значений
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
