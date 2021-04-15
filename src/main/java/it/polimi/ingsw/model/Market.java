package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.interfaces.Marble;


import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Market {
    private static final Integer NROW=3;
    private static final Integer NCOL=4;

    /**
     * Initializing the marble matrix and the pendingMarble in a defined order
     */
    private Marble[][] marbles={
            {new WhiteMarble(),new WhiteMarble(),new WhiteMarble(),new WhiteMarble()},
            {new ResourceMarble(ResourceType.GREY),new ResourceMarble(ResourceType.GREY),new ResourceMarble(ResourceType.BLUE),new ResourceMarble(ResourceType.BLUE)},
            {new ResourceMarble(ResourceType.VIOLET),new ResourceMarble(ResourceType.VIOLET),new ResourceMarble(ResourceType.YELLOW),new ResourceMarble(ResourceType.YELLOW)},
    };
    private Marble pendingMarble=new RedMarble();

    /**
     * Randomizing the order of the marbles
     */
    protected void initializeMarket(){
        Random ran=new Random();

        for(int r=0;r<NROW;r++){
            for(int c=0;c<NCOL;c++){
                int rowSwap=ran.nextInt(NROW);
                int colSwap=ran.nextInt(NCOL);
                Marble tmp=marbles[r][c];
                marbles[r][c]=marbles[rowSwap][colSwap];
                marbles[rowSwap][colSwap]=tmp;
            }
        }
        int rowSwap=ran.nextInt(NROW);
        int colSwap=ran.nextInt(NCOL);
        Marble tmp=pendingMarble;
        pendingMarble=marbles[rowSwap][colSwap];
        marbles[rowSwap][colSwap]=tmp;
    }

    /**
     * Extract the Resources from the row of the market
     * @param r index of the selected row
     * @param p refercence to the clicking player
     */
    protected void getRow(Integer r,Player p){
        if(r<0 || r>2){
            throw new IndexOutOfBoundsException();
        }
        for(int c=0;c<NCOL;c++){
            marbles[r][c].action(p);
        }
        Marble tmp=marbles[r][0];
        marbles[r][0]=marbles[r][1];
        marbles[r][1]=marbles[r][2];
        marbles[r][2]=marbles[r][3];
        marbles[r][3]=pendingMarble;
        pendingMarble=tmp;
    }
    /**
     * Extract the Resources from the column of the market
     * @param c index of the selected column
     * @param p refercence to the clicking player
     */
    protected void getColumn(Integer c,Player p){
        if(c<0 || c>3){
            throw new IndexOutOfBoundsException();
        }
        for(int r=0;r<NROW;r++){
            marbles[r][c].action(p);
        }
        Marble tmp=marbles[0][c];
        marbles[0][c]=marbles[1][c];
        marbles[1][c]=marbles[2][c];
        marbles[2][c]=pendingMarble;

        pendingMarble=tmp;
    }

    /**
     * @return market structure
     */
    protected Marble[][] getMarbles(){
        return marbles;
    }

    /**
     * @return pending marble attribute
     */
    protected Marble getPendingMarble(){
        return pendingMarble;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Market market = (Market) o;
        for(int r=0;r<NROW;r++){
            for(int c=0;c<NCOL;c++) {
                if(!marbles[r][c].equals(market.marbles[r][c])){
                    return false;
                }
            }
        }
        return pendingMarble.equals(market.pendingMarble);
    }

}
