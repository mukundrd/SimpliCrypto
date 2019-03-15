package com.trayis.simplicrypto

import android.content.Context
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec

class Cryptography(context: Context) {

    private val context: Context = context.applicationContext

    private lateinit var mStore: Store

    @Throws(CryptoException::class)
    fun initKeys() {
        mStore = Store(context, SecurityConstants.STORE_NAME, SecurityConstants.PASSWORD)
        if (mStore.hasSecretKey()) {
            mStore.fetchSecretKey()
            return
        }
        mStore.generateSymmetricKey(SecurityConstants.PASSWORD)
    }

    @Throws(CryptoException::class)
    fun encrypt(plaintext: String): String {
        return encryptInternal(plaintext, mStore.key)
    }

    @Throws(CryptoException::class)
    fun decrypt(ciphertext: String): String {
        return decryptInternal(ciphertext, mStore.key)
    }

    @Throws(CryptoException::class)
    private fun encryptInternal(data: String, key: Key): String {
        var result: String?
        try {
            val cipher = Cipher.getInstance(SecurityConstants.TRANSFORMATION_SYMMETRIC)
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val iv = cipher.iv
            val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
            result = ivString + IV_SEPARATOR

            val plainData = data.toByteArray(StandardCharsets.UTF_8)
            val decodedData: ByteArray
            decodedData = decode(cipher, plainData)

            val encodedString = Base64.encodeToString(decodedData, Base64.DEFAULT)
            result += encodedString
        } catch (e: NoSuchAlgorithmException) {
            throw CryptoException(e)
        } catch (e: NoSuchPaddingException) {
            throw CryptoException(e)
        } catch (e: InvalidKeyException) {
            throw CryptoException(e)
        } catch (e: IOException) {
            throw CryptoException(e)
        }

        return result
    }

    @Throws(CryptoException::class)
    private fun decryptInternal(data: String, key: Key): String {
        val result: String?
        try {
            val cipher = Cipher.getInstance(SecurityConstants.TRANSFORMATION_SYMMETRIC)

            val split = data.split(IV_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val ivString = split[0]
            val encodedString = split[1]
            val ivSpec = IvParameterSpec(Base64.decode(ivString, Base64.DEFAULT))
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

            val decodedData: ByteArray
            val encryptedData = Base64.decode(encodedString, Base64.DEFAULT)
            decodedData = decode(cipher, encryptedData)
            result = String(decodedData, StandardCharsets.UTF_8)
        } catch (e: NoSuchAlgorithmException) {
            throw CryptoException(e)
        } catch (e: NoSuchPaddingException) {
            throw CryptoException(e)
        } catch (e: InvalidKeyException) {
            throw CryptoException(e)
        } catch (e: IOException) {
            throw CryptoException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw CryptoException(e)
        }

        return result
    }

    @Throws(IOException::class)
    private fun decode(cipher: Cipher, plainData: ByteArray): ByteArray {
        val baos = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(baos, cipher)
        cipherOutputStream.write(plainData)
        cipherOutputStream.close()
        return baos.toByteArray()
    }

    companion object {
        private val IV_SEPARATOR = "]"
    }

}
