package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.enums.PopeFavorState;
import it.polimi.ingsw.model.interfaces.Token;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

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
}