package laba_1.model;
import laba_1.model.Buildings.Building;

import java.util.ArrayList;
import java.util.List;

public class Castle {
        private Player owner;
        private int x;
        private int y;
        private List<Building> buildings;

        public Castle(Player owner, int x, int y) {
            this.owner = owner;
            this.x = x;
            this.y = y;
            this.buildings = new ArrayList<>();
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
        public void setX(int x) {
            this.x = x;
        }
        public int getY() {
            return y;
        }
        public void setY(int y) {
            this.y = y;
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
}
