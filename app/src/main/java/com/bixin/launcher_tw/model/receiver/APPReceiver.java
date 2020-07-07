package com.bixin.launcher_tw.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bixin.launcher_tw.model.LauncherApp;
import com.bixin.launcher_tw.model.bean.Customer;
import com.bixin.launcher_tw.model.tool.InterfaceCallBackManagement;
import com.bixin.launcher_tw.view.activity.AppListActivity;

/**
 * @author Altair
 * @date :2020.04.02 下午 03:10
 * @description:
 */
public class APPReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        Log.d("APPReceiver", "onReceive:action "+action);
        if (action.equals(Intent.ACTION_PACKAGE_REMOVED) ||
                action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            LauncherApp.getInstance().initAppList();
            InterfaceCallBackManagement.getInstance().updateAppList();
            Log.d("APPReceiver", "onReceive: ");
        }
        if (action.equals(Customer.ACTION_TXZ_CUSTOM_COMMAND)) {
            String command = intent.getStringExtra("key_type");
            if (command.equals("CMD_OPEN_APP_MGT")) {
                Intent intent1 = new Intent(context, AppListActivity.class);
                LauncherApp.getInstance().startActivity(intent1);
            }
        }
    }
}
