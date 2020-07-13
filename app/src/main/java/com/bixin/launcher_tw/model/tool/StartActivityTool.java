package com.bixin.launcher_tw.model.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.LauncherApp;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @author Altair
 * @date :2019.10.21 下午 02:18
 * @description:
 */
public class StartActivityTool {
    private Context mContext;

    public StartActivityTool(Context mContext) {
        this.mContext = mContext;
    }

    public StartActivityTool() {
        this.mContext = LauncherApp.getInstance();

    }

    /**
     * 根据action跳转
     *
     * @param action action
     */
    public void jumpByAction(String action) {
        Intent intent = new Intent(action);
        mContext.startActivity(intent);
    }

    public void jumpToActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        mContext.startActivity(intent);
    }

    /**
     * 根据包名启动应用
     *
     * @param packageName clicked app
     */
    public void launchAppByPackageName(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i("StartActivityTool", "package name is null!");
            return;
        }

        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            ToastTool.showToast(R.string.app_not_install);
        } else {
            mContext.startActivity(launchIntent);
        }
    }

    /**
     * 返回桌面
     */
    public void goHome() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        mContext.startActivity(intent);
    }


    public boolean killPackageName(String packageName) {
        OutputStream out = null;
        try {
            ActivityManager am =
                    (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
            if (am != null) {
                am.killBackgroundProcesses(packageName);
                Process process = Runtime.getRuntime().exec("su");
                String cmd = "am force-stop " + packageName + " \n";
                out = process.getOutputStream();
                out.write(cmd.getBytes());
                out.flush();
            }
        } catch (IOException e) {
            Log.e(TAG, " e: " + e.getMessage());
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 根据包名强制关闭一个应用，不管前台应用还是后台进程，需要 sharedUserId
     * 需要权限 FORCE_STOP_PACKAGES
     *
     * @param packageName app package name
     */
    public void stopApps(String packageName) {
        try {
            ActivityManager am =
                    (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
            Method forceStopPackage = null;
            if (am != null) {
                forceStopPackage = am.getClass().getDeclaredMethod("forceStopPackage",
                        String.class);
                forceStopPackage.setAccessible(true);
                forceStopPackage.invoke(am, packageName);
                System.out.println("TimerV force stop package " + packageName + " successful");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("TimerV force stop package " + packageName + " error!");
        }
    }

    public void killApp(String packageName) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        assert am != null;
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            String processName = info.processName;
            Log.d(TAG, "killApp: " + processName + " size: " + processes.size());
            if (processName.equals(packageName)) {
                am.killBackgroundProcesses(processName);
                android.os.Process.killProcess(info.pid);
            }
        }
    }

    /**
     * 打开网络共享与热点设置页面
     */
    public void openAPUI() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //打开网络共享与热点设置页面
        ComponentName comp = new ComponentName("com.android.settings",
                "com.android.settings" +
                        ".Settings$TetherSettingsActivity");
        intent.setComponent(comp);
        LauncherApp.getInstance().startActivity(intent);
    }

    /**
     * 判断应用是否在运行
     *
     * @param context
     * @return
     */
    public boolean isRun() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        boolean isAppRunning = false;
        String MY_PKG_NAME = "com.zsi.powervideo";
        for (ActivityManager.RunningAppProcessInfo info : list) {
            Log.i("ActivityService isRun()", info.processName);
            if (info.processName.equals(MY_PKG_NAME)) {
                isAppRunning = true;

                break;
            }
        }
        return isAppRunning;
    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.d(TAG, "isServiceRunning: "+service.service.getClassName());
            if ("com.zsi.powervideo".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}