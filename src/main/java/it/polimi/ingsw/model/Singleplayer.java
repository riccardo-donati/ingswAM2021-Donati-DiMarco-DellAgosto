package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.PopeFavorState;
import it.polimi.ingsw.model.exceptions.FullGameException;
import it.polimi.ingsw.model.interfaces.Token;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

public class Singleplayer extends Game {
    private Stack<Token> tokenStack;
    private List<Token> tokens;
    private FaithPath blackFaithPath;

    public Singleplayer()  {
        super();
        loadTokensFromJSON();
        tokenShuffle();
        blackFaithPath = new FaithPath();
        blackFaithPath.addObserver(this);
    }

    public void loadTokensFromJSON(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Token.class, new InterfaceAdapter<Token>());
        Gson gson = builder.create();

        Type foundListType = new TypeToken<ArrayList<Token>>(){}.getType();

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("src/main/java/it/polimi/ingsw/model/json/token.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("token.json not found");
        }
        tokens=gson.fromJson(reader,foundListType);
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
    @Override
    public void updateDiscard(Warehouse wh) {
        super.updateDiscard(wh);
        blackFaithPath.addToPosition(1);
    }
    @Override
    public FaithPath getBlackCrossFaithPath(){return blackFaithPath;}


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
    public void nextTurn(){
        //controllo se non ci sono risorse in pending/picked up????
        Token t=tokenStack.pop();
        t.doAction(this);
    }
    /**
     * discard the top card of the passed color from the matrix (starting with row 0->1->2)
     * if the 3 stacks are empty -> trigger ENDGAME
     * @param toDiscard is the Color of the card we want to discard
     */
    @Override
    public void discardColor(Color toDiscard){
        int col=toDiscard.ordinal();
        int r=0;
        DevelopmentCard dc;
        try {
            dc = getCardMatrix()[r][col].pop();
        }catch (EmptyStackException e1){
            r++;
            try {
                dc = getCardMatrix()[r][col].pop();
            }catch (EmptyStackException e2){
                r++;
                try {
                    dc = getCardMatrix()[r][col].pop();
                    if(getCardMatrix()[r][col].size()==0){
                        endGame();
                    }

                }catch (EmptyStackException e3){
                    e3.printStackTrace();
                }
            }
        }
    }

    @Override
    public Result endGame(){
        boolean lose=false;
        int cont=0;
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
            result.setWinner("Lorenzo");
        }else{
            result.setWinner(result.checkWinner());
        }
        return result;
    }

}