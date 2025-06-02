package laba_1.model;

import com.google.gson.annotations.Expose;
import laba_1.model.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class Hero implements Occupant {
    @Expose
    public List<Unit> army;
    public String name;
    @Expose
    public int x;
    @Expose
    public int y;
    private transient Player owner;
    @Expose
    public List<Unit> dead_army;
    @Expose
    public List<Unit> revive_army;
    @Expose
    public List<Unit> sacked_army;

    public Hero() {}

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
        if (army == null) {
            army = new ArrayList<>(); // Ленивая инициализация
        }
        army.add(unit);
    }

    public void addDeadUnit(Unit dead_unit) {
        if (this.dead_army == null) {
            this.dead_army = new ArrayList<>();
        }
        this.dead_army.add(dead_unit);
    }

    public void addReviveUnit(Unit revive_unit) {
        if (this.revive_army == null) {
            this.revive_army = new ArrayList<>();
        }
        this.revive_army.add(revive_unit);
    }

    public void addSackedUnit(Unit sacked_unit) {
        if (this.sacked_army == null) {
            this.sacked_army = new ArrayList<>();
        }
        this.sacked_army.add(sacked_unit);
    }
    public List<Unit> getDeadArmy() {
        return dead_army;
    }
    public List<Unit> getReviveArmy() {
        return revive_army;
    }
    public List<Unit> getSackedArmy() {
        return sacked_army;
    }
    public Player getPlayer() {
        return owner;
    }
    public void setPlayer(Player player) {
        this.owner = player;
    }
    public void setDeadArmy(List<Unit> deadArmy) {
        this.dead_army = deadArmy;
    }
    public void setReviveArmy(List<Unit> reviveArmy) {
        this.revive_army = reviveArmy;
    }
    public void setSackedArmy(List<Unit> sackedArmy) {
        this.sacked_army = sackedArmy;
    }

}
