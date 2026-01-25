package dev.piyush.shyamduels.queue;

public enum QueueMode {
    ONE_V_ONE(1, "1v1"),
    TWO_V_TWO(2, "2v2"),
    THREE_V_THREE(3, "3v3"),
    FOUR_V_FOUR(4, "4v4");

    private final int teamSize;
    private final String display;

    QueueMode(int teamSize, String display) {
        this.teamSize = teamSize;
        this.display = display;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public int getTotalPlayers() {
        return teamSize * 2;
    }

    public String getDisplay() {
        return display;
    }
}
