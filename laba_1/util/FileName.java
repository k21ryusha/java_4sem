package laba_1.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class FileName {
    public static String generateSaveFileName(String playerName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "saves/" + playerName + "_save_" + timestamp + ".json";
    }
}