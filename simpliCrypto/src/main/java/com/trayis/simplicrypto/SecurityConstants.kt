package com.trayis.simplicrypto

class SecurityConstants {
    companion object {

        const val STORE_NAME = "security-mStore"

        const val PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore"

        val PASSWORD = "password".toCharArray()

        const val ALGORITHM_AES = "AES"

        const val BLOCK_MODE_CBC = "CBC"

        const val PADDING_PKCS_7 = "PKCS7Padding"

        const val AES_CBC_PKCS7PADDING = "AES/CBC/PKCS7Padding"

        const val TRANSFORMATION_SYMMETRIC = AES_CBC_PKCS7PADDING
    }
}
