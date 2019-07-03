package com.update.zeus.app;

import android.app.Application;
import android.content.Context;

import com.update.lib_plugin.helper.BaseDexClassLoaderHookHelper;
import com.update.lib_plugin.helper.Utils;

import java.io.File;
import java.io.IOException;

/**
 * @author : liupu
 * date   : 2019/6/26
 * desc   :
 */
public class App extends Application {
    private static final String apkName = "plugin-debug.apk";
    private static final String dexName = "plugin-debug.dex";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Utils.extractAssets(newBase, apkName);
        File dexFile = getFileStreamPath(apkName);
        File optDexFile = getFileStreamPath(dexName);

        try {
            BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}