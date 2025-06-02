package laba_1.model;

import com.google.gson.annotations.Expose;
import laba_1.model.buildings.Castle;

public class Player {
    @Expose
    public String name;
    @Expose
    public Hero hero;
    @Expose
    private Castle castle;
    @Expose
    public int gold;

    public Player(){
    }

    public Player(String name, int gold) {
        this.name = name;
        this.gold = gold;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }


    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public Castle getCastle() {
        return castle;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            return this.getName().equals(((Player) obj).getName());
        }
        return false;
    }
    public boolean isDefeated() {
        return (getCastle() == null) ||
                (getHero() == null && getGold() < 700) ||
                (getHero() != null && getHero().getArmy() == null && getGold() < 100);
    }
}
