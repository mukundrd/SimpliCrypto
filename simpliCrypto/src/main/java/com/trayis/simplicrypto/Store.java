package com.trayis.simplicrypto;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static java.security.KeyStore.getDefaultType;
import static java.security.KeyStore.getInstance;

public class Store {

    private static final String TAG = "Store";

    private final Context mContext;

    private final String mKeystoreName;

    private final char[] mKeystorePassword;

    private final File mKeystoreFile;

    private final String mKeyAlias;

    private KeyStore mDefaultKeyStore;

    private SecretKey mKey;

    public Store(@NonNull Context context, @NonNull String name, char[] password) {
        mContext = context;
        mKeystoreName = name;
        mKeystorePassword = password;
        mKeystoreFile = new File(mContext.getFilesDir(), mKeystoreName);
        mKeyAlias = mContext.getPackageName();
    }

    public boolean hasSecretKey() {
        boolean result = false;
        try {
            KeyStore keyStore;
            if (Utils.LOWER_THAN_JB3) {
                keyStore = getDefaultKeyStore();
                result = isKeyEntry(keyStore);
            } else if (Utils.LOWER_THAN_M) {
                keyStore = getAndroidKeystore();
                result = isKeyEntry(keyStore);
                if (!result) {
                    keyStore = getDefaultKeyStore();
                    result = isKeyEntry(keyStore);
                }
            } else {
                keyStore = getAndroidKeystore();
                result = isKeyEntry(keyStore);
            }

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
        }

        return result;
    }

    private boolean isKeyEntry(KeyStore keyStore) throws KeyStoreException {
        return keyStore != null && keyStore.isKeyEntry(mKeyAlias);
    }

    public void generateSymmetricKey(char[] password) throws CryptoException {
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 3);

        KeyProps keyProps = new KeyProps.Builder()
                .setAlias(mKeyAlias)
                .setPassword(password)
                .setKeySize(256)
                .setKeyType(SecurityConstants.ALGORITHM_AES)
                .setBlockModes(SecurityConstants.BLOCK_MODE_CBC)
                .setEncryptionPaddings(SecurityConstants.PADDING_PKCS_7)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
        mKey = generateSymmetricKey(keyProps);
    }

    public SecretKey generateSymmetricKey(@NonNull KeyProps keyProps) throws CryptoException {
        SecretKey result = null;
        if (Utils.LOWER_THAN_M) {
            result = generateDefaultSymmetricKey(keyProps);
        } else {
            result = generateAndroidSymmetricKey(keyProps);
        }
        return result;
    }

    private SecretKey generateDefaultSymmetricKey(KeyProps keyProps) throws CryptoException {
        try {
            SecretKey key = createSymmetricKey(keyProps);
            KeyStore.SecretKeyEntry keyEntry = new KeyStore.SecretKeyEntry(key);
            KeyStore keyStore = getDefaultKeyStore();

            keyStore.setEntry(keyProps.mAlias, keyEntry, new KeyStore.PasswordProtection(keyProps.mPassword));
            keyStore.store(new FileOutputStream(mKeystoreFile), mKeystorePassword);
            return key;
        } catch (NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException e) {
            throw new CryptoException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private SecretKey generateAndroidSymmetricKey(KeyProps keyProps) throws CryptoException {
        try {
            String provider = SecurityConstants.PROVIDER_ANDROID_KEY_STORE;
            KeyGenerator keyGenerator = KeyGenerator.getInstance(keyProps.mKeyType, provider);
            KeyGenParameterSpec keySpec = keyPropsToKeyGenParameterSSpec(keyProps);
            keyGenerator.init(keySpec);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private KeyGenParameterSpec keyPropsToKeyGenParameterSSpec(KeyProps keyProps) throws NoSuchAlgorithmException {
        int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
        return new KeyGenParameterSpec.Builder(keyProps.mAlias, purposes)
                .setKeySize(keyProps.mKeySize)
                .setBlockModes(keyProps.mBlockModes)
                .setEncryptionPaddings(keyProps.mEncryptionPaddings)
                .build();
    }

    private SecretKey createSymmetricKey(KeyProps keyProps) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyProps.mKeyType);
        keyGenerator.init(keyProps.mKeySize);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    private synchronized KeyStore getDefaultKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        if (mDefaultKeyStore == null) {
            String defaultType = getDefaultType();
            mDefaultKeyStore = getInstance(defaultType);
            if (!mKeystoreFile.exists()) {
                mDefaultKeyStore.load(null);
            } else {
                mDefaultKeyStore.load(new FileInputStream(mKeystoreFile), mKeystorePassword);
            }
        }
        return mDefaultKeyStore;
    }

    private synchronized KeyStore getAndroidKeystore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        if (mDefaultKeyStore == null) {
            mDefaultKeyStore = KeyStore.getInstance(SecurityConstants.PROVIDER_ANDROID_KEY_STORE);
            mDefaultKeyStore.load(null);
        }
        return mDefaultKeyStore;
    }

    public void fetchSecretKey() throws CryptoException {
        try {
            KeyStore keyStore = getDefaultKeyStore();
            mKey = (SecretKey) keyStore.getKey(mKeyAlias, mKeystorePassword);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new CryptoException(e);
        }
    }

    public SecretKey getKey() {
        return mKey;
    }
}
