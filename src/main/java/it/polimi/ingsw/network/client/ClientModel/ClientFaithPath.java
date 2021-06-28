package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.network.client.CLI.enums.ClientPopeFavorState;
import it.polimi.ingsw.network.client.CLI.enums.Color;
import it.polimi.ingsw.network.client.CLI.enums.Resource;

import java.util.HashMap;
import java.util.Map;

public class ClientFaithPath  {
    private Integer position;
    private Integer lorenzoPosition;
    private Map<Integer, ClientPopeFavorState> popeFavor=new HashMap<>();

    /**
     * initialize pope favor to UNACTIVE and position to 0
     */
    public ClientFaithPath(){
     position=0;
     popeFavor.put(1,ClientPopeFavorState.UNACTIVE);
     popeFavor.put(2,ClientPopeFavorState.UNACTIVE);
     popeFavor.put(3,ClientPopeFavorState.UNACTIVE);
    }

    public void setPopeFavor(Map<Integer, ClientPopeFavorState> popeFavor) {
        this.popeFavor = popeFavor;
    }

    public Integer getLorenzoPosition() {
        return lorenzoPosition;
    }

    public Integer getPosition() {
        return position;
    }

    public Map<Integer, ClientPopeFavorState> getPopeFavor() {
        return popeFavor;
    }

    public void setPopeFavor(int id, boolean activated){
        if(activated){
            popeFavor.replace(id,ClientPopeFavorState.ACTIVE);
        }else{
            popeFavor.replace(id,ClientPopeFavorState.DISCARDED);
        }
    }
    public void addToFaithPath(Integer n){
        position+=n;
        if(position>24) position=24;
    }
    public void setPosition(Integer position){
        this.position=position;
    }
    public void setLorenzoPosition(Integer lorenzoPosition) {
        this.lorenzoPosition = lorenzoPosition;
    }
    public void addLorenzoPosition(Integer n){
        if(lorenzoPosition!=null)
            lorenzoPosition+=n;
    }

