package com.gailo22.cryptofundamentals;

import static org.junit.Assert.assertEquals;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.Test;

public class SymmetricEncryptionTest {

  @Test
  public void testGenerateRandomAESKey() throws Exception {
    SecretKey key = SymmetricHelpers.generateAesKey();

    assertEquals("AES", key.getAlgorithm());
    assertEquals(32, key.getEncoded().length);
  }

  @Test
  public void testEncryptAMessageWithAES() throws Exception {
    String inputMessage = "Alice knows Bob's secret.";

    SecretKey key = SymmetricHelpers.generateAesKey();
    IvParameterSpec iv = SymmetricHelpers.generateInitializationVector();

    byte[] cipertext = SymmetricHelpers.encryptWithAes(inputMessage, key, iv);
    String outputMessage = SymmetricHelpers.decryptWithAes(cipertext, key, iv);

    assertEquals(inputMessage, outputMessage);
  }
}
