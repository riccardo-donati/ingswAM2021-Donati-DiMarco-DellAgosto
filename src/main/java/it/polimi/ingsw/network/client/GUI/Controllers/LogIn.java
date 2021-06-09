package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.RegisterResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogIn extends ControllerGUI{

    private boolean connected;
    public LogIn(){
        connected=false;
    }

    List<TextField> campi=new ArrayList<>();
    @FXML private TextField username;
    @FXML private TextField ipAddress;
    @FXML private TextField portNumber;
    @FXML private Label emptyInfo;

    public void userLogIn(ActionEvent event)throws Exception{
        if(!connected) {
            if (username.getText().isEmpty() || ipAddress.getText().isEmpty() || portNumber.getText().isEmpty()) {
                emptyInfo.setText("Empty info, try Again");
            } else {
                boolean response=false;
                try {
                    response=gui.connect(ipAddress.getText(), Integer.parseInt(portNumber.getText()));
                }catch (NumberFormatException e){
                    ComunicationController.showError(gui.getCurrentScene(),"Insert a valid number for the port");
                    return;
                }
                if(response) {
                    ipAddress.setDisable(true);
                    portNumber.setDisable(true);
                    connected = true;
                }
            }
        }else register();
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void unlock(){
        ipAddress.setDisable(false);
        portNumber.setDisable(false);
    }

    public void register(){
        gui.send(new RegisterResponse(username.getText()));
        gui.getClientModel().setNickname(username.getText());
    }

    public void quit(ActionEvent event){
        System.exit(0);
    }

}
