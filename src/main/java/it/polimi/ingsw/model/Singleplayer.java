package it.polimi.ingsw.model;

import it.polimi.ingsw.model.interfaces.Token;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Stack;

public class Singleplayer extends Game {
    private Stack<Token> tokenStack;
    private List<Token> tokens;
    private Integer blackCross;

    public Singleplayer()  {
        super();
        //initialization of tokens from JSON
        blackCross=0;
    }

}
