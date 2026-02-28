package laba_1.buildings;

import laba_1.model.Player;

import java.util.UUID;

public class Visitor {
    private final UUID id = UUID.randomUUID();
    private final String name;
    private final boolean isPlayer;
    private final Player linkedPlayer;

    public Visitor(String name, boolean isPlayer, Player linkedPlayer) {
        this.name = name;
        this.isPlayer = isPlayer;
        this.linkedPlayer = linkedPlayer;
    }

    public String getName() {
        return name;
    }

    public boolean isPlayer() {
        return isPlayer;
    }
    @Override
    public String toString() {
        return (isPlayer ? "Игрок" : "NPC") + " " + name;
    }
    public Player getLinkedPlayer() {
        return linkedPlayer;
    }
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Visitor visitor = (Visitor) obj;
        return id.equals(visitor.id);
    }

    public int hashCode() {
        return id.hashCode();
    }

    public UUID getId() {
        return id;
    }
}
