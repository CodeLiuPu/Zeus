package com.update.zeus.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class DLUtils {

    public static PackageInfo getPackageInfo(Context context, String apkFilepath){
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;

        try {
            pkgInfo = pm.getPackageArchiveInfo(apkFilepath,PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pkgInfo;
    }
}
