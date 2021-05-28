package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.CLI.enums.Color;

import java.util.ArrayList;
import java.util.List;

public class ClientMarket {
    List<ResourceType> marbles = new ArrayList<>();

    public void setMarbles(List<ResourceType> marbles) {
        this.marbles = marbles;
    }

    /**
     * given the position of a marble in the marbles array, returns its visual representation
     * @param pos - position of the marble in the marbles array
     * @return representation of the specified marble
     */
    private String getMarbleType(int pos){
        if(marbles.get(pos) == ResourceType.YELLOW) return Color.ANSI_YELLOW.escape()+"●"+Color.RESET;
        if(marbles.get(pos) == ResourceType.VIOLET) return Color.ANSI_PURPLE.escape()+"●"+Color.RESET;
        if(marbles.get(pos) == ResourceType.BLUE) return Color.ANSI_BLUE.escape()+"●"+Color.RESET;
        if(marbles.get(pos) == ResourceType.GREY) return "○";
        if(marbles.get(pos) == ResourceType.RED) return Color.ANSI_RED.escape()+"●"+ Color.RESET;
        else return "●";
    }

    public List<ResourceType> getMarbles() {
        return marbles;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape() + "MARKET:\n" + Color.RESET);
        int n=0;

        sb.append("     1   2   3   4\n");
        sb.append("   ╔═══╦═══╦═══╦═══╗\n");
        for (int i = 1; i <= 17; i++) {
            if (i == 5 || i == 9 || i == 13) sb.append("│");
            else if (i == 1 || i == 17) {
                if (i == 1) sb.append(" 1 ║");
                else sb.append("║ ←");
            }
            else if (i % 2 == 0) sb.append(" ");
            else {
                //sb.append("●");
                sb.append(getMarbleType(n));
                n++;
            }
        }
        sb.append("\n   ╠───╬───╬───╬───╣\n");
        for (int i = 1; i <= 17; i++) {
            if (i == 5 || i == 9 || i == 13) sb.append("│");
            else if (i == 1 || i == 17) {
                if (i == 1) sb.append(" 2 ║");
                else sb.append("║ ←");
            }
            else if (i % 2 == 0) sb.append(" ");
            else {
                //sb.append("●");
                sb.append(getMarbleType(n));
                n++;
            }
        }
        sb.append("\n   ╠───╬───╬───╬───╣\n");
        for (int i = 1; i <= 17; i++) {
            if (i == 5 || i == 9 || i == 13) sb.append("│");
            else if (i == 1 || i == 17) {
                if (i == 1) sb.append(" 3 ║");
                else sb.append("║ ←");
            }
            else if (i % 2 == 0) sb.append(" ");
            else {
                //sb.append("●");
                sb.append(getMarbleType(n));
                n++;
            }
        }
        sb.append("\n   ╚═══╩═══╩═══╩═══╝\n");
        sb.append("     ↑   ↑   ↑   ↑   "/* + getMarbleType(marbles, n)*/); //
        sb.append("\n   Pending Marble: " + getMarbleType(n));
        sb.append("\n");
        return sb.toString();
    }

    public void initializeMarbles(){
        marbles.add(ResourceType.YELLOW);
        marbles.add(ResourceType.VIOLET);
        marbles.add(ResourceType.EMPTY);
        marbles.add(ResourceType.VIOLET);
        marbles.add(ResourceType.GREY);
        marbles.add(ResourceType.RED);
        marbles.add(ResourceType.BLUE);
        marbles.add(ResourceType.YELLOW);
        marbles.add(ResourceType.GREY);
        marbles.add(ResourceType.EMPTY);
        marbles.add(ResourceType.BLUE);
        marbles.add(ResourceType.EMPTY);
        marbles.add(ResourceType.EMPTY);
    }
/*
    public static void main(String[] args) {
        ClientMarket cm = new ClientMarket();
        cm.initializeMarbles();
        System.out.println(cm.toString());
    }

 */
}
