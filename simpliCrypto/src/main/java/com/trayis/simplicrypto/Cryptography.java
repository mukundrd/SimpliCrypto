package com.trayis.simplicrypto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class Cryptography {

    private static final String UTF_8 = "UTF-8";

    private static final String IV_SEPARATOR = "]";

    private final Context context;

    private Store mStore;

    public Cryptography(Context context) {
        this.context = context.getApplicationContext();
    }

    public void initKeys() throws CryptoException {
        mStore = new Store(context, SecurityConstants.STORE_NAME, SecurityConstants.PASSWORD);
        if (mStore.hasSecretKey()) {
            mStore.fetchSecretKey();
            return;
        }
        mStore.generateSymmetricKey(SecurityConstants.PASSWORD);
    }

    public String encrypt(String plaintext) throws CryptoException {
        return encryptInternal(plaintext, mStore.getKey());
    }

    public String decrypt(String ciphertext) throws CryptoException {
        return decryptInternal(ciphertext, mStore.getKey());
    }

    private String encryptInternal(@NonNull String data, @NonNull Key key) throws CryptoException {
        String result = "";
        try {
            Cipher cipher = Cipher.getInstance(SecurityConstants.TRANSFORMATION_SYMMETRIC == null ? key.getAlgorithm() : SecurityConstants.TRANSFORMATION_SYMMETRIC);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] iv = cipher.getIV();
            String ivString = Base64.encodeToString(iv, Base64.DEFAULT);
            result = ivString + IV_SEPARATOR;

            byte[] plainData = data.getBytes(UTF_8);
            byte[] decodedData;
            decodedData = decode(cipher, plainData);

            String encodedString = Base64.encodeToString(decodedData, Base64.DEFAULT);
            result += encodedString;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            throw new CryptoException(e);
        }
        return result;
    }

    private String decryptInternal(@NonNull String data, @NonNull Key key) throws CryptoException {
        String result = null;
        try {
            String transformation = SecurityConstants.TRANSFORMATION_SYMMETRIC == null ? key.getAlgorithm() : SecurityConstants.TRANSFORMATION_SYMMETRIC;
            Cipher cipher = Cipher.getInstance(transformation);

            String[] split = data.split(IV_SEPARATOR);
            String ivString = split[0];
            String encodedString = split[1];
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.decode(ivString, Base64.DEFAULT));
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            byte[] decodedData;
            byte[] encryptedData = Base64.decode(encodedString, Base64.DEFAULT);
            decodedData = decode(cipher, encryptedData);
            result = new String(decodedData, UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IOException | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
        return result;
    }

    private byte[] decode(@NonNull Cipher cipher, @NonNull byte[] plainData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(baos, cipher);
        cipherOutputStream.write(plainData);
        cipherOutputStream.close();
        return baos.toByteArray();
    }

}
