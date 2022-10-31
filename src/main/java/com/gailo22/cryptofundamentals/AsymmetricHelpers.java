package com.gailo22.cryptofundamentals;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AsymmetricHelpers {

    static {
        UseBouncyCastle.please();
    }

    private static final String RSA = "RSA";

    public static KeyPair generateRsaKey() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    public static byte[] encryptWithRsa(PublicKey publicKey, SecretKey key) throws Exception {

        var rsa = Cipher.getInstance("RSA/NONE/OAEPWithSHA512AndMGF1Padding", "BC");
        rsa.init(Cipher.ENCRYPT_MODE, publicKey);

        return rsa.doFinal(key.getEncoded());
    }

    public static byte[] decryptWithRsa(PrivateKey privateKey, byte[] encryptedKey) throws Exception {

        var rsa = Cipher.getInstance("RSA/NONE/OAEPWithSHA512AndMGF1Padding", "BC");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);

        return rsa.doFinal(encryptedKey);
    }

    public static byte[] signMessage(PrivateKey privateKey, byte[] messageBytes) throws Exception {

        var signature = Signature.getInstance("SHA512withRSA", "BC");
        signature.initSign(privateKey);
        signature.update(messageBytes);

        return signature.sign();
    }

    public static boolean verifySignature(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes) throws Exception {

        var signature = Signature.getInstance("SHA512withRSA", "BC");
        signature.initVerify(publicKey);
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    public static KeyPair generateRSAKkeyPair() throws Exception
    {
        SecureRandom secureRandom = new SecureRandom();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);

        keyPairGenerator.initialize(2048, secureRandom);

        return keyPairGenerator.generateKeyPair();
    }
}