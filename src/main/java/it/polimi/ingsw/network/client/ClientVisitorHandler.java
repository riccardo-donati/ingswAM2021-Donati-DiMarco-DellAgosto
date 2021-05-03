package it.polimi.ingsw.network.client;



import it.polimi.ingsw.network.messages.*;

import java.io.IOException;

public class ClientVisitorHandler implements ClientVisitor{
    @Override
    public void visit(DisconnectionMessage dm, Client cl) {
        System.out.println(dm.getMessage());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cl.getIn().close();
        cl.getOut().close();
        try {
            cl.getEchoSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void visit(GenericMessage dm, Client cl) {
        System.out.println(dm.getMessage());
    }

    @Override
    public void visit(LobbyInfoMessage lim, Client cl) {
        for(String n : lim.getNickList()){
            System.out.println("LobbyPlayer: "+n);
        }
    }

    @Override
    public void visit(PingRequest pr, Client cl) {
        Message pResp=new PingResponse();
        //System.out.println(m.getMessage());
        cl.getOut().println(cl.getGson().toJson(pResp,Message.class));
        cl.getOut().flush();
    }

    @Override
    public void visit(PlayerNumberRequest pnr, Client cl) {
        System.out.println(pnr.getMessage());
        int n= 0;
        try {
            n = Integer.parseInt(cl.getStdIn().readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message m2=new PlayerNumberResponse(n);
        cl.getOut().println(cl.getGson().toJson(m2,Message.class));
    }

    @Override
    public void visit(RegisterRequest rr, Client cl) {
        System.out.println(rr.getMessage());
        String userInput = null;
        try {
            userInput = cl.getStdIn().readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message a = new RegisterResponse(userInput);
        cl.getOut().println(cl.getGson().toJson(a, Message.class));
    }
}

