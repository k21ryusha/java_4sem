package laba_1.model;

import laba_1.model.buildings.Cafe;

import java.util.Random;

public class Visitor implements Runnable {
    private final String name;
    private final Cafe cafe;
    private final Service chosenService;

    // Конструктор для случайного выбора услуги
    public Visitor(String name, Cafe cafe) {
        this.name = name;
        this.cafe = cafe;
        this.chosenService = cafe.getRandomService();
    }

    // Конструктор для очереди, где услуга уже выбрана
    public Visitor(String name, Cafe cafe, Service chosenService) {
        this.name = name;
        this.cafe = cafe;
        this.chosenService = chosenService;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (!Thread.currentThread().isInterrupted()) {
            cafe.tryEnter(name, chosenService);
            try {
                Thread.sleep(5000 + random.nextInt(5000));
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public Service getChosenService() {
        return chosenService;
    }
}
