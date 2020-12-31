package com.bixin.launcher_tw.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("APPReceiver", "onReceive:action " + action);

        if (action == null) {
            return;
        }
        if (action.equals(Intent.ACTION_PACKAGE_REMOVED) ||
                action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            LauncherApp.getInstance().initAppList();
            InterfaceCallBackManagement.getInstance().updateAppList();
            Log.d("APPReceiver", "onReceive: ");
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)){

                Log.d("APPReceiver", "onReceive is install: "+ LauncherApp.getInstance().isInstall());
            }
        }
        if (action.equals(Customer.ACTION_TXZ_CUSTOM_COMMAND)) {
            String command = intent.getStringExtra("key_type");
            if (command.equals("CMD_OPEN_APP_MGT")) {
                Intent intent1 = new Intent(context, AppListActivity.class);
                LauncherApp.getInstance().startActivity(intent1);
            }
        }
        if (action.equals(Customer.ACTION_TW_STATE)){
            Log.d("tag", "onReceive:kardidvr003 "
                    +intent.getIntExtra("machineState",-1));
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

                Log.d("APPReceiver",
                        "onReceive:getState " + info.getState() + " isConnected " + info.isConnected());
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isConnected()) {
                    if (LauncherApp.getInstance().isFirstLaunch()) {
                        if (TextUtils.isEmpty(Customer.PACKAGE_NAME_DVR3_TW)) {
                            Log.i("StartActivityTool", "package name is null!");
                            return;
                        }
                        Intent launchIntent = LauncherApp.getInstance().getPackageManager().getLaunchIntentForPackage(Customer.PACKAGE_NAME_DVR3_TW);
                        if (launchIntent == null) {
                            String s = LauncherApp.getInstance().getString(R.string.app_not_install);9
                            ToastTool.showToast(s + "\n" + Customer.PACKAGE_NAME_DVR3_TW);
                        } else {
//                            SharePreferencesTool.getInstance().saveBoolean("isFirstStart", false);
                            LauncherApp.getInstance().setFirstLaunch(false);
                            Log.d("APPReceiver", "onReceive: " + Customer.PACKAGE_NAME_DVR3_TW);
                            LauncherApp.getInstance().startActivity(launchIntent);
                        }
                    }
                }
            }
        }

    }

}
