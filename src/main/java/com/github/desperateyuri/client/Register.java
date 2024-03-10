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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Register {
    @FXML
    private TextField nameField, passwordField;
    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private final FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));

    public void back(ActionEvent e) throws IOException {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
    public void confirm(ActionEvent e) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {  // Need to get a new ID from server, or just throw exceptions.
        String name = nameField.getText();
        String password = passwordField.getText();

        if(name.length() >=15 || password.length() >= 20){
            alert.titleProperty().set("Exceeds");
            alert.headerTextProperty().set("Your name or password exceeds the limit of the length");
            alert.showAndWait();
            nameField.clear();
            passwordField.clear();
            return;
        }

        if(checkIllegalOrNot.name_illegalOrNot(name)){
            alert.titleProperty().set("Illegal");
            alert.headerTextProperty().set("Your name appears to contain illegal characters");
            alert.showAndWait();
            nameField.clear();
            passwordField.clear();
            return;
        }

        HashMap hashMap = new HashMap<String, Object>();
        hashMap.put("name", name);
        hashMap.put("password", password);
        clientMessage clientmsg = new clientMessage(clientMessage.Command.REGISTER, hashMap);

        Connection connection = new Connection(5000);

        ObjectMapper objectMapper = new ObjectMapper();
        connection.write(objectMapper.writeValueAsString(clientmsg));
        serverMessage servermsg = objectMapper.readValue(connection.read(), serverMessage.class);
        if(servermsg.command() == serverMessage.Command.REGISTER && servermsg.status() == serverMessage.Status.OK){
            alert.titleProperty().set("Success");
            alert.headerTextProperty().set("You've already register successfully, and your ID is: " + servermsg.map().get("ID"));
            alert.showAndWait();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Parent root = fxmlLoader.load();
            LogIn logIn = fxmlLoader.getController();
            logIn.setIDField((String) servermsg.map().get("ID"));
            Scene scene = new Scene(root);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

        }else if(servermsg.command() == serverMessage.Command.REGISTER && servermsg.status() == serverMessage.Status.ERROR){
            alert.titleProperty().set("Incident");
            alert.headerTextProperty().set("Register error, please try again.");
            alert.showAndWait();
        }
        connection.disconnect();
    }
}
