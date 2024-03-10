package com.github.desperateyuri.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LogIn {
    @FXML
    private TextField IDField, passwordField;
    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);
    public void setIDField (String ID){IDField.setText(ID);}

    public void register(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("register.fxml"));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
    private void forward_Chat(serverMessage servermsg, ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("client.fxml"));
        Parent root = fxmlLoader.load();
        Chat chat = fxmlLoader.getController();
        chat.setID(IDField.getText());
        chat.setName((String)servermsg.map().get("Name"));
        chat.displayName();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
    }
    public void login(ActionEvent e) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // You can log the sessionKey in the server and client, it can reduce the time of producing sessionKey

        Connection connection = new Connection(5000);

        ObjectMapper objectMapper = new ObjectMapper();

        if(checkIllegalOrNot.id_illegalOrNot(IDField.getText())){
            alert.titleProperty().set("Illegal");
            alert.headerTextProperty().set("Your ID appears to contain illegal characters");
            alert.showAndWait();
            IDField.clear();
            passwordField.clear();
            return;
        }

        Map map = new HashMap<String, Object>();
        map.put("ID", IDField.getText());
        map.put("Password", passwordField.getText());
        clientMessage clientmsg = new clientMessage(clientMessage.Command.LOGIN, map);
        connection.write(objectMapper.writeValueAsString(clientmsg));

        // This servermsg will also bring the name of this ID
        serverMessage servermsg = objectMapper.readValue(connection.read(), serverMessage.class);

        if(servermsg.command() == serverMessage.Command.LOGIN){
            connection.disconnect();
            if(servermsg.status() == serverMessage.Status.OK){
                forward_Chat(servermsg, e);
            }else if(servermsg.status() == serverMessage.Status.ERROR){
                alert.titleProperty().set("Login failed");
                alert.headerTextProperty().set("Password isn't correct, or the ID doesn't exist.");
                alert.showAndWait();
                IDField.clear();
            }
        }
    }
}
