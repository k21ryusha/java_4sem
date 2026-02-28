package laba_1.game;

import laba_1.MapController.Map;
import laba_1.records.RecordManager;
import laba_1.view.Console;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import laba_1.editor.MapEditor;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);
        MapEditor editor = new MapEditor();
        System.out.print("Введите ваше имя: ");
        String playerName = scanner.nextLine().trim();

        while (true) {
            System.out.println("\n=== Главное меню ===");
            System.out.println("1. Редактор карт");
            System.out.println("2. Начать новую игру");
            System.out.println("3. Загрузить сохранение");
            System.out.println("4. Посмотреть рекорды");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    editor.launchEditor();
                    break;
                case 2:
                    List<String> maps = editor.listMaps();
                    if (maps.isEmpty()) {
                        System.out.println("Нет доступных карт. Сначала создайте одну в редакторе.");
                        break;
                    }

                    System.out.println("Доступные карты:");
                    for (int i = 0; i < maps.size(); i++) {
                        System.out.println((i + 1) + ". " + maps.get(i));
                    }

                    System.out.print("Выберите карту: ");
                    int mapChoice = scanner.nextInt();
                    scanner.nextLine();

                    if (mapChoice < 1 || mapChoice > maps.size()) {
                        System.out.println("Неверный выбор карты.");
                        break;
                    }

                    Map selectedMap = editor.loadMapFromFile(maps.get(mapChoice - 1));
                    if (selectedMap == null) {
                        System.out.println("Ошибка загрузки карты.");
                        break;
                    }

                    Console console = new Console();
                    Game game = new Game(selectedMap, console,playerName);
                    game.startGame();
                    break;
                case 3:
                    Game loadedGame = Game.loadSavedGame(playerName);
                    if (loadedGame != null) {
                        loadedGame.startGame();
                    } else {
                        System.out.println("Сохранение по вашему имени не найдено или повреждено.");
                    }
                    break;
                case 4:
                    RecordManager.showRecords();
                    break;
                case 0:
                    System.out.println("Выход из программы.");
                    return;

                default:
                    System.out.println("Неверный выбор.");
            }
        }
    }
}



