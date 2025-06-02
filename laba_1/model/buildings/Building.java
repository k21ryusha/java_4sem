package laba_1.model.buildings;


public abstract class Building{
    public int cost;
    public String name;

    public Building(int cost, String name) {
        this.cost = cost;
        this.name = name;
    }

    // Геттеры и сеттеры
    public int getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }
}
