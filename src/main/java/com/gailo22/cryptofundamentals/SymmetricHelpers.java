package com.gailo22.cryptofundamentals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SymmetricHelpers {

    static {
        UseBouncyCastle.please();
    }

    public static SecretKey generateAesKey() throws Exception {
        var keyGenerator = KeyGenerator.getInstance("AES", "BC");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static IvParameterSpec generateInitializationVector() {
        var random = new SecureRandom();
        var buffer = new byte[16];
        random.nextBytes(buffer);
        return new IvParameterSpec(buffer);
    }

    public static byte[] encryptWithAes(String message, SecretKey key, IvParameterSpec iv) throws Exception {
        var out = new ByteArrayOutputStream();
        var aes = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        aes.init(Cipher.ENCRYPT_MODE, key, iv);
        var cipherOut = new CipherOutputStream(out, aes);
        var writer = new OutputStreamWriter(cipherOut);

        try {
            writer.write(message);
        }
        finally {
            writer.close();
        }

        return out.toByteArray();
    }

    public static String decryptWithAes(byte[] cipertext, SecretKey key, IvParameterSpec iv) throws Exception {
        var in = new ByteArrayInputStream(cipertext);
        var aes = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        aes.init(Cipher.DECRYPT_MODE, key, iv);
        var cipherIn = new CipherInputStream(in, aes);
        var reader = new InputStreamReader(cipherIn);
        var bufferedReader = new BufferedReader(reader);

        try {
            return bufferedReader.readLine();
        }
        finally {
            bufferedReader.close();
        }
    }
}