package com.bixin.launcher_tw.model;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.util.Log;

import com.bixin.launcher_tw.model.bean.AppInfo;
import com.bixin.launcher_tw.model.bean.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Altair
 * @date :2020.03.31 下午 06:03
 * @description:
 */
public class LauncherApp extends Application {
    private boolean isFirstLaunch = true;
    private static LauncherApp mLauncher;
    public ArrayList<AppInfo> mAppList = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        mLauncher = this;
//        initAppList();
//        setDefaultIME();
    }

    private void setDefaultIME() {
        ContentResolver resolver = getContentResolver();
//        Settings.Secure.putString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD,
//                "com.sohu.inputmethod.sogou/.SogouIME");
        Settings.Secure.putString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD,
                "com.android.inputmethod.latin/.LatinIME");
    }

    public static LauncherApp getInstance() {
        return mLauncher;
    }

    /**
     * 获得所有安装的APP
     */
    public void initAppList() {
        if (mAppList.size() > 0) {
            mAppList.clear();
        }
        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(main, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo resolveInfo : apps) {
            AppInfo info = new AppInfo();
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (ignoreApp(activityInfo.packageName)) {
                info.setAppName(resolveInfo.loadLabel(pm).toString());
                info.setPkgName(activityInfo.packageName);
                info.setFlags(activityInfo.flags);
                info.setAppIcon(activityInfo.loadIcon(pm));
                info.setAppIntent(pm.getLaunchIntentForPackage(activityInfo.packageName));
                mAppList.add(info);
            }
            Log.d("MyApplication",
                    "AppList apps info: " + activityInfo.packageName);
        }
    }

    /**
     * 获得所有安装的APP
     */
    public boolean isInstall() {
        if (mAppList.size() > 0) {
            mAppList.clear();
        }
        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(main, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo resolveInfo : apps) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo.packageName.equals(Customer.PACKAGE_NAME_DVR)){
                return true;
            }
            Log.d("MyApplication",
                    "AppList apps info: " + activityInfo.packageName);
        }
        return false;
    }

    public ArrayList<AppInfo> getShowAppList() {
        if (mAppList.size() <= 0) {
            initAppList();
        }
        return mAppList;
    }

    /**
     * 过滤显示
     *
     * @param pkgName 包名
     * @return true or false
     */
    private boolean ignoreApp(String pkgName) {
        if (pkgName.equals("com.bixin.launcher_tw")) {
            return false;
        }
        return true;
    }

    public void setFirstLaunch(boolean isFirst) {
        this.isFirstLaunch = isFirst;
    }

    public boolean isFirstLaunch() {
        return this.isFirstLaunch;
    }
}
