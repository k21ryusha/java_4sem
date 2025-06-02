package laba_1.model.units;

import com.google.gson.annotations.Expose;
import laba_1.model.Occupant;
import laba_1.model.Player;

public abstract class Unit{
    @Expose
    public String name;
    @Expose
    public int level;
    @Expose
    public int hp;
    @Expose
    public int damage;
    @Expose
    public int movement;
    @Expose
    public int x;
    @Expose
    public int y;
    public int cost;
    protected transient Player owner;
    public int reward;

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

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isAlive() {
        return hp > 0;
    }
    public int getReward() {
        return reward;
    }

    public void setMovement(int i) {
        this.movement = i;
    }

    public void setDamage(int i) {
        this.damage = i;
    }

    public void setReward(int i) {
        this.reward = i;
    }
}