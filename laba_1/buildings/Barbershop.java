package laba_1.buildings;

import laba_1.model.units.Unit;

import java.util.List;

public class Barbershop extends Resort {
    private final int x, y;

    public Barbershop(int x, int y) {
        super("Парикмахерская «Отрезанное ухо»", 2);
        this.x = x;
        this.y = y;
    }
    public Barbershop() {
        super("Парикмахерская «Отрезанное ухо»", 2);
        this.x = -1;
        this.y = -1;
    }
    @Override
    public List<Service> getAvailableServices() {
        return List.of(
                new Service("Просто стрижка", 10, player ->
                        System.out.println("Просто стрижка завершена, бонусов нет.")
                ),
                new Service("Модная стрижка", 30, player ->  {
                    if (player.getHero() != null) {
                        for (Unit unit : player.getHero().getArmy()) {
                            unit.increaseDamage(5);
                        }
                    }
                    System.out.println("Каждый юнит получил +5 к урону благодаря модной стрижке!");
                }));
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
