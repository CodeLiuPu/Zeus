package com.update.zeus.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class DLUtils {

    public static PackageInfo getPackageInfo(Context context, String apkFilepath){
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;

        try {
            pkgInfo = pm.getPackageInfo(apkFilepath,PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pkgInfo;
    }
}
