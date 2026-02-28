package test;

import laba_1.buildings.*;
import laba_1.model.Hero;
import laba_1.model.Player;
import laba_1.model.units.Swordsman;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class ResortQueueTest {

    @Test
    void cafe_shouldNotAcceptMoreVisitorsThanCapacity() {
        Resort cafe = new Cafe();
        long now = System.currentTimeMillis();
        Service service = cafe.getAvailableServices().get(0);

        List<Visitor> visitors = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Visitor v = new Visitor("v" + i, false, new Player("P" + i, 1000));
            visitors.add(v);
            cafe.serveVisitor(v, service, now);
            assertTrue(cafe.isVisitorInside(v), "Посетитель должен попасть в кафе, пока есть места");
        }

        assertFalse(cafe.isAvailable(), "После заполнения кафе должно стать недоступным");

        Visitor overflow = new Visitor("overflow", false, new Player("Overflow", 1000));
        cafe.serveVisitor(overflow, service, now);
        assertFalse(cafe.isVisitorInside(overflow), "Лишний посетитель не должен попасть в кафе");
    }

    @Test
    void barbershop_shouldRespectConfiguredCapacity() {
        Resort barbershop = new Barbershop();
        long now = System.currentTimeMillis();
        Service service = barbershop.getAvailableServices().get(0);

        Visitor first = new Visitor("first", false, new Player("P1", 1000));
        Visitor second = new Visitor("second", false, new Player("P2", 1000));
        Visitor third = new Visitor("third", false, new Player("P3", 1000));

        barbershop.serveVisitor(first, service, now);
        barbershop.serveVisitor(second, service, now);

        assertTrue(barbershop.isVisitorInside(first));
        assertTrue(barbershop.isVisitorInside(second));
        assertFalse(barbershop.isAvailable());

        barbershop.serveVisitor(third, service, now);
        assertFalse(barbershop.isVisitorInside(third), "Третий посетитель не должен попасть в парикмахерскую при capacity=2");

        long finishTime = now + service.getDurationMinutes() * TimeManager.MILLIS_PER_GAME_MINUTE + 1;
        barbershop.update(finishTime);

        assertFalse(barbershop.isVisitorInside(first), "Первый посетитель должен покинуть парикмахерскую после завершения услуги");
        assertTrue(barbershop.isAvailable(), "После завершения услуги место должно освободиться");
    }

    @Test
    void hotel_shouldReleaseSlotAfterServiceEnds() {
        Resort hotel = new Hotel();
        long now = System.currentTimeMillis();
        Service service = hotel.getAvailableServices().get(0);

        Visitor guest = new Visitor("guest", false, new Player("P", 1000));
        hotel.serveVisitor(guest, service, now);

        long beforeEnd = now + service.getDurationMinutes() * TimeManager.MILLIS_PER_GAME_MINUTE - 1;
        hotel.update(beforeEnd);
        assertTrue(hotel.isVisitorInside(guest), "До окончания времени гость должен оставаться в отеле");

        long afterEnd = now + service.getDurationMinutes() * TimeManager.MILLIS_PER_GAME_MINUTE + 1;
        hotel.update(afterEnd);
        assertFalse(hotel.isVisitorInside(guest), "После окончания времени гость должен покинуть отель");
    }

    @Test
    void barbershop_concurrentServeVisitor_shouldKeepCapacityInvariant() throws InterruptedException {
        Resort barbershop = new Barbershop();
        long now = System.currentTimeMillis();
        Service service = barbershop.getAvailableServices().get(0);

        int threads = 20;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        List<Visitor> visitors = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            visitors.add(new Visitor("v" + i, false, new Player("P" + i, 1000)));
        }

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            Thread t = new Thread(() -> {
                ready.countDown();
                try {
                    start.await();
                    barbershop.serveVisitor(visitors.get(idx), service, now);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
            t.start();
        }

        ready.await();
        start.countDown();
        done.await();

        int inside = 0;
        for (Visitor v : visitors) {
            if (barbershop.isVisitorInside(v)) {
                inside++;
            }
        }

        assertEquals(2, inside, "При конкурентном входе в парикмахерской должно обслуживаться не больше 2 посетителей");
    }

    @Test
    void cafe_shouldApplyServiceBonusOnlyAfterCompletion() {
        Resort cafe = new Cafe();
        Player player = new Player("Player", 1000);
        Hero hero = new Hero("Hero", 0, 0, player);
        player.setHero(hero);
        Swordsman swordsman = new Swordsman(player);
        hero.addUnit(swordsman);

        Visitor playerVisitor = new Visitor("Игрок", true, player);
        Service service = cafe.getAvailableServices().get(0);
        long now = System.currentTimeMillis();
        int beforeMove = swordsman.getMovement();

        cafe.serveVisitor(playerVisitor, service, now);

        long beforeEnd = now + service.getDurationMinutes() * TimeManager.MILLIS_PER_GAME_MINUTE - 1;
        cafe.update(beforeEnd);
        assertEquals(beforeMove, swordsman.getMovement(), "До окончания услуги бонус применяться не должен");

        long afterEnd = now + service.getDurationMinutes() * TimeManager.MILLIS_PER_GAME_MINUTE + 1;
        cafe.update(afterEnd);
        assertEquals(beforeMove + 2, swordsman.getMovement(), "После окончания услуги бонус кафе должен примениться");
    }
}