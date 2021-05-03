package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.exceptions.ReconnectionException;
import it.polimi.ingsw.network.messages.*;

public class ServerVisitorHandler implements ServerVisitor {
    @Override
    public void visit(RegisterResponse res,ClientHandler ch) {
        ch.stopTimer();
        if(ch.isTimeout()) {
            try {
                ch.closeConnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            String nickname = res.getMessage();
            if(nickname.equals("")){
                ch.send(new GenericMessage("Illegal nickname"));
                ch.send(new RegisterRequest());
                return;
            }
            VirtualClient vc = new VirtualClient(nickname, ch);
            try {
                ch.getServer().addVirtualClient(vc);
            }catch (IllegalArgumentException e){
                return;
            } catch (ReconnectionException e) {
                ch.getPinger().start();
                return;
            }
            synchronized (ch.getServer()) {
                if (ch.getServer().getNickLobbyMap().get(vc.getNickname()) == null) {
                    ClientMessage req = new PlayerNumberRequest();
                    ch.getOut().println(ch.getGson().toJson(req, ClientMessage.class));
                    ch.getOut().flush();
                    ch.startTimer(50000);
                    String l = ch.getIn().nextLine();
                    ch.handleMessage(ch.getGson().fromJson(l, Message.class));
                } else ch.getPinger().start();
            }

        }
    }

    @Override
    public void visit(PlayerNumberResponse pnr,ClientHandler ch) {
        ch.stopTimer();
        if(ch.isTimeout()) {
            try {
                ch.closeConnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            int nPlayers = pnr.getNPlayers();
            ch.getServer().createNewLobby(nPlayers, ch);
            ch.getPinger().start();
        }
    }

    @Override
    public void visit(PingResponse pr,ClientHandler ch) {
        System.out.println(pr.getMessage()+" "+ch.getId());
        ch.setPing(true);
    }
}
