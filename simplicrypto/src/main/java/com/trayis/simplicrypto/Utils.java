package com.trayis.simplicrypto;

import android.os.Build;

public @interface Utils {

    int VERSION = Build.VERSION.SDK_INT;

    boolean LOWER_THAN_JB3 = VERSION < Build.VERSION_CODES.JELLY_BEAN_MR2;

    boolean LOWER_THAN_M = VERSION < Build.VERSION_CODES.M;

}
