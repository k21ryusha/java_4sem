package laba_1.laba_4_buildings;

import laba_1.model.Player;
import laba_1.model.units.Unit;

import java.util.List;

public class Cafe extends Resort{

    private final int x, y;

    public Cafe(int x, int y) {
        super("Кафе «Сырники от тети Глаши»", 12);
        this.x = x;
        this.y = y;
    }
    public Cafe() {
        super("Кафе «Сырники от тети Глаши»", 12);
        this.x = -1;
        this.y = -1;
    }

    @Override
    public List<Service> getAvailableServices() {
        return List.of(
                new Service("Просто перекус", 15, (Player player) -> {
                    if (player.getHero() != null) {
                        for (Unit unit : player.getHero().getArmy()) {
                            unit.setMovement(unit.getMovement() + 2);
                        }
                        System.out.println("Юниты получили +2 к перемещению после перекуса.");
                    }
                }),
                new Service("Плотный обед", 30, (Player player) -> {
                    if (player.getHero() != null) {
                        for (Unit unit : player.getHero().getArmy()) {
                            unit.setMovement(unit.getMovement() + 3);
                        }
                        System.out.println("Юниты получили +3 к перемещению после обеда.");
                    }
                })
        );
    }



@Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }
}
