package com.bixin.launcher_tw.model.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.LauncherApp;
import com.bixin.launcher_tw.model.bean.Customer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @author Altair
 * @date :2019.10.21 下午 02:18
 * @description:
 */
public class StartActivityTool {
    private Context mContext;
    private ContentResolver mResolver;

    public StartActivityTool(Context mContext) {
        this.mContext = mContext;
    }

    public StartActivityTool() {
        this.mContext = LauncherApp.getInstance();
        this.mResolver = mContext.getContentResolver();
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
            String s = LauncherApp.getInstance().getString(R.string.app_not_install);
            ToastTool.showToast(s + "\n" + packageName);
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
            Log.d(TAG, "isServiceRunning: " + service.service.getClassName());
            if ("com.zsi.powervideo".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    public static Map getIMEIforO(Context context) {
        Map<String, String> map = new HashMap<String, String>();
        TelephonyManager tm = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
        String imei1 = tm.getImei(0);
        String imei2 = tm.getImei(1);
        if (TextUtils.isEmpty(imei1) && TextUtils.isEmpty(imei2)) {

            map.put("imei1", tm.getMeid()); //如果CDMA制式手机返回MEID
        } else {
            map.put("imei1", imei1);

            map.put("imei2", imei2);
        }
        return map;
    }

    public void getAllContacts() {
        if (mResolver == null) {
            mResolver = mContext.getContentResolver();
        }
        Uri uri = ContactsContract.Data.CONTENT_URI;
        Cursor cursorUser = mResolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
        boolean isSave = false;
        if (cursorUser != null) {
            while (cursorUser.moveToNext()) {
                int id = cursorUser.getInt(0); // 按上面数组的声明顺序获取
                String name = cursorUser.getString(1);
                String rawContactsId = cursorUser.getString(2);
                if (name.equals("明台產物保險公司")) {
                    if (rawContactsId.equals("0907913491")) {
                        isSave = true;
                    }
                }
            }
            cursorUser.close();
        }
        if (!isSave) {
            addContact("明台產物保險公司", "0907913491");
            Log.d(TAG, "getAllContacts: 添加 明台產物保險公司");
        } else {
            Log.d(TAG, "getAllContacts:已经保存 明台產物保險公司");
        }
    }

    private void addContact(String name, String phoneNumber) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = mResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        // 向联系人URI添加联系人名字
        mResolver.insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        mResolver.insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();
    }

    private boolean isAirplaneModeOn() {
        int state = Settings.Global.getInt(LauncherApp.getInstance().getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0);
        if (state == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void startValidationTools() {
        if (!Customer.IS_START_TEST_APP) {
            return;
        }
        String path = StoragePaTool.getStoragePath(true);
        Log.d(TAG, "startValidationTools: " + path);
        if (path != null) {
            path = path + "/BixinTest";
            File file = new File(path);
            if (file.exists()) {
                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.sprd.validationtools",
                        "com.sprd.validationtools.ValidationToolsMainActivity");
                intent.setComponent(cn);
                mContext.startActivity(intent);
                Log.d(TAG, "startValidationTools: OK");
            } else {
                Log.d(TAG, "startValidationTools: !exists");
            }
        }
    }

    public void changeAirplaneModeSystemSetting(boolean on) {
        boolean currentState = isAirplaneModeOn();
        Log.i(TAG, "changeAirplaneModeSystemSetting() on=" + on + ",currentState=" + currentState);
        if (currentState != on) {
            Settings.Global.putInt(
                    LauncherApp.getInstance().getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON,
                    on ? 1 : 0);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
            intent.putExtra("state", on);
            LauncherApp.getInstance().sendBroadcast(intent);
        }
    }

}