package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.interfaces.BoardObserver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public abstract class Game implements BoardObserver {
    private static final Integer ROW=3;
    private static final Integer COL=4;

    private Market market;
    private Stack<DevelopmentCard>[][] cardMatrix;
    private List<DevelopmentCard> developmentCards;
    private List<LeaderCard> leaderCards;
    private List<Player> players;
    private Player currPlayer;
    private String firstPlayer;

    /**
     * get of developmentCards
     * @return
     */
    public List<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * get of cardMatrix
     * @return
     */
    public Stack<DevelopmentCard>[][] getCardMatrix() {
        return cardMatrix;
    }

    /**
     * Constructor of the class Game where we initialize all the attributes
     */
    public Game()  {
        //initialization of the developmentCards from JSON
        developmentCards=new ArrayList<>();
        loadDevelopmentCardsFromJSON();
        //initialization of the leaderCards from JSON
        //TO DO!
        market=new Market();
        market.initializeMarket();
        initializeCardMatrix();
        this.players=new ArrayList<>();
    }

    /**
     * Using GSON we initialize the developmentCard list
     */
    public void loadDevelopmentCardsFromJSON() {
        Gson gson=new Gson();
        Type foundListType=new TypeToken<ArrayList<DevelopmentCard>>(){}.getType();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("src/main/java/it/polimi/ingsw/model/json/developmentCard.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("DevelopmentCard.json not found");
        }
        developmentCards=gson.fromJson(reader,foundListType);

    }

    /**
     * Random push in the stacks of the cardMatrix for each DevelopmentCard
     * ROW 0: level 1 | ROW 1: level 2 | ROW 2: level 3
     * COL 0: GREEN | COL 1: BLUE | COL 2: YELLOW | COL 3: VIOLET
     */
    public void initializeCardMatrix(){
        cardMatrix=new Stack[ROW][COL];
        for(int r=0;r<ROW;r++){
            for(int c=0;c<COL;c++){
                cardMatrix[r][c]=new Stack<>();
            }
        }
        List<DevelopmentCard> copy=new ArrayList<>(developmentCards);
        Random rn=new Random();
        for(int i=0;i<developmentCards.size();i++){
            int n=rn.nextInt(copy.size());
            DevelopmentCard dc=copy.get(n);
            cardMatrix[dc.getLevel()-1][dc.getColor().ordinal()].push(dc);
            copy.remove(dc);
        }
    }
    @Override
    public void updateEndGame() {

    }

    @Override
    public void updatePopeFavor() {

    }

    @Override
    public void updateDiscard() {

    }


}
