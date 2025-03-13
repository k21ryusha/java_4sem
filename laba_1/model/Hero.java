package laba_1.model;

import laba_1.model.Units.Unit;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    private String name;
    private int x;
    private int y;
    private List<Unit> army;
    private int gold;

    public Hero(String name, int x, int y, int gold) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.gold = gold;
        this.army = new ArrayList<>();
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public List<Unit> getArmy() {
        return army;
    }
    public void setArmy(List<Unit> army) {
        this.army = army;
    }
    public int getGold() {
        return gold;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }
    public void addUnit(Unit unit) {
        this.army.add(unit);
    }
}
