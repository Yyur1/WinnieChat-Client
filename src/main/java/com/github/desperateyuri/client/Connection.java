package com.github.desperateyuri.client;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Connection {
    private Socket s;
    private BufferedReader reader;
    private BufferedWriter writer;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private byte[] sessionKey;
    public Connection(int port) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException {
        s = new Socket("127.0.0.1", port);
        reader = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
        dataInput = new DataInputStream(s.getInputStream());
        dataOutput = new DataOutputStream(s.getOutputStream());
        sessionKey = Crypt.keyExchange(dataInput, dataOutput);
    }

    public void write(String msg) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        writer.write(Crypt.EncryptAES(msg, sessionKey) + "\n");
        writer.flush();
    }

    public String read() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return Crypt.DecryptAES(reader.readLine(), sessionKey);
    }

    public void disconnect() throws IOException {
        s.close();
    }
}
