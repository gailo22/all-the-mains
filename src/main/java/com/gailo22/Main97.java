package com.gailo22;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Main97 {

    private static final String DEFAULT_ALGORITHM = "AES";
    private static final String DEFAULT_FULL_ALGORITHM = "AES/CBC/PKCS5Padding";


    public static void main(String[] args) throws Exception {

        String file_key = "";
        String file_url = "";
        InputStream intstream = new URL(file_url).openStream();
        Main97.decryptFile(file_key, intstream, new File("/Users/xx/tmp/image-1.jpeg"));
    }

    //Encryption
    public static File encryptFile(String key, File originFile, String destPath) {
        FileInputStream in = null;
        FileOutputStream out = null;
        File destFile = null;
        try {
            destFile = new File(destPath);
            if (originFile.exists() && originFile.isFile()) {
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();
                in = new FileInputStream(originFile);
                out = new FileOutputStream(destFile, true);
                byte[] iv = getIv();
                Cipher cipher = initAESCipher(iv, key, Cipher.ENCRYPT_MODE);
                CipherInputStream cipherInputStream = new CipherInputStream(in, cipher);
                byte[] cache = new byte[1024];
                int nRead;
                out.write(new byte[4]);
                out.write(iv);
                out.write(new byte[4]);
                out.write(new byte[40]);
                out.flush();
                while ((nRead = cipherInputStream.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
                cipherInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return destFile;
    }

    //Decrypt
    public static File decryptFile(String key, InputStream in, File destFile) {
        FileOutputStream out = null;
        try {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            out = new FileOutputStream(destFile);

            byte[] iv = new byte[16];
            in.skip(4);
            int read = in.read(iv);
            if (read != 16) {
                throw new IOException("iv length error");
            }
            in.skip(44);
            Cipher cipher = initAESCipher(iv, key, Cipher.DECRYPT_MODE);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = in.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, r);
            }
            cipherOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return destFile;
    }

    private static Cipher initAESCipher(byte[] iv, String sKey, int cipherMode) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(sKey.getBytes(), DEFAULT_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DEFAULT_FULL_ALGORITHM);
            cipher.init(cipherMode, key, zeroIv);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getIv() {
        StringBuilder uid = new StringBuilder();
        Random rd = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            int type = rd.nextInt(3);
            switch (type) {
                case 0:
                    uid.append(rd.nextInt(10));
                    break;
                case 1:
                    uid.append((char) (rd.nextInt(25) + 65));
                    break;
                case 2:
                    uid.append((char) (rd.nextInt(25) + 97));
                    break;
                default:
                    break;
            }
        }
        return uid.toString().getBytes();
    }

}
