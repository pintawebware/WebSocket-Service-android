package com.pinta.ws_service;

import android.support.compat.BuildConfig;
import android.util.Log;

public class L {
    private static boolean isDebug = BuildConfig.DEBUG;
    private static String tagL = "tag";

    public static void d(Object msg) {
        if (isDebug) {
            Log.d(tagL, "........................" + msg);
        }
    }
}
