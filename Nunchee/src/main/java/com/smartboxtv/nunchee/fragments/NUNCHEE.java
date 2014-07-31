package com.smartboxtv.nunchee.fragments;

import android.app.Application;

import com.androidquery.callback.BitmapAjaxCallback;

/**
 * Created by Esteban- on 19-04-14.
 */
public class NUNCHEE extends Application {
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        BitmapAjaxCallback.clearCache();
    }

    /*@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }*/

}
