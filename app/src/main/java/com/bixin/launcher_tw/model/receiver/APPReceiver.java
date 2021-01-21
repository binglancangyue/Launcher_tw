package com.bixin.launcher_tw.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.LauncherApp;
import com.bixin.launcher_tw.model.bean.Customer;
import com.bixin.launcher_tw.model.tool.InterfaceCallBackManagement;
import com.bixin.launcher_tw.model.tool.SharePreferencesTool;
import com.bixin.launcher_tw.model.tool.ToastTool;
import com.bixin.launcher_tw.view.activity.AppListActivity;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * @author Altair
 * @date :2020.04.02 下午 03:10
 * @description:
 */
public class APPReceiver extends BroadcastReceiver {
    private String mNetworkType="";
    private final static String TAG = "APPReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive:action " + action);

        if (action == null) {
            return;
        }
        if (action.equals(Intent.ACTION_PACKAGE_REMOVED) ||
                action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            LauncherApp.getInstance().initAppList();
            InterfaceCallBackManagement.getInstance().updateAppList();
            Log.d(TAG, "onReceive: ");
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {

                Log.d(TAG, "onReceive is install: " + LauncherApp.getInstance().isInstall());
            }
        }
        if (action.equals(Customer.ACTION_TXZ_CUSTOM_COMMAND)) {
            String command = intent.getStringExtra("key_type");
            if (command.equals("CMD_OPEN_APP_MGT")) {
                Intent intent1 = new Intent(context, AppListActivity.class);
                LauncherApp.getInstance().startActivity(intent1);
            }
        }
        if (action.equals(Customer.ACTION_TW_STATE)) {
            Log.d(TAG, "onReceive:kardidvr003 "
                    + intent.getIntExtra("machineState", -1));
        }
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo info =
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info == null) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();

            }
            if (info == null) {
                return;
            }
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (!"4G".equals(GetNetworkType(info))) {
                    Log.d(TAG, "onReceive: changeAirMode");
                    InterfaceCallBackManagement.getInstance().changeAirMode();
//                    changeAirplaneModeSystemSetting(false);
                }
            }
        }
    }

    private void startDVR003(NetworkInfo info) {
        Log.d(TAG,
                "onReceive:getState " + info.getState() + " isConnected " + info.isConnected());
        if (NetworkInfo.State.CONNECTED == info.getState() && info.isConnected()) {
            if (LauncherApp.getInstance().isFirstLaunch()) {
                if (TextUtils.isEmpty(Customer.PACKAGE_NAME_DVR3_TW)) {
                    Log.i(TAG, "package name is null!");
                    return;
                }
                Intent launchIntent = LauncherApp.getInstance().getPackageManager()
                        .getLaunchIntentForPackage(Customer.PACKAGE_NAME_DVR3_TW);
                if (launchIntent == null) {
                    String s = LauncherApp.getInstance().getString(R.string.app_not_install);
                    ToastTool.showToast(s + "\n" + Customer.PACKAGE_NAME_DVR3_TW);
                } else {
//                            SharePreferencesTool.getInstance().saveBoolean("isFirstStart", false);
                    LauncherApp.getInstance().setFirstLaunch(false);
                    Log.d(TAG, "onReceive: " + Customer.PACKAGE_NAME_DVR3_TW);
                    LauncherApp.getInstance().startActivity(launchIntent);
                }
            }
        }
    }

    private String GetNetworkType(NetworkInfo networkInfo) {
        String type;
        String _strSubTypeName = networkInfo.getSubtypeName();
        Log.e(TAG, "Network getSubtypeName : " + _strSubTypeName);
        // TD-SCDMA   networkType is 17
        int networkType = networkInfo.getSubtype();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                type = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
            case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
            case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                type = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                type = "4G";
                break;
            default:
                // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") ||
                        _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                    type = "3G";
                } else {
                    type = _strSubTypeName;
                }
                break;
        }
        Log.e(TAG, "Network Type : " + type);
        return type;
    }

}
