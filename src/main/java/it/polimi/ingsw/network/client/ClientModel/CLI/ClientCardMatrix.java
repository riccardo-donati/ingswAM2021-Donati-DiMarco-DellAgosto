package it.polimi.ingsw.network.client.ClientModel.CLI;

import java.util.*;

import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;

public class ClientCardMatrix {
    protected Stack<DevelopmentCard>[][] dCard;
    Color c;

    public ClientCardMatrix(){
        Stack<DevelopmentCard>[][] newCardMatrix=new Stack[3][4];
        for(int r = 0; r < 3; r++){
            for(int c = 0; c < 4; c++){
                newCardMatrix[r][c]=new Stack<>();
            }
        }
        dCard = newCardMatrix;
    }

    public void push(int row, int column, DevelopmentCard developmentCard){
        dCard[row][column].push(developmentCard);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape()+"\nCARD MARKET: \n"+Color.RESET);
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

}



