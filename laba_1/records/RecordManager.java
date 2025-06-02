package laba_1.records;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RecordManager {
    private static final String FILE = "records/_records.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Map<String, Record> loadRecords() {
        File file = new File(FILE);
        if (!file.exists()) return new HashMap<>();

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Record>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("Ошибка чтения расширенных рекордов: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public static void saveRecords(Map<String, Record> records) {
        File dir = new File("records");
        if (!dir.exists()) dir.mkdirs();

        try (Writer writer = new FileWriter(FILE)) {
            gson.toJson(records, writer);
        } catch (IOException e) {
            e.printStackTrace();  // важно не скрывать ошибки
        }
    }

    public static void updateRecord(Record newStats) {
        Map<String, Record> records = loadRecords();
        Record current = records.getOrDefault(newStats.getPlayerName(), new Record(newStats.getPlayerName()));

        if (newStats.getTotalTurns() < current.getTotalTurns() || current.getTotalTurns() == 0) {
            current.setTotalTurns(newStats.getTotalTurns());
        }

        if (newStats.getMostGoldFromKills() > current.getMostGoldFromKills()) {
            current.addGoldFromKills(newStats.getMostGoldFromKills());
        }

        if (newStats.getTimeToVictoryMillis() > 0 &&
                (current.getTimeToVictoryMillis() == 0 || newStats.getTimeToVictoryMillis() < current.getTimeToVictoryMillis())) {
            current.setTimeToVictoryMillis(newStats.getTimeToVictoryMillis());
        }

        current.incrementBattleVictories();

        if (newStats.getResurrectedUnits() > current.getResurrectedUnits()) {
            current.getResurrectedUnits();
        }

        records.put(current.getPlayerName(), current);
        saveRecords(records);
    }

    public static void showRecords() {
        Map<String, Record> records = loadRecords();

        if (records.isEmpty()) {
            System.out.println("Рекорды ещё не установлены.");
            return;
        }

        System.out.println("=== Расширенные рекорды игроков ===");
        for (Record r : records.values()) {
            System.out.println("- " + r.getPlayerName());
            System.out.println("  • Время до победы: " + (r.getTimeToVictoryMillis() / 1000.0) + " сек");
            System.out.println("  • Всего побед в битвах: " + r.getBattleVictories());
            System.out.println("  • Золото за убийства: " + r.getMostGoldFromKills());
            System.out.println("  • Воскрешённых юнитов: " + r.getResurrectedUnits());
            System.out.println("  • Кол-во ходов до победы: " + r.getTotalTurns());
            System.out.println();
        }
    }
}
