package laba_1.model.Units;

import laba_1.model.Player;

public abstract class Unit {
    private String name;
    private int level;
    private int hp;
    private int damage;
    private int movement;
    private int x;
    private int y;
    private int cost;
    private Player owner;
    private int reward;

    public Unit(String name,int level, int hp, int damage, int movement, int x, int y, int cost, Player owner, int reward) {
        this.level = level;
        this.hp = hp;
        this.damage = damage;
        this.movement = movement;
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.owner = owner;
        this.name = name;
        this.reward = reward;
    }

    // Геттеры и сеттеры

public String getName() {
        return name;
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

    public int getMovement() {
        return movement;
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

    public int getCost() {
        return cost;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isAlive() {
        return hp > 0;
    }
    public int getReward() {
        return reward;
    }
}