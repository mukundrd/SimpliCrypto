package com.trayis.simplicrypto;

import java.math.BigInteger;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

public class KeyProps {

    String mAlias, mKeyType;

    char[] mPassword;

    int mKeySize;

    String mBlockModes, mEncryptionPaddings, mSignatureAlgorithm;

    BigInteger mSerialNumber;

    X500Principal mSubject;

    Date mStartDate, mEndDate;

    public static final class Builder {
        private KeyProps mProps = new KeyProps();

        public Builder setAlias(String alias) {
            mProps.mAlias = alias;
            return this;
        }

        public Builder setKeyType(String keyType) {
            mProps.mKeyType = keyType;
            return this;
        }

        public Builder setPassword(char[] password) {
            mProps.mPassword = password;
            return this;
        }

        public Builder setKeySize(int keySize) {
            mProps.mKeySize = keySize;
            return this;
        }

        public Builder setSerialNumber(BigInteger serialNumber) {
            mProps.mSerialNumber = serialNumber;
            return this;
        }

        public Builder setSubject(X500Principal subject) {
            mProps.mSubject = subject;
            return this;
        }

        public Builder setStartDate(Date startDate) {
            mProps.mStartDate = startDate;
            return this;
        }

        public Builder setEndDate(Date endDate) {
            mProps.mEndDate = endDate;
            return this;
        }

        public Builder setBlockModes(String blockModes) {
            mProps.mBlockModes = blockModes;
            return this;
        }

        public Builder setEncryptionPaddings(String encryptionPaddings) {
            mProps.mEncryptionPaddings = encryptionPaddings;
            return this;
        }

        public Builder setSignatureAlgorithm(String signatureAlgorithm) {
            mProps.mSignatureAlgorithm = signatureAlgorithm;
            return this;
        }

        public KeyProps build() {
            return mProps;
        }
    }
}
