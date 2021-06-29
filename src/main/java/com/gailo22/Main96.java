package com.gailo22;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Main96 {
    public static void main(String[] args) throws Exception {
        String _initVector = "yyy";
        String _encryptionKey = SHA256("xxx", 32);

        System.out.println(_encryptionKey);

        String cipherText = "1UkJTUsS1YOaypkGD9p5LOv7FHCWrgz/2/285IUlBaBBA4KY7mhvONuk3/l2QaHHMPN/J6TZr53sBH3Hg39ICrQyxKUI9S2OIVvpGRGzQQgUOYWWfd+e9622ISM6TAnx2lvyatvxSFpJWDYVuZXNlpbrY3gzBSLsBBRQljAezUywS90Q65LVHxByZe4H9JUQKyYFzD9QE3BOd9S0kw7gvzlXDtYFvr00+r03TrULX1SAQbRwUqPOAVp4scsIROxD5I5iWcXB4azPsuzvr8KEF5uoOxRUD09ahQFyVCz4BVJHq0l8opK91EiQqdKSBrIby54MSQ9RMGpOsd+EXwRtBcT2cCEeI8i8wN/sU0H9H7A=";
        SecretKey key = new SecretKeySpec(_encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(_initVector.getBytes(StandardCharsets.UTF_8));
        String algorithm = "AES/CBC/PKCS5Padding";
        String plainText = decrypt(algorithm, cipherText, key, ivParameterSpec);
        System.out.println(plainText);
    }

    public static String SHA256(String text, int length) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String resultStr;
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes("UTF-8"));
        byte[] digest = md.digest();

        StringBuffer result = new StringBuffer();
        for (byte b : digest) {
            /**
             * Convert to hex
             */
            result.append(String.format("%02x", b));
        }

        if (length > result.toString().length()) {
            resultStr = result.toString();
        } else {
            resultStr = result.toString().substring(0, length);
        }

        return resultStr;

    }

    public static String encrypt(String algorithm, String input, SecretKey key,
                                 IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key,
                                 IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
