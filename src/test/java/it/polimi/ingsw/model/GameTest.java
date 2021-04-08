package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game game;

    @Test
    public void TestJSONDevelopmentCard(){
        game=new Singleplayer();
        assertEquals(game.getDevelopmentCards().size(),48);
        for(DevelopmentCard dc : game.getDevelopmentCards()){
            assertNotNull(dc.getColor());
            assertNotNull(dc.getLevel());
            assertNotNull(dc.getPoints());
            assertNotNull(dc.getProd());
            assertNotNull(dc.getCost());
        }
    }
    @Test
    public void TestJSONLeaderCard(){
        game=new Singleplayer();
        assertEquals(game.getLeaderCards().size(),16);
        for(LeaderCard lc : game.getLeaderCards()){
            assertNotNull(lc.getRequirements());
            assertNotNull(lc.getSpecialAbilities());
            assertNotNull(lc.getPoints());
        }
    }

    @Test
    public void TestCardMatrixDimension(){
        game=new Singleplayer();
        Stack<DevelopmentCard>[][] matrix=game.getCardMatrix();
        for(int r=0;r<3;r++){
            for(int c=0;c<4;c++){
                assertEquals(4,matrix[r][c].size());
            }
        }
    }
    @Test
    public void TestCardMatrixRowsColumns(){
        game=new Singleplayer();
        Stack<DevelopmentCard>[][] matrix=game.getCardMatrix();
        for(int r=0;r<3;r++){
            for(int c=0;c<4;c++){
                for(DevelopmentCard dp : matrix[r][c]){
                    assertEquals(r+1,dp.getLevel());
                    assertEquals(c,dp.getColor().ordinal());
                }
            }
        }
    }


}