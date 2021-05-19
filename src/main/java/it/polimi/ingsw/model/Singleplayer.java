package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.Token;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

public class Singleplayer extends Game {
    private Stack<Token> tokenStack;
    private List<Token> tokens;
    private FaithPath blackFaithPath;
    private Token lastUsedToken;

    protected Singleplayer()  {
        super();
        loadTokensFromJSON();
        tokenShuffle();
        blackFaithPath = new FaithPath();
        blackFaithPath.addObserver(this);
    }

    @Override
    public Stack<Token> getTokenStack() { return tokenStack; }

    public void loadTokensFromJSON(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Token.class, new InterfaceAdapter<Token>());
        Gson gson = builder.create();

        Type foundListType = new TypeToken<ArrayList<Token>>(){}.getType();

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("src/main/resources/json/token.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("token.json not found");
        }
        tokens=gson.fromJson(reader,foundListType);
    }

    /**
     * for testing purposes
     */
    @Override
    public void orderTokenStack() {
        this.tokenStack = new Stack<>() ;
        for(Token t : tokens)
            tokenStack.push(t);
    }


    @Override
    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public void pushBlackCross(Integer push){
        blackFaithPath.addToPosition(push);
    }

    @Override
    public void tokenShuffle(){
        List<Token> temp = new ArrayList<>(tokens);
        tokenStack = new Stack<>();
        tokenStack.addAll(temp);
        Collections.shuffle(tokenStack);
    }

    @Override
    public void updatePopeFavor(){
        super.updatePopeFavor();
        for (PopeFavor pf: blackFaithPath.getPopeFavorList()) {
            if(pf.getState().equals(PopeFavorState.UNACTIVE)){
                if(pf.checkInside(blackFaithPath.getPosition())){
                    pf.changeState(PopeFavorState.ACTIVE);
                }
                else pf.changeState(PopeFavorState.DISCARDED);
                break;
            }

        }
    }

    /**
     * when a resource is discarded, increase the black cross's position by 1
     * @param wh player's warehouse
     */
    @Override
    public void updateDiscard(Warehouse wh) {
        super.updateDiscard(wh);
        blackFaithPath.addToPosition(1);
    }
    /**
     *
     * @return position of the black cross
     */
    @Override
    protected FaithPath getBlackCrossFaithPath(){return blackFaithPath;}

    /**
     * before applying the super method check if the size of players is 1
     * @param nick of the player
     * @throws FullGameException if there is already a player
     */
    @Override
    public void addPlayer(String nick) throws FullGameException {
        if(getPlayers().size()==1){
            throw new FullGameException();
        }
        super.addPlayer(nick);
    }

    /**
     * draw a token from the stack and execute the related action
     */
    @Override
    protected void nextTurn(){
        lastUsedToken=tokenStack.pop();
        lastUsedToken.doAction(this);
    }


    /**
     * discard the top card of the passed color from the matrix (starting with row 0->1->2)
     * if the 3 stacks are empty -> trigger ENDGAME
     * @param toDiscard is the Color of the card we want to discard
     */
    @Override
    protected void discardColor(Color toDiscard){
        int col=toDiscard.ordinal();
        int r=0;
        try {
            getCardMatrix()[r][col].pop();
        }catch (EmptyStackException e1){
            r++;
            try {
                getCardMatrix()[r][col].pop();
            }catch (EmptyStackException e2){
                r++;
                try {
                    getCardMatrix()[r][col].pop();
                    if(getCardMatrix()[r][col].size()==0){
                        updateEndGame();
                    }
                }catch (EmptyStackException e3){

                }
            }
        }
    }

    //----------------PublicInterface----------------------------------------------------------------------

    /**
     *
     * @return game result
     */
    @Override
    public Result endGame(){
        boolean lose=false;
        int cont;
        for(int c=0;c<COL;c++){
            cont=0;
            for(int r=0;r<ROW;r++){
                if(getCardMatrix()[r][c].size()==0){
                    cont++;
                }
            }
            if(cont==3){
                lose=true;
                break;
            }
        }
        if(blackFaithPath.getPosition()==24){
            lose=true;
        }
        Result result=super.endGame();
        if(lose){
            List<String> winner = new ArrayList<>();
            winner.add("Lorenzo");
            result.setWinner(winner);
        }else{
            result.setWinner(result.checkWinner());
        }
        return result;
    }
    @Override
    public Token getLastUsedToken(){
        return lastUsedToken;
    }
    @Override
    public void passTurn() throws IllegalActionException {
        super.passTurn();
        if(getGamePhase()==GamePhase.ONGOING) {
            if (isEndGameTrigger()) {
                setGamePhase(GamePhase.ENDGAME);
                endGame();
            } else {
                setTurnPhase(TurnPhase.STARTTURN);
            }
        }
    }
    @Override
    public void buyCard(Integer row,Integer col,Integer slot) throws IllegalActionException, ResourcesNotAvailableException, TooManyResourcesException, IllegalSlotException {
        super.buyCard(row,col,slot);
        //if the player buy the last card of 1 column -> insta lose
        //if the player buy the 7th card -> insta win
        if(getCardMatrix()[row][col].size()==0 && row==ROW-1 || isEndGameTrigger()){
            updateEndGame();
            setGamePhase(GamePhase.ENDGAME);
            endGame();
        }

    }
    @Override
    public void activateProductions() throws IllegalActionException, IllegalResourceException, ResourcesNotAvailableException, TooManyResourcesException, UnknownFoundException {
        super.activateProductions();
        //insta win if the player reach the last cell of the faithpath
        if(isEndGameTrigger()){
            updateEndGame();
            setGamePhase(GamePhase.ENDGAME);
            endGame();
        }
    }
    @Override
    public void buyAtMarketInterface(char rc,int index) throws IllegalActionException {
        super.buyAtMarketInterface(rc,index);
        if(isEndGameTrigger()){
            updateEndGame();
            setGamePhase(GamePhase.ENDGAME);
            endGame();
        }
    }
    @Override
    public void discardResource(ResourceType res) throws IllegalActionException, DepositableResourceException, IllegalResourceException {
        super.discardResource(res);
        if(isEndGameTrigger()){
            updateEndGame();
            setGamePhase(GamePhase.ENDGAME);
            endGame();
        }
    }
}