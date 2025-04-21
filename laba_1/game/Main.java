package laba_1.game;

import laba_1.MapController.Map;
import laba_1.util.Constants;
import laba_1.view.Console;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Map map = new Map(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        Console console = new Console();
        Game game = new Game(map, console);
        game.startGame();
    }
}


