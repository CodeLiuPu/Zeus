package com.update.zeus.app;

import android.app.Application;
import android.content.Context;

/**
 * @author : liupu
 * date   : 2019/6/26
 * desc   :
 */
public class App extends Application {
    private static Context sContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = base;
    }

    public static Context getContext() {
        return sContext;
    }

}