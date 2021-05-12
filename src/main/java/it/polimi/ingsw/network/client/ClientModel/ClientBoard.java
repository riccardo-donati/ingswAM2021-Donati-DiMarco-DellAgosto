package it.polimi.ingsw.network.client.ClientModel;

public class ClientBoard {
    private ClientFaithPath faithPath;
    private ClientDeposits deposits;
    //slots
    //leaders

    public ClientBoard(){
        deposits=new ClientDeposits();
        faithPath=new ClientFaithPath();
    }
    public String toString(){
        String stringBoard= faithPath.toString();
        stringBoard+=deposits.toString();
        return stringBoard;
    }

    public ClientDeposits getDeposits() {
        return deposits;
    }

    public ClientFaithPath getFaithPath() {
        return faithPath;
    }
}
