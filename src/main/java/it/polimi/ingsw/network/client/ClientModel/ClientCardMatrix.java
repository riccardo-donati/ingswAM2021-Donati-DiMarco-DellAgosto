package it.polimi.ingsw.network.client.ClientModel;

import java.util.*;

import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;

public class ClientCardMatrix {
    protected Stack<DevelopmentCard>[][] dCard;
    Color c;

    public ClientCardMatrix(){
        dCard=new Stack[3][4];
        for(int r = 0; r < 3; r++){
            for(int c = 0; c < 4; c++){
                dCard[r][c]=new Stack<>();
            }
        }
    }

    public Stack<DevelopmentCard>[][] getdCard() {
        return dCard;
    }

    public DevelopmentCard pop(int row, int col){
        return dCard[row][col].pop();
    }
    public void push(int row, int column, DevelopmentCard developmentCard){
        dCard[row][column].push(developmentCard);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape()+"\nPRODUCTION CARDS: \n"+Color.RESET);
        for(int column=0; column <=3; column++){
            if(column == 0) c = Color.ANSI_GREEN;
            else if (column == 1) c = Color.ANSI_BLUE;
            else if (column == 2) c = Color.ANSI_YELLOW;
            else c = Color.ANSI_PURPLE;
            sb.append(c.escape()+"══════════════════════════════════════════════════════════════════════════════════════════════════════\n"+Color.RESET);
            for(int level=0; level<=2; level++){
                if(dCard[level][column].size() != 0) sb.append("  "+Utilities.stringify(dCard[level][column].peek())+"\n");
            }
            sb.append(c.escape()+"══════════════════════════════════════════════════════════════════════════════════════════════════════\n"+Color.RESET);
        }
        return sb.toString();
    }

    public void setDCard(Stack<DevelopmentCard>[][] dCard) {
        this.dCard = dCard;
    }

    public static void main(String[] args) {
        List<DevelopmentCard> list=Utilities.loadDevelopmentCardsFromJSON();
        ClientCardMatrix ccd=new ClientCardMatrix();
        ccd.push(1,1,list.get(2));

    }
}



