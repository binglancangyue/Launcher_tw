package com.bixin.launcher_tw.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.listener.OnLocationListener;
import com.bixin.launcher_tw.model.tool.DialogTool;
import com.bixin.launcher_tw.model.tool.InterfaceCallBackManagement;
import com.bixin.launcher_tw.model.tool.SettingsFunctionTool;
import com.bixin.launcher_tw.model.tool.ToastTool;

import java.lang.ref.WeakReference;

public class SettingsLinkActivity extends BaseAppCompatActivity implements OnLocationListener.OnUpdateViewListener {
    private DialogTool mDialogTool;
    private TextView tvQR;
    private Switch switchGPS;
    private Switch switch4G;
    private String IMEI = null;
    private String ICCID = null;
    private SettingsFunctionTool mSettingsUtils;
    private int color;
    private MyHandle myHandle;
    private TextView tv4GState;
    private TextView tvGPSState;
    private boolean isOpen4G;
    private boolean isOpenGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void init() {
        myHandle = new MyHandle(this);
        mDialogTool = new DialogTool(this);
        mSettingsUtils = new SettingsFunctionTool();
        InterfaceCallBackManagement.getInstance().setOnUpdateViewListener(this);
        color = getResources().getColor(R.color.colorChecked);
        getIMEIAndICCID();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings_link;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void initView() {
        tvQR = findViewById(R.id.tv_qr);
        tv4GState = findViewById(R.id.tv_4g_state);
        tvGPSState = findViewById(R.id.tv_gps_state);
        tvQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });
        switchGPS = findViewById(R.id.switch_gps);
        switch4G = findViewById(R.id.switch_4g);
        switch4G.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("1231", "onCheckedChanged: " + isChecked);
                if (!mSettingsUtils.isHasSimCard()) {
                    switch4G.setChecked(false);
                    ToastTool.showToast(R.string.no_sim_card);
                    return;
                }
                isOpen4G = isChecked;
                if (!isChecked) {
                    mDialogTool.showClose4GDialog(mContext);
                } else {
                    open4G();
                }
                myHandle.sendEmptyMessage(1);
            }
        });
        switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("12144", "onCheckedChanged: " + isChecked);
                isOpenGPS = isChecked;
                if (!isChecked) {
                    mDialogTool.showCloseGPSDialog(mContext);
                } else {
//                    sendBroadcastToSystemUI("CLOSE_GPS", false);
                    mSettingsUtils.openGPS(true);
                }
                myHandle.sendEmptyMessage(2);
            }
        });
        initData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void initData() {
        if (!mSettingsUtils.isHasSimCard()) {
            switch4G.setChecked(false);
            isOpen4G = false;
        } else {
            isOpen4G = mSettingsUtils.getDataEnabled();
        }
        isOpenGPS = mSettingsUtils.isGpsOpen();
        switch4G.setChecked(isOpen4G);
        switchGPS.setChecked(isOpenGPS);
        update4GState();
        updateGPSState();
    }

    private void jump() {
        Intent it = new Intent("com.transiot.kardidvr003.qrcode");
        it.putExtra("guid", "kd003"); // guid  String
        it.putExtra("sn", "DVR"); // DVR String
        it.putExtra("imei", IMEI); // IMEI String
        it.putExtra("iccid", ICCID); // Sim ICCID sendBroadcast(it); String
        sendBroadcast(it);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void updateView(int type, boolean isChecked) {
        if (type == 1) {
            if (isChecked) {
                switchGPS.setChecked(isChecked);
            } else {
                mSettingsUtils.openGPS(false);
            }

        }
        if (type == 2) {
            if (isChecked) {
                switch4G.setChecked(isChecked);
            } else {
                mSettingsUtils.setDataEnabled(false);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void open4G() {
        if (mSettingsUtils.isHasSimCard()) {
            mSettingsUtils.setDataEnabled(!mSettingsUtils.getDataEnabled());
//            sendBroadcastToSystemUI("CLOSE_GPS", false);
        } else {
            switch4G.setChecked(false);
            ToastTool.showToast(R.string.no_sim_card);
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getIMEIAndICCID() {
        TelephonyManager tm = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        IMEI = tm.getImei(0);
        ICCID = tm.getSimSerialNumber();
        Log.d("tag", "getIMEI: " + IMEI + " ICCID " + ICCID);
    }

    private static class MyHandle extends Handler {
        private SettingsLinkActivity mActivity;

        public MyHandle(SettingsLinkActivity activity) {
            WeakReference<SettingsLinkActivity> weakReference = new WeakReference<>(activity);
            mActivity = weakReference.get();
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 1) {
                mActivity.update4GState();
            }
            if (msg.what == 2) {
                mActivity.updateGPSState();
            }
        }
    }

    private void update4GState() {
        if (isOpen4G) {
            tv4GState.setText(R.string.state_on);
            tv4GState.setTextColor(color);
        } else {
            tv4GState.setText(R.string.state_off);
            tv4GState.setTextColor(Color.WHITE);
        }
    }

    private void updateGPSState() {
        Log.d("tag", "updateGPSState:isOpenGPS " + isOpenGPS);
        if (isOpenGPS) {
            tvGPSState.setText(R.string.state_on);
            tvGPSState.setTextColor(color);
        } else {
            tvGPSState.setText(R.string.state_off);
            tvGPSState.setTextColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandle != null) {
            myHandle.removeCallbacksAndMessages(null);
            myHandle = null;
        }
    }
}
