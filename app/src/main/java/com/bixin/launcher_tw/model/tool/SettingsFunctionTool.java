package com.bixin.launcher_tw.model.tool;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.bixin.launcher_tw.model.LauncherApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;

/**
 * @author Altair
 * @date :2019.10.25 上午 09:43
 * @description: 设置弹窗功能工具类
 */
public class SettingsFunctionTool {
    private Context mContext;
    private LocationManager locationManager;
    private AudioManager audioManager;
    private final float baseValue = 2.55f;//0-255
    private BluetoothAdapter bluetoothAdapter;
    private static final String TAG = "SettingsFunctionTool";
    private TelephonyManager mTelephonyManager;
    private int lastCount = -1;
    //    private PowerManager mPowerManager;
    public static final String fm_power_path = "/sys/class/QN8027/QN8027/power_state";
    public static final String fm_tunetoch_path = "/sys/class/QN8027/QN8027/tunetoch";
    public static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    public static final String EXTRA_FORMAT_PRIVATE = "format_private";
    public static final String EXTRA_FORGET_UUID = "forget_uuid";

    public SettingsFunctionTool(Context mContext) {
        this.mContext = mContext;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
    }

    public SettingsFunctionTool() {
        this.mContext = LauncherApp.getInstance();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        mFmManager = (FmManager) getSystemService(mContext, ContextDef.FM_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        //        locationManager.addGpsStatusListener(statusListener);
//        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
    }


    //打开或者关闭gps
    public void openGPS(boolean open) {
        if (Build.VERSION.SDK_INT < 19) {
            Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(),
                    LocationManager.GPS_PROVIDER, open);
        } else {
            if (!open) {
                Settings.Secure.putInt(mContext.getContentResolver(),
                        Settings.Secure.LOCATION_MODE,
                        Settings.Secure.LOCATION_MODE_OFF);
            } else {
                Settings.Secure.putInt(mContext.getContentResolver(),
                        Settings.Secure.LOCATION_MODE,
                        Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
            }
        }
    }

    //判断gps是否处于打开状态
    public boolean isGpsOpen() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            int state = Settings.Secure.getInt(mContext.getContentResolver(),
                    Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            if (state == Settings.Secure.LOCATION_MODE_OFF) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 获得屏幕亮度值
     *
     * @return 系统屏幕亮度值
     */
    public int getScreenBrightness() {
        ContentResolver contentResolver = mContext.getContentResolver();
        int defValue = 125;
        return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, defValue);
    }

    /**
     * 获得屏百分比制幕亮度值
     *
     * @return 百分比值
     */
    public int getScreenBrightnessPercentageValue() {
//        double value = (int) (getScreenBrightness() / baseValue);
//        return (int) Math.floor(value);
        double value = getCurrentBrightness();
        Log.d(TAG, "getScreenBrightnessPercentageValue: " + value);
        String s = NumberFormat.getPercentInstance().format(value);
        s = s.replace("%", "");
        return Integer.parseInt(s);
    }

    /**
     * 将百分比值转成亮度值
     *
     * @return
     */
    public int percentageValueChangeToBrightness(int value) {
        return (int) (value * baseValue);
    }

    /**
     * 关闭光感，设置手动调节背光模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC 自动调节屏幕亮度模式值为1
     * SCREEN_BRIGHTNESS_MODE_MANUAL 手动调节屏幕亮度模式值为0
     **/
    private void setScreenManualMode() {
        ContentResolver contentResolver = mContext.getContentResolver();
        int mode;
        try {
            mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "setScreenManualMode Exception: " + e.toString());
        }
    }

    /**
     * 系统值 0-255
     * 修改Setting 中屏幕亮度值
     * 修改Setting的值需要动态申请权限<uses-permission
     * android:name="android.permission.WRITE_SETTINGS"/>
     **/

    public void modifyScreenBrightness(int brightnessValue) {
        // 首先需要设置为手动调节屏幕亮度模式
        setScreenManualMode();
        ContentResolver contentResolver = mContext.getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
    }

