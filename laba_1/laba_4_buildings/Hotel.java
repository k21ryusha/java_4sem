package laba_1.laba_4_buildings;

import java.util.List;

public class Hotel extends Resort {

    private final int x, y;

    public Hotel(int x, int y) {
        super("Отель «У погибшего альпиниста»", 5);
        this.x = x;
        this.y = y;
    }
    public Hotel() {
        super("Отель «У погибшего альпиниста»", 5);
        this.x = -1;
        this.y = -1;
    }


    @Override
    public List<Service> getAvailableServices() {
        return List.of(
                new Service("Короткий отдых (1 день)", 1440, player -> {
                    if (player.getHero() != null) {
                        player.getHero().getArmy().forEach(unit -> unit.setHp(unit.getHp()+2)); // ✅ лечим
                    }
                    System.out.println("Юниты восстановили +2 здоровья.");
                }),
                new Service("Длинный отдых (3 дня)", 4320, player ->{
                    if (player.getHero() != null) {
                        player.getHero().getArmy().forEach(unit -> unit.setHp(unit.getHp()+3)); // ✅ лечим
                    }
                    System.out.println("Юниты восстановили +3 здоровья.");
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
