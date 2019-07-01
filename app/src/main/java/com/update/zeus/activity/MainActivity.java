package com.update.zeus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.update.zeus.R;
import com.update.zeus.data.PluginItem;
import com.update.zeus.helper.DLUtils;
import com.update.zeus.helper.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String apkName = "plugin-debug.apk";

    protected Activity activity;
    PluginItem pluginItem;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Utils.extractAssets(newBase, apkName);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        pluginItem = generatePluginItem(apkName);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    String serviceName = pluginItem.packageInfo.packageName + ".service.TestService";
                    intent.setClass(activity, Class.forName(serviceName));
                    startService(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    private PluginItem generatePluginItem(String pluginName) {
        File file = getFileStreamPath(pluginName);
        PluginItem item = new PluginItem();
        item.pluginPath = file.getAbsolutePath();
        item.packageInfo = DLUtils.getPackageInfo(activity, item.pluginPath);
        return item;
    }
}
