package com.trayis.simplicrypto

import android.os.Build

class Utils {
    companion object {

        val VERSION = Build.VERSION.SDK_INT

        val LOWER_THAN_JB3 = VERSION < Build.VERSION_CODES.JELLY_BEAN_MR2

        val LOWER_THAN_M = VERSION < Build.VERSION_CODES.M
    }

}
