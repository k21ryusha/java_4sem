package laba_1.model.buildings;

import com.google.gson.annotations.Expose;
import laba_1.model.Occupant;
import laba_1.model.Player;

import java.util.ArrayList;
import java.util.List;


public class Castle implements Occupant {
    private transient Player owner;
    @Expose
    public int x;
    @Expose
    public int y;
    @Expose
    public List<Building> buildings;
    @Expose
    public float discontent;

    public Castle() {}

    public Castle(Player owner, int x, int y) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.buildings = new ArrayList<>();
        this.discontent = discontent;
    }

    // Геттеры и сеттеры
    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

    public void addBuilding(Building building) {
        this.buildings.add(building);
    }

    public boolean hasBuilding(Class<? extends Building> buildingClass) {
        for (Building building : buildings) {
            if (buildingClass.isInstance(building)) {
                return true;
            }
        }
        return false;
    }
    public float getDiscontent() {
        return discontent;
    }
    public void setDiscontent(float discontent) {
        this.discontent = discontent;
    }

    public String getOwnerName() {
        return owner.getName();
    }
}
