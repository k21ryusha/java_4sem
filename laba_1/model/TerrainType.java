package laba_1.model;

public enum TerrainType {
        ROAD("||"), OBSTACLE("$"), PLAYER_ZONE("@"), BOT_ZONE("#"), NEUTRAL("*");
        private final String symbol;

        TerrainType(String symbol) {
                this.symbol = symbol;
        }
        public String getSymbol() {
                return symbol;
        }
}
