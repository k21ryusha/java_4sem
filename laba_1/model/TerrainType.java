package laba_1.model;

public enum TerrainType {
    ROAD("\uD83D\uDEE3"), LAKE("\uD83C\uDF0A"), PLAYER_ZONE("♣\uFE0F"), BOT_ZONE("♦\uFE0F"), NEUTRAL("\uD83C\uDF00"),
    BOT_CASTLE("B"), PLAYER_CASTLE("P"), CROSSBOWMAN("\uD83D\uDC0A"), SPEARMAN("⚔\uFE0F"),
    SWORDSMAN("\uD83D\uDDE1"), PALADIN("\uD83E\uDDD9\u200D♂\uFE0F"), CAVALRY("\uD83D\uDC0E"),HERO ("\uD83D\uDC35");
    private final String symbol;

    TerrainType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
