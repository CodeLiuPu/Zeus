package com.update.lib_plugin.base;

import android.app.Activity;
import android.content.res.Resources;

import com.update.lib_plugin.helper.PluginManager;

public class ZeusBaseActivity extends Activity {
    @Override
    public Resources getResources() {
        return PluginManager.mNowResources;
    }
}
