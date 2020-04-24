package com.wangxingxing.opengldemo.base;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

public class App extends Application {

    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Utils.init(this);
    }

}
