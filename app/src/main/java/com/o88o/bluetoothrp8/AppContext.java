package com.o88o.bluetoothrp8;

import android.app.Application;

import cn.com.startai.kp8.crash.CrashExceptionHandle;

public class AppContext extends Application {
    //    private static final String TAG = AppContext.class.getSimpleName();
    private static final String TAG = "AppContext";

    private static AppContext sInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashExceptionHandle.getIntance().init(this);
    }

    public AppContext() {
        sInstance = this;
    }

    public static AppContext getContext() {
        return sInstance;
    }

}
