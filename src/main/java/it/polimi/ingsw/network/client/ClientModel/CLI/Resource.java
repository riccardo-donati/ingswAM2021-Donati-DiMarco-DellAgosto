package it.polimi.ingsw.network.client.ClientModel.CLI;

public enum Resource {
    COIN(Color.ANSI_YELLOW.escape()+"◉"+Color.RESET),
    SHIELD(Color.ANSI_BLUE.escape()+"▣"+Color.RESET),
    SERVANT(Color.ANSI_PURPLE.escape()+"▼"+Color.RESET),
    STONE("▨"),
    FAITH(Color.ANSI_RED.escape()+"♰"+Color.RESET),
    QUESTIONMARK("?"),
    EMPTY("  ");

    public String label;

    Resource(String type) {
        label = type;
    }

}
