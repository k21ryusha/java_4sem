package laba_1.game;

import laba_1.model.*;
import laba_1.util.Constants;
import laba_1.view.Console;

public class Main {
    public static void main(String[] args) {
        Map map = new Map(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        Console console = new Console();
        console.initializeMap(map);
        Game game = new Game(map,console);
        console.displayMainMenu(game);
    }
}


