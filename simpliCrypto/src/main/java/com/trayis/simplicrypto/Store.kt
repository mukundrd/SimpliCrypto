package com.trayis.simplicrypto

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.*
import java.security.KeyStore.getDefaultType
import java.security.KeyStore.getInstance
import java.security.cert.CertificateException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class Store(mContext: Context, mKeystoreName: String, private val mKeystorePassword: CharArray) {

    private val mKeystoreFile: File = File(mContext.filesDir, mKeystoreName)

    private val mKeyAlias: String = mContext.packageName

    private lateinit var mDefaultKeyStore: KeyStore

    lateinit var key: SecretKey

    private val defaultKeyStore: KeyStore?
        @Synchronized @Throws(KeyStoreException::class, CertificateException::class, NoSuchAlgorithmException::class, IOException::class)
        get() {
            if (!::mDefaultKeyStore.isInitialized) {
                val defaultType = getDefaultType()
                mDefaultKeyStore = getInstance(defaultType)
                if (!mKeystoreFile.exists()) {
                    mDefaultKeyStore.load(null)
                } else {
                    mDefaultKeyStore.load(FileInputStream(mKeystoreFile), mKeystorePassword)
                }
            }
            return mDefaultKeyStore
        }

    private val androidKeystore: KeyStore?
        @Synchronized @Throws(KeyStoreException::class, CertificateException::class, NoSuchAlgorithmException::class, IOException::class)
        get() {
            if (!::mDefaultKeyStore.isInitialized) {
                mDefaultKeyStore = KeyStore.getInstance(SecurityConstants.PROVIDER_ANDROID_KEY_STORE)
                mDefaultKeyStore.load(null)
            }
            return mDefaultKeyStore
        }

    fun hasSecretKey(): Boolean {
        var result = false
        try {
            var keyStore: KeyStore?
            if (Utils.LOWER_THAN_JB3) {
                keyStore = defaultKeyStore
                result = isKeyEntry(keyStore)
            } else if (Utils.LOWER_THAN_M) {
                keyStore = androidKeystore
                result = isKeyEntry(keyStore)
                if (!result) {
                    keyStore = defaultKeyStore
                    result = isKeyEntry(keyStore)
                }
            } else {
                keyStore = androidKeystore
                result = isKeyEntry(keyStore)
            }

        } catch (e: KeyStoreException) {
        } catch (e: CertificateException) {
        } catch (e: IOException) {
        } catch (e: NoSuchAlgorithmException) {
        }

        return result
    }

    @Throws(KeyStoreException::class)
    private fun isKeyEntry(keyStore: KeyStore?): Boolean {
        return keyStore?.isKeyEntry(mKeyAlias) ?: false
    }

    @Throws(CryptoException::class)
    fun generateSymmetricKey(password: CharArray) {
        // val start = Calendar.getInstance()
        // val end = Calendar.getInstance()
        // end.add(Calendar.YEAR, 3)

        val keyProps = KeyProps().apply {
            mAlias = mKeyAlias
            mPassword = password
            mKeySize = 256
            mKeyType = SecurityConstants.ALGORITHM_AES
            mBlockModes = SecurityConstants.BLOCK_MODE_CBC
            mEncryptionPaddings = SecurityConstants.PADDING_PKCS_7
            // mStartDate = start.time
            // mEndDate = end.time
        }
        key = generateSymmetricKey(keyProps)
    }

    @Throws(CryptoException::class)
    fun generateSymmetricKey(keyProps: KeyProps): SecretKey {
        if (Utils.LOWER_THAN_M) {
            return generateDefaultSymmetricKey(keyProps)
        }
        return generateAndroidSymmetricKey(keyProps)
    }

    @Throws(CryptoException::class)
    private fun generateDefaultSymmetricKey(keyProps: KeyProps): SecretKey {
        try {
            val key = createSymmetricKey(keyProps)
            val keyEntry = KeyStore.SecretKeyEntry(key)
            val keyStore = defaultKeyStore

            keyStore!!.setEntry(keyProps.mAlias, keyEntry, KeyStore.PasswordProtection(keyProps.mPassword))
            keyStore.store(FileOutputStream(mKeystoreFile), mKeystorePassword)
            return key
        } catch (e: NoSuchAlgorithmException) {
            throw CryptoException(e)
        } catch (e: CertificateException) {
            throw CryptoException(e)
        } catch (e: KeyStoreException) {
            throw CryptoException(e)
        } catch (e: IOException) {
            throw CryptoException(e)
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Throws(CryptoException::class)
    private fun generateAndroidSymmetricKey(keyProps: KeyProps): SecretKey {
        try {
            val provider = SecurityConstants.PROVIDER_ANDROID_KEY_STORE
            val keyGenerator = KeyGenerator.getInstance(keyProps.mKeyType, provider)
            val keySpec = keyPropsToKeyGenParameterSSpec(keyProps)
            keyGenerator.init(keySpec)
            return keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw CryptoException(e)
        } catch (e: NoSuchProviderException) {
            throw CryptoException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw CryptoException(e)
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Throws(NoSuchAlgorithmException::class)
    private fun keyPropsToKeyGenParameterSSpec(keyProps: KeyProps): KeyGenParameterSpec {
        val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        return KeyGenParameterSpec.Builder(keyProps.mAlias, purposes)
                .setKeySize(keyProps.mKeySize)
                .setBlockModes(keyProps.mBlockModes)
                .setEncryptionPaddings(keyProps.mEncryptionPaddings)
                .build()
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun createSymmetricKey(keyProps: KeyProps): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(keyProps.mKeyType)
        keyGenerator.init(keyProps.mKeySize)
        return keyGenerator.generateKey()
    }

    @Throws(CryptoException::class)
    fun fetchSecretKey() {
        try {
            val keyStore = defaultKeyStore
            key = keyStore!!.getKey(mKeyAlias, mKeystorePassword) as SecretKey
        } catch (e: KeyStoreException) {
            throw CryptoException(e)
        } catch (e: CertificateException) {
            throw CryptoException(e)
        } catch (e: IOException) {
            throw CryptoException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw CryptoException(e)
        } catch (e: UnrecoverableKeyException) {
            throw CryptoException(e)
        }

    }

    companion object {
        private const val TAG = "Store"
    }

}
