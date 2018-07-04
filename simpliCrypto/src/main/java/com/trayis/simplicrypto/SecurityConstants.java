package com.trayis.simplicrypto;

public @interface SecurityConstants {

    String STORE_NAME = "security-mStore";

    String PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore";

    char[] PASSWORD = "password".toCharArray();

    String ALGORITHM_AES = "AES";

    String BLOCK_MODE_CBC = "CBC";

    String PADDING_PKCS_7 = "PKCS7Padding";

    String AES_CBC_PKCS7PADDING = "AES/CBC/PKCS7Padding";

    String TRANSFORMATION_SYMMETRIC = AES_CBC_PKCS7PADDING;
}
