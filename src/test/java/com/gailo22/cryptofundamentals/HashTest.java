package com.gailo22.cryptofundamentals;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HashTest {
    
    @Test
    public void testHashMessage() throws Exception {
        String message = "Alice knows Bob's secret";

        byte[] digest = HashHelpers.computeHash(message);

        assertEquals(64, digest.length);
    }

    @Test
    public void testPasswordBasedKeyDerivationFunction() throws Exception {
        String passphrase = "Twas brillig and the slithy toves did gire and gimble in the wabe";
        byte[] salt = HashHelpers.generateSalt();

        byte[] key = HashHelpers.deriveKey(passphrase, salt);

        assertEquals(32, key.length);
    }
}