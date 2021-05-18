package it.polimi.ingsw.network.client.ClientModel;

import java.util.*;

import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;

public class ClientCardMatrix {
    protected Stack<DevelopmentCard>[][] cards;
    private Integer discardedCards;

    public Integer getDiscardedCards() {
        return discardedCards;
    }

    /**
     * class constructor, creates empty matrix for development cards
     */
    public ClientCardMatrix(){
        cards = new Stack[3][4];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                cards[r][c] = new Stack<>();
            }
        }
        discardedCards=0;
    }

    public Stack<DevelopmentCard>[][] getCards() {
        return cards;
    }

    public void setCards(Stack<DevelopmentCard>[][] cards) {
        this.cards = cards;
    }

    /**
     * pops a development card from the specified stack
     * @param row - stack's row
     * @param col - stack's column
     * @return development card on top of the stack
     */
    public DevelopmentCard popCard(int row, int col){
        return cards[row][col].pop();
    }

    /**
     * adds a development card to the specified stack
     * @param row - stack's row
     * @param column - stack's column
     * @param developmentCard - development card to be added
     */
    public void pushCard(int row, int column, DevelopmentCard developmentCard){
        cards[row][column].push(developmentCard);
    }

    /**
     * transforms the cards on top of the stack in strings using the stringify method in Utilities and appends them together
     * @return the card matrix as a string
     */
    @Override
    public String toString() {
        Color color;
        StringBuilder sb = new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape()).append("\nPRODUCTION CARDS: \n").append(Color.RESET);
        for (int column=0; column <=3; column++) {
            if(column == 0) color = Color.ANSI_GREEN;
            else if (column == 1) color = Color.ANSI_BLUE;
            else if (column == 2) color = Color.ANSI_YELLOW;
            else color = Color.ANSI_PURPLE;
            sb.append(color.escape()).append("══════════════════════════════════════════════════════════════════════════════════════════════════════\n").append(Color.RESET);
            for (int level=0; level<=2; level++) {
                if (cards[level][column].size() != 0) sb.append("  ").append(Utilities.stringify(cards[level][column].peek())).append("\n");
            }
            sb.append(color.escape()).append("══════════════════════════════════════════════════════════════════════════════════════════════════════\n").append(Color.RESET);
        }
        return sb.toString();
    }

    public void discard(int column){
        for (int r=0; r < 3; r++) {
            if (cards[r][column].size() > 0) {
                cards[r][column].pop();
                discardedCards++;
                return;
            }
        }
    }

//    public static void main(String[] args) {
//        List<DevelopmentCard> list=Utilities.loadDevelopmentCardsFromJSON();
//        ClientCardMatrix ccd = new ClientCardMatrix();
//        ccd.pushCard(1, 1, list.get(2));
//    }
}



