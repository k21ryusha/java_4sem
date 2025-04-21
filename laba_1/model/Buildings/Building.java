package laba_1.model.Buildings;

public abstract class Building {
    private int cost;
    private String name;

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
