package it.polimi.ingsw.network.client.ClientModel.CLI;

public enum ClientPopeFavorState {
    UNACTIVE(Color.ANSI_BLUE.escape()+"▄"+Color.RESET),
    ACTIVE(Color.ANSI_GREEN.escape()+"▄"+Color.RESET),
    DISCARDED(Color.ANSI_RED.escape()+"▄"+Color.RESET);

    public String label;

    ClientPopeFavorState(String type) {
        label = type;
    }
}
