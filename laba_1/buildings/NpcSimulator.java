package laba_1.buildings;

import java.util.List;
import java.util.Random;

public class NpcSimulator implements Runnable {
    private final List<Visitor> visitors;
    private final List<Resort> resorts;
    private volatile boolean running = true;
    private final Random random = new Random();

    public NpcSimulator(List<Visitor> visitors, List<Resort> resorts) {
        this.visitors = visitors;
        this.resorts = resorts;
    }

    @Override
    public void run() {
        while (running) {
            long now = TimeManager.getCurrentGameTimeMillis();
            for (Resort resort : resorts) {
                resort.update(now);
            }

            if (!resorts.isEmpty()) {
                for (Visitor npc : visitors) {
                    if (!npc.isPlayer() && !isInAnyResort(npc) && random.nextDouble() < 0.3) {
                        Resort resort = resorts.get(random.nextInt(resorts.size()));
                        List<Service> services = resort.getAvailableServices();
                        if (!services.isEmpty()) {
                            Service service = services.get(random.nextInt(services.size()));
                            if (resort.isAvailable()) {
                                resort.serveVisitor(npc, service, now);
                            }
                        }
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private boolean isInAnyResort(Visitor visitor) {
        for (Resort resort : resorts) {
            if (resort.isVisitorInside(visitor)) {
                return true;
            }
        }
        return false;
    }

    public void stop() {
        running = false;
    }
}
