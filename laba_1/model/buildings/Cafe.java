package laba_1.model.buildings;

import laba_1.model.Occupant;
import laba_1.model.Service;
import laba_1.model.Visitor;

import java.util.*;
import java.util.concurrent.*;

public class Cafe implements Occupant {
    private int x;
    private int y;

    public Cafe(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    private final int waiters = 3;
    private final int capacity = waiters * 4;
    private final Semaphore waiterSemaphore = new Semaphore(capacity);
    private final List<Service> services = List.of(
            new Service("Просто перекус", 15, "+2 к перемещению"),
            new Service("Плотный обед", 30, "+3 к перемещению")
    );

    private final ConcurrentHashMap<String, Long> busyVisitors = new ConcurrentHashMap<>();

    public Service getRandomService() {
        Random random = new Random();
        return services.get(random.nextInt(services.size()));
    }

    private void printBusySlots() {
        long now = System.currentTimeMillis();
        busyVisitors.forEach((visitor, time) -> {
            long remainingMs = time - now;
            if (remainingMs > 0) {
                System.out.println(visitor + " освободится через " + remainingMs / 100 + " минут");
            }
        });
    }
    private final Queue<Visitor> waitingQueue = new ConcurrentLinkedQueue<>();

    public synchronized void tryEnter(String visitorName, Service chosenService) {
        if (chosenService == null) {
            chosenService = getRandomService();
        }

        if (waiterSemaphore.tryAcquire()) {
            serveVisitor(visitorName, chosenService);
        } else {
            System.out.println(visitorName + " не смог войти. Все официанты заняты. Добавлен в очередь.");
            waitingQueue.add(new Visitor(visitorName, this, chosenService));
        }
    }

    private void serveVisitor(String visitorName, Service chosenService) {
        long endTime = System.currentTimeMillis() + chosenService.getDurationMinutes() * 100;
        busyVisitors.put(visitorName, endTime);
        System.out.println(visitorName + " начал " + chosenService.getName() + " (" + chosenService.getBonus() + ")");
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            busyVisitors.remove(visitorName);
            waiterSemaphore.release();
            System.out.println(visitorName + " закончил " + chosenService.getName());
            checkQueue();  // Проверка очереди после освобождения
        }, chosenService.getDurationMinutes() * 100L, TimeUnit.MILLISECONDS);
    }

    private void checkQueue() {
        while (waiterSemaphore.availablePermits() > 0 && !waitingQueue.isEmpty()) {
            Visitor nextVisitor = waitingQueue.poll();
            serveVisitor(nextVisitor.getName(), nextVisitor.getChosenService());
        }
    }
}