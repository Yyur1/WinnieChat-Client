package com.github.desperateyuri.client;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Crypt {
    public static byte[] keyExchange(DataInputStream datainput, DataOutputStream dataoutput) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        SecureRandom secureRandom = new SecureRandom();
        keyPairGenerator.initialize(256, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        byte[] clientPublicKey = keyPair.getPublic().getEncoded();
        byte[] exchange = datainput.readNBytes(91);  // Read first!
        dataoutput.write(clientPublicKey);
        dataoutput.flush();
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec x = new X509EncodedKeySpec(exchange);
        PublicKey serverPublicKey = keyFactory.generatePublic(x);
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(serverPublicKey, true);
        byte[] sessionKey = keyAgreement.generateSecret();
        return sessionKey;
    }
    public static String EncryptAES(String text, byte[] sessionKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = new SecretKeySpec(sessionKey, "AES");
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] KEY_IV = "c558Gq0YQK2QUlMc".getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new javax.crypto.spec.IvParameterSpec(KEY_IV));
        byte[] ciphertext = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(ciphertext);
    }
    public static String DecryptAES(String text, byte[] sessionKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = new SecretKeySpec(sessionKey, "AES");
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] KEY_IV = "c558Gq0YQK2QUlMc".getBytes();
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new javax.crypto.spec.IvParameterSpec(KEY_IV));
        byte[] temp = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(temp, StandardCharsets.UTF_8);
    }
}