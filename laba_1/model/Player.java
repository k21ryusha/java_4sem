package laba_1.model;

import laba_1.model.Buildings.Castle;

public class Player {
    private String name;
    private Hero hero;
    private Castle castle;
    private int gold;

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
