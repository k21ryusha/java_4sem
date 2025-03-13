package laba_1.model;

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
    public void setName(String name) {
        this.name = name;
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
}
