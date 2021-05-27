package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.RegisterResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LogIn extends ControllerGUI{


    public LogIn(){
    }

    @FXML private TextField username;
    @FXML private TextField ipAddress;
    @FXML private TextField portNumber;
    @FXML private Label emptyInfo;

    public void userLogIn(ActionEvent event)throws Exception{
        if(username.getText().isEmpty() || ipAddress.getText().isEmpty() || portNumber.getText().isEmpty()){
            emptyInfo.setText("Empty info, try Again");
        }
        else{
            gui.connect(ipAddress.getText(),Integer.parseInt(portNumber.getText()));
        }
    }
    public void register(){
        gui.send(new RegisterResponse(username.getText()));
    }
    public void quit(ActionEvent event){
        System.exit(0);
    }

}
