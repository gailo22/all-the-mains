package com.gailo22;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;

public class Main94 {

    private static final int iterationCount = 2;

    public static void main(String[] args) {
        try {
            String secret = "rXxUPqj7SryXYxKsZDDiV3c27qw3kW"; // client_secret
//			String encString = encrypt("21988154,tmn.10001767921".getBytes(), secret);
            String encString = encrypt("21988154,tmn.22222".getBytes(), secret);
            System.out.println(encString);
//			byte[] decString1 = decrypt("cf2+Z7MxVM+8E0EX5alNL6IWc8qmBS1iowYnse4PdYST57bTim/RhYUTEGiCfbHjyoROcrHGCZ7cTBOnCDaLNWkPLX8=", "password");
//			System.out.println(String.join("decString1: ", new String(decString1)));
            byte[] decString2 = decrypt(encString, secret);
            System.out.println(String.join("decString1: ", new String(decString2)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String encrypt(byte[] textBytes, String password) throws Exception {

        byte[] ivBytes;

        /*you can give whatever you want for password. This is for testing purpose*/
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        byte[] saltBytes = bytes;

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterationCount, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        //encrypting the word
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);

        AlgorithmParameters params = cipher.getParameters();
        ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(textBytes);

        //prepend salt and vi
        byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];
        System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
        System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
        System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);
        return new Base64().encodeToString(buffer);

    }

    public static byte[] decrypt(String encryptedText, String password) throws Exception {

        if (encryptedText == null) {
            return null;
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // strip off the salt and iv
        ByteBuffer buffer = ByteBuffer.wrap(new Base64().decode(encryptedText));
        byte[] saltBytes = new byte[20];
        buffer.get(saltBytes, 0, saltBytes.length);
        byte[] ivBytes1 = new byte[cipher.getBlockSize()];
        buffer.get(ivBytes1, 0, ivBytes1.length);
        byte[] encryptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes1.length];

        buffer.get(encryptedTextBytes);

        // Deriving the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterationCount, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes1));
        byte[] decryptedTextBytes = null;

        decryptedTextBytes = cipher.doFinal(encryptedTextBytes);

        return decryptedTextBytes;
    }
}

