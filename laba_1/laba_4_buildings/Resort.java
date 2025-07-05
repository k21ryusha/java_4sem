package laba_1.laba_4_buildings;


import laba_1.model.Occupant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Resort implements Occupant {
    protected final String name;
    protected final int capacity;

    private static class VisitInfo {
        long endTime;
        Service service;

        VisitInfo(long endTime, Service service) {
            this.endTime = endTime;
            this.service = service;
        }
    }

    protected final Map<Visitor, VisitInfo> activeVisitors = new HashMap<>();

    public Resort(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public abstract List<Service> getAvailableServices();

    public boolean isAvailable() {
        return activeVisitors.size() < capacity;
    }

    public synchronized void serveVisitor(Visitor visitor, Service service, long currentTimeMillis) {
        if (isAvailable() && !activeVisitors.containsKey(visitor)) {
            long endTime = currentTimeMillis + service.getDurationMinutes() * TimeManager.MILLIS_PER_GAME_MINUTE;
            activeVisitors.put(visitor, new VisitInfo(endTime, service));
        }
    }
    public synchronized void update(long currentTimeMillis) {
        List<Visitor> completed = new ArrayList<>();

        for (Map.Entry<Visitor, VisitInfo> entry : activeVisitors.entrySet()) {
            if (entry.getValue().endTime < currentTimeMillis) {
                completed.add(entry.getKey());
            }
        }

        for (Visitor visitor : completed) {
            VisitInfo info = activeVisitors.remove(visitor);
            if (visitor.isPlayer()) {
                System.out.println(visitor.getName() + " завершил " + info.service.getName() + " в " + name);
                info.service.applyBonus(visitor.getLinkedPlayer());
            }
        }
    }

    public synchronized void printStatus(long currentTimeMillis) {
        update(currentTimeMillis);
        System.out.println("=== " + name + " ===");

        boolean anyoneActive = false;

        for (Map.Entry<Visitor, VisitInfo> entry : activeVisitors.entrySet()) {
            long remaining = entry.getValue().endTime - currentTimeMillis;
            if (remaining > 0) {
                System.out.println(entry.getKey().getName() + " занят ещё " +
                        (remaining / TimeManager.MILLIS_PER_GAME_MINUTE) + " мин.");
                anyoneActive = true;
            }
        }

        if (!anyoneActive) {
            System.out.println("Все свободны.");
        }
    }

    public String getName() {
        return name;
    }
    public boolean isVisitorInside(Visitor visitor) {
        return activeVisitors.containsKey(visitor);
    }
}