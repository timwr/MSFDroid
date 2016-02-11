package com.msfdroid;


import android.app.Application;

public class MsfApplication extends Application {

    private static MsfApplication sInstance;
    private static Msf msf;

    public static MsfApplication getApplication() {
        return sInstance;
    }

    public static Msf Msf() {
        return msf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        msf = new Msf();
    }

}
