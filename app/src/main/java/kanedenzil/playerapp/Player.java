package kanedenzil.playerapp;

class Player {

    String playerId;
    String playerName;
    String playerTeam;
    Double latitude;
    Double longitude;

    public Player(String playerId, String playerName, String playerTeam) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerTeam = playerTeam;

        StartActivity location  = new StartActivity();


       location.getLastKnownLocation();




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