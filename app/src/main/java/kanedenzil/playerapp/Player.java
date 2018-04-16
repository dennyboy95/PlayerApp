package kanedenzil.playerapp;

import java.util.Map;

class Player implements Map<String, Object> {

    String playerId;
    String playerName;
    String playerTeam;
    Double latitude;
    Double longitude;

    public Player(String playerId, String playerName, String playerTeam) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerTeam = playerTeam;
    }

    public Player(double latitude, double longitude) {
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerTeam() {
        return playerTeam;
    }
}