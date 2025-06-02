package laba_1.save;

import com.google.gson.annotations.Expose;
import laba_1.MapController.Map;
import laba_1.model.Player;
import java.util.List;

public class GameState {
    @Expose
    public Player player;
    @Expose
    public Player bot;
    @Expose
    public boolean gameOver;
    @Expose
    public List<List<java.util.Map<String, Object>>> map;

    public GameState() {
    }
}