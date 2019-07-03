package com.update.lib_plugin.helper;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.update.lib_plugin.data.PluginItem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Plugin 管理类
 */
public class PluginManager {
    public static final List<PluginItem> plugins = new ArrayList<>();

    // 正在使用的 Resources
    public static volatile Resources mNowResources;

    // 原始 application 中 BaseContext,不能是其他的,否则会发生内存泄漏
    public static volatile Context mBaseContext;

    // ContextImpl 中的 loadApk对象mPackageInfo
    private static Object mPackageInfo;

    public static void init(Application application) {
        mBaseContext = application.getBaseContext();
        mPackageInfo = RefInvoke.getFieldObject(mBaseContext, "mPackageInfo");
        mNowResources = mBaseContext.getResources();

        try {
            AssetManager am = application.getAssets();
            String[] paths = am.list("");
            List<String> pluginPaths = new ArrayList<>();
            for (String path : paths) {
                if (path.endsWith(".apk")) {
                    String apkPath = path;
                    String dexPath = apkPath.replace(".apk", ".dex");
                    Utils.extractAssets(mBaseContext, apkPath);
                    mergeDexs(apkPath, dexPath);
                    PluginItem item = generatePluginItem(apkPath);
                    plugins.add(item);
                    pluginPaths.add(item.pluginPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void mergeDexs(String apkPath, String dexPath) {
        File dexFile = mBaseContext.getFileStreamPath(apkPath);
        File optDexFile = mBaseContext.getFileStreamPath(dexPath);

        try {
            BaseDexClassLoaderHookHelper.patchClassLoader(mBaseContext.getClassLoader(), dexFile, optDexFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PluginItem generatePluginItem(String apkPath) {
        File file = mBaseContext.getFileStreamPath(apkPath);
        PluginItem item = new PluginItem();
        item.pluginPath = file.getAbsolutePath();
        item.packageInfo = DLUtils.getPackageInfo(mBaseContext, item.pluginPath);
        return item;
    }

    private static void reloadInstalledPluginResources(ArrayList<String> pluginPaths) {
        try {
            AssetManager am = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(am, mBaseContext.getPackageResourcePath());
            for (String pluginPath : pluginPaths) {
                addAssetPath.invoke(am, pluginPath);
            }

            Resources newResources = new Resources(am,
                    mBaseContext.getResources().getDisplayMetrics(),
                    mBaseContext.getResources().getConfiguration());

            RefInvoke.setFieldObject(mBaseContext,"mResources", newResources);
            // 这是最主要的需要替换的,如果不支持插件运行时更新,只保留这一个就可以了
            RefInvoke.setFieldObject(mPackageInfo,"mResources",newResources);

            mNowResources = mNowResources;
            // 需要清理mTheme对象,否则通过inflate方式加载资源会报错
            // 如果是 activity 动态加载插件,则需要将 activity 的mTheme对象也设置为null
            RefInvoke.setFieldObject(mBaseContext,"mTheme",null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
