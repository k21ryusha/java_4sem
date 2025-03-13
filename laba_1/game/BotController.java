package laba_1.game;

import laba_1.model.Player;

public class BotController {
    private Player bot;

    public BotController(Player bot) {
        this.bot = bot;
    }

    // Геттеры и сеттеры
    public Player getBot() {
        return bot;
    }
    public void setBot(Player bot) {
        this.bot = bot;
    }

    public void makeMove() {
    }
}
