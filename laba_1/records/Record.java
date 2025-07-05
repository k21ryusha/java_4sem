package laba_1.records;

public class Record {
    private final String playerName;
    private int mostGoldFromKills = 0;
    private int resurrectedUnits = 0;
    private int battleVictories = 0;
    private int totalTurns = 0;
    private long timeToVictoryMillis;


    public Record(String name) {
        this.playerName = name;
    }

    public String getPlayerName() { return playerName; }
    public int getMostGoldFromKills() { return mostGoldFromKills; }
    public int getResurrectedUnits() { return resurrectedUnits; }
    public int getBattleVictories() { return battleVictories; }
    public int getTotalTurns() { return totalTurns; }


    public void addGoldFromKills(int amount) {
        this.mostGoldFromKills += amount;
    }
    public void setGoldFromKills(int amount) {
        this.mostGoldFromKills = amount;
    }

    public void incrementResurrectedUnits() {
        this.resurrectedUnits++;
    }

    public void incrementBattleVictories() {
        this.battleVictories++;
    }

    public void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }

    public void setResurrectedUnitsCount(int resurrectedUnitsCount) {
        this.resurrectedUnits = resurrectedUnitsCount;
    }
    public long getTimeToVictoryMillis() {
        return timeToVictoryMillis;
    }

    public void setTimeToVictoryMillis(long timeToVictoryMillis) {
        this.timeToVictoryMillis = timeToVictoryMillis;
    }
}
