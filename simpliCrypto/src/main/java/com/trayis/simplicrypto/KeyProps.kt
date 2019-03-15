package com.trayis.simplicrypto

class KeyProps {

    internal lateinit var mAlias: String
    internal lateinit var mKeyType: String

    internal lateinit var mPassword: CharArray

    internal var mKeySize: Int = 0

    internal lateinit var mBlockModes: String
    internal lateinit var mEncryptionPaddings: String

    /*internal lateinit var mSignatureAlgorithm: String

    internal lateinit var mSerialNumber: BigInteger

    internal lateinit var mSubject: X500Principal

    internal lateinit var mStartDate: Date
    internal lateinit var mEndDate: Date*/

}