    /**
     * 根据seeBar progress 转换后屏幕亮度
     *
     * @param progress seekBar progress
     */
    public void progressChangeToBrightness(int progress) {
//        int brightnessValue = (int) Math.ceil(progress * baseValue);
        int brightnessValue = (int) Math.round(progress * baseValue);

        Log.d(TAG, "progressChangeToBrightness: brightnessValue " + brightnessValue);
        try {
            modifyScreenBrightness(brightnessValue);
        } catch (Exception e) {
            Log.e(TAG, "progressChangeToBrightness Exception: " + e.getMessage());
        }
    }

    /**
     * 获取开启静音(音量设为0)的权限
     */
    private void getDoNotDisturb() {
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(Settings
                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            mContext.startActivity(intent);
        }
    }

    /**
     * 根据类型获取最大音量
     *
     * @param type 声音类型
     * @return 系统音量值
     */
    public int getMaxValue(int type) {
        return audioManager.getStreamMaxVolume(type);
    }

    /**
     * 获取系统当前音量
     *
     * @return 系统当前音量
     */
    public int getCurrentVolume() {
        return audioManager.getStreamVolume(STREAM_TYPE);
    }

    public void setVolume(int volume) {
        audioManager.setStreamVolume(STREAM_TYPE, volume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    public void setVolume(int volume,int type) {
        audioManager.setStreamVolume(type, volume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 蓝牙是否开启
     *
     * @return true or false
     */
    public boolean isBlueToothEnable() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 开启或关闭蓝牙
     *
     * @param isOpen true or false
     */
    public void openOrCloseBT(boolean isOpen) {
        if (isOpen) {
            if (!isBlueToothEnable()) {
                bluetoothAdapter.enable();
            }
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            mContext.startActivity(intent);
        } else {
            bluetoothAdapter.disable();
        }
    }


    private static Object getSystemService(Context context, String name) {
        return context.getSystemService(name);
    }

    public void openOrCloseFM(boolean isOpen) {
        if (isOpen) {
            Log.d(TAG, "openOrCloseFM: on");
            if (!getFmStatus()) {
                openFm();
            }
        } else {
            Log.d(TAG, "openOrCloseFM: off");
            closeFm();
        }
    }

    /**
     * 设置息屏或屏保时间
     * * 管理器方式
     *
     * @param time 时间值
     */
    public void setScreenOffTimeOut(int time) {
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, time);
        Uri uri = Settings.System
                .getUriFor(Settings.System.SCREEN_OFF_TIMEOUT);
        mContext.getContentResolver().notifyChange(uri, null);
    }

    /**
     * 获取当前系统的息屏或屏保时间
     * 管理器方式
     *
     * @return 时间
     */
    public int getScreenOutTime() {
        try {
            int time = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT) / 1000;
            Log.d(TAG, "instance initializer: " + time);
            if (time >= 15 && time <= 60) {
                return 1;
            } else if (time > 60 && time <= 180) {
                return 2;
            } else if (time > 180 && time <= 300) {
                return 3;
            } else {
                return 4;
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "getScreenOutTime: error " + e.getMessage());
        }
        return 1;
    }

    /**
     * 移动网络开关
     */
    public void toggleMobileData(boolean enabled) {
        ConnectivityManager conMgr =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass; // ConnectivityManager类
        Field iConMgrField; // ConnectivityManager类中的字段
        Object iConMgr; // IConnectivityManager类的引用
        Class<?> iConMgrClass; // IConnectivityManager类
        Method setMobileDataEnabledMethod; // setMobileDataEnabled方法
        try {
            // 取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            // 取得ConnectivityManager类中的对象mService
            iConMgrField = conMgrClass.getDeclaredField("mService");
            // 设置mService可访问
            iConMgrField.setAccessible(true);
            // 取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            // 取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled",
                    Boolean.TYPE);
            // 设置setMobileDataEnabled方法可访问
            setMobileDataEnabledMethod.setAccessible(true);
            // 调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException
                | NoSuchMethodException | IllegalArgumentException | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回手机移动数据的状态
     *
     * @param arg 默认填null
     * @return true 连接 false 未连接
     */
    public boolean getMobileDataState(Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }
            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
            return isOpen;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean ExistSDCard() {
        Log.d(TAG, "ExistSDCard: state " + Environment.getExternalStorageState());
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isHasSimCard() {
        int simState = mTelephonyManager.getSimState();
        boolean result = true;
        @SuppressLint("MissingPermission")
        String imsi = mTelephonyManager.getSubscriberId();
        Log.d(TAG, "isHasSimCard: imsi " + imsi);
        if (simState == TelephonyManager.SIM_STATE_ABSENT ||
                simState == TelephonyManager.SIM_STATE_UNKNOWN) {
            result = false;
        }
        Log.d(TAG, result ? "有SIM卡" : "无SIM卡");
        return result;
    }


    private double getCurrentBrightness() {
        ContentResolver contentResolver = mContext.getContentResolver();
        final int mMinBrightness = 1;
        final int mMaxBrightness = 255;
        final double value = Settings.System.getInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, mMinBrightness);
        return getPercentage(value, mMinBrightness, mMaxBrightness);
    }

    private double getPercentage(double value, int min, int max) {
        if (value > max) {
            return 1.0;
        }
        if (value < min) {
            return 0.0;
        }
        return (value - min) / (max - min);
    }


    private void setLockPatternEnabled(String systemSettingKey, boolean enabled) {
        //推荐使用
        ContentResolver contentResolver = mContext.getContentResolver();
        Settings.Secure.putInt(contentResolver, systemSettingKey, enabled ? 1 : 0);
    }

    public boolean getFmStatus() {
        boolean isOpen = false;
        BufferedReader reader;
        String prop;
        try {
            reader = new BufferedReader(new FileReader(new File(fm_power_path)));
            prop = reader.readLine();
            if (prop.equals("1")) {
                isOpen = true;
            }
            Log.d(TAG, "getFmStatus:prop " + prop);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isOpen;
    }


    private void openFm() {
        try {
            Writer fm_power = new FileWriter(fm_power_path);
            fm_power.write("on");
            fm_power.close();
            Log.d(TAG, "openFm:on ");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "closeFm: " + e.getMessage());
        }
    }

    private void closeFm() {
        try {
            Writer fm_power = new FileWriter(fm_power_path);
            fm_power.write("off");
            fm_power.flush();
            fm_power.close();
            Log.d(TAG, "openFm:on off");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "closeFm: " + e.getMessage());
        }
    }

    /**
     * 检查wifi是否处开连接状态
     *
     * @return
     */
    public boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return false;
        }
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void setDataEnabled(boolean enable) {
        try {
            @SuppressLint("MissingPermission") SubscriptionInfo subscriptionInfo =
                    SubscriptionManager.from(mContext).getActiveSubscriptionInfoForSimSlotIndex(0);
            if (subscriptionInfo == null) {
                Log.d(TAG, "setDataEnabled:subscriptionInfo==null ");
                return;
            }
            int subid = subscriptionInfo.getSubscriptionId();
            Method setDataEnabled = mTelephonyManager.getClass().getDeclaredMethod("setDataEnabled"
                    , int.class, boolean.class);
            if (null != setDataEnabled) {
                setDataEnabled.invoke(mTelephonyManager, subid, enable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("MissingPermission")
    public boolean getDataEnabled() {
        boolean enabled = false;
        try {
            SubscriptionInfo subscriptionInfo =
                    SubscriptionManager.from(mContext).getActiveSubscriptionInfoForSimSlotIndex(0);
            if (subscriptionInfo == null) {
                Log.d(TAG, "getDataEnabled:subscriptionInfo==null ");
                return false;
            }
            int subid = subscriptionInfo.getSubscriptionId();
            Method getDataEnabled = mTelephonyManager.getClass().getDeclaredMethod("getDataEnabled"
                    , int.class);
            if (null != getDataEnabled) {
                enabled = (Boolean) getDataEnabled.invoke(mTelephonyManager, subid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enabled;
    }

    /**
     * 获取SN
     *
     * @return
     */
    public String getSN() {
        String serial = "";
        //通过android.os获取sn号
        try {
            serial = android.os.Build.SERIAL;
            if (!serial.equals("") && !serial.equals("unknown")) {
                return serial;
            }
        } catch (Exception e) {
            serial = "";
        }
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
            if (!serial.equals("") && !serial.equals("unknown")) {
                return serial;
            }
            //9.0及以上无法获取到sn，此方法为补充，能够获取到多数高版本手机 sn
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = Build.getSerial();
            }
        } catch (Exception e) {
            serial = "";
        }
        return serial;
    }

    private void goWifiSettings() {
        Intent intent = new Intent();
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        LauncherApp.getInstance().startActivity(intent);
    }

}
