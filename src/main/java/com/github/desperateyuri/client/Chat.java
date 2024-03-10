package com.github.desperateyuri.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Chat {
    @FXML
    private TextField sendField, ipField, portField;
    @FXML
    private TextArea textCache;
    @FXML
    private Label label;
    private byte[] sessionKey = null;
    private Socket s = null;
    private BufferedWriter writer = null;
    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private String name = null, id = null;
    public void displayName(){ label.setText(this.name + "!"); }
    public void setName(String name) {this.name = name; }
    public void setID(String id){ this.id = id; }
    public void enter(ActionEvent event) throws Exception {
        if(s != null && s.isConnected()){
            s.close();
        }
        String ip = ipField.getText();
        String port = portField.getText();
        alert.titleProperty().set("Attention");
        alert.headerTextProperty().set("Connection failed, please try again.");
        if (!Judge.judgeIP(ip)) { ip = "127.0.0.1";}
        if (!Judge.judgePort(port)) { port = "8964";}
        try{
            s = new Socket(ip, Integer.parseInt(port)); // Already connected to the server
        }catch (Exception exception){
            alert.showAndWait();
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
        DataInputStream dataInput = new DataInputStream(s.getInputStream());
        DataOutputStream dataOutput = new DataOutputStream(s.getOutputStream());

        if(sessionKey == null){
            sessionKey = Crypt.keyExchange(dataInput,dataOutput);
        }
        System.out.println("Already get SessionKey");

        writer.write(Crypt.EncryptAES(id, sessionKey) + "\n");
        System.out.println("IDCiphertext:" + Crypt.EncryptAES(id, sessionKey));
        writer.write(Crypt.EncryptAES(name, sessionKey) + "\n");
        writer.flush();
        System.out.println("NameCiphertext:" + Crypt.EncryptAES(name, sessionKey));

        Thread t = new handler(reader, textCache, sessionKey, alert);
        t.start();
        alert.titleProperty().set("Success");
        alert.headerTextProperty().set("IP Address is " + ip + ", port is " + port + ".");
        alert.showAndWait();
    }

    public void send(ActionEvent event) {
        String msg = sendField.getText();
        sendField.clear();
        alert.titleProperty().set("Attention");
        if(s == null || !s.isConnected()){
            alert.headerTextProperty().set("Socket isn't connected, please reconnect again.");
            alert.showAndWait();
            return;
        }
        if (msg.length() == 0) {
            alert.headerTextProperty().set("You cannot send the empty text.");
            alert.showAndWait();
            return;
        }
        try{
            System.out.print(msg);
            writer.write(Crypt.EncryptAES(msg, sessionKey) + "\n");
            writer.flush();
        }catch (Exception exception){
            alert.headerTextProperty().set("Socket is dead while sending, please reconnect again.");
            alert.showAndWait();
        }
    }
    public void keyboardEnter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            ActionEvent e = new ActionEvent();
            send(e);
        }
    }
}
class handler extends Thread{
    private final TextArea textCache;
    private final BufferedReader reader;
    private final byte[] sessionKey;
    private final Alert alert;
    public handler(BufferedReader reader, TextArea textCache, byte[] sessionKey, Alert alert){
        this.reader = reader;
        this.textCache = textCache;
        this.sessionKey = sessionKey;
        this.alert = alert;
        this.alert.titleProperty().set("Attention");
    }
    public void run(){
        while(true){
            try{
                String ciphertext = reader.readLine();
                textCache.appendText(Crypt.DecryptAES(ciphertext, sessionKey));
            }catch (Exception exception){
                alert.headerTextProperty().set("Socket is dead while receiving message, please reconnect again.");
                break;
            }
        }
    }
}