    /**
     *
     * @return a readable faith path string
     */
    @Override
    public String toString() {
        StringBuilder b=new StringBuilder();
        b.append(Color.ANSI_PURPLE.escape()).append("FAITH PATH:\n").append(Color.RESET);
        for(int i=0;i<25;i++){
            if(i==5 || i==9 || i==12 || i==16|| i==19)b.append(" ");
            if(i<10) b.append("  ").append(i).append("  ");
            else b.append(" ").append(i).append("  ");
        }
        b.append("\n");
        for(int i=0;i<25;i++){
            if(i==5 || i==12 || i==19) b.append(Color.ANSI_BLUE.escape()).append("┌─────");
            else if(i>5 && i<8)b.append("─────");
            else if(i==8 || i==16 ||i==24)b.append("─────┐");
            else if(i>12 && i<16)b.append("─────");
            else if(i>19)b.append("─────");
            else b.append("     ");
        }
        b.append(Color.RESET+"\n");
        for(int i=0;i<25;i++){
            if(i==5 || i==9 || i==12 || i==17 || i==19)
                b.append(Color.ANSI_BLUE.escape()).append("│").append(Color.RESET);
            if(i%3==0 && i!=0) b.append(Color.ANSI_YELLOW.escape());
            b.append("╔═══╗");
            b.append(Color.RESET);
        }
        b.append(Color.ANSI_BLUE.escape()).append("│").append(Color.RESET);
        b.append("\n");
        for(int i=0;i<25;i++){
            if(i==5 || i==9 || i==12 || i==17||i==19)
                b.append(Color.ANSI_BLUE.escape()).append("│").append(Color.RESET);
            if(i%3==0 && i!=0) {
                b.append(Color.ANSI_YELLOW.escape());
                if (position == i) {
                    if(lorenzoPosition!=null && lorenzoPosition.equals(position))
                        b.append("║").append(Color.ANSI_RED.escape()).append("♰").append(Color.RESET).append(" ♰").append(Color.ANSI_YELLOW.escape()).append("║");
                    else b.append("║ ").append(Color.ANSI_RED.escape()).append("♰").append(Color.ANSI_YELLOW.escape()).append(" ║");
                }else if(lorenzoPosition!=null && lorenzoPosition==i)
                    b.append("║ " + Color.RESET + "♰").append(Color.ANSI_YELLOW.escape()).append(" ║");
                else b.append("║   ║"+Color.RESET);
                b.append(Color.RESET);
            }else{
                if (position == i) {
                    if(lorenzoPosition!=null && lorenzoPosition.equals(position))
                        b.append("║").append(Color.ANSI_RED.escape()).append("♰").append(Color.RESET).append(" ♰").append("║");
                    else b.append("║ ").append(Color.ANSI_RED.escape()).append("♰").append(Color.RESET).append(" ║");
                }else if(lorenzoPosition!=null && lorenzoPosition==i)b.append("║ "  + "♰" + Color.RESET + " ║");
                else b.append("║   ║");//
            }
        }
        b.append(Color.ANSI_BLUE.escape()).append("│").append(Color.RESET);
        b.append("\n");
        for(int i=0;i<25;i++){
            if(i==5 || i==9 || i==12 || i==17|| i==19)
                b.append(Color.ANSI_BLUE.escape()).append("│").append(Color.RESET);
            if(i%3==0 && i!=0) b.append(Color.ANSI_YELLOW.escape());
            b.append("╚═══╝"+Color.RESET);
        }
        b.append(Color.ANSI_BLUE.escape()).append("│").append(Color.RESET);
        b.append("\n");
        b.append(Color.ANSI_YELLOW.escape());
        for(int i=0;i<25;i++){
            if(i==5 || i==9 || i==12 || i==17|| i==19)
                b.append(Color.ANSI_BLUE.escape()).append("│").append(Color.RESET);
            if(i==3) b.append(Color.ANSI_YELLOW.escape()).append("  1  ");
            else if(i==6) b.append(Color.ANSI_YELLOW.escape()).append("  2  ");
            else if(i==9) b.append(Color.ANSI_YELLOW.escape()).append("  4  ");
            else if(i==12) b.append(Color.ANSI_YELLOW.escape()).append("  6  ");
            else if(i==15) b.append(Color.ANSI_YELLOW.escape()).append("  9  ");
            else if(i==18) b.append(Color.ANSI_YELLOW.escape()).append(" 12  ");
            else if(i==21) b.append(Color.ANSI_YELLOW.escape()).append(" 16  ");
            else if(i==24) b.append(Color.ANSI_YELLOW.escape()).append(" 20  ");
            else b.append("     ");
        }
        b.append(Color.ANSI_BLUE.escape()).append("│").append(Color.RESET);
        b.append(Color.RESET+"\n");
        for(int i=0;i<25;i++){
            if(i==5 || i==12 || i==19) b.append(Color.ANSI_BLUE.escape()).append("└─────");
            else if(i>5 && i<8)b.append("─────");
            else if(i==8 || i==16|| i==24)b.append("─────┘");
            else if(i>12 && i<16)b.append("─────");
            else if(i>19)b.append("─────");
            else b.append("     ");
        }
        b.append("\n");
        //popefavor
        for(int i=0;i<25;i++){
            if(i==6||i==13||i==21){
                b.append(Color.ANSI_BLUE.escape()).append(" ╔════════╗ ");
            }else if(i==7 || i==14 || i==22) b.append("");
            else b.append("     ");
        }
        b.append("\n");
        for(int i=0;i<25;i++){
            if(i==6) {
                b.append(Color.ANSI_BLUE.escape()).append(" ║  ").append(Color.ANSI_YELLOW.escape()).append(2).append(Color.ANSI_BLUE.escape()).append("  ").append(popeFavor.get(1).label).append(Color.ANSI_BLUE.escape()).append("  ║ ");
            }else if(i==13) {
                b.append(Color.ANSI_BLUE.escape()).append(" ║  ").append(Color.ANSI_YELLOW.escape()).append(3).append(Color.ANSI_BLUE.escape()).append("  ").append(popeFavor.get(2).label).append(Color.ANSI_BLUE.escape()).append("  ║ ");
            }else if(i==21){
                b.append(Color.ANSI_BLUE.escape()).append(" ║  ").append(Color.ANSI_YELLOW.escape()).append(4).append(Color.ANSI_BLUE.escape()).append("  ").append(popeFavor.get(3).label).append(Color.ANSI_BLUE.escape()).append("  ║ ");
            }else if(i==7 || i==14 || i==22) b.append("");
            else b.append("     ");
        }
        b.append("\n");
        for(int i=0;i<25;i++){
            if(i==6||i==13||i==21){
                b.append(Color.ANSI_BLUE.escape()).append(" ╚════════╝ ");
            }else if(i==7 || i==14 || i==22) b.append("");
            else b.append("     ");
        }
        b.append(Color.RESET+"\n");
        b.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
        return b.toString();
    }

    public static void main(String[] args) {
        ClientFaithPath cfp=new ClientFaithPath();
        cfp.addToFaithPath(8);
        cfp.setPopeFavor(3,false);

        cfp.setLorenzoPosition(1);
        cfp.addLorenzoPosition(7);

        System.out.println(cfp.toString());


        ClientDeposits wh=new ClientDeposits();
        Shelf a=new Shelf(1,1);
        a.put(0,Resource.SERVANT);
        wh.addShelf(a);
        wh.addShelf(new Shelf(2,2));
        Shelf b=new Shelf(3,3);
        b.put(0,Resource.STONE);
        b.put(1,Resource.STONE);
        b.put(2,Resource.STONE);
        wh.addShelf(b);
        wh.addShelf(new Shelf(2,4));
        Shelf s=new Shelf(2,5);
        s.put(1,Resource.COIN);
        wh.addShelf(s);
        wh.putResourceInStrongbox(Resource.COIN);
        wh.putResourceInStrongbox(Resource.COIN);
        wh.putResourceInStrongbox(Resource.SERVANT);
        wh.removeResourceFromStrongbox(Resource.COIN);

        System.out.println(wh.toString());
    }





}
