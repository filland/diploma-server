package bntu.diploma.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AdvancedEncryptionStandard {

    private static final String ALGORITHM = "AES";


    /**
     * Encrypts the given plain text
     *
     * @param plainText The plain text to encrypt
     *
     * @param key is the key for encrypting which is
     * being owned by both the client and the server
     */
    public static byte[] encrypt(byte[] plainText, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(plainText);
    }


    /**
     * Decrypts the given byte array
     *
     * @param cipherText The data to decrypt
     */
    public static String decrypt(byte[] cipherText, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return new String(cipher.doFinal(cipherText));
    }

}
