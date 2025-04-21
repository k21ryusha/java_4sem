package laba_1.model;

import laba_1.model.Units.Unit;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    public List<Unit> army;
    private String name;
    private int x;
    private int y;
    private Player owner;
    private int hp = 500;
    private int damage = 100;
    public List<Unit> dead_army;
    public List<Unit> revive_army;
    public List<Unit> sacked_army;


    public Hero(String name, int x, int y, Player owner) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.army = new ArrayList<>();
        this.dead_army = new ArrayList<>();
        this.revive_army = new ArrayList<>();
        this.sacked_army = new ArrayList<>();

    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public Player getOwner() {
        return owner;
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

    public void setArmy(List<Unit> army) {
        this.army = army;
    }

    public List<Unit> getArmy() {
        return army;
    }

    public void addUnit(Unit unit) {
        this.army.add(unit);
    }


    public List<Unit> getDeadArmy() {
        return dead_army;
    }

    public void addDeadUnit(Unit dead_unit) {
        this.dead_army.add(dead_unit);
    }
    public List<Unit> getReviveArmy() {
        return revive_army;
    }
    public void addReviveUnit(Unit revive_unit) {
        this.revive_army.add(revive_unit);
    }
    public List<Unit> getSackedArmy() {
        return sacked_army;
    }
    public void addSackedUnit(Unit sacked_unit) {
        this.sacked_army.add(sacked_unit);
    }

}
