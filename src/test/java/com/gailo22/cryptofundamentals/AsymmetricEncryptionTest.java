package com.gailo22.cryptofundamentals;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.junit.Test;

public class AsymmetricEncryptionTest {

    @Test
    public void testGenerateRSAKeyPair() throws Exception {
        KeyPair keyPair = AsymmetricHelpers.generateRsaKey();

        assertEquals("RSA", keyPair.getPublic().getAlgorithm());
        assertTrue(keyPair.getPublic().getEncoded().length > 2048 / 8);
        assertTrue(keyPair.getPrivate().getEncoded().length > 2048 / 8);
    }

    @Test
    public void testEncryptSymmetricKey() throws Exception {
        KeyPair keyPair = AsymmetricHelpers.generateRsaKey();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        SecretKey key = SymmetricHelpers.generateAesKey();

        byte[] encryptedKey = AsymmetricHelpers.encryptWithRsa(publicKey, key);
        byte[] decryptedKey = AsymmetricHelpers.decryptWithRsa(privateKey, encryptedKey);

        assertArrayEquals(key.getEncoded(), decryptedKey);
    }

    @Test
    public void testSignMessage() throws Exception {
        KeyPair keyPair = AsymmetricHelpers.generateRsaKey();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String message = "Alice knows Bob's secret.";
        byte[] messageBytes = message.getBytes();

        byte[] signatureBytes = AsymmetricHelpers.signMessage(privateKey, messageBytes);
        boolean verified = AsymmetricHelpers.verifySignature(publicKey, messageBytes, signatureBytes);

        assertTrue(verified);
    }
}