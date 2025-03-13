package laba_1.model.Units;

public abstract class Unit {
    private int level;
    private int hp;
    private int damage;
    private int movement;
    private int x;
    private int y;

    public Unit(int level, int hp, int damage, int movement, int x, int y) {
        this.level = level;
        this.hp = hp;
        this.damage = damage;
        this.movement = movement;
        this.x = x;
        this.y = y;
    }

    // Геттеры и сеттеры
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = hp;
    }
    public int getDamage() {
        return damage;
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }
    public int getMovement() {
        return movement;
    }
    public void setMovement(int movement) {
        this.movement = movement;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    // Абстрактные методы
    public abstract void move(int newX, int newY);
    public abstract void attack(Unit enemy);
}