package com.gailo22.cryptofundamentals;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HashHelpers {

    static {
        UseBouncyCastle.please();
    }

    public static byte[] computeHash(String message) throws Exception {
        var sha512 = MessageDigest.getInstance("SHA-512", "BC");
        sha512.update(message.getBytes());
        return sha512.digest();
    }

	public static byte[] generateSalt() {
        var bytes = new byte[16];
        var random = new SecureRandom();
        random.nextBytes(bytes);
		return bytes;
	}

	public static byte[] deriveKey(String passphrase, byte[] salt) throws Exception {
        int iterationCount = 10000;
        int keyLength = 256;
        var pbkdf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256", "BC");
        var keySpec = new PBEKeySpec(passphrase.toCharArray(), salt, iterationCount, keyLength);
        var secretKey = pbkdf.generateSecret(keySpec);
        
        return secretKey.getEncoded();
	}
}